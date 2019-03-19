package com.source.sdk.widget.window.decorchild;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.source.sdk.widget.R;
import com.source.sdk.widget.window.IDecorWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by yangjian on 2019/3/6.
 */

public class DecorChildView extends RelativeLayout implements IDecorWindow {

    private ViewGroup mDecorView;

    private Map<Class<?>, DecorChildRecord> mapViews = new HashMap<>();

    public DecorChildView(Context context) {
        super(context);
        this.mDecorView = ((Activity) context).findViewById(android.R.id.content);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        setLayoutParams(params);
        setFitsSystemWindows(true);
        this.mDecorView.addView(this, mDecorView.getChildCount());
        this.mDecorView.setTag(R.id.decor_view_window_id, this);
        this.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    @Override
    public void attach(IDecorWindow popupWindow) {

        popupWindow.attach(this);
    }

    @Override
    public void showPopupWindow(View view, Class<?> cls, int level,IDecorWindow decorWindow) {

        if (isPopupWindowShow(cls)) {
            return;
        }
        checkDecorView();
        if (mapViews.containsKey(cls)) {

            DecorChildRecord record = mapViews.get(cls);
            removeView(record.mDecorChildView);
        }
        if (view.getParent() != null) {
            ViewGroup parentView = (ViewGroup) view.getParent();
            parentView.removeView(view);
        }
        ViewGroup.LayoutParams viewGroupParams = view.getLayoutParams();

        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (viewGroupParams != null) {
            width = viewGroupParams.width;
            height = viewGroupParams.height;
        }
        LayoutParams params = new LayoutParams(
                width,
                height);
        view.setLayoutParams(params);
        DecorChildRecord record = new DecorChildRecord();
        record.mLevel = level;
        record.mDecorChildView = view;
        record.mCls = cls;
        record.decorWindow = decorWindow;
        int childSize = getChildCount();
        if (childSize <= 0) {
            level = 0;
        } else {
            for (int i = childSize - 1; i >= 0; i--) {
                View lastView = getChildAt(i);
                DecorChildRecord lastChildRecord = (DecorChildRecord) lastView.getTag(R.id.decor_view_child_id);
                if (lastChildRecord.isLevelLess(record)) {
                    level = i + 1;
                    break;
                }
                if (i == 0) {
                    level = 0;
                }
            }
        }
        if (level > getChildCount()) {
            level = getChildCount();
        }
        view.setTag(R.id.decor_view_child_id, record);
        addView(view, level);
        mapViews.put(cls, record);
    }

    private void checkDecorView() {
        if (this.mDecorView.indexOfChild(this) == -1) {
            this.mDecorView.addView(this, mDecorView.getChildCount());
        } else if (this.mDecorView.indexOfChild(this) != mDecorView.getChildCount() - 1) {
            this.mDecorView.removeView(this);
            this.mDecorView.addView(this, mDecorView.getChildCount());
        }
    }

    @Override
    public void hidePopupWindow(Class<?> cls) {

        if (!isPopupWindowShow(cls)) {
            return;
        }
        DecorChildRecord record = mapViews.get(cls);
        removeView(record.mDecorChildView);
        mapViews.remove(record);
    }

    @Override
    public void hidePopupWindow() {

    }

    @Override
    public ViewGroup getDecorView() {
        return this;
    }

    @Override
    public boolean isPopupWindowShow(Class<?> cls) {

        if (!mapViews.containsKey(cls)) {
            return false;
        }
        DecorChildRecord record = mapViews.get(cls);
        if (indexOfChild(record.mDecorChildView) == -1) {
            mapViews.remove(cls);
            return false;
        }
        return true;
    }

    @Override
    public int getViewCurentLevel(Class<?> cls) {
        if (mapViews.containsKey(cls)) {
            DecorChildRecord record = mapViews.get(cls);
            return indexOfChild(record.mDecorChildView);
        }
        return -1;
    }

    @Override
    public int getViewLevel(Class<?> cls) {
        if (mapViews.containsKey(cls)) {
            DecorChildRecord record = mapViews.get(cls);
            return record.mLevel;
        }
        return -1;
    }

    @Override
    public void setOutsideTouchable(boolean outCanTouch, Class<?> cls) {

        if (mapViews.containsKey(cls)) {
            DecorChildRecord record = mapViews.get(cls);
            record.isOutsideCanTouch = outCanTouch;
        }
    }

    @Override
    public void setOutsideClickHide(boolean outClickHide, Class<?> cls) {
        if (mapViews.containsKey(cls)) {
            DecorChildRecord record = mapViews.get(cls);
            record.isOutsideClickHide = outClickHide;
        }
    }

    private boolean isOutsizeCanTouch(MotionEvent event) {
        boolean isOutsizeCanTouch = true;
        Map<Class<?>, DecorChildRecord> defaultMap = new HashMap<>();
        defaultMap.putAll(mapViews);
        ListIterator<Map.Entry<Class<?>, DecorChildRecord>> listIterator =
                new ArrayList<>(defaultMap.entrySet()).listIterator(defaultMap.size());
        while(listIterator.hasPrevious()) {
            Map.Entry<Class<?>, DecorChildRecord> entry= listIterator.previous();
            DecorChildRecord record = entry.getValue();
            if(record.isOutsideClickHide && isPopupWindowShow(record.mCls)){
                record.decorWindow.hidePopupWindow();
                isOutsizeCanTouch = false;
            }
            if(!isOutsideCanTouchable(event,record) && isOutsizeCanTouch){
                isOutsizeCanTouch = false;
            }
        }
        return isOutsizeCanTouch;
    }

//    private boolean isOutsideCanTouchable(MotionEvent event) {
//        ListIterator<Map.Entry<Class<?>, DecorChildRecord>> listIterator =
//                new ArrayList<>(mapViews.entrySet()).listIterator(mapViews.size());
//        while(listIterator.hasPrevious()) {
//            Map.Entry<Class<?>, DecorChildRecord> entry= listIterator.previous();
//            DecorChildRecord record = entry.getValue();
//            if(onChildTouchEvent(event,record) && isPopupWindowShow(record.mCls)){
//                return true;
//            }
//            if(!record.isOutsideCanTouch && isPopupWindowShow(record.mCls)){
//                return false;
//            }
//        }
//        return true;
//    }

    private boolean isOutsideCanTouchable(MotionEvent event,DecorChildRecord record) {

        if(onChildTouchEvent(event,record) && isPopupWindowShow(record.mCls)){
            return false;
        }
        if(!record.isOutsideCanTouch && isPopupWindowShow(record.mCls)){
            return false;
        }
        return true;
    }

    private boolean onChildTouchEvent(MotionEvent event,DecorChildRecord record){

        if(record.decorWindow == null){
           throw new NullPointerException("没有存入decorWindow 信息");
        }
        return record.decorWindow.onTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean isTouch = super.onTouchEvent(event);
        if(!isOutsizeCanTouch(event)){
            return true;
        }
//        if(!isOutsideCanTouchable(event)){
//            return true;
//        }
        return isTouch;
    }

    class DecorChildRecord {

        View mDecorChildView;

        int mLevel = -1;

        Class<?> mCls;

        boolean isOutsideCanTouch = true;

        boolean isOutsideClickHide = false;

        IDecorWindow decorWindow;

        boolean isLevelLess(DecorChildRecord record) {

            if (this.mLevel < 0 ||
                    this.mLevel < record.mLevel) {
                return true;
            }
            if (this.mLevel == record.mLevel) {

                throw new NullPointerException(record.mCls.getSimpleName() + " 设置的level : " + record.mLevel

                        + "与 " + this.mCls.getSimpleName() + "设置的 level ： " + this.mLevel + "一样"
                );
            }
            return false;
        }
    }
}
