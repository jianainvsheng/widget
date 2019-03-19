package com.source.sdk.widget.search.array.base.model;

import java.io.Serializable;

/**
 * Created by yangjian on 2018/8/7.
 */

public class BaseTabModel implements Serializable {

    private int mType;

    public int getType(){

        return mType;
    }

    public void setType(int type){

        this.mType = type;
    }
}
