package com.nfinity.mvvm.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;


public class AppController extends MultiDexApplication  {
    public static AppController mInstance;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sApplication = this;

    }

    private static Application sApplication;

    public static Application getApplication() {
        return sApplication;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }


    public static synchronized AppController getInstance() {
        return mInstance;
    }

    /* For Saving secret token and refresh token according to mechanism
     * @param token
     * @ refreshtoken*/




}