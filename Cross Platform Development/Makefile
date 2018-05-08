CC = cl
CFLAGS = /nologo /W3 /EHsc /Za /DWIN /D_CRT_SECURE_NO_DEPRECATE

main: main.obj table.obj hash.lib
	link /out:tema1.exe main.obj table.obj hash.lib

table.obj: table.c
	$(CC) /DWIN /D_CRT_SECURE_NO_DEPRECATE /c table.c

main.obj: main.c
	$(CC) /DWIN /D_CRT_SECURE_NO_DEPRECATE /c main.c