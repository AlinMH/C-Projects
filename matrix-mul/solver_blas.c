/*
 * Tema 2 ASC
 * 2018 Spring
 * Catalin Olaru / Vlad Spoiala
 */
#include "utils.h"
#include "cblas.h"

/* 
 * Add your BLAS implementation here
 */
double* my_solver(int N, double *A) {
	printf("BLAS SOLVER\n");
	double *C = calloc(2 * N * N, sizeof(double));
	double *alpha = calloc(2, sizeof(double));
	double *beta = calloc(2, sizeof(double));

	alpha[0] = 1;
	alpha[1] = 0;

	beta[0] = 0;
	beta[1] = 0;

	cblas_zsyrk(CblasRowMajor, CblasUpper, CblasNoTrans, N, N, alpha, A, N, beta, C, N);
	free(alpha);
	free(beta);
	return C;
}
