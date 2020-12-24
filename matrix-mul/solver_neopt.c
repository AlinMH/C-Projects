/*
 * Tema 2 ASC
 * 2018 Spring
 * Catalin Olaru / Vlad Spoiala
 */
#include "utils.h"

/*
 * Add your unoptimized implementation here
 */
double* my_solver(int N, double *A) {
	printf("NEOPT SOLVER\n");
	double *C = calloc(2 * N * N, sizeof(double));
	int i, j, k;

	for (i = 0; i < N; i++) {
		for (j = i; j < N; j++) {
			for (k = 0; k < N; k++) {
				C[2 * (i * N + j)] += A[2 * (i * N + k)] * A[2 * (j * N + k)] - A[2 * (i * N + k) + 1] * A[2 * (j * N + k) + 1];
				C[2 * (i * N + j) + 1] += A[2 * (i * N + k)] * A[2 * (j * N + k) + 1] + A[2 * (i * N + k) + 1] * A[2 * (j * N + k)];
			}
		}
	}

	return C;
}
