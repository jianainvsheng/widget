package com.source.sdk.widget.window.popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.RelativeLayout;

import com.source.sdk.widget.R;
import com.source.sdk.widget.window.IDecorWindow;
import com.source.sdk.widget.window.builder.BasePopupBuilder;
import com.source.sdk.widget.window.decorchild.DecorChildView;
import com.source.sdk.widget.window.helper.BasePopupHelper;
import com.source.sdk.widget.window.windowenum.WindowEnum;

/**
 * Created by yangjian on 2019/3/6.
 */

public class BasePopupWindow<B extends BasePopupBuilder<B>, H extends BasePopupHelper<B>>
        implements IDecorWindow,ViewTreeObserver.OnScrollChangedListener {

    private IDecorWindow mDecorWindow;

    private B mBuilder;

    private H mHelper;

    private View mlongitudinalView;

    int y = 0;

    public BasePopupWindow(Context context, B builder, H helper) {

        this.mBuilder = builder;
        this.mHelper = helper;
        IDecorWindow decorChildView = findDecorChild(context);
        decorChildView.attach(this);
        this.mBuilder.attach(this);
    }

    @Override
    public void attach(IDecorWindow popupWindow) {
        this.mDecorWindow = popupWindow;
    }

    @Override
    public void showPopupWindow(View view, Class<?> cls, int level,IDecorWindow decorWindow) {

        mDecorWindow.showPopupWindow(view, cls, level,decorWindow);
        setOutsideClickHide(mBuilder.getOutsideClickHide(),cls);
        setOutsideTouchable(mBuilder.getOutsideTouchable(),cls);
    }

    public void showPopupWindow(View longitudinalView) {

        showPopupWindow(longitudinalView, mBuilder.getLevel());
    }

    public void showPopupWindow(final View longitudinalView, int level) {
        if (longitudinalView == null || isPopupWindowShow() || mHelper.canNotShowPopupWindow()) {
            return;
        }
        int height = getCorrectionHeight(longitudinalView);
        final Rect startPosition = new Rect();
        longitudinalView.getGlobalVisibleRect(startPosition);
        startPosition.top = startPosition.top - height <= 0 ? 0: startPosition.top - height;
        final View popupWindowView = this.mHelper.attachView(mBuilder, getDecorView(), this);
        if (popupWindowView == null) {
            throw new NullPointerException(this.getClass().getCanonicalName() + " 获取加载的popupWindwoView 为空");
        }
        final int viewWidth = longitudinalView.getMeasuredWidth();
        final Rect endPosition = new Rect();
        getDecorView().getGlobalVisibleRect(endPosition);
        endPosition.top = endPosition.top - height <= 0 ? 0: endPosition.top - height;

        WindowEnum windowEnum = mBuilder.getWindowEnum();
        windowEnum = windowEnum == null ? WindowEnum.WINDOW_LEFT : windowEnum;
        final WindowEnum wEnum = windowEnum;
        popupWindowView.setVisibility(View.INVISIBLE);
        showPopupWindow(popupWindowView, mHelper.getClass(), level,this);
        popupWindowView.post(new Runnable() {
            @Override
            public void run() {
                setGlobalLayout(longitudinalView,popupWindowView,startPosition,endPosition,viewWidth,wEnum);
                mHelper.showPopupWindow(BasePopupWindow.this);
                popupWindowView.setVisibility(View.VISIBLE);
                showLongitudinalView(longitudinalView);
            }
        });
    }


    private void setGlobalLayout(View longitudinalView,View popupWindowView , Rect startPosition,Rect endPosition,int viewWidth,WindowEnum wEnum){

        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) popupWindowView.getLayoutParams();
        int longitudinalY = startPosition.top + mBuilder.getOffY() + mHelper.postVectorY(BasePopupWindow.this,longitudinalView);
        int longitudinalX = startPosition.left + mBuilder.getOffX() + mHelper.postVectorX(BasePopupWindow.this,longitudinalView);
        int maxWidthSize = mDecorWindow.getDecorView().getMeasuredWidth();
        int contentViewWidthSize = mHelper.getContentView().getMeasuredWidth();
        int popupWidth = popupWindowView.getMeasuredWidth();
        System.out.println("================onScrollChanged longitudinalY.top = " + longitudinalY);
        if (wEnum == WindowEnum.WINDOW_LEFT) {
            params.topMargin = longitudinalY;
            params.leftMargin = longitudinalX;
            if(longitudinalX + contentViewWidthSize > maxWidthSize){

                int leftMargin = longitudinalX - (longitudinalX + contentViewWidthSize - maxWidthSize);
                params.leftMargin = leftMargin < 0 ? 0: leftMargin;
            }
        } else if (wEnum == WindowEnum.WINDOW_CENTER) {
            params.topMargin = longitudinalY;
            params.leftMargin = longitudinalX - (popupWidth - viewWidth) / 2 < 0 ? 0 : longitudinalX - (popupWidth - viewWidth) / 2;
            if(params.leftMargin + contentViewWidthSize > maxWidthSize){
                int leftMargin = params.leftMargin - (params.leftMargin + contentViewWidthSize - maxWidthSize);
                params.leftMargin = leftMargin < 0 ? 0: leftMargin;
            }

        } else if (wEnum == WindowEnum.WINDOW_RIGHT) {
            params.topMargin = longitudinalY;
            params.leftMargin = longitudinalX + viewWidth - popupWidth < endPosition.left ? endPosition.left : longitudinalX + viewWidth - popupWidth;
            if(params.leftMargin + contentViewWidthSize > maxWidthSize){
                int leftMargin = params.leftMargin - (params.leftMargin + contentViewWidthSize - maxWidthSize);
                params.leftMargin = leftMargin < 0 ? 0: leftMargin;
            }
        }
        popupWindowView.setLayoutParams(params);
    }

    private void showLongitudinalView(View longitudinalView){

        mlongitudinalView = longitudinalView;
        mlongitudinalView.getViewTreeObserver().addOnScrollChangedListener(this);
    }

    private void hideLongitudinalView(){

        if(mlongitudinalView != null){

            mlongitudinalView.getViewTreeObserver().removeOnScrollChangedListener(this);
        }
        mlongitudinalView = null;
    }

    private int getCorrectionHeight(View view){
        int height = 0;
        Context context = view.getContext();
        if(context instanceof Activity && mBuilder.isFitsSystemWindows()){
            Activity activity = (Activity) context;
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            int vis = decorView.getSystemUiVisibility();
            if(vis != 0){
                return -view.getMeasuredHeight();
            }
        }
        if(context instanceof AppCompatActivity){
            ViewGroup decorView = ((AppCompatActivity)context).findViewById(android.R.id.content);
            if( decorView.getParent() == null ||
                    !(decorView.getParent() instanceof ViewGroup)){

                return height;
            }
            ViewGroup viewGroup = (ViewGroup) decorView.getParent();
            for(int i = 0 ; i < decorView.getChildCount() ; i++){
                if(decorView != viewGroup.getChildAt(i)){
                    height += viewGroup.getChildAt(i).getMeasuredHeight();
                }
            }

        }
        return height;
    }

    @Override
    public void hidePopupWindow() {

        hidePopupWindow(mHelper.getClass());
    }

    @Override
    public void hidePopupWindow(final Class<?> cls) {
        if (mHelper != null && mHelper.canNotHidePopupWindow()) {
            return;
        }
        if (isPopupWindowShow(mHelper.getClass())) {
            int duration =  mHelper.hidePopupWindowDuration();
            if (duration > 0) {
                this.mHelper.getContentView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDecorWindow.hidePopupWindow(cls);
                        hideLongitudinalView();
                    }
                }, duration);
            } else {
                mDecorWindow.hidePopupWindow(cls);
                hideLongitudinalView();
            }
        }
    }

    @Override
    public ViewGroup getDecorView() {
        return this.mDecorWindow.getDecorView();
    }

    @Override
    public boolean isPopupWindowShow(Class<?> cls) {
        return mDecorWindow.isPopupWindowShow(cls);
    }

    @Override
    public int getViewCurentLevel(Class<?> cls) {
        return mDecorWindow.getViewCurentLevel(cls);
    }

    /**
     * 获取当前控件实际层级
     * @return
     */
    public int getViewCurentLevel(){

        return getViewCurentLevel(mHelper.getClass());
    }

    /**
     * 获取控件设置的层级
     * @return
     */
    public int getViewLevel() {

        return getViewLevel(mHelper.getClass());
    }

    @Override
    public int getViewLevel(Class<?> cls) {
        return mDecorWindow.getViewLevel(cls);
    }

    @Override
    public void setOutsideTouchable(boolean outCanTouch, Class<?> cls) {
        this.mDecorWindow.setOutsideTouchable(outCanTouch,cls);
    }

    @Override
    public void setOutsideClickHide(boolean outClickHide, Class<?> cls) {

        this.mDecorWindow.setOutsideClickHide(outClickHide,cls);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mHelper.onTouchEvent(event);
    }

    public boolean isPopupWindowShow() {
        return isPopupWindowShow(mHelper.getClass());
    }

    private IDecorWindow findDecorChild(Context context) {
        Activity activity = (Activity) context;
        View decorView = activity.findViewById(android.R.id.content);
        Object decorObject = decorView.getTag(R.id.decor_view_window_id);
        if (decorObject == null) {
            return new DecorChildView(context);
        } else {
            return (IDecorWindow) decorObject;
        }
    }

    @Override
    public void onScrollChanged() {
        if(mlongitudinalView == null || !isPopupWindowShow()){
            return;
        }
        int height = getCorrectionHeight(mlongitudinalView);
        final Rect startPosition = new Rect();
        mlongitudinalView.getGlobalVisibleRect(startPosition);
        startPosition.top = startPosition.top - height <= 0 ? 0: startPosition.top - height;
        final View popupWindowView = this.mHelper.attachView(mBuilder, getDecorView(), this);
        if (popupWindowView == null) {
            throw new NullPointerException(this.getClass().getCanonicalName() + " 获取加载的popupWindwoView 为空");
        }
        final int viewWidth = mlongitudinalView.getMeasuredWidth();
        final Rect endPosition = new Rect();
        getDecorView().getGlobalVisibleRect(endPosition);
        endPosition.top = endPosition.top - height <= 0 ? 0: endPosition.top - height;

        WindowEnum windowEnum = mBuilder.getWindowEnum();
        windowEnum = windowEnum == null ? WindowEnum.WINDOW_LEFT : windowEnum;
        final WindowEnum wEnum = windowEnum;
        setGlobalLayout(mlongitudinalView,popupWindowView,startPosition,endPosition,viewWidth,wEnum);
    }
}
