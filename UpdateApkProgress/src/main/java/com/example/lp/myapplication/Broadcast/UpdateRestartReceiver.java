package com.example.lp.myapplication.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.lp.myapplication.MainActivity;

import java.io.File;


import static com.example.lp.myapplication.inter.configInterface.DonwLoadPath;

/**
 * 更新后自启动
 * */
public class UpdateRestartReceiver extends BroadcastReceiver {
    private static final String TAG="UpdateRestartReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")){
            //Toast.makeText(context,"已升级到新版本",Toast.LENGTH_SHORT).show();
            Log.i(TAG, "已升级到新版本: ");
            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
            File file=new File(DonwLoadPath);
            removeFiles(file,true);
            Log.i(TAG, "升级完成，已经删除安装包 ");
        }

        if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
            Log.d(TAG, "监听到系统广播,包被添加: ");
            //删除apk文件
        }

        if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
            Log.d(TAG, "监听到系统广播,包被移除: ");
            Log.d(TAG, "onReceive: " + intent.getDataString());
        }

        if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
            Log.d(TAG, "监听到系统广播,包被替换: ");
            Log.d(TAG, "onReceive: "+ intent.getPackage());

        }

    }
/**
 * @param file 文件
 * @param  b   是否连同当前文件夹一起删除
 * */
    private void removeFiles(File file,boolean b) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                removeFiles(f,true);
            }
           if(b){
               file.delete();//如要保留文件夹，只删除文件，请注释这行
           }
        } else if (file.exists()) {
            file.delete();
        }

    }
}
