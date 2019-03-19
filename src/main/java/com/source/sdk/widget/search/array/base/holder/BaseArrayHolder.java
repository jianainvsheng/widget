package com.source.sdk.widget.search.array.base.holder;

import android.view.View;

/**
 * Created by yangjian on 2018/8/6.
 */

public class BaseArrayHolder<T> extends BaseViewHord<T> {

    private int mType;

    private int mPosition;

    public BaseArrayHolder(View view) {
        super(view);
    }

    public int getType(){

        return mType;
    }

    public void setType(int type){

        this.mType = type;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }
}
