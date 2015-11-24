package com.example.loader.loaderdemo;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by kyle.jablonski on 11/24/15.
 */
public class LoaderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        Stetho.initializeWithDefaults(this);
    }
}
