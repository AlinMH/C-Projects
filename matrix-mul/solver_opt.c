/*
 * Tema 2 ASC
 * 2018 Spring
 * Catalin Olaru / Vlad Spoiala
 */
#include "utils.h"

/*
 * Add your optimized implementation here
 */
double* my_solver(int N, double *A) {
	printf("OPT SOLVER\n");
	double *C = calloc(2 * N * N, sizeof(double));
	int i, j, k;

	for (i = 0; i < N; i++) {
		double *orig_a = &A[2 * i * N];
		double *orig_b = &A[2 * i * N + 1];
		for (j = i; j < N; j++) {
			double *a = orig_a;
			double *b = orig_b;
			
			double *c = &A[2 * j * N];
			double *d = &A[2 * j * N + 1];
			register double sum_r = 0;
			register double sum_i = 0;
			for (k = 0; k < N; k++) {
				sum_r += (*a) * (*c) - (*b) * (*d);
				sum_i += (*a) * (*d) + (*c) * (*b);

				a += 2;
				b += 2;

				c += 2;
				d += 2;
			}
			C[2 * (i * N + j)] = sum_r;
			C[2 * (i * N + j) + 1] = sum_i;
		}
	}
	return C;
}