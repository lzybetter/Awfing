package com.lzybetter.awfing;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by lzy17 on 2017/8/27.
 */

public class NetState {

    public static int getNetState(){
        int netState = 0;
        ConnectivityManager manager = (ConnectivityManager)
                MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if(networkInfo == null){
            return netState;
        }

        switch (networkInfo.getType()){
            case ConnectivityManager.TYPE_WIFI:
                netState = 1;
                break;
            default:
                netState = 2;
                break;
        }

        return netState;
    }
}
