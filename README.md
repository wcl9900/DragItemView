# DragItemView
列表视图Item拖拽，可镜像拖动Item View并回调监听拖动的动作事件

#效果图
![name](https://raw.githubusercontent.com/wcl9900/DragItemView/master/dragitemview.gif)

#使用方式

    Gradle
    repositories {
        jcenter()
    }
    compile 'com.wcl:dragitemview:1.0.0'
    
    
    1.创建
    dragItemViewWrapper = new DragItemViewWrapper(absListView);
    
    1.拖动监听回调
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
    dragItemViewWrapper.setOnDragListener(dragItemListener);
    
    2.镜像视图获取回调
    //可通过此接口设定需要产生拖拽镜像的视图，不设定此接口时默认会遍历选择ImageView视图
    dragItemViewWrapper.setOnImageViewGetListener(new DragItemViewWrapper.onImageViewGetListener() {
        @Override
        public View getImageView(View itemView) {
            return itemView.findViewById(R.id.itemview);
        }
    });
    
    3.设定可拖拽角度
    dragItemViewWrapper.setDragDegree(45);
    
    4.是否可拖拽
    dragItemViewWrapper.setDragEnable(true);
    
    5.设定拖拽震动反馈
    dragItemViewWrapper.setDragVibratorEnable(true);
    
    6.拖拽相对item视图区域可取消结束回调
    dragItemViewWrapper.setOverCancelEnable(true);