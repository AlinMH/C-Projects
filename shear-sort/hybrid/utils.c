#include "utils.h"

void init_random_matrix(int* matrix, int num_lines_per_proc, int size){
	int i, j;
	srand(size);

	#pragma omp parallel for private(i, j) collapse(2)
	for(i = 0; i < num_lines_per_proc; i++){
		for(j = 0; j < size; j++){
			matrix[i * size + j] = rand() % (size * 2) + 1; 
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

void sort_lines_start_finish(int* matrix, int remaining_lines, int size){
	int i, j;
	int line[size];
	int start = size - remaining_lines - 1;
	int finish = size;
	
	for (i = start; i < finish; i++){
		#pragma omp parallel for private(j)
		for (j = 0; j < size ; j++) {
			line[j] = matrix[i * size + j];
		}
	
		if (i % 2 == 0) {// even lines > ascending
			qsort(line, size, sizeof(int), compare_asc);
		} else { // odd lines > decending
			qsort(line, size, sizeof(int), compare_dsc);
		}
		
		#pragma omp parallel for private(j)
		for (j = 0; j < size ; j++) {
			matrix[i * size + j] = line[j];
		}
	}
}

void sort_lines(int* matrix, int num_lines_per_proc, int size){
	int i, j;
	int line[size];

	for (i = 0; i < num_lines_per_proc; i++){
		#pragma omp parallel for private(j)
		for (j = 0; j < size ; j++) {
			line[j] = matrix[i * size + j];
		}
	
		if (i % 2 == 0) {// even lines > ascending
			qsort(line, size, sizeof(int), compare_asc);
		} else { // odd lines > decending
			qsort(line, size, sizeof(int), compare_dsc);
		}
		
		#pragma omp parallel for private(j)
		for (j = 0; j < size ; j++) {
			matrix[i * size + j] = line[j];
		}
	}
}

void sort_columns(int* matrix,  int size) {
	int i, j;
	int col[size];
	
	for (j = 0; j < size; j++){
		#pragma omp parallel for private(i)
		for (i = 0; i < size; i++) {
			col[i] = matrix[i * size + j];
		}

		qsort(col, size, sizeof(int), compare_asc);

		#pragma omp parallel for private(i)
		for (i = 0; i < size; i++) {
			matrix[i * size + j] = col[i] ;
		}
	}
}

int check_sorted(int* matrix, int num_lines_per_proc, int size){
	int i, j, even, current;
	
	for (i = 0; i < size; i++) {
		if (i % 2 == 0){
			even = 1; // even line > ascending
		} else {
			even = 0; // odd line > descending
		}
		
		for (j = 0; j < size - 1; j++) {
			current = i * size + j;
			if (even){
				if (matrix[current] > matrix[current + 1])
					return 0;
			} else {
				if (matrix[current] < matrix[current + 1])
					return 0;
			}
		}
	}
	
	return 1;
}

void print_matrix(int* matrix, int num_lines_per_proc, int size){
	int i, j;
	for (i = 0; i < num_lines_per_proc; i++) {
		for (j = 0; j < size; j++) {
			printf("%d\t", matrix[i * size + j]);
		}
		printf("\n");
	}
	printf("\n");
}
