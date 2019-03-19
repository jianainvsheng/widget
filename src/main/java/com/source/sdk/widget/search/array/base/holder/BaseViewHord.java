package com.source.sdk.widget.search.array.base.holder;

import android.content.Context;
import android.view.View;

/**
 * Created by yangjian on 2017/8/8.
 */

public class BaseViewHord<T> {

	private Context mContext;

	private T data;

	private int mPosition;

	private View mContentView;

	public BaseViewHord() {
	}
	public BaseViewHord(View contentView) {
		this(contentView.getContext(),contentView);
	}

	public BaseViewHord(Context context, View contentView) {

		this.mContext = context;
		this.mContentView = contentView;
	}

	public void setData(T data) {

		this.data = data;
	}

	public Context getContext() {
		return mContext;
	}

	public T getData() {
		return data;
	}

	public void setContext(Context context) {
		this.mContext = context;
	}

	public int getPosition() {
		return mPosition;
	}

	public void setPosition(int position) {
		this.mPosition = position;
	}

	public View getContentView() {
		return mContentView;
	}

	public void setContentView(View contentView) {
		this.mContentView = contentView;
	}
}
