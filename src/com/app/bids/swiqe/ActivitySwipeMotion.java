package com.app.bids.swiqe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ActivitySwipeMotion implements ActivitySwipeInterface, View.OnTouchListener {
    static final int MIN_DISTANCE = 100;
    private PointerCoords mDownPos = new PointerCoords();
    private PointerCoords mUpPos = new PointerCoords();
 
    public ActivitySwipeMotion(Activity activity) {
    }
 
    public void onSwipeLeft() {
    }
 
    public void onSwipeRight() {
    }
 
    public void onSwipeDown() {
    }
 
    public void onSwipeUp() {
    }
 
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
            // Capture the position where swipe begins
            case MotionEvent.ACTION_DOWN: {
                event.getPointerCoords(0, mDownPos);
                return true;
            }
 
            // Get the position where swipe ends
            case MotionEvent.ACTION_UP: {
                event.getPointerCoords(0, mUpPos);
 
                float dx = mDownPos.x - mUpPos.x;
 
                // Check for horizontal wipe
                if (Math.abs(dx) > MIN_DISTANCE) {
                    if (dx > 0)
                        onSwipeLeft();
                    else
                        onSwipeRight();
                    return true;
                }
 
                float dy = mDownPos.y - mUpPos.y;
 
                // Check for vertical wipe
                if (Math.abs(dy) > MIN_DISTANCE) {
                    if (dy > 0)
                        onSwipeUp();
                    else
                        onSwipeDown();
                    return true;
                }
            }
        }
        return false;
    }
}