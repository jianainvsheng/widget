package com.source.sdk.widget.bezie.value;

import android.content.Context;
import android.graphics.PointF;
import android.view.View;

/**
 * Created by yangjian on 2018/10/15.
 */

public abstract class BaseBezierValue<D extends View> {

    private D mAddView;

    public BaseBezierValue(D addView){

        this.mAddView = addView;
    }

    public abstract void onBezieValue(PointF pointF);

    public D getAddView() {
        return mAddView;
    }

    public Context getContext(){

        return mAddView.getContext();
    }
}
