package com.lzybetter.awfing;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lzybetter on 2017/2/13.
 */

public class HttpGet {


    public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
        //发送http申请，读取网页源文件
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = reader.readLine())!=null){
                        response.append(line);
                    }
                    if(listener!=null){
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    listener.onError(e);
                }finally {
                    connection.disconnect();
                }
            }
        }).start();
    }


    public static String[] getSlider(String response){
        //获取轮播图的标题、图片与连接地址
        StringBuilder titleBuilder = new StringBuilder();
        StringBuilder linkBuilder = new StringBuilder();
        StringBuilder imgSrcBuilder = new StringBuilder();
        Document doc = Jsoup.parse(response);
        Elements elements = doc.select("div.picshow_img");
        Elements titlesAndLinks = elements.get(0).select("a");
        Elements imgs = elements.get(0).select("img");
        for(int j=0;j<titlesAndLinks.size();j++){
            titleBuilder.append(titlesAndLinks.get(j).attr("alt")).append("|");
            linkBuilder.append(MyApplication.AFWING).append(titlesAndLinks.get(j).attr("href")).append("|");
            imgSrcBuilder.append(MyApplication.AFWING).append(imgs.get(j).attr("src")).append("|");
        }
        String[] address = new String[3];
        address[0] = titleBuilder.toString();
        address[1] = linkBuilder.toString();
        address[2] = imgSrcBuilder.toString();
        return address;
    }

    public static String[] getListContest(String respose){
        StringBuilder titleBuilder = new StringBuilder();
        StringBuilder linkBuilder = new StringBuilder();
        StringBuilder imgSrcBuilder = new StringBuilder();
        StringBuilder subBuilder = new StringBuilder();
        Document doc = Jsoup.parse(respose);
        Elements elements = doc.select("div.content3_txt");
        Elements imgElement = doc.select("div.content3_pic");
        for (int j=5;j<elements.size();j++){
            Elements titles = elements.get(j).select("a.title");
            Elements subs = elements.get(j).select("p");
            Elements links = elements.get(j).select("a.title");
            Elements imgs = imgElement.get(j).select("img");
            titleBuilder.append(titles.text()).append("|");
            subBuilder.append(subs.text()).append("|");
            linkBuilder.append(MyApplication.AFWING).append(links.attr("href")).append("|");
            imgSrcBuilder.append(MyApplication.AFWING).append(imgs.attr("src")).append("|");
        }
        String[] address = new String[4];
        address[0] = titleBuilder.toString();
        address[1] = subBuilder.toString();
        address[2] = linkBuilder.toString();
        address[3] = imgSrcBuilder.toString();
        return address;
    }



    public static String[] getChoose_List(String response){
        StringBuilder titleBuilder = new StringBuilder();
        StringBuilder linkBuilder = new StringBuilder();
        StringBuilder imgSrcBuilder = new StringBuilder();
        StringBuilder subBuilder = new StringBuilder();
        Document doc = Jsoup.parse(response);
        Elements elements = doc.select("div.content").select("li");
        Elements number = doc.select("div.content_txt");
        for(int i=0;i<number.size();i++){
            titleBuilder.append(elements.get(i).select("a").get(1).text()).append("|");
            linkBuilder.append(MyApplication.AFWING).append(elements.get(i).select("a").get(0).attr("href")).append("|");
            imgSrcBuilder.append(MyApplication.AFWING).append(elements.get(i).select("img").get(0).attr("src")).append("|");
            subBuilder.append(elements.get(i).select("p").text()).append("|");
        }
        String[] address = new String[4];
        address[0] = titleBuilder.toString();
        address[1] = subBuilder.toString();
        address[2] = linkBuilder.toString();
        address[3] = imgSrcBuilder.toString();
        return address;
    }

    public static Bitmap getHttpBitmap(final String imgAddress){
        HttpURLConnection connection = null;
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            URL url = new URL(imgAddress);
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setUseCaches(false);
            is = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(connection != null){
                connection.disconnect();
            }
        }
        return bitmap;
    }

    public static String getContent_Show(String response){
        Document doc = Jsoup.parse(response);
        Elements elements = doc.select("div.article");
        Elements contents = elements.get(0).select("p");
        StringBuilder content = new StringBuilder();
        for(int i=0;i<contents.size();i++){
            content.append(contents.get(i).toString());
        }
        return content.toString();
    }

    public static String[] getPageNumber(String response){
        Document doc = Jsoup.parse(response);
        Elements elements = doc.select("div.pages");
        String currentPageNumber = elements.get(0).select("strong").text();
        String totalPageNumber = elements.get(0).select("span").attr("title")
                .replace("共 ","").replace(" 页","");
        String[] pageNumber = new String[2];
        if(currentPageNumber.length()==0&&totalPageNumber.length()==0){
            currentPageNumber = "1";
            totalPageNumber = "1";
        }
        pageNumber[0] = currentPageNumber;
        pageNumber[1] = totalPageNumber;
        return pageNumber;
    }

    public static String[] getPageLink(String response,String[] pageNumber){
        String currentPageNumber = pageNumber[0];
        String totalPageNumber = pageNumber[1];
        Document doc = Jsoup.parse(response);
        Elements elements = doc.select("div.pages");
        Elements page = elements.get(0).select("a");
        String nextPageLink = null;
        String beforePageLink = null;
        if(!totalPageNumber.equals("1")){
            if(currentPageNumber.equals("1")){
                nextPageLink = MyApplication.AFWING + page.get(page.size()-3).attr("href");
                beforePageLink = null;
            }else if(currentPageNumber.equals(totalPageNumber)){
                nextPageLink = null;
                beforePageLink = MyApplication.AFWING + page.get(1).attr("href");
            }else{
                nextPageLink = MyApplication.AFWING + page.get(page.size()-3).attr("href");
                beforePageLink = MyApplication.AFWING + page.get(1).attr("href");
            }
        }else{
            nextPageLink = null;
            beforePageLink = null;
        }
        String[] pageLink = new String[2];
        pageLink[0] = nextPageLink;
        pageLink[1] = beforePageLink;
        return pageLink;
    }

    public static String[] getListNumberAndLink(String response){
        Document doc = Jsoup.parse(response);
        Elements elements = doc.select("li.pages");
        String currentPageNumber = elements.get(0).select("strong").text();
        String totalPageNumber = elements.get(0).select("span").attr("title")
                .replace("共 ","").replace(" 页","");
        if(currentPageNumber.length()==0&&totalPageNumber.length()==0){
            currentPageNumber = "1";
            totalPageNumber = "1";
        }
        Elements page = elements.get(0).select("a");
        String nextPageLink = null;
        if(!totalPageNumber.equals("1")){
            if(currentPageNumber.equals("1")){
                nextPageLink = MyApplication.AFWING + page.get(page.size()-2).attr("href");
            }else if(currentPageNumber.equals(totalPageNumber)){
                nextPageLink = null;
            }else{
                nextPageLink = MyApplication.AFWING + page.get(page.size()-2).attr("href");
            }
        }else{
            nextPageLink = null;
        }
        String[] listNumberandLink = new String[3];
        listNumberandLink[0] = currentPageNumber;
        listNumberandLink[1] = totalPageNumber;
        listNumberandLink[2] = nextPageLink;
        return listNumberandLink;
    }

    public class URLDrawable extends BitmapDrawable{
        Bitmap bitmap;

        @Override
        public void draw(Canvas canvas) {
            if(bitmap != null){
                canvas.drawBitmap(bitmap,0,0,getPaint());
            }
        }
    }

    public class URLImageParser implements Html.ImageGetter{

        TextView mTextView;
        int mWidth;

        public URLImageParser(TextView textView,int width) {
            mTextView = textView;
            mWidth = width;
        }

        @Override
        public Drawable getDrawable(String source) {

            final URLDrawable urlDrawable = new URLDrawable();

            ImageLoader.getInstance().loadImage(source,new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    SharedPreferences pref = MyApplication.getContext().getSharedPreferences
                            (MyApplication.SETTING_NAME, Context.MODE_PRIVATE);
                    boolean isLoadImage = pref.getBoolean(MyApplication.ISIMAGELOAD,true);

                    int reqHeight = 0;

                    if(isLoadImage){
                        reqHeight = mWidth*loadedImage.getHeight()/loadedImage.getWidth();
                    };

                    if(!(mWidth == 0 || reqHeight == 0)){
                        urlDrawable.bitmap =
                                BitmapUtils.decodeSampledBitmap(loadedImage,mWidth,reqHeight);
                        urlDrawable.setBounds(0,0,mWidth,reqHeight);
                        mTextView.invalidate();
                        mTextView.setText(mTextView.getText());
                    }
                }
            });
            return urlDrawable;
        }
    }

}
