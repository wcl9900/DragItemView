package com.wcl.dragitemviewdemo;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;

/**
 * 拖动视图监听适配器
 * @author 王春龙
 *
 */
public class OnDragItemListenerAdapter implements DragItemViewWrapper.OnDragItemListener{

	@Override
	public void onDragItemStart(AbsListView absListView, View itemView,
								int position, MotionEvent event) {
		
	}

	@Override
	public void onDragItemStatic(AbsListView absListView, View itemView,
								 int position, MotionEvent event) {
		
	}

	@Override
	public void OnDragItemEnd(AbsListView absListView, View itemView,
							  int position, MotionEvent event) {
		
	}
}
