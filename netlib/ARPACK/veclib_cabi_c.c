#define WRAP_F77(a) a##_
void WRAP_F77(veclib_sdot)(const int *N, const float *X, const int
*incX, const float *Y, const int *incY, float *dot)
{
    *dot = cblas_sdot(*N, X, *incX, Y, *incY);
}

