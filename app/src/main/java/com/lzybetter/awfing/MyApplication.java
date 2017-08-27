package com.lzybetter.awfing;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v7.app.AlertDialog;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;


/**
 * Created by lzybetter on 2017/2/18.
 */

public class MyApplication extends Application {
    private static Context context;
    private static LruCache<String,Bitmap> mMemoryCaches;


    public static final String AFWING = "http://www.afwing.com";
    public static final String ISIMAGELOAD = "isImageLoad";
    public static final String ISDAYORNIGHT = "isDayorNight";
    public static final String SETTING_NAME = "setting";
    public static final String NOPICSMART = "nopicsmart";

    public static final int NO_CONNECT = 0;//网络连接状态，0代表没有网，1代表流WIFI，2代表流量
    public static final int WIFI_CONNECT = 1;
    public static final int MONET_CONNECT = 2;

    private int maxMemory = (int) Runtime.getRuntime().maxMemory();
    private int cacheSizes = maxMemory/5;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
        initMemoryCaches();
    }

    private void initMemoryCaches() {
        mMemoryCaches = new LruCache<String, Bitmap>(cacheSizes){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public static Context getContext() {
        return context;
    }

    public static LruCache<String, Bitmap> getmMemoryCaches() {
        return mMemoryCaches;
    }

    public static void allExit(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("确认退出？");
        builder.setMessage("请确认退出");
        builder.setCancelable(false);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCollector.finishAll();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

}
