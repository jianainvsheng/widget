package com.source.sdk.widget.bezie.listener;

import android.view.View;

/**
 * Created by yangjian on 2018/10/15.
 */

public interface OnBezieValuerListener<V extends View> {


    /**
     * 开始
     * @param view
     * @param x
     * @param y
     */
    void onStartValue(V view, float x, float y);

    /**
     * 更新
     * @param view
     * @param x
     * @param y
     */
    void onBezieValue(V view, float x, float y);

    /**
     * 结束
     * @param view
     * @param x
     * @param y
     */
    void onEndValue(V view, float x, float y);
}
