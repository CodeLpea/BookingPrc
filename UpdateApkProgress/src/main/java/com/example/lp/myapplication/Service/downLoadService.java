package com.example.lp.myapplication.Service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.lp.myapplication.MyApplication;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.lp.myapplication.inter.configInterface.DonwLoadPath;
import static com.example.lp.myapplication.inter.configInterface.DonwLoadZipName;
import static com.example.lp.myapplication.inter.configInterface.DownLoadUri;
import static com.example.lp.myapplication.inter.configInterface.LOCAL_BROADCAST_ACTION.DOWNLOAD_APP_ACTION;

public class downLoadService extends IntentService {
    private static final String TAG="downLoadService";

    private Lock queueLock = new ReentrantLock();
    private int queueLen = 0;


    public downLoadService() {
        super(downLoadService.class.getSimpleName());
    }
    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        /**
         * intentservice将消息放入消息队列，如果队列长度过长，那么抛弃后续的消息
         * 避免队列过长
         */
        Log.i(TAG, "onStart: ");
        if (queueLen < 5) {
            super.onStart(intent, startId);
            addMessageLen();
        } else{

        }
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        StartDownLoad();
        reduceMessageLen();
    }

    private void StartDownLoad() {
        Log.i(TAG, "StartDownLoad: ");
        FileDownloader.getImpl().create(DownLoadUri).setWifiRequired(true).setPath(DonwLoadPath+ File.separator+DonwLoadZipName).setListener(new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                /**
                 * 网络状态差，可能经常重复尝试，这里会调用到
                 */
                notifyUpgradeStatus("pending", " 网络状态差");
                Log.i(TAG, "pending: ");

            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                int percent=(int) ((double) soFarBytes / (double) totalBytes * 100);
                //Log.i(TAG, "progress: "+percent);
                notifyUpgradeStatus("progress", String.valueOf(percent));
                //textView.setText("("+percent+"%"+")");
            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {
                Log.i(TAG, "blockComplete: ");

            }

            @Override
            protected void completed(BaseDownloadTask task) {
                Log.i(TAG, "completed: ");
              //  textView.setText("("+"100%"+")");
                notifyUpgradeStatus("completed", "完成");
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                /**
                 * 下载发生错误，重新下载
                 */
            }

            @Override
            protected void warn(BaseDownloadTask task) {
                continueDownLoad(task);//如果存在了相同的任务，那么就继续下载
            }
        }).start();
    }

    private void continueDownLoad(BaseDownloadTask task) {
        while (task.getSmallFileSoFarBytes()!=task.getSmallFileTotalBytes()){
            int percent=(int) ((double) task.getSmallFileSoFarBytes() / (double) task.getSmallFileTotalBytes() * 100);
            notifyUpgradeStatus("continueDownLoad", String.valueOf(percent));
           // textView.setText("("+percent+"%"+")");
        }
    }

    /**
     * 增加消息队列长度
     */
    public void addMessageLen() {
        Log.i(TAG, "addMessageLen: ");
        queueLock.lock();
        queueLen++;
        queueLock.unlock();
    }

    /**
     * 减少消息队列长度
     */
    public void reduceMessageLen() {
        Log.i(TAG, "reduceMessageLen: ");
        queueLock.lock();
        queueLen--;
        queueLock.unlock();
    }


    public void notifyUpgradeStatus(String status, String detail) {
        Intent intent = new Intent(DOWNLOAD_APP_ACTION);
        intent.putExtra("status", status);
        intent.putExtra("detail", detail);
        LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);
    }
}
