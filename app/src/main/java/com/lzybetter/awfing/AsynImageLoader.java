package com.lzybetter.awfing;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by lzybetter on 2017/2/18.
 */

class AsynImageLoader{



    public AsynImageLoader() {
    }



    class AsynDownLoadTask extends AsyncTask<Void,Void,Bitmap> {

        private ImageView imageView;
        private String mUrl;
        private LruCache<String ,Bitmap> mMemoryCaches;


        public AsynDownLoadTask(ImageView imageView, String mUrl, LruCache<String,Bitmap> mMemoryCaches) {
            this.imageView = imageView;
            this.mUrl = mUrl;
            this.mMemoryCaches = mMemoryCaches;
        }

        @Override
        protected Bitmap doInBackground(Void... strings) {

            Bitmap bitmap = HttpGet.getHttpBitmap(mUrl);
            if(bitmap != null){
                addBitmapToLruCache(mUrl,bitmap,mMemoryCaches);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (imageView.getTag().equals(mUrl)) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public Bitmap getBitmapFromLruCache(String url,LruCache<String,Bitmap> mMemoryCaches){
        Bitmap bitmap = mMemoryCaches.get(url);
        return  bitmap;

    }

    public void addBitmapToLruCache(String url, Bitmap bitmap,LruCache<String,Bitmap> mMemoryCaches){
        if(mMemoryCaches.get(url) == null){
            mMemoryCaches.put(url,bitmap);
        }
    }
}
