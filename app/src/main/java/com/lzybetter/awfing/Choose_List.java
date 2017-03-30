package com.lzybetter.awfing;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
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


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Choose_List extends BaseActivity {

    private ListView choose_list;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String address_page,title_page ;
    private int totalItemNumber = 0;
    private boolean isLastRaw = false;
    private String[] listNumberandLink;

    private List<ListContent> list = new ArrayList<>();
    private ContentAdapter chooseAdapter;

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
        setContentView(R.layout.choose__list);

        Intent intent = getIntent();
        address_page = intent.getStringExtra("address");
        title_page = intent.getStringExtra("title");

        toolbar = (Toolbar)findViewById(R.id.choose_Toolbar);
        toolbar.setTitle(title_page);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.choose_list_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.
                OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        choose_list = (ListView)findViewById(R.id.choose_list);
        choose_list.setOnScrollListener(new ChooseListScroll());
        choose_list.setOnTouchListener(new ChooseListTouch());
        mDrawerLayout = (DrawerLayout)findViewById(R.id.choose_drawerLayout);
        NavigationView navigationView = (NavigationView)findViewById(R.id.choose_nav);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String address = null;
                switch (item.getItemId()){
                    case R.id.nav_home:
                        Intent intent = new Intent(Choose_List.this,MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_aircraft:
                        address = "http://www.afwing.com/aircraft/";
                        if(!address.equals(address_page)){
                            init_ListView(address);
                            address_page = address;
                            title_page = "飞机图介";
                            toolbar.setTitle(title_page);
                            setSupportActionBar(toolbar);
                        }
                        break;
                    case R.id.nav_weapons:
                        address = "http://www.afwing.com/weapon/";
                        if(!address.equals(address_page)){
                            init_ListView(address);
                            address_page = address;
                            title_page = "鹰之利爪";
                            toolbar.setTitle(title_page);
                            setSupportActionBar(toolbar);
                        }
                        break;
                    case R.id.nav_knowledge:
                        address = "http://www.afwing.com/encyclopaedia/";
                        if(!address.equals(address_page)){
                            init_ListView(address);
                            address_page = address;
                            title_page = "航空百科";
                            toolbar.setTitle(title_page);
                            setSupportActionBar(toolbar);
                        }
                        break;
                    case R.id.nav_history:
                        address = "http://www.afwing.com/war-history/";
                        if(!address.equals(address_page)){
                            init_ListView(address);
                            address_page = address;
                            title_page = "战史战例";
                            toolbar.setTitle(title_page);
                            setSupportActionBar(toolbar);
                        }
                        break;
                    case R.id.nav_picture:
                        address = "http://www.afwing.com/pics/";
                        if(!address.equals(address_page)){
                            init_ListView(address);
                            address_page = address;
                            title_page = "名机靓影";
                            toolbar.setTitle(title_page);
                            setSupportActionBar(toolbar);
                        }
                        break;
                    case R.id.nav_setting:
                        Intent setting_Intent = new Intent(Choose_List.this,Setting.class);
                        setting_Intent.putExtra("startActivity","List");
                        setting_Intent.putExtra("address",address_page);
                        setting_Intent.putExtra("title",title_page);
                        startActivity(setting_Intent);
                        finish();
                        break;
                    case R.id.nav_exit:
                        AlertDialog.Builder builder = new AlertDialog.Builder(Choose_List.this);
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

        init_ListView(address_page);
    }

    private void init_ListView(String address_page) {
        HttpGet.sendHttpRequest(address_page, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final String[] address = HttpGet.getChoose_List(response);
                        final String[] imgAddress = address[3].split("\\|");
                        final String[] title = address[0].split("\\|");
                        String[] sub = address[1].split("\\|");
                        final String[] link = address[2].split("\\|");
                        if(list!=null){
                            list.clear();
                        }
                        for(int i=0;i<title.length;i++){
                            ListContent listContent = new ListContent(sub[i],title[i],imgAddress[i],link[i]);
                            list.add(listContent);
                        }
                        totalItemNumber = list.size();
                        chooseAdapter = new ContentAdapter(Choose_List.this,
                                R.layout.list_item, list);

                        choose_list.setAdapter(chooseAdapter);
                        choose_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                browseListView(position);
                            }
                        });
                    }
                });
                listNumberandLink = HttpGet.getListNumberAndLink(response);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void browseListView(int position) {
        ListContent listContent = chooseAdapter.getItem(position);
        Intent intent = new Intent(Choose_List.this,Content_Show.class);
        intent.putExtra("imgAddress",listContent.getImgSrc());
        intent.putExtra("title",listContent.getTitle());
        intent.putExtra("link",listContent.getLink());
        intent.putExtra("sub",listContent.getSub());
        startActivity(intent);
    }

    class ChooseListScroll implements AbsListView.OnScrollListener{

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(firstVisibleItem+visibleItemCount == totalItemCount){
                isLastRaw = true;
            }
        }
    }

    class ChooseListTouch implements View.OnTouchListener{

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float yDown = 0;
            float yMove = 0;
            int YDISTANCE_MAX = 400;
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    yDown = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    yMove = event.getRawY();
                    int yDistance = (int)(yMove - yDown);
                    if(isLastRaw && yDistance>YDISTANCE_MAX){
                        changeNextPage();
                    }
            }
            return false;
        }
    }

    private void changeNextPage(){
        String currentPageNumber = listNumberandLink[0];
        String totalPageNumber = listNumberandLink[1];
        String nextPageLink = listNumberandLink[2];

        if(!currentPageNumber.equals(totalPageNumber)){
            HttpGet.sendHttpRequest(nextPageLink, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    String[] address = HttpGet.getChoose_List(response);
                    final String[] imgAddress = address[3].split("\\|");
                    final String[] title = address[0].split("\\|");
                    final String[] sub = address[1].split("\\|");
                    final String[] link = address[2].split("\\|");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i=0;i<title.length;i++){
                                ListContent listContent = new ListContent(sub[i],title[i],imgAddress[i],link[i]);
                                list.add(listContent);
                            }
                            totalItemNumber = list.size();
                            chooseAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }

    }

    private void refresh(){
        init_ListView(address_page);
        Toast.makeText(Choose_List.this,"刷新成功",Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
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
                refresh();
                break;
            case R.id.Btn_Random:
                if(choose_list != null){
                    Random random = new Random();
                    int position = random.nextInt(totalItemNumber);
                    browseListView(position);
                }
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
            default:
                break;
        }
        return true;
    }
}
