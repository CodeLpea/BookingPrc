package com.example.lp.myapplication;

import android.app.Application;

import com.liulishuo.filedownloader.FileDownloader;

public class MyApplication extends Application {
    private static MyApplication myApplication = null;
    public static MyApplication getInstance() {
        return myApplication;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication=this;
        FileDownloader.setup(this);//注意作者已经不建议使用init方法

    }
}
