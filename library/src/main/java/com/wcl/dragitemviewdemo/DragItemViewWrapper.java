package com.wcl.dragitemviewdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ScrollView;

/**
 * 列表视图中可拖动子视图的包装类, 可支持任何基于{@link AbsListView}的列表视图；当使用列表视图时使用{@link OnDragItemListener}监听器;
 * @author 王春龙
 *
 */
public class DragItemViewWrapper implements OnTouchListener {

	/**
	 * 拖动子视图监听事件
	 * @author 王春龙
	 *
	 */
	public interface OnDragItemListener{
		/**
		 * 拖动子视图开始
		 * @param absListView
		 * @param itemView
		 * @param position
		 */
		void onDragItemStart(AbsListView absListView, View itemView, int position, MotionEvent event);
		/**
		 * 拖动子视图静止
		 * @param absListView
		 * @param itemView
		 * @param position
		 */
		void onDragItemStatic(AbsListView absListView, View itemView, int position, MotionEvent event);
		/**
		 * 拖动子视图结束
		 * @param absListView
		 * @param itemView
		 * @param position
		 */
		void OnDragItemEnd(AbsListView absListView, View itemView, int position, MotionEvent event);
	}

	/**
	 * 子视图非ImageView时获取子视图中的ImageView接口
	 * @author 王春龙
	 *
	 */
	public interface onImageViewGetListener{
		View getImageView(View itemView);
	}
	
	private float degree = 45;

	private AbsListView absListView;
	private ViewParent parent;

	private PointF downPoint ;
	private WindowManager windowManager;
    private WindowManager.LayoutParams windowLayoutParams;
    
    private ImageView dragImageView;

	private VelocityTracker velocityTracker;
	private Vibrator vibrator;
	
	private boolean isDraging = false;
	
	private View selectItemView;
	private View selectImageView;
	private AnimatorSet selectItemAnim ;
	
	private int fireDragMotionSpeed = 0; //dp
	private float fireDragDistance = 0;   //dp
	
	private boolean vibratorEnable = false;
	private int vibratorTime = 200;
	
	private OnDragItemListener dragListener;
	
	private onImageViewGetListener imageViewGetListener;
	
	private int dragItemDefineSizeValue = -100;
	private int dragItemDefineWidth = dragItemDefineSizeValue;
	private int dragItemDefineHeight = dragItemDefineSizeValue;
	private int dragItemWidth = WindowManager.LayoutParams.WRAP_CONTENT;
	private int dragItemHeight = WindowManager.LayoutParams.WRAP_CONTENT;

	private float action_static_speed = 2;
	private boolean action_static_enable = true;

	private boolean dragEnable = true;
	
	private boolean overCancelEnable = true;
	
	private int statusHeight;
	
	public DragItemViewWrapper(AbsListView absListView) {
		this.absListView = absListView;
		init(absListView);
	}
	
	private void init(AbsListView absListView) {
		vibrator = (Vibrator) absListView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
		windowManager = (WindowManager) absListView.getContext().getSystemService(Context.WINDOW_SERVICE);
		gestureDetector = new GestureDetector(absListView.getContext(), gestureListener);
		absListView.setOnTouchListener(this);
		
		absListView.setOnScrollListener(scrollListener);
		statusHeight = getStatusHeight(absListView.getContext());

		downPoint = new PointF();
	}

