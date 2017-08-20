package com.lzybetter.awfing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

import java.io.ByteArrayOutputStream;

import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;

public class Content_Show extends BaseActivity {

    private CollapsingToolbarLayout collapsingLayout;
    private ImageView title_img;
    private TextView title_show,content_show;
    private NestedScrollView nestedScrollView;

    private String title;
    private String[] pageLink;
    private String[] pageNumber;

    private static final int XDISTANCE_MIN = 400;
    private static final int YDISTANCE_MAX = 300;
    private static final int XSPEED_MIN = 200;

    private boolean completed = false;

    private float xDown;
    private float yDown;
    private float xMove;
    private float yMove;
    private VelocityTracker mVelocityTracker;

    private SharedPreferences pref = MyApplication.getContext().getSharedPreferences(MyApplication.SETTING_NAME,
            MODE_PRIVATE);
    private boolean isLoadImage = pref.getBoolean(MyApplication.ISIMAGELOAD,true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences(MyApplication.SETTING_NAME,MODE_PRIVATE);
        boolean isDayorNight = pref.getBoolean(MyApplication.ISDAYORNIGHT,false);
        //true代表夜间，false代表白天
        if(isDayorNight){
            setTheme(R.style.ContentShowNight);
        }else{
            setTheme(R.style.ContentShowDay);
        }
        setContentView(R.layout.content__show);

        Toolbar toolbar = (Toolbar)findViewById(R.id.showPageTitle);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingLayout = (CollapsingToolbarLayout)
                findViewById(R.id.collapsing_toolbar);
        title_img = (ImageView)findViewById(R.id.title_pic);
        title_show = (TextView)findViewById(R.id.title_show);
        content_show = (TextView)findViewById(R.id.content_show);
        content_show.setMovementMethod(ScrollingMovementMethod.getInstance());
        content_show.setOnTouchListener(new TouchListener());

        LinearLayout lineaLayout = (LinearLayout)findViewById(R.id.content_linearLayout);
        lineaLayout.setOnTouchListener(new TouchListener());
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout)findViewById(R.id.content_coordinatorLayout);
        coordinatorLayout.setOnTouchListener(new TouchListener());
        nestedScrollView = (NestedScrollView)findViewById(R.id.content_nestedScrollView);
        nestedScrollView.setOnTouchListener(new TouchListener());

        init();

    }

    private void init() {

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        String link = intent.getStringExtra("link");
        String imgAddress = intent.getStringExtra("imgAddress");

        if(isLoadImage){
            AsynImageLoader asynImageLoader = new AsynImageLoader();
            title_img.setTag(imgAddress);
            AsynImageLoader.AsynDownLoadTask asynDownLoadTask = asynImageLoader.new AsynDownLoadTask(title_img,
                    imgAddress, MyApplication.getmMemoryCaches());
            asynDownLoadTask.execute();
        }else{
            title_img.setImageResource(R.drawable.ic_loading);
        }

        collapsingLayout.setTitle(title);
        display(link);
    }

    private void display(String address){
        HttpGet.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title_Display(response);
                        pageLink = HttpGet.getPageLink(response,pageNumber);
                        content_Display(response);
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void content_Display(final String response){

        int width = content_show.getMeasuredWidth();
        HttpGet httpGet = new HttpGet();
        String content_address = httpGet.getContent_Show(response);
        content_show.setText(Html.fromHtml(content_address, httpGet.new URLImageParser(content_show, width), null), null);
        nestedScrollView.scrollTo(10,10);
        completed = true;
    }

    private void title_Display(final String response){

        pageNumber = HttpGet.getPageNumber(response);
        String currentPageNumber = pageNumber[0];
        String titlePageNumber = pageNumber[1];
        String title = this.title + "(" + currentPageNumber + "/"
                + titlePageNumber + ")";
        title_show.setText(title);
    }

    class TouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            createVelocityTracker(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    xDown = event.getRawX();
                    yDown = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    xMove = event.getRawX();
                    yMove = event.getRawY();
                    int distanceX = (int) (xMove - xDown);
                    int distanceY = (int) (yMove - yDown);
                    int xSpeed = getScrollVelocity();
                    //左滑加载下一页
                    if (completed) {
                        if (distanceX < 0) {
                            if (Math.abs(distanceY) < YDISTANCE_MAX &&Math.abs(distanceX) > XDISTANCE_MIN && xSpeed > XSPEED_MIN) {
                                changeNextPage();
                            }
                        } else {
                            //右滑返回上一页
                            if (Math.abs(distanceY) < YDISTANCE_MAX && distanceX > XDISTANCE_MIN && xSpeed > XSPEED_MIN) {
                                changeBeforePage();
                            }
                        }
                    }
                    recycleVelocityTracker();
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    private void changeNextPage() {

        completed = false;
        String currentPageNumber = pageNumber[0];
        String totalPageNumber = pageNumber[1];
        if(!currentPageNumber.equals(totalPageNumber)){
            String address = pageLink[0];
            display(address);
        }else{
            Toast.makeText(this,"已经是最后一页了",Toast.LENGTH_SHORT).show();
            completed = true;
        }

    }

    private void changeBeforePage() {
        completed = false;
        String currentPageNumber = pageNumber[0];
        if(!currentPageNumber.equals("1")){
            String address= pageLink[1];
            display(address);
        }else {
            Toast.makeText(this,"已经是第一页了",Toast.LENGTH_SHORT).show();
            completed = true;
        }

    }

    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * 获取手指在content界面滑动的速度。
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    @Override
    public void onBackPressed() {
        String currentPageNumber = pageNumber[0];
        back_btn(currentPageNumber);
    }

    private void back_btn(String currentPageNumber){
        if(currentPageNumber.equals("1")){
            super.onBackPressed();
        }else{
            changeBeforePage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.content_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
//            case R.id.content_all:
//
//                break;
            case R.id.content_share:
                Intent intent = getIntent();
                String title = intent.getStringExtra("title");
                String link = intent.getStringExtra("link");
                String sub = intent.getStringExtra("sub");
                String imgAddress = intent.getStringExtra("imgAddress");
                Intent share_intent = new Intent(Content_Show.this,Share.class);
                share_intent.putExtra("title",title);
                share_intent.putExtra("link",link);
                share_intent.putExtra("sub",sub);
                share_intent.putExtra("imgAddress",imgAddress);
                startActivity(share_intent);
                break;
            case android.R.id.home:
                back_btn("1");
            default:
                break;
        }
        return true;
    }


}
