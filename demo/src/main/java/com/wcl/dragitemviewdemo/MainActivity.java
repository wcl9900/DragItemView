package com.wcl.dragitemviewdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_listview).setOnClickListener(this);
        findViewById(R.id.btn_gridview).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_listview){
            startActivity(new Intent(this, ListViewActivity.class));
            return;
        }
        if(v.getId() == R.id.btn_gridview){
            startActivity(new Intent(this, GridViewActivity.class));
            return;
        }
    }
}
