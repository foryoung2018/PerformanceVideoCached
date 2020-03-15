//
// Created by foryoung on 2020/3/15.
//

#include "Mwrite.h"
#include <errno.h>

void Mwrite::mmap_write(const char *src_ptr, const int32_t len, const char *path_ptr,
                        const char *name_ptr) {

        if(!src_ptr || !path_ptr || !name_ptr || len<=0){
            LOG("非法参数");
            return;
        }
        char name[256] = {0};
        strcat(name, path_ptr);
        strcat(name + strlen(path_ptr), "/");
        strcat(name + strlen(path_ptr) + 1, name_ptr);

        LOG(name);
        _fd=open(name,O_RDWR | O_CREAT, 0644);//打开或创建指定绝对文件名对应的文件并赋予当前用户可读可写权限
        LOG("errno%d",errno);
        if(_fd==-1)
        {
            LOG("打开文件失败");
            return;
        }
#if 1
        _size= (size_t) getpagesize() *100;//要写入到磁盘文件的字节长度，不一定是页的大小，可以是任意值
        ftruncate(_fd,_size);
        _ptr= (int8_t *) mmap(0, _size + 1, PROT_READ | PROT_WRITE, MAP_SHARED, _fd, 0);
        LOG("errno%d",errno);
        if(_ptr==MAP_FAILED)
        {
            LOG("映射失败1");
            return;
        }
        LOG("offset %d", offset);
        memcpy(_ptr +offset,src_ptr,len);
        msync(_ptr,len,MS_SYNC);
        munmap(_ptr,len);
        readFile(name);
        offset = offset + len;
#endif
        LOG("写入数据成功");
        // close(_fd);

}

void Mwrite::mmap_write(const std::string src, const std::string path, const std::string name) {

    if(src.empty() || path.empty() || name.empty())
    {
        return;
    }
    size_t len=src.length();
    std::string file=path+name;
    _fd=open(file.data(),O_RDWR | O_CREAT, 0644);//打开或创建指定绝对文件名对应的文件并赋予当前用户可读可写权限
    if(_fd==-1)
    {
        LOG("打开文件失败");
        return;
    }
    ftruncate(_fd,_size);
    _ptr= (int8_t *) mmap(0, len, PROT_READ | PROT_WRITE, MAP_SHARED, _fd, 0);
    if(_ptr==MAP_FAILED)
    {
        LOG("映射失败2");
        return;
    }
    memcpy(_ptr,src.data(),len);
    msync(_ptr,len,MS_SYNC);
    munmap(_ptr,len);
}

void Mwrite::mmap_write(const void *src_ptr, const int32_t len, const char *path_ptr,
                        const char *name_ptr) {
    if(!src_ptr || !path_ptr || !name_ptr || len<=0 ){
        LOG("非法参数");
        return;
    }
    char name[256] = {0};
    strcat(name, path_ptr);
    strcat(name + strlen(path_ptr), "/");
    strcat(name + strlen(path_ptr) + 1, name_ptr);

    _fd=open(name,O_RDWR | O_CREAT, 0644);//打开或创建指定绝对文件名对应的文件并赋予当前用户可读可写权限
    if(_fd==-1)
    {
        LOG("打开位图文件失败");
        return;
    }
#if 1
    _size= (size_t) getpagesize();//要写入到磁盘文件的字节长度，不一定是页的大小，可以是任意值
    ftruncate(_fd,len);
    _ptr= (int8_t *) mmap(0, len, PROT_READ | PROT_WRITE, MAP_SHARED, _fd, 0);
    if(_ptr==MAP_FAILED)
    {
        LOG("映射位图失败");
        return;
    }
    memcpy(_ptr,src_ptr,len);

    msync(_ptr,len,MS_SYNC);
    munmap(_ptr,len);
    readFile(name);
#endif
    LOG("写入位图数据成功");
}

void Mwrite::readFile(char *path) {
    char buf[MAX_LINE];  /*缓冲区*/
    FILE *fp;            /*文件指针*/
    int len;             /*行字符个数*/
    if((fp = fopen(path,"r")) == NULL)
    {
        LOG("fail to read");
        exit (1) ;
    }
    while(fgets(buf,MAX_LINE,fp) != NULL)
    {
        len = strlen(buf);
        buf[len-1] = '\0';  /*去掉换行符*/
        LOG("%s %d \n",buf,len );
    }
}
