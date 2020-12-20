#include "utils.h"

int** matrix;
int size;
int num_lines_per_proc;
pthread_barrier_t line_barrier;
pthread_barrier_t column_barrier;

void init_random_matrix() {
	int i, j;
	srand(time(NULL));
	for(i = 0; i < size; i++) {
		for(j = 0; j < size; j++) {
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

void* sort_lines(void* start_line) {
	int i, k;
	int start = *(int*)start_line;
	int finish = start + num_lines_per_proc;
	int dif = size - finish;
	
	// the last process will work with more lines
	if (dif <= num_lines_per_proc) {
		finish = size;
	}
	
	for(k = 0; k <= ceil(log2(size)); k++) {
		
		pthread_barrier_wait(&column_barrier);
		for (i = start; i < finish; i++) {
			if (i % 2 == 0) { // even lines > ascending
				qsort(matrix[i], size, sizeof(int), compare_asc);

			} else { // odd lines > decending
				qsort(matrix[i], size, sizeof(int), compare_dsc);
			}
		}
		
		pthread_barrier_wait(&line_barrier);
	}
	pthread_barrier_wait(&column_barrier);
	return NULL;
}

void* sort_columns(void* arg) {
	int i, j, k;
	int col[size];
		
	for(k = 0; k <= ceil(log2(size)); k++) {
		pthread_barrier_wait(&line_barrier);
		for (j = 0; j < size; j++) {
			for (i = 0; i < size; i++) {
				col[i] = matrix[i][j];
			}

			qsort(col, size, sizeof(int), compare_asc);

			for (i = 0; i < size; i++) {
				matrix[i][j] = col[i] ;
			}
		}
		pthread_barrier_wait(&column_barrier);
	}
	return NULL;
}

void sort_matrix(int num_cores) {
	int i;
	num_lines_per_proc = size / (num_cores - 1);
	
	// create threads for sorting
	pthread_t* threads = calloc(num_cores, sizeof(pthread_t));
	int* start_line = calloc(num_cores - 1, sizeof(int));	

	pthread_barrier_init(&line_barrier, NULL, num_cores);
	pthread_barrier_init(&column_barrier, NULL, num_cores);
		
	// first_thread > sort columns; the others > sort lines
	for (i = 0; i < num_cores - 1; i++) {
		// the line from with the thread will start working
		start_line[i] = i * num_lines_per_proc;
		pthread_create(&threads[i], NULL, &sort_lines, &start_line[i]);
	}
	// thread for sorting columns
	pthread_barrier_wait(&column_barrier);
	pthread_create(&threads[i], NULL, &sort_columns, NULL);
	
	// wait the threads to complete the work
	for (i = 0; i < num_cores; i++) {
		pthread_join(threads[i], NULL);
	}
	
	free(threads);
	
}

int check_sorted() {
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

void print_matrix() {
	int i, j;
	for (i = 0; i < size; i++) {
		for (j = 0; j < size; j++) {
			printf("%d\t", matrix[i][j]);
		}
		printf("\n");
	}
	printf("\n");
}

void free_memory() {
	int i;	
	for(i = 0; i < size; i++) {
		free (matrix[i]);
	}
	
	free (matrix);
}