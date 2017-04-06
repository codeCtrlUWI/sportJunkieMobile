package com.example.renadoparia.sportjunkiem;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Renado_Paria on 3/25/2017 at 7:29 AM.
 */

class RecyclerItemClicker extends RecyclerView.SimpleOnItemTouchListener
{
    private static final String TAG = "RecyclerItemClicker";
    private final OnRecyclerClickListener mListener;
    private final GestureDetectorCompat mGestureDetector;

    public RecyclerItemClicker(Context context, final RecyclerView recyclerView, OnRecyclerClickListener listener)
    {
        mListener = listener;
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener()
        {

            @Override
            public boolean onDoubleTap(MotionEvent e)
            {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null)
                {
                    // Log.d(TAG, "onDoubleTap: calling on Item Double Tap");
                    mListener.onItemDoubleTap(childView, recyclerView.getChildAdapterPosition(childView));
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e)
            {
                //  Log.d(TAG, "onLongPress: starts");
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null)
                {
                    //Log.d(TAG, "onLongPress: calling listener.onItemLongClick");
                    mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e)
            {
                //  Log.d(TAG, "onSingleTapUp: starts");
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null)
                {
                    // Log.d(TAG, "onSingleTapConfirmed: calling on itemclick");
                    mListener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
    {
        // Log.d(TAG, "onInterceptTouchEvent: starts ");
        if (mGestureDetector != null)
        {
            boolean result = mGestureDetector.onTouchEvent(e);
            //Log.d(TAG, "onInterceptTouchEvent(): returned " + result);
            return result;
        }
        else
        {
            //Log.d(TAG, "onInterceptTouchEvent() returned false");
            return false;
        }
    }
}
