CFLAGS=-Wall -std=gnu99
LDFLAGS=-pthread
LDLIBS=-lm

all: palindromic

clean: 
	rm -f palindromic
	rm -f *.o

palindromic: palindromic.o timer.o

.PHONY: clean
