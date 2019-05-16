package com.example.lp.myapplication.inter;

import android.os.Environment;

import java.io.File;

public interface configInterface {
    public String DonwLoadPath = Environment.getExternalStorageDirectory().getPath()+ File.separator + "updateDownLoadTest"+File.separator + "download";
    public String DonwLoadZipName ="ddn.zip";
    public String DownLoadUri ="http://imagecdn.didano.com/version/branch/DdnDetect.zip";

    interface LOCAL_BROADCAST_ACTION {
        String DOWNLOAD_APP_ACTION = "com.lp.downloadapp";

    }

}
