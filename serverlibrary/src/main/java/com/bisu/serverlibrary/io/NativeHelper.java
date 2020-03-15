package com.bisu.serverlibrary.io;

public class NativeHelper {

    static {
        System.loadLibrary("native-lib");
    }

    public native static long init(String url);

    public static long ptr;

    public native static void mmapWrite(String data,   String dir,long ptr, String file);

    public native static void mmapWriteByte(byte[] data, String path,long ptr, String file);
}
