package com.studio.jarn.backfight.Gameboard;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;



/**
 * PanZoomView
 * inspiration for this class has been found here: http://www.wglxy.com/android-tutorials/android-zoomable-game-board
 * This view supports both zooming and panning.
 * What gets shown in the view depends on the onDraw method provided by subclasses.
 */

public class PanZoomView extends View {


    private static final int INVALID_POINTER_ID = -1;
    static private final float SCROLL_THRESHOLD = 20; // Used to define if a touch is scroll or click
    final Context mContext;
    float mPosX;
    float mPosY;
    float mScaleFactor = 1.f;
    private float mLastTouchX;
    private float mLastTouchY;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private boolean mDoTouchUp = false;
    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;
    private ScaleGestureDetector mScaleDetector;
    private boolean mIsMove;

    //Click handling
    private boolean mSupportsOnTouchUp = true;

public PanZoomView (Context context) {
    this(context, null, 0);
}

public PanZoomView (Context context, AttributeSet attrs) {
    this(context, attrs, 0);
}

public PanZoomView (Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    mContext = context;
    setupToDraw (context, attrs, defStyle);
    setupScaleDetector (context, attrs, defStyle);
}

@Override
public void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    onDrawPz (canvas);
}

    void onDrawPz(Canvas canvas) {

}

/**
 * Handle touch and multitouch events so panning and zooming can be supported.
 * This method is copied from here http://www.wglxy.com/android-tutorials/android-zoomable-game-board
 */

@Override
public boolean onTouchEvent (MotionEvent e) {

    // Let the ScaleGestureDetector inspect all events.
    mScaleDetector.onTouchEvent(e);

    // for a description for the use of MotionEvent.ACTION_MASK: http://stackoverflow.com/questions/16464187/the-difference-of-using-motionevent-getaction-method
    switch (e.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN: {
            final float x = e.getX();
            final float y = e.getY();

            mIsMove = false;            // Assume action is a click until the scroll threshold is met.

            mLastTouchX = x;
            mLastTouchY = y;
            mActivePointerId = e.getPointerId(0);

            if (mSupportsOnTouchUp) {
                mInitialTouchX = x;
                mInitialTouchY = y;
                mDoTouchUp = true;
            }
            break;
        }

        case MotionEvent.ACTION_MOVE: {
            final int pointerIndex = e.findPointerIndex(mActivePointerId);
            final float x = e.getX(pointerIndex);
            final float y = e.getY(pointerIndex);

            if (!mIsMove && (Math.abs(mInitialTouchX - x) > SCROLL_THRESHOLD
                    || Math.abs(mInitialTouchY - y) > SCROLL_THRESHOLD)) {
                mIsMove = true;
            }

            // Only move if the view supports panning and
            // ScaleGestureDetector isn't processing a gesture.
            boolean scalingInProgress = mScaleDetector.isInProgress();
            boolean mSupportsPan = true;
            if (mSupportsPan && !scalingInProgress) {
                if (mIsMove) {
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    mPosX += dx;
                    mPosY += dy;

                    invalidate();
                }
            }

            mLastTouchX = x;
            mLastTouchY = y;

            break;
        }

        case MotionEvent.ACTION_UP: {
            if (mIsMove) {
                mDoTouchUp = false;
            } else {
                mActivePointerId = INVALID_POINTER_ID;
                if (mSupportsOnTouchUp && mDoTouchUp) {
                    final float x = e.getX();
                    final float y = e.getY();
                    try {
                        onTouchUp(mInitialTouchX, mInitialTouchY, x, y);
                    } finally {

                    }
                    mDoTouchUp = false;
                }
            }
            break;
        }

        case MotionEvent.ACTION_CANCEL: {
            mActivePointerId = INVALID_POINTER_ID;
            break;
        }

        case MotionEvent.ACTION_POINTER_UP: {
            final int pointerIndex = (e.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                    >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
            final int pointerId = e.getPointerId(pointerIndex);
            if (pointerId == mActivePointerId) {
                // This was our active pointer going up. Choose a new
                // active pointer and adjust accordingly.
                final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                mLastTouchX = e.getX(newPointerIndex);
                mLastTouchY = e.getY(newPointerIndex);
                mActivePointerId = e.getPointerId(newPointerIndex);
            }
            break;
        }
    }


    this.performClick();           // Do this to get rid of warning message.
                                // Not sure what it does.
    return true;
}

@Override
public boolean performClick() {
   super.performClick();
   return true;
}

    void onTouchUp(float downX, float downY, float upX, float upY) {
        //Gets overwrited by GameActivity which implements GameTouchListener interface.
    }

    private void setupScaleDetector(Context context, AttributeSet attrs, int defStyle) {
    // Create our ScaleGestureDetector
    mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
}


    void setupToDraw(Context context, AttributeSet attrs, int defStyle) {
    mIsMove = false;
}


/**
 * ScaleListener
 * This method is taken from the android training: https://developer.android.com/training/gestures/scale.html
 */

private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        boolean mSupportsZoom = true;
        if (!mSupportsZoom) return true;
        mScaleFactor *= detector.getScaleFactor();

        // Don't let the object get too small or too large.
        float mMinScaleFactor = 0.2f;
        float mMaxScaleFactor = 2.0f;
        mScaleFactor = Math.max(mMinScaleFactor, Math.min(mScaleFactor, mMaxScaleFactor));

/*
        mPosX = mPosX * (-3616*mScaleFactor+3616);
        mPosY = mPosY * (-3616*mScaleFactor+3616);
*/

        invalidate();
        return true;
    }
}
} // end class
