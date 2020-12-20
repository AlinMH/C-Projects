build:
	mpicc shear_sort_mpi.c utils.c -o shear_sort_mpi -lm

clean:
	rm shear_sort_mpi

run:
	mpirun -n 4 shear_sort_mpi 8