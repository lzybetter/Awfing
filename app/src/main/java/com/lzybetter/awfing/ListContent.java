package com.lzybetter.awfing;

import android.graphics.Bitmap;

/**
 * Created by lzybetter on 2017/2/16.
 */

public class ListContent {

    private String title;
    private String sub;
    private String imgSrc;
    private String link;

    public ListContent( String sub, String title,String imgSrc,String link) {
        this.sub = sub;
        this.title = title;
        this.imgSrc = imgSrc;
        this.link = link;
    }

    public String getSub() {
        return sub;
    }

    public String getTitle() {
        return title;
    }

    public String getImgSrc(){return imgSrc;}

    public String getLink() {
        return link;
    }
}
