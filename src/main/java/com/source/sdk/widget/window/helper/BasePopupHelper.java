package com.source.sdk.widget.window.helper;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.source.sdk.widget.window.builder.BasePopupBuilder;
import com.source.sdk.widget.window.popup.BasePopupWindow;

/**
 * Created by yangjian on 2019/3/6.
 */

public abstract class BasePopupHelper<B extends BasePopupBuilder> {

    private B mBuilder;

    private View mContentView;

    private @LayoutRes
    int mLayoutResId = -1;

    public View attachView(B builder, ViewGroup parentView,BasePopupWindow decorWindow) {

        if (builder == null) {
            throw new NullPointerException(this.getClass().getCanonicalName() + " attach 方法中 builder 为空");
        }

        this.mBuilder = builder;
        if(mContentView != null && onCreateViewLayoutId() == mLayoutResId){
            return mContentView;
        }
        return onCreateView(parentView, builder,decorWindow);
    }

    View onCreateView(ViewGroup parentView, B builder,BasePopupWindow decorWindow){

        this.mLayoutResId = onCreateViewLayoutId();
        mContentView = LayoutInflater.from(parentView.getContext())
                .inflate(mLayoutResId , parentView ,false);
        onBuilder(mContentView,builder,decorWindow);
        return mContentView;
    }

    /**
     * layout id
     * @return
     */
    public abstract @LayoutRes
    int onCreateViewLayoutId();

    /**
     * 绑定数据
     * @param view
     * @param builder
     */
    public abstract void onBuilder(View view , B builder,BasePopupWindow decorWindow);


    /**
     * 添加的视图被创建，对Y轴的矢量修改
     * @param decorWindow
     * @param longitudinalView
     * @return
     */
    public abstract int postVectorY(BasePopupWindow decorWindow,View longitudinalView);

    /**
     * 添加的视图被创建，对X轴的矢量修改
     * @param decorWindow
     * @param longitudinalView
     * @return
     */
    public abstract int postVectorX(BasePopupWindow decorWindow,View longitudinalView);

    /**
     * 展示
     * @param decorWindow
     */
    public abstract void showPopupWindow(BasePopupWindow decorWindow);

    /**
     * popup消失
     */
    public abstract int hidePopupWindowDuration();

    /**
     * 是否可以隐藏popupwindow
     * @return
     */
    public abstract boolean canNotHidePopupWindow();

    /**
     * 是否可以展示popupWindow
     * @return
     */
    public abstract boolean canNotShowPopupWindow();

    public B getBuilderData() {

        return mBuilder;
    }

    public View getContentView(){

        return mContentView;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

}
