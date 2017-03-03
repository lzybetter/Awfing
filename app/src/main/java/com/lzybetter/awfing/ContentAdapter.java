package com.lzybetter.awfing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzybetter on 2017/2/17.
 */

public class ContentAdapter extends ArrayAdapter<ListContent> {

    private  ViewHolder viewHolder;
    private ListContent listContent;
    private Context context;


    private int resourceId;



    public ContentAdapter(Context context, int resource, List<ListContent> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resourceId = resource;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        listContent = getItem(position);
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.list_img = (ImageView)view.findViewById(R.id.list_image);
            viewHolder.list_title = (TextView)view.findViewById(R.id.list_title);
            viewHolder.list_sub = (TextView)view.findViewById(R.id.list_sub);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.list_img.setTag(listContent.getImgSrc());
            AsynImageLoader asynImageLoader = new AsynImageLoader();
            final Bitmap bitmap = asynImageLoader.getBitmapFromLruCache(listContent.getImgSrc(),
                    MyApplication.getmMemoryCaches());
            if(bitmap != null){
                viewHolder.list_img.setImageBitmap(bitmap);
            }else{
                viewHolder.list_img.setImageResource(R.drawable.ic_loading);
                SharedPreferences pref = MyApplication.getContext().getSharedPreferences(MyApplication.SETTING_NAME
                        ,Context.MODE_PRIVATE);
                boolean isLoadImage = pref.getBoolean(MyApplication.ISIMAGELOAD,true);
                if(isLoadImage){
                    AsynImageLoader.AsynDownLoadTask asynDownLoadTask = asynImageLoader.new AsynDownLoadTask(viewHolder.list_img,
                            listContent.getImgSrc(), MyApplication.getmMemoryCaches());
                    asynDownLoadTask.execute();
                }
            }

        viewHolder.list_title.setText(listContent.getTitle());
        viewHolder.list_sub.setText(listContent.getSub());

        return view;
    }

    class ViewHolder{
        ImageView list_img;
        TextView list_title;
        TextView list_sub;
    }

}
