package com.wcl.dragitemviewdemo;

import android.os.Bundle;

public class ListViewActivity extends AbsListViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        init();
    }
}
