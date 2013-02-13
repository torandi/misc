#ifndef _TIMER_H_
#define _TIMER_H_
#include <sys/time.h>

typedef struct {
	struct timeval start;
	struct timeval stop;
} timer;

//timer functions
void start_timer(timer * t);
void stop_timer(timer * t);
float get_timer_result(timer * t);
#endif
