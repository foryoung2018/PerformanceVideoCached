package com.bisu.serverlibrary.net;

import java.io.IOException;
import java.net.MalformedURLException;

public interface DataSource {

    boolean connected();

    void get(long offset) throws IOException;

    int read(byte[] bytes) throws Exception;

    void close();

    long length();

    void open(long offset);
}
