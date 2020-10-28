// IMyAidlInterface.aidl
package com.bisu.performancevideocached;
import android.os.ParcelFileDescriptor;

// Declare any non-default types here with import statements

interface IMyAidlInterface {

    void read(in ParcelFileDescriptor  fd);
}
