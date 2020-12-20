build:
	mpicc -fopenmp shear_sort_hybrid.c utils.c -o shear_sort_hybrid -lm

clean:
	rm shear_sort_hybrid

run:
	mpirun -n 4 shear_sort_hybrid 8