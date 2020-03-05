package com.bisu.serverlibrary.server;

import android.util.Log;

import com.bisu.serverlibrary.Config;
import com.bisu.serverlibrary.io.FileHandler;
import com.bisu.serverlibrary.io.FileNameGenerator;
import com.bisu.serverlibrary.io.StreamHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.bisu.serverlibrary.server.Preconditions.checkNotNull;

public class CachedServer  {

    private static final String TAG = CachedServer.class.getSimpleName();
    private ServerSocket serverSocket;
    private  int port;
    private  Thread socketThread;
    private static final String PROXY_HOST = "127.0.0.1";
    Config config;
    private final ExecutorService socketProcessor = Executors.newFixedThreadPool(8);

    public CachedServer(Config config) {
        this.config = config;
        try {
            initServerSync(); //同步创建本地服务
            initClientSync(); //同步创建获取数据的任务
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //TODO
    private void initClientSync() {

    }

    //TODO
    private void initServerSync() throws IOException, InterruptedException {
        CountDownLatch startSignal = new CountDownLatch(1);
        InetAddress inetAddress = InetAddress.getByName(PROXY_HOST);
        this.serverSocket = new ServerSocket(0, 8, inetAddress);
        this.port = serverSocket.getLocalPort();
        this.socketThread = new Thread(new SocketRunable(startSignal), "socketserver");
        this.socketThread.start();
        startSignal.await();
    }

    class SocketRunable implements Runnable{


        private final CountDownLatch startSigal;

        public SocketRunable(CountDownLatch startSignal) {
            this.startSigal = startSignal;
        }

        @Override
        public void run() {
            startSigal.countDown();
            waitForRequest();
        }

        private void waitForRequest() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Socket socket = serverSocket.accept();
                    socketProcessor.submit(new SocketProcessorRunnable(socket));
                }
            } catch (IOException e) {

            }
        }

        private final class SocketProcessorRunnable implements Runnable {

            private final Socket socket;

            public SocketProcessorRunnable(Socket socket) {
                this.socket = socket;
            }

            @Override
            public void run() {
                processSocket(socket);
            }
        }

        private void processSocket(Socket socket) {
            try {
                // 读取客户端数据    
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String clientInputStr = input.readLine();//这里要注意和客户端输出流的写方法对应,否则会抛 EOFException  
                // 处理客户端数据    
                Log.d(TAG, "processSocket() called with: clientInputStr = [" + clientInputStr + "]");
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        socket = null;
                        e.printStackTrace();
                    }
                }
            } 
        }

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
        return String.format(Locale.US, "http://%s:%d/%s", PROXY_HOST, port, CacheUtils.encode(url));
    }
//    public String getProxyUrl(String url) {
//        return getCacheFile(url).getAbsolutePath();
//    }

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
