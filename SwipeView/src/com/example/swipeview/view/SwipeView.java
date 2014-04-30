 package com.example.swipeview.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class SwipeView extends FrameLayout implements OnTouchListener{
    private View mBackgroundView;
    private View mForegroundView;
    private FlingRunnable mFlingRunnable;
    private GestureDetector mGestureDetector;
    private static final String TAG = SwipeView.class.getSimpleName();
    private FrameLayout.LayoutParams params;
    {
        params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }
    
    public SwipeView(Context context) {
        this(context, null);
    }
    
    public SwipeView(Context context, AttributeSet attr){
        super(context, attr);
        init();
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        
        int width  = (right - left) - getPaddingLeft() - getPaddingRight();
        int height = (bottom - top) - getPaddingTop() - getPaddingBottom();
        int _left = getPaddingLeft();
        int _top = getPaddingTop();
        int _right = _left + width;
        int _bottom = _top + height;
        Log.d(TAG, "left : " + _left + " top : " + _top + " right : " + _right + " bottom : " + _bottom);
        
        int childCount = getChildCount();
        for(int i = 0; i < childCount; i++){
            View view = getChildAt(i);
            
            final int measureWidth  = view.getMeasuredWidth();
            final int measureHeight = view.getMeasuredHeight();
            view.layout( _left, _top, _left + measureWidth, _top + measureHeight);
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        
        setMeasuredDimension(width, height);
        
        int childCount = getChildCount();
        for(int i = 0; i < childCount; i++){
            View view = getChildAt(i);
            
            final int widthMeasure = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            final int heightMeasure = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            view.measure(widthMeasure, heightMeasure);
        }
    }

    private void init() {
        setLongClickable(true);
        setOnTouchListener(this);
        mBackgroundView = createBackgroundView();
        mForegroundView = createForegroudView();
        mBackgroundView.setBackgroundColor(Color.BLUE);
        mForegroundView.setBackgroundColor(Color.CYAN);
        mFlingRunnable   = new FlingRunnable();
        mGestureDetector = new GestureDetector(getContext(), onGestureListener);
        addView(mBackgroundView, params);
        addView(mForegroundView, params);
    }
    
    private View createBackgroundView(){
        return new View(getContext());
    }
    
    private View createForegroudView(){
        return new View(getContext());
    }
    
    public void setBackGroundView(View view){
        mBackgroundView = view;
    }
    
    public void setForeGroundView(View view){
        mForegroundView = view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch");
        if(event.getAction() == MotionEvent.ACTION_UP){
            Log.d(TAG, "onTouch Action_UP");
            final int startX = (int) mForegroundView.getX();
            final int dx = -startX;
            mFlingRunnable.startScroll(startX, dx);
        }else{
            mGestureDetector.onTouchEvent(event);
        }
        return false;
    }

    private final OnGestureListener onGestureListener = new OnGestureListener() {
        
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp");
            return false;
        }
        
        @Override
        public void onShowPress(MotionEvent e) {
            Log.d(TAG, "onShowPress");
        }
        
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                float distanceY) {
            Log.d(TAG, "onScroll");
            Log.d(TAG, "distanceX : " + distanceX + " distanceY : " + distanceY);
            int positionX = (int) e2.getX();
            int positionY = (int) e2.getY();
            Log.d(TAG, "positionX : " + positionX + " positionY : " + positionY);
            
            final int left = (int) (mForegroundView.getX() - distanceX);
            final int top  = (int) mForegroundView.getY();
            final int right = left + mForegroundView.getWidth();
            final int bottom = top + mForegroundView.getHeight();
            
            mForegroundView.layout(left, top, right, bottom);
            return false;
        }
        
        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress");
        }
        
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
            Log.d(TAG, "onFling");
            mFlingRunnable.startFling( (int) mForegroundView.getX(), 0, (int) velocityX, 0, -getWidth() / 4, 0, 0, 0);
            return false;
        }
        
        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDown");
            return true;
        }
    };
    
    private class FlingRunnable implements Runnable{
        private Scroller mScroller;
        
        public FlingRunnable() {
            mScroller = new Scroller(getContext());
        }
        
        private void startCommon(){
            removeCallbacks(this);
        }
        
        public void startScroll( int startX, int dx){
            startCommon();
            Log.d(TAG, "移動量 : " + dx);
            mScroller.startScroll( startX, 0, dx, 0);
            post(this);
        }
        
        public void startFling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY){
            startCommon();
            mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            post(this);
        }
        
        @Override
        public void run() {
            if(mScroller.computeScrollOffset()){
                int distanceX = mScroller.getCurrX();

                final int left = distanceX;
                final int top  = mForegroundView.getTop();
                final int right = left + getWidth();
                final int bottom = top + getHeight();
                
                Log.d(TAG, "left : " + left + " top : " + top + " right : " + right + " bottom : " + bottom);
                mForegroundView.layout( left, top, right, bottom);
                postDelayed( this, 10);
            }
        }
        
    }
}
