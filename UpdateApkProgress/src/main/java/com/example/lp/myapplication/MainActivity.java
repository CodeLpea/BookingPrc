package com.example.lp.myapplication;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private DownloadManager mDownloadManager;
    private long mId;
    private Dialog mDialog1;
    private ProgressBar mProgressBar;
    private TextView mPrecent;
    private TextView mComplete;
    private String path=Environment.getExternalStorageDirectory().getPath()+ File.separator + "ddnDetect"+File.separator + "download";
    private myHandler mhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        installApk();
    }
    private void init() {
        mhandler=new myHandler();
       //1、网络获取版本号，跟本地apk版本号比对,如果发现服务器版本号高于本地版本号即弹出对话框，提醒用户更新
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("版本更新")
                .setMessage("发现新的app版本，请及时更新")
                .setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateDownLoad(dialog);//开启更新
                    }
                })
                .create().show();

    }

    private void updateDownLoad(DialogInterface dialog){
        //此处使用DownLoadManager开启下载任务
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        //DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://www.wandoujia.com/apps/com.tencent.mm/binding?source=web_inner_referral_binded"));
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://imagecdn.didano.com/version/branch/DdnDetect.zip"));
        // 下载过程和下载完成后通知栏有通知消息。
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("下载");
        request.setDescription("apk正在下载");
        //设置保存目录  /storage/emulated/0/Android/包名/files/Download
        //  request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS,"jiaogeyi.apk");
        request.setDestinationInExternalPublicDir(path,"ddnDetect.zip");
        mId = mDownloadManager.enqueue(request);

        //注册内容观察者，实时显示进度
        MyContentObserver downloadChangeObserver = new MyContentObserver(null);
        getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, downloadChangeObserver);

        //广播监听下载完成
        //listener(mId);
        //弹出进度条，先隐藏前一个dialog
        dialog.dismiss();
        //显示进度的对话框
        mDialog1 = new Dialog(MainActivity.this, R.style.Theme_AppCompat_Dialog_Alert);
        View view = MainActivity.this.getLayoutInflater().inflate(R.layout.progress_dialogs, null);
        mProgressBar = view.findViewById(R.id.pb);
        mPrecent = view.findViewById(R.id.tv_precent);
        mDialog1.setContentView(view);
        mDialog1.show();

    }

    private void installApk(){
        File file = new File(Environment.getExternalStorageDirectory(), "test.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 仅需改变这一行
        FileProvider7.setIntentDataAndType(this,
                intent, "application/vnd.android.package-archive", file, true);
        startActivity(intent);
    }
    private void listener(final long id) {
        //Toast.makeText(MainActivity.this,"XXXX",Toast.LENGTH_SHORT).show();
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long longExtra = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == longExtra){
//                    Uri downloadUri = mDownloadManager.getUriForDownloadedFile(id);
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    File apkFile = getExternalFilesDir("DownLoad/jiaogeyi.apk");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Uri uriForFile = FileProvider.getUriForFile(context, "com.example.lp.myapplication.fileprovider", apkFile);
                        install.setDataAndType(uriForFile,"application/vnd.android.package-archive");
                    }else {
                        install.setDataAndType(Uri.fromFile(apkFile),"application/vnd.android.package-archive");
                    }
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(install);
                    Toast.makeText(MainActivity.this,"ZZZZ",Toast.LENGTH_SHORT).show();
                }
            }

        };

        registerReceiver(broadcastReceiver,intentFilter);
    }

    class MyContentObserver extends ContentObserver {

        public MyContentObserver(Handler handler) {
            super(handler);
        }


        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public void onChange(boolean selfChange) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(mId);
            DownloadManager dManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            final Cursor cursor = dManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                final int totalColumn = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                final int currentColumn = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                int totalSize = cursor.getInt(totalColumn);
                int currentSize = cursor.getInt(currentColumn);
                float percent = (float) currentSize / (float) totalSize;
                float progress = (float) Math.floor(percent * 100);
                Message message=Message.obtain();
                message.obj=progress;
                message.what=1;
                mhandler.sendMessage(message);
                if (progress == 100)
                    mDialog1.dismiss();
            }
        }

    }

    private  class myHandler extends Handler{
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    float progress= (float) msg.obj;
                    mPrecent.setText(progress+"%");
                    Log.i("progress", String.valueOf(progress));
                    mProgressBar.setProgress((int) progress,true);
                    break;
            }

        }
    }
}
