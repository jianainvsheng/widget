package com.source.sdk.widget.wrapcontentlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 *  @author Created by yangjian-ds3 on 2018/4/10.
 */

public class WrapContentLayout extends ViewGroup {
    private int mMeasuredWidth;

    public WrapContentLayout(Context context) {
        super(context);
    }

    public WrapContentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapContentLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSize;
        for(heightSize = 0; heightSize < this.getChildCount(); ++heightSize) {
            View child = this.getChildAt(heightSize);
            child.measure(0, 0);
        }

        this.mMeasuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        heightSize = this.measureHeight();
        heightSize = Math.max(heightSize, this.getSuggestedMinimumHeight());
        heightSize = resolveSize(heightSize, heightMeasureSpec);
        this.setMeasuredDimension(getDefaultSize(this.getSuggestedMinimumWidth(), widthMeasureSpec), heightSize);
    }

    @Override
    protected void onLayout(boolean arg0, int pLeft, int pTop, int pRight, int pBottom) {
        int count = this.getChildCount();
        int row = 0;
        int lengthX = 0;

        for(int i = 0; i < count; ++i) {
            View child = this.getChildAt(i);
            if(View.VISIBLE == child.getVisibility()) {
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                lengthX += width + 2;
                int lengthY = row * (height + 2) + 2 + height;
                if(lengthX + pLeft > pRight) {
                    lengthX = width + 2;
                    ++row;
                    lengthY = row * (height + 2) + 2 + height;
                }

                int left = lengthX - width - 2;
                int top = lengthY - height;
                child.layout(left, top, lengthX, lengthY);
            }
        }

    }

    private int measureHeight() {
        int count = this.getChildCount();
        int row = 0;
        int lengthX = 0;
        int lengthY = 0;

        for(int i = 0; i < count; ++i) {
            View child = this.getChildAt(i);
            if(View.VISIBLE == child.getVisibility()) {
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                lengthX += width + 2;
                lengthY = row * (height + 2) + 2 + height;
                if(lengthX + this.getPaddingLeft() > this.mMeasuredWidth) {
                    lengthX = width + 2;
                    ++row;
                    lengthY = row * (height + 2) + 2 + height;
                }
            }
        }
        return lengthY;
    }
}
