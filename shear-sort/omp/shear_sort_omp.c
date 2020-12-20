#include "utils.h"

int main(int argc, char** argv) {
	int i;
	int** matrix;
	
	if (argc != 2) {
		printf("Use: %s <size>\n", argv[0]);
		return 0;
	}

	int n = atoi(argv[1]);
	matrix = calloc(n, sizeof (int *));

	for (i = 0; i < n; i++) {
		matrix[i] = calloc(n, sizeof (int));
	}

	init_random_matrix(matrix, n);
	sort_matrix(matrix, n);

	int check = check_sorted (matrix, n);
	if (check == 1) {
		printf ("Is sorted!\n");
	} else {
		printf ("It is not sorted!\n");
	}
	print_matrix(matrix, n);
	free_memory(matrix, n);
	return 0;
}
