package com.bisu.serverlibrary;

import com.bisu.serverlibrary.io.FileHandler;
import com.bisu.serverlibrary.io.FileNameGenerator;
import com.bisu.serverlibrary.io.StreamHandler;

import java.io.File;
import java.util.List;

public class Config {

    public final File cacheRoot;
    public final FileNameGenerator fileNameGenerator;
    public final List<StreamHandler> streamHandlers; //加工流 加密等
    public final List<FileHandler> fileHandlers; //加工文件 加密等


    public Config(File cacheRoot, FileNameGenerator fileNameGenerator,
     List<StreamHandler>  streamHandlers,List<FileHandler> fileHandlers ) {
        this.fileHandlers = fileHandlers;
        this.streamHandlers = streamHandlers;
        this.cacheRoot = cacheRoot;
        this.fileNameGenerator = fileNameGenerator;
    }
}
