package com.source.sdk.widget.linearityview.adabpter;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import com.source.sdk.widget.linearityview.LinearityView;

/**
 * @author Created by yangjian on 2017/8/2.
 */

public abstract class AbstractLinearityAdapter<T, V extends LinearityView> {

	private List<T> mData;

	private int mCurrentItem = 0;

	private V v;

	public AbstractLinearityAdapter(V view) {
		this.v = view;
	}

	/**
	 * 获得显示的view
	 * @param position
	 * @param parentView
	 * @return
	 */
	public abstract View getContentView(int position, V parentView);

	public List<T> getData() {
		return mData;
	}

	public int getCount() {

		return mData == null ? 0 : mData.size();
	}

	public void setData(List<T> data){
		if(data == null){
			return;
		}
		if(mData == null){
			this.mData = new ArrayList<>();
		}
		mData.clear();
		mData.addAll(data);
		v.notifyDataSetChanged();
	}
	public void setCurrentItem(V view, int position) {

		this.mCurrentItem = position;
	}

	public int getCurrentItem() {
		return mCurrentItem;
	}

	public Context getContext() {
		return (v == null ? null : v.getContext());
	}

	public void initChildView(V view, int position) {

		this.mCurrentItem = position;
	}

	public View getChildView(int position){

		if(v != null && v.getChildParentView() != null){

			return v.getChildParentView().getChildAt(position);
		}
		return null;
	}
}
