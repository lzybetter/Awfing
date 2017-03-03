package com.lzybetter.awfing;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by lzybetter on 2017/2/20.
 */

public class Content implements Serializable {

    private String title;
    private String content_url;
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }
}
