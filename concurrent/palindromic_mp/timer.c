#include <stdio.h>
#include <stdlib.h>
#include "timer.h"

/*
 * Starts the timer t
 */
void start_timer(timer * t) {
	gettimeofday(&(t->start),NULL);
}

/*
 * Stops the timer t
 */
void stop_timer(timer * t) {
	gettimeofday(&(t->stop),NULL);
}

/*
 * Returns the number of seconds the timer run (with much precision)
 */
float get_timer_result(timer *t) {
	return ((t->stop.tv_sec-t->start.tv_sec)*1000000.0 + (t->stop.tv_usec-t->start.tv_usec))/1000000.0;
}
