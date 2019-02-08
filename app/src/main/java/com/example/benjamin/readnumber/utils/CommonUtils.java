package com.example.benjamin.readnumber.utils;

import android.os.Environment;
import android.util.Log;

public class CommonUtils {
    public static String TAG = "COMPA";
    public static String APP_PATH = Environment.getExternalStorageDirectory() + "/RecognizeTextOCR/";

    public static void info(Object msg) {
        Log.i(TAG, msg.toString());
    }
}
