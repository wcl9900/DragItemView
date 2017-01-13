package com.wcl.dragitemviewdemo;

import android.os.Bundle;

/**
 * ListView视图
 */
public class ListViewActivity extends AbsListViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        init();
    }
}
