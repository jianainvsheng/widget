package com.source.sdk.widget.recycleviewpager.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by yangjian on 2017/9/7.
 */

public class RecyclerBasePagerHolder<T> extends RecyclerView.ViewHolder{

	private View mContentView;

	private T mData;

	public RecyclerBasePagerHolder(View itemView) {

		super(itemView);
		this.mContentView = itemView;
	}

	public void setData(T data){

		this.mData = data;
	}

	public T getData(){

		return mData;
	}

	public View getContentView(){

		return mContentView;
	}
}
