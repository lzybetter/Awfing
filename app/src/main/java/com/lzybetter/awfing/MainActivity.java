package com.lzybetter.awfing;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends BaseActivity {

    private SliderLayout slider;
    private PagerIndicator indicator;
    private ListView content;
    private DrawerLayout mDrawerLayout;

    private List<ListContent> contentList = new ArrayList<>();
    private ContentAdapter contentAdapter;

    private int totalItemNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences(MyApplication.SETTING_NAME,MODE_PRIVATE);
        boolean isDayorNight = pref.getBoolean(MyApplication.ISDAYORNIGHT,false);
        //true代表夜间，false代表白天
        if(isDayorNight){
            setTheme(R.style.NightTheme);
        }else{
            setTheme(R.style.DayTheme);
        }
        setContentView(R.layout.activity_main);

        getPermission();

        init();

    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.
                permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new
                    String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if (ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.
                permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new
                    String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }


    private void init() {
        slider = (SliderLayout)findViewById(R.id.slider);
        content = (ListView)findViewById(R.id.contentList);

        initDrawerLayout();

        HttpGet.sendHttpRequest(MyApplication.AFWING, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                initSlider(response);
                initListView(response);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void initDrawerLayout() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.mainPageTitle);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = new Intent(MainActivity.this,Choose_List.class);
                String ADDRESS = "address";
                String TITLE = "title";
                switch (item.getItemId()){
                    case R.id.nav_home:
                        break;
                    case R.id.nav_aircraft:
                        intent.putExtra(ADDRESS,"http://www.afwing.com/aircraft/");
                        intent.putExtra(TITLE,"飞机图介");
                        startActivity(intent);
                        break;
                    case R.id.nav_weapons:
                        intent.putExtra(ADDRESS,"http://www.afwing.com/weapon/");
                        intent.putExtra(TITLE,"鹰之利爪");
                        startActivity(intent);
                        break;
                    case R.id.nav_knowledge:
                        intent.putExtra(ADDRESS,"http://www.afwing.com/encyclopaedia/");
                        intent.putExtra(TITLE,"航空百科");
                        startActivity(intent);
                        break;
                    case R.id.nav_history:
                        intent.putExtra(ADDRESS,"http://www.afwing.com/war-history/");
                        intent.putExtra(TITLE,"战史战例");
                        startActivity(intent);
                        break;
                    case R.id.nav_picture:
                        intent.putExtra(ADDRESS,"http://www.afwing.com/pics/");
                        intent.putExtra(TITLE,"名机靓影");
                        startActivity(intent);
                        break;
                    case R.id.nav_setting:
                        Intent setting_Intent = new Intent(MainActivity.this,Setting.class);
                        setting_Intent.putExtra("startActivity","Main");
                        startActivity(setting_Intent);
                        finish();
                        break;
                    case R.id.nav_exit:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                        break;
                    default:
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void initSlider(String response) {
        //这是在子线程里跑的程序
        final String[] address = HttpGet.getSlider(response);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] title = address[0].split("\\|");
                String[] link = address[1].split("\\|");
                String[] imgAddress = address[2].split("\\|");
                for (int i=0;i<title.length;i++){
                    final TextSliderView customerView = new TextSliderView(MainActivity.this);
                    final String title_item = title[i];
                    final String link_item = link[i];
                    String imgAddress_temp = null;
                    SharedPreferences pref = getSharedPreferences(MyApplication.SETTING_NAME,MODE_PRIVATE);
                    boolean isLoadImage = pref.getBoolean(MyApplication.ISIMAGELOAD,true);
                    if(isLoadImage){
                        imgAddress_temp = imgAddress[i];
                    }
                    final String imgAddress_item = imgAddress_temp;
                    customerView.image(imgAddress[i])
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .description(title_item);
                    customerView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
                            browseSlider(imgAddress_item,title_item,link_item);
                        }
                    });
                    slider.addSlider(customerView);
                }
                slider.setCustomAnimation(new DescriptionAnimation());
                slider.setDuration(3500);
            }
        });
    }

    private void browseSlider(String imgAddress_item,
                              String title_item,String link_item) {
        Intent intent = new Intent(MainActivity.this,Content_Show.class);
        intent.putExtra("imgAddress",imgAddress_item);
        intent.putExtra("title",title_item);
        intent.putExtra("link",link_item);
        startActivity(intent);
    }

    private void initListView(final String response) {
        //这段程序是在子线程里跑的
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String[] address = HttpGet.getListContest(response);
                final String[] imgAddress = address[3].split("\\|");
                final String[] title = address[0].split("\\|");
                String[] sub = address[1].split("\\|");
                final String[] link = address[2].split("\\|");
                for(int i=0;i<title.length;i++){
                    ListContent listContent = new ListContent(sub[i],title[i],imgAddress[i],link[i]);
                    contentList.add(listContent);
                }
                totalItemNumber = contentList.size();
                contentAdapter = new ContentAdapter(MainActivity.this,
                        R.layout.list_item, contentList);
                content.setAdapter(contentAdapter);
                content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        browseListView(position);
                    }
                });
            }
        });
    }

    private void browseListView(int position) {
        ListContent listContent = contentAdapter.getItem(position);
        Intent intent = new Intent(MainActivity.this,Content_Show.class);
        intent.putExtra("imgAddress",listContent.getImgSrc());
        intent.putExtra("title",listContent.getTitle());
        intent.putExtra("link",listContent.getLink());
        intent.putExtra("sub",listContent.getSub());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.Btn_Update:
                refreshList();
                break;
            case R.id.Btn_Random:
                if(contentList != null){
                    Random random = new Random();
                    int random_position = random.nextInt(totalItemNumber);
                    browseListView(random_position);
                }
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    private void refreshList(){
        HttpGet.sendHttpRequest(MyApplication.AFWING, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                initSlider(response);
                initListView(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        slider.stopAutoCycle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        slider.startAutoCycle();
    }
}
