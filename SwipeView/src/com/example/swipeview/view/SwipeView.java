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
    private GestureDetector mGestureDetector;
    private static final String TAG = SwipeView.class.getSimpleName();
    private FrameLayout.LayoutParams params;
    {
        params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }
    private FlingRunnable runnable = new FlingRunnable();
    
    public SwipeView(Context context) {
        this(context, null);
    }
    
    public SwipeView(Context context, AttributeSet attr){
        super(context, attr);
        init();
    }

    private void init() {
        setLongClickable(true);
        setOnTouchListener(this);
        mBackgroundView = createBackgroundView();
        mForegroundView = createForegroudView();
        mBackgroundView.setBackgroundColor(Color.BLUE);
        mForegroundView.setBackgroundColor(Color.CYAN);
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
        mGestureDetector.onTouchEvent(event);
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
            runnable.startScroll((int) distanceX);
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
        
        public void startScroll(int dx){
            mScroller.startScroll( 0, 0, dx, 0);
            post(this);
        }
        
        @Override
        public void run() {
            if(mScroller.computeScrollOffset()){
                Log.d(TAG, "getCurrX" + mScroller.getCurrX());
                mForegroundView.scrollTo( mScroller.getCurrX(), 0);
                invalidate();
                post(this);
            }
        }
        
    }
}
