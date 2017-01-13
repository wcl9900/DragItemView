package com.wcl.dragitemviewdemo;

import android.os.Bundle;

/**
 * GridView视图
 */
public class GridViewActivity extends AbsListViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        init();
    }
}
