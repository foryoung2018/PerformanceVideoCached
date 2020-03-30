package com.bisu.serverlibrary.net;

import com.bisu.serverlibrary.Constant;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_SEE_OTHER;


//真正的网络请求
public class HttpDataSource implements DataSource {


        private HttpURLConnection connection;
    private String url;
    private InputStream inputStream;
    private boolean connected;

    public HttpDataSource(String url) {
        this.url = url;
    }

    @Override
    public boolean connected() {
        return connected;
    }

    @Override
    public void get(long offset)  {
        boolean redirected;
        int redirectCount = 0;
        try {
            do {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connected = true;
                if (offset > 0) {
                    connection.setRequestProperty("Range", "bytes=" + offset + "-");
                }
                int code = connection.getResponseCode();
                redirected = code == HTTP_MOVED_PERM || code == HTTP_MOVED_TEMP || code == HTTP_SEE_OTHER;
                if (redirected) {
                    url = connection.getHeaderField("Location");
                    redirectCount++;
                    connection.disconnect();
                }
            } while (redirected);
            inputStream = new BufferedInputStream(connection.getInputStream(), Constant.DEFAULT_BUFFER_SIZE);
        } catch (IOException e) {
            connected = false;
            e.printStackTrace();
        }

    }

    @Override
    public int read(byte[] bytes) throws Exception {
        if (inputStream == null) {
            throw new Exception("Error reading data from " + url + ": connection is absent!");
        }
        try {
            return inputStream.read(bytes, 0, bytes.length);
        } catch (InterruptedIOException e) {
            throw new Exception("Reading source " + url + " is interrupted", e);
        } catch (IOException e) {
            throw new Exception("Error reading data from " + url, e);
        }
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.disconnect();
            } catch (NullPointerException | IllegalArgumentException e) {
                String message = "Wait... but why? WTF!? " +
                        "Really shouldn't happen any more after fixing https://github.com/danikula/AndroidVideoCache/issues/43. " +
                        "If you read it on your device log, please, notify me danikula@gmail.com or create issue here " +
                        "https://github.com/danikula/AndroidVideoCache/issues.";
                throw new RuntimeException(message, e);
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public long length() {
        return 0;
    }

    @Override
    public void open(long offset) {

    }
}
