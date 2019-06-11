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
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lp.myapplication.Service.downLoadService;
import com.example.lp.myapplication.util.ZipUtil;
import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;

import static com.example.lp.myapplication.inter.configInterface.DonwLoadPath;
import static com.example.lp.myapplication.inter.configInterface.DonwLoadZipName;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";

    private DownloadManager mDownloadManager;
    private long mId;
    private Dialog mDialog1;
    private ProgressBar mProgressBar;
    private ProgressBar progesss;
    private TextView progesssValue;
    private TextView mPrecent;
    private TextView mComplete;
    private Button btn_backDown;

    private myHandler mhandler;
    private BroadcastReceiver downLoadastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
       // initCurrentDownLoad();//前台下载
        updateServiceDownLoad();//后台下载
        initBroadcat();
    }

    private void initView() {
        progesss = (ProgressBar) findViewById(R.id.progesss1);
        progesssValue = (TextView) findViewById(R.id.progesss_value1);
        btn_backDown=findViewById(R.id.btn_banckDown);
        btn_backDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: ");
                updateServiceDownLoad();//后台下载
            }
        });
    }

    private void initBroadcat() {
        downLoadastReceiver  =new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status=intent.getStringExtra("status");
                String detail=intent.getStringExtra("detail");
                Log.i(TAG, "status: "+status);
                Log.i(TAG, "detail: "+detail);
                if(status.equals("progress")){
                    int cuureentProgess= Integer.parseInt(detail);
                    progesss.setProgress(cuureentProgess);
                    progesssValue.setText(new StringBuffer().append(progesss.getProgress()).append("%"));
                }


            }
        };
        IntentFilter intentFilter = new IntentFilter("com.lp.downloadapp");
        LocalBroadcastManager.getInstance(MyApplication.getInstance()).registerReceiver(downLoadastReceiver,intentFilter);
    }

    private void initCurrentDownLoad() {
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
                        //updateDownLoad(dialog);//开启更新


                    }
                })
                .create().show();

    }

    /**
     * 下载的后台方法
     * */
    private void updateServiceDownLoad(){
        Log.i(TAG, "updateServiceDownLoad: ");
        Intent intentdownLoad = new Intent(this, downLoadService.class);
        MyApplication.getInstance().startService(intentdownLoad);
    }

    private void updateDownLoad(DialogInterface dialog){

        //此处使用DownLoadManager开启下载任务
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        //DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://www.wandoujia.com/apps/com.tencent.mm/binding?source=web_inner_referral_binded"));
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://imagecdn.didano.com/version/branch/DdnDetect.zip"));
        // 下载过程和下载完成后通知栏有通知消息。
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("下载");
        request.setDescription("正在下载");
        //设置保存目录  /storage/emulated/0/Android/包名/files/Download
        //  request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS,"jiaogeyi.apk");
       // request.setDestinationInExternalPublicDir(DonwLoadPath,"ddnDetect.zip");

         File file = new File(DonwLoadPath, DonwLoadZipName);
        //这个Uri不用使用FileProvider
         Uri fileUri1= Uri.fromFile(file);
         request.setDestinationUri(fileUri1);
         mId = mDownloadManager.enqueue(request);
        //注册内容观察者，实时显示进度
        MyContentObserver downloadChangeObserver = new MyContentObserver(null);
        getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, downloadChangeObserver);

        //广播监听下载完成
         listener(mId);
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


    private void listener(final long id) {
        //Toast.makeText(MainActivity.this,"XXXX",Toast.LENGTH_SHORT).show();
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long longExtra = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == longExtra){
                    File srcZipFile = new File(DonwLoadPath, DonwLoadZipName);
                   boolean result= ZipUtil.unzip(srcZipFile, DonwLoadPath);
                    Log.i(TAG, "result: "+result);
                    if(result){
                        File apkFile=new File(DonwLoadPath,"test.apk");
                        installApk(apkFile);
                    }
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

    private void installApk(File file){
        //File file = new File(Environment.getExternalStorageDirectory(), "test.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 仅需改变这一行
        FileProvider7.setIntentDataAndType(this,
                intent, "application/vnd.android.package-archive", file, true);
        startActivity(intent);
       // sendInstallOk();
    }
    private void sendInstallOk() {
        Log.i(TAG, "发送安装成功广播: ");
        //静态注册自启动广播
        Intent intents=new Intent();
        //与清单文件的receiver的anction对应
        intents.setAction("android.intent.action.PACKAGE_REPLACED");
        //发送广播
        MyApplication.getInstance().sendBroadcast(intents);
    }

}
