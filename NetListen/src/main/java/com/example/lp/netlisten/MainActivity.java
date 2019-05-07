package com.example.lp.netlisten;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NetEvevt {
    private static final String TAG="MainActivity";
    private static final int NetOk=11111;
    private static final int NetNotOk=11112;
    private NetBroadcastReceiver netBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //注册网络状态监听的广播
        registerBroadcastReceiver();

    }

    /**
     * 注册网络状态广播
     */
    private void registerBroadcastReceiver() {
        //注册广播
        if (netBroadcastReceiver == null) {
            netBroadcastReceiver = new NetBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netBroadcastReceiver, filter);
            //设置监听
            netBroadcastReceiver.setStatusMonitor(this);
        }
    }


    @Override
    public void onNetChange(boolean netStatus) {
        Message message=new Message();
        if (netStatus){
            message.what=NetOk;
            handler.sendMessage(message);
        }else {
            Log.e(TAG, "当前网络不可用: ");
            message.what=NetNotOk;
            handler.sendMessage(message);
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==NetOk){
                Log.i(TAG, "网络正常: ");
                Toast.makeText(MainActivity.this,"网络正常",Toast.LENGTH_LONG).show();
            }else {
                Log.i(TAG, "网络异常: ");
                Toast.makeText(MainActivity.this,"网络异常",Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (netBroadcastReceiver != null) {
            //注销广播
            unregisterReceiver(netBroadcastReceiver);
        }
    }
}
