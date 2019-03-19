package com.source.sdk.widget.bezie;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.source.sdk.widget.bezie.listener.OnBezieValuerListener;
import com.source.sdk.widget.bezie.type.BezierTypeEvaluator;
import com.source.sdk.widget.bezie.value.BaseBezierValue;

/**
 * Created by yangjian on 2018/10/15.
 */

public abstract class BaseBezierHelper<V extends View,B extends BaseBezierValue<V>> {

    private View mStartView;

    private View mEndView;

    private OnBezieValuerListener<V> mOnBezieValuerListener;

    private ValueAnimator mValueAnimator;

    private FrameLayout mContentView;

    private Activity mActivity;

    private long mDuration = 600;

    private int[] mPosition = new int[2];

    private boolean isAnnotationFinish = true;

    public void setBezieValue(Activity activity, View startView , View endView){

        setBezieValue(activity,startView,endView,mDuration);
    }

    public void setBezieValue(Activity activity, View startView , View endView, long duration){

        this.mEndView = endView;
        this.mStartView = startView;

        this.mActivity = activity;
        if(mContentView == null){
            this.mContentView = new FrameLayout(startView.getContext());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            this.mContentView.setLayoutParams(params);
            ViewGroup contentView = activity.findViewById(android.R.id.content);
            contentView.addView(this.mContentView);
            contentView.getLocationOnScreen(mPosition);
        }
        startAnimator(duration);
    }

    public void startAnimator(){

        startAnimator(mDuration);
    }

    public void startAnimator(long duration){

        if(mEndView == null || mStartView == null){

            isAnnotationFinish = true;
            return;
        }

        if(!isAnnotationFinish){

            return;
        }
        //贝塞尔起始数据点
        int[] startPosition = new int[2];
        //贝塞尔结束数据点
        int[] endPosition = new int[2];
        mEndView.getLocationOnScreen(endPosition);
        mStartView.getLocationOnScreen(startPosition);
        if(mValueAnimator != null){

            mValueAnimator.cancel();
            mValueAnimator.removeAllUpdateListeners();
            mValueAnimator = null;
        }

        final PointF startF = new PointF();
        final PointF endF = new PointF();
        PointF controllF = new PointF();

        startF.x = startPosition[0];
        startF.y = startPosition[1] - mPosition[1];
        endF.x = endPosition[0];
        endF.y = endPosition[1] - mPosition[1];
        controllF.x = endF.x;
        controllF.y = startF.y;

        final B value = onCreateBezieValue(mContentView,startF);
        if(value == null){

            throw new NullPointerException("创建的value为空");
        }

        this.mContentView.removeAllViews();
        final V addView = value.getAddView();
        addView.setX(startF.x);
        addView.setY(startF.y);
        this.mContentView.addView(addView);
        isAnnotationFinish = false;
        this.mValueAnimator = ValueAnimator.ofObject(new BezierTypeEvaluator(controllF), startF, endF);
        this.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                value.onBezieValue(pointF);

                if(pointF.x == startF.x && pointF.y == startF.y){
                    //开始
                    if(mOnBezieValuerListener != null){
                        mOnBezieValuerListener.onStartValue(addView,pointF.x,pointF.y);
                    }
                }else if(pointF.x == endF.x && pointF.y == endF.y){
                    //结束
                    isAnnotationFinish = true;
                    if(mOnBezieValuerListener != null){
                        mOnBezieValuerListener.onEndValue(addView,pointF.x,pointF.y);
                    }
                }else {
                    //刷新
                    if(mOnBezieValuerListener != null){
                        mOnBezieValuerListener.onBezieValue(addView,pointF.x,pointF.y);
                    }
                }
            }
        });

        this.mValueAnimator.setDuration(duration);
        this.mValueAnimator.start();
    }

    public void setBezieValueListener(OnBezieValuerListener<V> onBezieValuerListener){

        this.mOnBezieValuerListener = onBezieValuerListener;
    }

    /**
     * 微调初始化的坐标和结束坐标
     * @param startF
     * @param endF
     */
    public void onOffSizePointF(PointF startF, PointF endF){

    }
    public void onDestory(){

        if(mValueAnimator != null){

            mValueAnimator.cancel();
            mValueAnimator.removeAllUpdateListeners();
            mValueAnimator = null;
        }
        if(mContentView != null){
            ViewGroup contentView = mActivity.findViewById(android.R.id.content);
            if(contentView.indexOfChild(this.mContentView) != -1){
                contentView.removeView(this.mContentView);
            }
            this.mContentView.removeAllViews();
            this.mContentView = null;
        }

    }

    public boolean isAnnotationFinish(){

        return isAnnotationFinish;
    }

    public abstract B onCreateBezieValue(ViewGroup viewGroup, PointF startF);
}
