package com.wcl.dragitemviewdemo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AbsListViewActivity extends Activity {

    TextView tvInfo;
    AbsListView absListView;
    private DragItemViewWrapper dragItemViewWrapper;

    protected void init(){
        tvInfo = (TextView) findViewById(R.id.tv_info);
        absListView = (AbsListView) findViewById(R.id.abslistview);

        List<Integer> data = new ArrayList<Integer>();
        for (int i = 0; i < 30; i ++){
            data.add(R.drawable.image);
        }
        absListView.setAdapter(new ListViewAdapter(data));


        dragItemViewWrapper = new DragItemViewWrapper(absListView);
        dragItemViewWrapper.setOnDragListener(dragItemListener);

        //可通过此接口设定需要产生拖拽镜像的视图，不设定此接口时默认会遍历选择ImageView视图
        dragItemViewWrapper.setOnImageViewGetListener(new DragItemViewWrapper.onImageViewGetListener() {
            @Override
            public View getImageView(View itemView) {
                return itemView.findViewById(R.id.itemview);
            }
        });
    }

    private DragItemViewWrapper.OnDragItemListener dragItemListener = new DragItemViewWrapper.OnDragItemListener() {
        @Override
        public void onDragItemStart(AbsListView absListView, View itemView, int position, MotionEvent event) {
            tvInfo.setText("开始拖动第" + position +"个item"+" x:"+event.getRawX()+" y:"+event.getRawY());
        }

        @Override
        public void onDragItemStatic(AbsListView absListView, View itemView, int position, MotionEvent event) {
            tvInfo.setText("静止在第" + position +"个item"+" x:"+event.getRawX()+" y:"+event.getRawY());
        }

        @Override
        public void OnDragItemEnd(AbsListView absListView, View itemView, int position, MotionEvent event) {
            tvInfo.setText("结束拖动第" + position +"个item"+" x:"+event.getRawX()+" y:"+event.getRawY());
        }
    };

    private class ListViewAdapter extends BaseAdapter {
        List<Integer> data;
        public ListViewAdapter(List<Integer> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, null);
            }
            ImageView iv = (ImageView) convertView.findViewById(R.id.itemview);
            iv.setImageResource(data.get(position));
            return convertView;
        }
    }
}
