package com.bisu.serverlibrary.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.bisu.serverlibrary.Config;
import com.bisu.serverlibrary.Constant;
import com.bisu.serverlibrary.io.Cached;
import com.bisu.serverlibrary.net.HttpRequest;
import com.bisu.serverlibrary.server.CacheUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Locale;

import static com.bisu.serverlibrary.server.CacheUtils.DEFAULT_BUFFER_SIZE;


public class RealHttpClient {

    private Cached cached;
    private DataSource source;
    Context context;

    String url;
    Config config;
    private volatile Thread sourceReaderThread;
    private volatile boolean stopped;
    private final Object wc = new Object();
    private final Object stopLock = new Object();
    HttpRequest httpRequest;
    Socket socket;


    public RealHttpClient(String url, Config config , Context context) {
        this.url = url;
        this.config = config;
        this.context = context;
    }

    public void processHttpRequest(HttpRequest httpRequest, Socket socket) throws IOException {
        this.httpRequest = httpRequest;
        this.socket = socket;
        startHttpSource();
    }

    private void startHttpSource() throws IOException {
        source = new HttpDataSource(url);
        source.get(httpRequest.rangeOffset);
        OutputStream out = new BufferedOutputStream(socket.getOutputStream());
        String responseHeaders = newResponseHeaders(httpRequest);
        out.write(responseHeaders.getBytes("UTF-8"));
        sourceReaderThread = new Thread(new ClientRunable(source, out));
        sourceReaderThread.start();
//        long offset = httpRequest.rangeOffset;
//        responseWithoutCache(out, offset);
    }

    private void responseWithoutCache(OutputStream out, long offset) throws  IOException {
        HttpDataSource newSourceNoCache = new HttpDataSource(url);
        try {
            newSourceNoCache.open((int) offset);
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int readBytes;
            while ((readBytes = newSourceNoCache.read(buffer)) != -1) {
                out.write(buffer, 0, readBytes);
                offset += readBytes;
            }
            out.flush();
        } catch (Exception e){

        } finally {
            newSourceNoCache.close();
        }
    }


    private class ClientRunable implements Runnable {
        private DataSource dataSource;
        private  OutputStream out;

        public ClientRunable(DataSource dataSource, OutputStream out) {
            this.dataSource = dataSource;
            this.out = out;
        }

        @Override
        public void run() {

            //TODO 拿到请求后真正执行http请求后拿到的流操作未成功
            OutputStream os = null;
            try {
                os =new FileOutputStream(new File(context.getFilesDir().getAbsolutePath() + "4.mp4"));
                byte[] buffer = new byte[Constant.DEFAULT_BUFFER_SIZE];
                int readBytes;
                while (dataSource.connected()&& (readBytes = dataSource.read(buffer)) != -1){
                    Log.d(Constant.TAG, "run() called buffer = " + Arrays.toString(buffer));
                    out.write(buffer,0,readBytes);
                    os.write(buffer,0,readBytes);
                }
                os.close();
                out.close();
                dataSource.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dataSource.close();
            }
        }
    }


    private String newResponseHeaders(HttpRequest request) throws IOException {
        return new StringBuilder()
                .append(request.partial ? "HTTP/1.1 206 PARTIAL CONTENT\n" : "HTTP/1.1 200 OK\n")
                .append("Accept-Ranges: bytes\n")
//                .append(lengthKnown ? format("Content-Length: %d\n", contentLength) : "")
//                .append(addRange ? format("Content-Range: bytes %d-%d/%d\n", request.rangeOffset, length - 1, length) : "")
//                .append(mimeKnown ? format("Content-Type: %s\n", mime) : "")
                .append("\n") // headers end
                .toString();
    }

    private String format(String pattern, Object... args) {
        return String.format(Locale.US, pattern, args);
    }


    private synchronized void readSourceAsync() {
        boolean readingInProgress = sourceReaderThread != null && sourceReaderThread.getState() != Thread.State.TERMINATED;
        if (!stopped && !readingInProgress) {
            sourceReaderThread = new Thread(new SourceReaderRunnable(), "Source reader for ");
            sourceReaderThread.start();
        }
    }


    private class SourceReaderRunnable implements Runnable {

        @Override
        public void run() {
            readSource();
        }
    }

    private boolean isStopped() {
        return Thread.currentThread().isInterrupted() || stopped;
    }

    protected void onCacheAvailable(long cacheAvailable, long sourceLength) {

    }

    private void notifyNewCacheDataAvailable(long cacheAvailable, long sourceAvailable) {
        onCacheAvailable(cacheAvailable, sourceAvailable);

        synchronized (wc) {
            wc.notifyAll();
        }
    }

    private void readSource() {

    }
}
