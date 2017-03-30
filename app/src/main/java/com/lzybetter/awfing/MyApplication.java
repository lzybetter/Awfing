package com.lzybetter.awfing;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

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

}
