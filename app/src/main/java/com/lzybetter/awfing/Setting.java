package com.lzybetter.awfing;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class Setting extends BaseActivity {

    private boolean isSaved = true;
    private CheckBox isLoadImage_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = (Toolbar)findViewById(R.id.setting_title);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Button aboutButton = (Button)findViewById(R.id.about_btn);
        aboutButton.setOnClickListener(new CheckBoxListener());

        SharedPreferences pref = getSharedPreferences(MyApplication.SETTING_NAME,MODE_PRIVATE);
        boolean isLoadImage = pref.getBoolean(MyApplication.ISIMAGELOAD,true);

        isLoadImage_check = (CheckBox)findViewById(R.id.isLoadImage_check);
        isLoadImage_check.setChecked(!isLoadImage);
        isLoadImage_check.setOnClickListener(new CheckBoxListener());
    }

    class CheckBoxListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.isLoadImage_check:
                    isSaved = false;
                    break;
                case R.id.about_btn:
                    Intent about = new Intent(Setting.this,About.class);
                    startActivity(about);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_title,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting_save:
                if(!isSaved){
                    SharedPreferences.Editor editor = getSharedPreferences(MyApplication.SETTING_NAME
                            ,MODE_PRIVATE).edit();
                    editor.remove(MyApplication.ISIMAGELOAD);
                    editor.putBoolean(MyApplication.ISIMAGELOAD,!isLoadImage_check.isChecked());
                    editor.apply();
                    Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
                    isSaved = true;
                }
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(!isSaved){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("放弃？");
            builder.setMessage("您的设置尚未保存，是否放弃更改？");
            builder.setPositiveButton("是，放弃", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Setting.super.onBackPressed();
                }
            });
            builder.setNegativeButton("不", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }else{
            Setting.super.onBackPressed();
        }
    }

}
