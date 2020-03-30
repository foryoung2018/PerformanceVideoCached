package com.bisu.serverlibrary.io;

public interface Cached {
    long available();

    void append(byte[] buffer, int readBytes);
}
