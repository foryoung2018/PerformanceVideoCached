package com.bisu.serverlibrary.io;

public class NativeHelper {

    static {
        System.loadLibrary("native-lib");
    }

    public native static void init(String url);

    public native static void mmapWrite(String data, String dir, String file);

    public native static void mmapWriteByte(byte[] data, String path, String file);
}
