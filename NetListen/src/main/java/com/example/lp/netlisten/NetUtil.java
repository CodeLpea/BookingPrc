package com.example.lp.netlisten;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetUtil {
    private static final String TAG="NetUtil";
    //网络未连接
    private static final boolean NETWORK_NONE = false;
    //移动数据或无线网络连接
    private static final boolean NETWORK_AVAILABLE = true;

    /**
     * 获取当前网络状态
     * @param context 上下文对象
     * @return boolean
     */
    public static boolean getNetStatus(Context context) {
        // 获取系统连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取网络状态信息
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager
                .getActiveNetworkInfo() : null;
        //判断网络NetworkInfo是否不为空且连接
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            Log.i(TAG, "网络连接可用: ");
            //网络连接可用
            return NETWORK_AVAILABLE;

        } else {
            Log.e(TAG, "网络连接不可用: ");
            return NETWORK_NONE;//网络不可用（未连接）
        }

    }
}