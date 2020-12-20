#include "utils.h"

void init_random_matrix(int** matrix, int size) {
	int i, j;
	srand(time(NULL));

	#pragma omp parallel for private(i, j) collapse(2)
	for (i = 0; i < size; i++) {
		for (j = 0; j < size; j++) {
			matrix[i][j] = rand() % (size * size) + 1; 
		}
	}
}

int compare_asc(const void* a, const void* b) {
	int A = *(int*)a;
	int B = *(int*)b;
	return A - B;
}

int compare_dsc(const void* a, const void* b) {
   	int A = *(int*)a;
	int B = *(int*)b;
	return B - A;
}

void sort_lines(int** matrix, int size) {
	int i;
	#pragma omp parallel for
	for (i = 0; i < size; i++) {
		if (i % 2 == 0) {
			qsort(matrix[i], size, sizeof(int), compare_asc);
		} else {
			qsort(matrix[i], size, sizeof(int), compare_dsc);
		}
	}
}

void sort_columns(int** matrix, int size) {
	int i, j;
	int col[size];
	
	for (j = 0; j < size; j++) {
		#pragma omp parallel for
		for (i = 0; i < size; i++) {
			col[i] = matrix[i][j];
		}
		qsort(col, size, sizeof(int), compare_asc);
		
		#pragma omp parallel for
		for (i = 0; i < size; i++) {
			matrix[i][j] = col[i];
		}
	}
}

void sort_matrix(int** matrix, int size) {
	int k;
	
	for(k = 0; k <= ceil(log2(size)); k++) {
		sort_lines(matrix, size);
		sort_columns(matrix, size);
	}
}

void print_matrix(int** matrix, int size) {
	int i, j;
	for (i = 0; i < size; i++) {
		for (j = 0; j < size; j++) {
			printf("%d\t", matrix[i][j]);
		}
		printf("\n");
	}
	printf("\n");
}

int check_sorted(int** matrix, int size) {
	int i, j, even;
	
	for (i = 0; i < size; i++) {
		if (i % 2 == 0) {
			even = 1; // even line > ascending
		} else {
			even = 0; // odd line > descending
		}
		
		for (j = 0; j < size - 1; j++) {
			if (even) {
				if (matrix[i][j] > matrix[i][j + 1])
					return 0;
			} else {
				if (matrix[i][j] < matrix[i][j + 1])
					return 0;
			}
		}
	}
	
	return 1;
}


void free_memory(int** matrix, int size) {
	int i;
	for(i = 0; i < size; i++) {
		free (matrix[i]);
	}
	
	free (matrix);
}