#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include <omp.h>

void init_random_matrix(int** matrix, int size);
int compare_asc(const void* a, const void* b);
int compare_dsc(const void* a, const void* b);
void sort_lines(int** matrix, int size);
void sort_columns(int** matrix, int size);
void sort_matrix(int** matrix, int size);
void print_matrix(int** matrix, int size);
int check_sorted(int** matrix, int size);
void free_memory(int** matrix, int size);