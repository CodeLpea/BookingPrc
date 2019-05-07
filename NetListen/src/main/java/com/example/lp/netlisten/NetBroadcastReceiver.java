package com.example.lp.netlisten;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class NetBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG="NetBroadcastReceiver";
    public NetEvevt evevt ;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive: ");
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            boolean netWorkState = NetUtil.getNetStatus(context);
            // 接口回调传过去状态的类型
            evevt.onNetChange(netWorkState);
        }
    }
    /**
     * 设置网络状态监听接口
     */
    public void setStatusMonitor(NetEvevt netEvevt) {
        this.evevt = netEvevt;
        Log.e(TAG, "setStatusMonitor: ");
    }

}
