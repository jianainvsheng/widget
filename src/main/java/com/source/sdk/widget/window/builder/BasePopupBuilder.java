package com.source.sdk.widget.window.builder;

import android.view.View;

import com.source.sdk.widget.window.popup.BasePopupWindow;
import com.source.sdk.widget.window.popup.gwindow.GPopupWindow;
import com.source.sdk.widget.window.windowenum.WindowEnum;

/**
 * Created by yangjian on 2019/3/6.
 */

public class BasePopupBuilder<B extends BasePopupBuilder<B>> {

    private BasePopupWindow mPopupWindow;

    private Object mData;

    private int mType;

    private int mLevel = -1;

    private int offX = 0;

    private int offY = 0;

    private boolean isOutCanTouch = true;

    private boolean isOutClickHide = false;

    private WindowEnum mWindowEnum = WindowEnum.WINDOW_LEFT;

    public boolean isFitsSystemWindows = true;

    public B setData(Object data) {
        this.mData = data;
        return (B) this;
    }

    public B setType(int type) {

        this.mType = type;
        return (B) this;
    }

    public B setWindowEnum(WindowEnum windowEnum) {

        this.mWindowEnum = windowEnum;
        return (B) this;
    }

    public B setLevel(int mLevel) {
        this.mLevel = mLevel;
        return (B)this;
    }

    public int getLevel() {
        return mLevel;
    }

    public B setFitsSystemWindows(boolean fitsSystemWindows) {
        isFitsSystemWindows = fitsSystemWindows;
        return (B)this;
    }

    public B setOffY(int offY) {
        this.offY = offY;
        return (B)this;
    }

    public B setOffX(int offX) {
        this.offX = offX;
        return (B)this;
    }

    /**
     * 设置window之外的区域是否可以触碰 默认为true
     * @param outCanTouch
     */
    public B setOutsideTouchable(boolean outCanTouch){

        this.isOutCanTouch = outCanTouch;
        return (B) this;
    }

    public boolean isFitsSystemWindows() {
        return isFitsSystemWindows;
    }

    /**
     * 设置window之外的区域点击隐藏window
     * @param outClickHide
     */
    public B setOutsideClickHide(boolean outClickHide){

        this.isOutClickHide = outClickHide;
        return (B) this;
    }

    public boolean getOutsideClickHide(){

        return isOutClickHide;
    }

    public boolean getOutsideTouchable(){

        return isOutCanTouch;
    }

    public int getOffY() {
        return offY;
    }

    public int getOffX() {
        return offX;
    }

    public int getType() {
        return mType;
    }

    public Object getData() {
        return mData;
    }

    public WindowEnum getWindowEnum() {
        return mWindowEnum;
    }

    public GPopupWindow showPopupView(View longitudinalView) {
        this.mPopupWindow.showPopupWindow(longitudinalView,getLevel());
        return (GPopupWindow) mPopupWindow;
    }

    public void attach(BasePopupWindow popupWindow) {

        this.mPopupWindow = popupWindow;
    }

    public BasePopupWindow getGPopupWindow(){

        return mPopupWindow;
    }
}
