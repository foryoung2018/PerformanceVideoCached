package com.bisu.performancevideocached;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;

import androidx.annotation.Nullable;

public class FdService extends Service {


    private IMyAidlInterface.Stub mBinder = new IMyAidlInterface.Stub() {

        @Override
        public void read(ParcelFileDescriptor fd) throws RemoteException {
            FileInputStream fis = new FileInputStream(fd.getFileDescriptor());
            byte[] bytes = new byte[100];
            try {
                fis.read(bytes);
                Log.i(" ",bytes.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


    @Nullable
    @android.support.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
