package com.example.diariodemascotas.utils;

import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class AppLogger {
    public static void info(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void warn(String tag, String msg) {
        Log.w(tag, msg);
    }

    public static void error(String tag, String msg, Throwable t) {
        Log.e(tag, msg, t);
        FirebaseCrashlytics.getInstance().log(msg);
        FirebaseCrashlytics.getInstance().recordException(t);
    }
}