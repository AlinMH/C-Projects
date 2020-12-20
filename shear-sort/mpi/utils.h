#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <mpi.h>

void init_random_matrix(int* matrix, int num_lines_per_proc, int size);
int compare_asc(const void* a, const void* b);
int compare_dsc(const void* a, const void* b);
void sort_lines_start_finish(int* matrix, int remaining_lines, int size);
void sort_lines(int* matrix, int num_lines_per_proc, int size);
void sort_columns(int* matrix, int size);
int check_sorted(int* matrix, int num_lines_per_proc, int size);
void print_matrix(int* matrix, int num_lines_per_proc, int size);