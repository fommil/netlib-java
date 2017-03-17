@echo off

cd "%~dp0"
rem Build and install the generator
pushd generator
mvn clean && mvn install %*
if ERRORLEVEL 1 (
   popd
   goto error
)
popd
rem Build and install the actual project
mvn clean && mvn install %*
if errorlevel 1 goto error
exit /b 0
:error
exit /b 1
