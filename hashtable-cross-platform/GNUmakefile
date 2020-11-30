CC = gcc
CFLAGS = -Wall -fPIC

LDFLAGS = -L.

all: main

main: main.o table.o
	$(CC) $(CFLAGS) main.o table.o -o main -L. -lhash

main.o: main.c
	$(CC) $(CFLAGS) -c main.c

table.o: table.c
	$(CC) $(CFLAGS) -c table.c

clean:
	rm *.o main