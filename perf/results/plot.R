library(stringr)

# this quickly-knocked-up-only-to-be-run-once file produces
# (pretty ugly) performance charts of netlib-java on various
# platforms.

# for A in ddot dgemm dgetri dsaupd ; do convert -density 300 -depth 8 -quality 85 $A.pdf $A.png ; done

benchmarks = c("linpack")
pBenchmarks = c("ddot", "dgemm", "dgetri", "dsaupd")
targets = c("linux-amd64", "linux-i386", "mac_os_x-x86_64", "windows_8-amd64", "windows_8-x86")
impls = c("f2jblas", "nativerefblas")

par(cex = 1.5, cex.lab=1.5, cex.axis=1.5, cex.main=1.5, cex.sub=1.5, family="Palatino")

setPdfOut <- function(filename){
	if (interactive()) {
		quartz()
	} else {
		pdf(filename, width=11, height=8.5)
	}
}

# calls f(file, target, implementation, count) for every file that matches the 
# pattern expected for the benchmark b
foreachResult = function(f, b) {
	regex = paste("(.*)-", b, "-(.*)\\.csv.gz", sep="")
	files = list.files(pattern=regex)
	count = 0
	for (file in files) {
		groups = str_match(file, regex)
		t = groups[1,2]
		i = groups[1,3]
		count = count + 1
		f(file, t, i, count)
	}
}

printer = function(f, t, i, c) {
	print(paste(f, t, i, c))
}


doPlot = function(data, col, sym) {
	# could potentially do a confidence plot like
	# https://github.com/fommil/golf-stats/blob/master/SbsScraper/R/golfStats.R
	
	d = data.frame(data)
	dd = c()
	for (x in unique(data[,1])) {
		sub = subset(d, size == x)
		avg = mean(sub[,2])
		dd = rbind(dd, c(x, avg))
	}	
	points(dd, pch=as.numeric(sym), lwd=1, col=col)
}

# nobody would be proud of this function...
getPlotParams = function(t, i) {
	if (regexpr("arm", t) > 0) {
		sym = 8
	} else if (regexpr("linux", t) > 0) {
		sym = 3
	} else if (regexpr("mac", t) > 0) {
		sym = 20
	} else if (regexpr("win", t) > 0) {
		sym = 4
	}
	
	if (regexpr("nativeref", i) > 0) {
		col = "red"
	}
	else if (regexpr("nativesystem", i) > 0) {
		col = "cyan"
	}
	else if (regexpr("jamvm", t) > 0) {
		col = "magenta"
	}
	else if (regexpr("avian", t) > 0) {
		col = "blue"
	}
	else if (regexpr("jdk7", t) > 0) {
		col = "orange"
	}
	else if (regexpr("veclib", i) > 0) {
		col = "black"
	}
	else if (regexpr("CBLAS", i) > 0) {
		col = "tomato1"
	}
	else if (regexpr("mkl", i) > 0) {
		col = "orange"
	}
	else if (regexpr("atlas", i) > 0) {
		col = "gray"
	}
	else if (regexpr("cuda_nooh", i) > 0) {
		col = "blue"
	}
	else if (regexpr("cuda", i) > 0) {
		col = "purple"
	}
	else if (regexpr("clblas", i) > 0) {
		col = "brown"
	}
	else {
		col = "yellow"
	}
	
	c(col, sym)
}

leg = c()
addData = function(f, t, i, c) {
	data = read.csv(gzfile(f), col.names=c("size", "time"))
	data[,2] = data[,2] / 1000000000
	settings = getPlotParams(t, i)
	col = settings[1]
	sym = settings[2]
	
	doPlot(data, col, sym)
	myleg <- rbind(leg, c(paste(t, i), col, sym))
	assign("leg", myleg, env=globalenv()) 
}

addDataSingle = function(f, t, i, c) {
	data = read.csv(f, col.names=c("time"))
	data[,1] = data[,1] / 1000000000
	settings = getPlotParams(t, i)
	col = settings[1]
	sym = settings[2]
	
	points(data[,1], pch=as.numeric(sym), lwd=1, col=col)

	myleg <- rbind(leg, c(paste(t, i), col, sym))
	assign("leg", myleg, env=globalenv()) 
	print(leg)
}

logAxis = function(type, lims) {
	x1 <- floor(log10(lims))
	pow <- seq(x1[1], x1[2]+1)
	ticksat <- as.vector(sapply(pow, function(p) (1:10)*10^p))
	axis(type, 10^pow)
	axis(type, ticksat, labels=NA, tcl=-0.25, lwd=0, lwd.ticks=1)
}

for (b in benchmarks) {
	assign("leg", c(), env=globalenv()) 
	xlim = c(0, 10)
	ylim = c(1e-04, 100)
	setPdfOut(paste("./", b, ".pdf", sep=""))

	plot(c(), xlab="Iteration", ylab="Time (seconds)", log="y", xlim=xlim, ylim=ylim, main=paste(b, "Performance"))
	
	foreachResult(addDataSingle, b)
	
	logAxis(2, ylim)
	
	legend("topright", legend=leg[,1], pch=as.numeric(leg[,3]), col=leg[,2], bty="n")
}


for (b in pBenchmarks) {
	assign("leg", c(), env=globalenv())
	xlim = c(10, 1e06)
	ylim = c(1e-06, 1)
	setPdfOut(paste("./", b, ".pdf", sep=""))
	plot(1:100, xlab="Array (size)", ylab="Time (seconds)", log="xy", xaxt="n", yaxt="n", xlim=xlim, ylim=ylim, main=paste(b, "Performance"))
	
	foreachResult(addData, b)
	
	logAxis(1, xlim)
	logAxis(2, ylim)
	
	legend("topleft", legend=leg[,1], pch=as.numeric(leg[,3]), col=leg[,2], bty="n")
}

