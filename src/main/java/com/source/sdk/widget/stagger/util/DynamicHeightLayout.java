package com.source.sdk.widget.stagger.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * An {@link ImageView} layout that maintains a consistent width to height aspect ratio.
 */
public class DynamicHeightLayout extends RelativeLayout {

    private double mHeightRatio;

    public DynamicHeightLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicHeightLayout(Context context) {
        super(context);
    }

    public void setHeightRatio(double ratio) {
        if (ratio != mHeightRatio) {
            mHeightRatio = ratio;
            requestLayout();
        }
    }

    public double getHeightRatio() {
        return mHeightRatio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeightRatio > 0.0) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) (width * mHeightRatio);
            setMeasuredDimension(widthMeasureSpec, height);
        }else{
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