	private int scrollState = OnScrollListener.SCROLL_STATE_IDLE;
	private OnScrollListener scrollListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			DragItemViewWrapper.this.scrollState = scrollState;
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
							 int visibleItemCount, int totalItemCount) {
		}
	};
	
	public boolean isDragEnable() {
		return dragEnable;
	}

	private float distanceX, distanceY;
	private GestureDetector gestureDetector;
	private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
								float distanceX, float distanceY) {
			DragItemViewWrapper.this.distanceX = distanceX;
		    DragItemViewWrapper.this.distanceY = distanceY;
		    
		    float speed = (float) Math.sqrt(Math.pow(distanceX, 2.0f) + Math.pow(distanceY, 2.0f));
			
		    if(isDraging){	
				dragImageView((int)e2.getRawX(), (int)e2.getRawY());
				return true;
			}

			float minCosValue = (float) Math.cos(Math.PI * (degree / 180));
			float cosValue;
			try{
				cosValue = (float) (Math.abs(distanceY) / Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2)));
			}catch (Exception e) {
				cosValue = minCosValue;
			}
			
			float moveDistance = (float) (Math.sqrt(Math.pow(distanceX, 2) + Math.sqrt(Math.pow(distanceY, 2))));

			boolean dragFireEnable = cosValue < minCosValue;

			if(!dragFireEnable){
				return false;
			}
			
			if(moveDistance >= fireDragDistance && speed >= fireDragMotionSpeed && scrollState == OnScrollListener.SCROLL_STATE_IDLE){
				return startDrag(e2);
			}
			else{
				return super.onScroll(e1, e2, distanceX, distanceY);
			}
		}
	};
	
	/**
	 * 可拖动功能开关
	 * @param dragEnable
	 */
	public void setDragEnable(boolean dragEnable) {
		this.dragEnable = dragEnable;
	}

	/**
	 * 设定当拖动的视图在选中的视图之上时是否取消拖动触发事件
	 * @param overCanceEnable
	 */
	public void setOverCancelEnable(boolean overCanceEnable){
		this.overCancelEnable = overCanceEnable;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if(!dragEnable) return false;

		boolean scroll = gestureDetector.onTouchEvent(event);
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downPoint.x = event.getX();
			downPoint.y = event.getY();
			break;
			
		case MotionEvent.ACTION_MOVE:
			if(isDraging){
				float speed = (float) Math.sqrt(Math.pow(distanceX, 2.0f) + Math.pow(distanceY, 2.0f));
				dragStatic(event, speed);
				return true;
			}

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			endDrag(event);
			
			if(velocityTracker != null){
				velocityTracker.clear();
			}
			v.performClick();
			
			action_static_enable = true;
			break;
			
		default:
			break;
		}
		
		return scroll;
	}

	private void dragStatic(MotionEvent event, float speed) {
		if(overCancelEnable && isDragOverOnSelectView(event)){
	        return;
        }
		
		if(speed <= action_static_speed && action_static_enable){
			if(dragListener != null && !(absListView instanceof ExpandableListView)){
				dragListener.onDragItemStatic(absListView, selectItemView, absListView.getPositionForView(selectItemView), event);
			}
			action_static_enable = false;
		}
		else if(speed > action_static_speed){
			action_static_enable = true;
		}
	}

	/**
	 * 设定子视图可拖动的角度
	 * @param degree degree>=0 && degree<90
	 */
	public void setDragDegree(int degree){
		if(degree >= 0 && degree <= 90){
			this.degree = degree;
		}
		if(degree > 90){
			this.degree = 90;
		}
		else{
			this.degree = 0;
		}
	}
	
	/**
	 * 是否在拖动子视图
	 * @return
	 */
	public boolean isDraging(){
		return isDraging;
	}

	/**
	 * 设定拖动震动时间
	 * @param vibratorTime 毫秒时间
	 */
	public void setVibratorTime(int vibratorTime){
		this.vibratorTime = vibratorTime;
	}
	
	/**
	 * 列表视图拖动监听器
	 * @param listener
	 */
	public void setOnDragListener(OnDragItemListener listener){
		this.dragListener = listener;
	}

	public void setOnImageViewGetListener(onImageViewGetListener imageViewGetListener){
		this.imageViewGetListener = imageViewGetListener;
	}
	
	public void setDragVibratorEnable(boolean enable){
		this.vibratorEnable = enable;
	}
	
	private void endDrag(MotionEvent event) {
		if(!isDraging) return;
		setParentTouchEnable(false);
		isDraging = false;
		removeDragImage();
		
		if (velocityTracker != null) {  
			velocityTracker.recycle();  
			velocityTracker = null;  
		}  
        
        if(selectItemAnim != null){
        	selectItemAnim.cancel();
        	selectItemAnim = null;
        }
		
        if(overCancelEnable && isDragOverOnSelectView(event)){
	        return;
        }
        
		if(dragListener != null && !(absListView instanceof ExpandableListView)){
			dragListener.OnDragItemEnd(absListView, selectItemView, absListView.getPositionForView(selectItemView), event);
		}
		
		selectItemView = null;
	}

	private boolean isDragOverOnSelectView(MotionEvent event) {
		Rect rect = new Rect();
		selectItemView.getGlobalVisibleRect(rect);
		if(rect.contains((int)event.getRawX(), (int)event.getRawY())){
			return true;
		}
		return false;
	}
	
	private boolean finded = false;
	private ViewParent getParent(){
		if(parent == null && !finded){
			parent = findparent(absListView);
			finded = true;
		}
		return parent;
	}
	
	private ViewParent findparent(ViewParent parent){
		if(parent==null) return null;
		if(parent instanceof ScrollView){
			return parent;
		}	
		return findparent(parent.getParent());
	}
	
	private void setParentTouchEnable(boolean enable) {
		ViewParent getpartent=getParent();
		if(getpartent!=null){
			getpartent.requestDisallowInterceptTouchEvent(enable);
		}
	}
	

	private boolean startDrag(MotionEvent event) {
		setParentTouchEnable(true);
		
		int pointToPosition = absListView.pointToPosition((int)downPoint.x, (int)downPoint.y);
		if(pointToPosition != AdapterView.INVALID_POSITION){
			selectItemView = absListView.getChildAt(pointToPosition - absListView.getFirstVisiblePosition());

			if(vibrator.hasVibrator() && vibratorEnable){
				vibrator.vibrate(vibratorTime);
			}
			
			if(imageViewGetListener != null){
				selectImageView = imageViewGetListener.getImageView(selectItemView);
			}
			else{
				selectImageView = getChildImageView(selectItemView);
			}
			
			if(selectImageView == null){
				selectImageView = selectItemView;
			}

			selectImageView.setDrawingCacheEnabled(true);
			createDragImage(Bitmap.createBitmap(selectImageView.getDrawingCache()), (int)event.getRawX(), (int)event.getRawY());
			selectImageView.destroyDrawingCache();
			
			ObjectAnimator oaScaleX = ObjectAnimator.ofFloat(selectImageView, "scaleX", 1.0f, 0.9f);
		    ObjectAnimator oaScaleY = ObjectAnimator.ofFloat(selectImageView, "scaleY", 1.0f, 0.9f);
		    oaScaleX.setRepeatMode(ValueAnimator.REVERSE);
		    oaScaleY.setRepeatMode(ValueAnimator.REVERSE);
		    oaScaleX.setRepeatCount(ValueAnimator.INFINITE);
		    oaScaleY.setRepeatCount(ValueAnimator.INFINITE);
		    selectItemAnim = new AnimatorSet();
		    selectItemAnim.playTogether(oaScaleX, oaScaleY);
		    selectItemAnim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationCancel(Animator animation) {
					selectImageView.setScaleX(1);
					selectImageView.setScaleY(1);
				}
		    	
			});
		    selectItemAnim.start();

		    if(dragListener != null && !(absListView instanceof ExpandableListView)){
		    	dragListener.onDragItemStart(absListView, selectItemView, absListView.getPositionForView(selectItemView), event);
		    }
			isDraging = true;
		    return true;
		}
		
		return false;
	}

  
	
	private ImageView getChildImageView(View view){
		if(view == null){
			return null;
		}
		
		if(view instanceof ImageView){
			return (ImageView) view;
		}
		
		if(view instanceof ViewGroup){
			ViewGroup vg = (ViewGroup)view;
			int childCount = vg.getChildCount();
			for(int i = 0; i < childCount; i++){
			
				View childView = vg.getChildAt(i);
				childView = getChildImageView(childView);
				if(childView != null){
					return (ImageView) childView;
				}
			}
		}
		
		return null;
	}
	
	public void setDragItemWidth(int width){
		this.dragItemDefineWidth = width;
	}
	
	public void setDragItemHeight(int height){
		this.dragItemDefineHeight = height;
	}
	
	@SuppressLint("RtlHardcoded")
	private void createDragImage(Bitmap bitmap, int rawX , int rawY){
        windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        windowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        windowLayoutParams.x = (int) (rawX - selectImageView.getWidth() / 2.0f);  
        windowLayoutParams.y = (int) (rawY - selectImageView.getHeight() / 2.0f) - statusHeight;  
        windowLayoutParams.alpha = 0.75f; 
        
        if(dragItemDefineWidth == dragItemDefineSizeValue){
	        if(dragItemWidth != WindowManager.LayoutParams.WRAP_CONTENT && dragItemWidth != WindowManager.LayoutParams.MATCH_PARENT){
	        	dragItemWidth = CommonUtil.dpToPx(absListView.getResources(), dragItemWidth);  
	        }  
        }
        else{
        	dragItemWidth = CommonUtil.dpToPx(absListView.getResources(), dragItemDefineWidth);  
        }
        if(dragItemDefineHeight == dragItemDefineSizeValue){
	        if(dragItemHeight != WindowManager.LayoutParams.WRAP_CONTENT && dragItemHeight != WindowManager.LayoutParams.MATCH_PARENT){
	        	dragItemHeight = CommonUtil.dpToPx(absListView.getResources(), dragItemHeight);  
	        }
        }
        else{
        	dragItemHeight = CommonUtil.dpToPx(absListView.getResources(), dragItemDefineHeight);  
        }
        
    	windowLayoutParams.width = dragItemWidth;  
        windowLayoutParams.height = dragItemHeight;    
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE ;
            
        dragImageView = new ImageView(absListView.getContext());
        dragImageView.setImageBitmap(bitmap);   
        dragImageView.setScaleType(ScaleType.CENTER_INSIDE);
        windowManager.addView(dragImageView, windowLayoutParams);
    }
	
	private void dragImageView(int rawX, int rawY){
        windowLayoutParams.x = (int) (rawX - (dragItemDefineWidth != dragItemDefineSizeValue ? dragItemWidth : selectImageView.getWidth()) / 2.0f); 
        windowLayoutParams.y = (int) (rawY - (dragItemDefineHeight != dragItemDefineSizeValue ? dragItemHeight : selectImageView.getHeight()) / 2.0f) - statusHeight;
        if(dragImageView != null){
        	windowManager.updateViewLayout(dragImageView, windowLayoutParams);
        }
	}
	
	private void removeDragImage(){  
        if(dragImageView == null) return;
        
        final View dragView = dragImageView;
        
        ObjectAnimator oaScaleX = ObjectAnimator.ofFloat(dragView, "scaleX", 1.0f, 0f);
    	ObjectAnimator oaScaleY = ObjectAnimator.ofFloat(dragView, "scaleY", 1.0f, 0f);
    	ObjectAnimator oaScaleAlpha = ObjectAnimator.ofFloat(dragView, "alpha", 1.0f, 0.2f);
    	AnimatorSet animatorSet = new AnimatorSet();
    	animatorSet.playTogether(oaScaleX, oaScaleY, oaScaleAlpha);
    	animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				windowLayoutParams.width = 0;
				windowLayoutParams.height = 0;
				windowManager.updateViewLayout(dragView, windowLayoutParams);
				windowManager.removeView(dragView); 
			}
		});
    	
    	animatorSet.start();
        dragImageView = null;  
    }  
	
	/**
	 * 获取状态栏的高度
	 * @param context
	 * @return
	 */
	private int getStatusHeight(Context context){
		WindowManager.LayoutParams attrs = ((Activity)context).getWindow().getAttributes();

        if((attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != WindowManager.LayoutParams.FLAG_FULLSCREEN){
        	Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }  
        }
        else{
        	statusHeight = 0;
        }  
        return statusHeight;
    }
}
