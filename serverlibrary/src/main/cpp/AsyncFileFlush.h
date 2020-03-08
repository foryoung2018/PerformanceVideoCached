

#ifndef PERFORMANCEVIDEOCACHED_MMAP_UTIL_H
#define PERFORMANCEVIDEOCACHED_MMAP_UTIL_H

#include <sys/types.h>
#include <vector>
#include <thread>
#include <mutex>
#include <condition_variable>
#include <unistd.h>
#include "FlushBuffer.h"

class AsyncFileFlush {

public:
    AsyncFileFlush();
    ~AsyncFileFlush();
    bool async_flush(FlushBuffer* flushBuffer);
    void stopFlush();

private:
    void async_log_thread();
    ssize_t flush(FlushBuffer* flushBuffer);

    bool exit = false;
    std::vector<FlushBuffer*> async_buffer;
    std::thread async_thread;
    std::condition_variable async_condition;
    std::mutex async_mtx;
};
#endif //PERFORMANCEVIDEOCACHED_MMAP_UTIL_H