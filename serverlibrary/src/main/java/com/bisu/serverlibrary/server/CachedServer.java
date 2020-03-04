package com.bisu.serverlibrary.server;

import android.content.Context;

import com.bisu.serverlibrary.Config;
import com.bisu.serverlibrary.io.FileHandler;
import com.bisu.serverlibrary.io.FileNameGenerator;
import com.bisu.serverlibrary.io.StreamHandler;

import java.io.File;
import java.util.List;

import static com.bisu.serverlibrary.Preconditions.checkNotNull;

public class CachedServer  {

    Config config;

    public CachedServer(Config config) {
        this.config = config;
        initServerSync(); //同步创建本地服务
        initClientSync(); //同步创建获取数据的任务
    }

    //TODO
    private void initClientSync() {

    }

    //TODO
    private void initServerSync() {

    }

    public static final class Builder {

        private File cacheRoot;
        private FileNameGenerator fileNameGenerator;
        private List<StreamHandler> streamHandlers;
        private List<FileHandler> fileHandlers;

        public Builder() {
            this.cacheRoot = getAvaibleCachedFile();
        }

        public Builder cacheDirectory(File file) {
            this.cacheRoot = checkNotNull(file);
            return this;
        }

        public Builder fileNameGenerator(FileNameGenerator fileNameGenerator) {
            this.fileNameGenerator = checkNotNull(fileNameGenerator);
            return this;
        }

        public CachedServer build(){
            return new CachedServer(new Config(cacheRoot,fileNameGenerator,streamHandlers,  fileHandlers ));
        }
    }

    public static  File getAvaibleCachedFile(){
        return null;
    }

    public String getProxyUrl(String url) {
        return getCacheFile(url).getAbsolutePath();
    }

    private File getCacheFile(String url) {
        File cacheDir = config.cacheRoot;
        String fileName = config.fileNameGenerator.generate(url);
        return new File(cacheDir, fileName);
    }

    public  void registSreamHandleChain(List<StreamHandler> list){

    }

    public  void registFileHandleChain(List<FileHandler> list){

    }


}
