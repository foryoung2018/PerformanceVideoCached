package com.bisu.serverlibrary.server;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.bisu.serverlibrary.Config;
import com.bisu.serverlibrary.Constant;
import com.bisu.serverlibrary.R;
import com.bisu.serverlibrary.io.FileHandler;
import com.bisu.serverlibrary.io.FileNameGenerator;
import com.bisu.serverlibrary.io.NativeHelper;
import com.bisu.serverlibrary.io.StreamHandler;
import com.bisu.serverlibrary.net.DataSource;
import com.bisu.serverlibrary.net.HttpDataSource;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static com.bisu.serverlibrary.server.Preconditions.checkNotNull;

public class CachedServer  {



    private static final String TAG = CachedServer.class.getSimpleName();
    private final Context context;
    private ServerSocket serverSocket;
    private  int port;
    private  Thread socketThread;
    private static final String PROXY_HOST = "127.0.0.1";
    Config config;
    private final ExecutorService socketProcessor = Executors.newFixedThreadPool(8);

    public CachedServer(Config config, Context context) {
        this.config = config;
        this.context = context;
        try {
            initServerSync(); //同步创建本地服务
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //TODO
    private void initClient(String url) throws IOException {
        DataSource dataSource = new HttpDataSource(url);
        dataSource.get(0);
        new Thread(new ClientRunable(dataSource)).start();

    }

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
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
                // 处理客户端数据
                String line;
                StringBuilder requetLink = new StringBuilder();
                while (!TextUtils.isEmpty(line = input.readLine())) { // until new line (headers ending)
//                    Log.d(Constant.TAG, "processSocket() called with: line = [" + line + "]");
                    requetLink.append(line);
                }
                String url = decode(findUri(requetLink.toString()));
//                Log.d(Constant.TAG, "processSocket() called with: uri = [" + url + "]");
                initClient(url); //同步创建获取数据的任务
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

        private  final Pattern URL_PATTERN = Pattern.compile("GET /(.*) HTTP");

        private String findUri(String request) {
            Matcher matcher = URL_PATTERN.matcher(request);
            if (matcher.find()) {
                return matcher.group(1);
            }
            throw new IllegalArgumentException("Invalid request `" + request + "`: url not found!");
        }

        String decode(String url) {
            try {
                return URLDecoder.decode(url, "utf-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Error decoding url", e);
            }
        }

    }


    public static final class Builder {

        private File cacheRoot;
        private FileNameGenerator fileNameGenerator;
        private List<StreamHandler> streamHandlers;
        private List<FileHandler> fileHandlers;
        private Context context;

        public Builder(Context context) {
            this.context = context;
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
            return new CachedServer(new Config(cacheRoot,fileNameGenerator,streamHandlers,  fileHandlers ), context );
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


    private class ClientRunable implements Runnable {
        private DataSource dataSource;

        public ClientRunable(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public void run() {

            OutputStream os = null;
            try {
                os =new FileOutputStream(new File(context.getFilesDir().getAbsolutePath() + "a.jpg"));
                byte[] buffer = new byte[Constant.DEFAULT_BUFFER_SIZE];
                int readBytes = 0;
                while (dataSource.connected()&& (readBytes = dataSource.read(buffer)) != -1){
                    Log.d(Constant.TAG, "run() called buffer = " + Arrays.toString(buffer));
                    os.write(buffer,0,readBytes);
                }
                Log.d(Constant.TAG, "run() bitmap path = " + context.getFilesDir().getAbsolutePath() + "a.jpg");
                os.close();
                dataSource.close();
                Bitmap bitmap = BitmapFactory.decodeFile(context.getFilesDir().getAbsolutePath() + "a.jpg");
                byte[] bytes = bitmap2Bytes(bitmap);
                NativeHelper.mmapWriteByte(bytes, context.getFilesDir().getAbsolutePath(),NativeHelper.ptr, "b.jpg");

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    os.close();
                    dataSource.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream byteArrOutStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrOutStream);
        return byteArrOutStream.toByteArray();
    }

}
