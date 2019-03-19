package com.source.sdk.widget.window;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yangjian on 2019/3/6.
 */

public interface IDecorWindow {

    /**
     * 初始化
     * @param popupWindow
     */
    void attach(IDecorWindow popupWindow);

    /**
     * 展示popouwindow 向下展示
     * @param view
     * @param cls
     * @param level
     * @param decorWindow
     */
    void showPopupWindow(View view, Class<?> cls, int level, IDecorWindow decorWindow);

    /**
     * 隐藏view
     * @param cls
     */
    void hidePopupWindow(Class<?> cls);

    /**
     * 隐藏view
     */
    void hidePopupWindow();

    /**
     * 加载PopupWindow的控件
     * @return
     */
    ViewGroup getDecorView();

    /**
     * window是否展示
     * @param cls 以class 作为区分是否是同一个window
     * @return
     */
    boolean isPopupWindowShow(Class<?> cls);

    /**
     * 获取层级 当前实际的层级
     * @param cls
     * @return
     */
    int getViewCurentLevel(Class<?> cls);


    /**
     * 获取层级 设置的层级 默认为-1
     * @param cls
     * @return
     */
    int getViewLevel(Class<?> cls);

    /**
     * 设置window之外的区域是否可以触碰 默认为true
     * @param outCanTouch
     * @param cls
     */
    void setOutsideTouchable(boolean outCanTouch, Class<?> cls);

    /**
     * 设置window之外的区域点击隐藏window
     * @param outClickHide
     * @param cls
     */
    void setOutsideClickHide(boolean outClickHide, Class<?> cls);

    /**
     * 事件分发
     * @param event
     * @return
     */
    boolean onTouchEvent(MotionEvent event);

}
