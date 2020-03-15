//
// Created by foryoung on 2020/3/15.
//

#ifndef PERFORMANCEVIDEOCACHED_MWRITE_H
#define PERFORMANCEVIDEOCACHED_MWRITE_H

#include <stdio.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <strings.h>
#include <android/log.h>
#include <string>
#include <stdio.h>
#include <unistd.h>
#include <error.h>


#define  LOG(...) __android_log_print(ANDROID_LOG_ERROR,"cached_server",__VA_ARGS__);
#define MAX_LINE 1024

class Mwrite {
public:
    //写在头文件里的变量如果不加上static 的话会造成重复引用 最好的办法就是不要写在头文件
     int8_t *_ptr=0;
     int32_t offset;
     size_t _size;
     int32_t _fd;

public:
    void mmap_write(const char *src_ptr,const int32_t len, const char *path_ptr, const char *name_ptr);
    void mmap_write(const std::string src_ptr, const std::string path, const std::string name);
    void mmap_write(const void *src_ptr, const int32_t len, const char *path_ptr, const char *name_ptr);
    void readFile(char* path);
};



#endif //PERFORMANCEVIDEOCACHED_MWRITE_H
