package com.example.loader.loaderdemo.adapters.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Item decoration for the list items
 * Created by kyle.jablonski on 11/24/15.
 */
public class UnderlineDecoration extends RecyclerView.ItemDecoration {

    private final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private Drawable mDivider;

    public UnderlineDecoration(Context context){
        final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
        mDivider = styledAttributes.getDrawable(0);
        styledAttributes.recycle();
    }

    public UnderlineDecoration(Context context, int resId){
        mDivider = ContextCompat.getDrawable(context, resId);
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft() +  (parent.getWidth() / 4);
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            if( i != childCount - 1) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
        }
    }
}
