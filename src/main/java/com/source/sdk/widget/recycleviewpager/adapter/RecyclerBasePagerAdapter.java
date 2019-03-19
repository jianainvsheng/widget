package com.source.sdk.widget.recycleviewpager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

import com.source.sdk.widget.R;
import com.source.sdk.widget.recycleviewpager.holder.RecyclerBasePagerHolder;

/**
 * Created by yangjian on 2017/9/7.
 */

public abstract class RecyclerBasePagerAdapter<D,T extends RecyclerBasePagerHolder<D>> extends RecyclerView.Adapter<T>{

	private List<D> mData;

	private Context mContext;

	private OnItemClick<T> ml;
	public RecyclerBasePagerAdapter(Context context) {

		this(context,null);
	}

	public RecyclerBasePagerAdapter(Context context, List<D> mData) {

		this.mContext = context;
		init();
		setData(mData);
	}

	private void init(){

		mData = new ArrayList<>();
	}

	/**
	 * 设置数据
	 * @param mData
	 */
	public void setData(List<D> mData){

		if(mData == null || mData.size() <= 0)
			return;
		this.mData.clear();
		this.mData.addAll(mData);
		notifyDataSetChanged();
	}

	/**
	 * 增加元素
	 * @param mData
	 */
	public void addData(List<D> mData){

		if(mData == null || mData.size() <= 0)
			return;
		this.mData.addAll(mData);
		notifyDataSetChanged();
	}

	@Override
	public T onCreateViewHolder(ViewGroup parent, int viewType) {

		final View view = getContentView(parent,viewType);

		if(view != null){

			final T t = getViewHolder(view,viewType);
			view.setTag(R.id.widget_product_detail_recycler_tiem_view_id, t);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onItemClick(view,t);
				}
			});
			return t;
		}
		return null;
	}

	public Context getContext(){
		return mContext;
	}

	public List<D> getData(){

		return mData;
	}

	@Override
	public int getItemCount() {
		return mData == null ? 0 : mData.size();
	}

	public void onItemClick(View itemView, T dataHolder){

		if(ml != null){
			ml.onItemClick(this,itemView,dataHolder);
		}
	}

	public void setOnItemClick(OnItemClick<T> l){

		this.ml = l;
	}
	/**
	 * 不用关注重复利用的，recyclerview有处理
	 * @param parent
	 * @param viewType
	 * @return
	 */
	public abstract View getContentView(ViewGroup parent, int viewType);

	/**
	 * 不用关注重复利用的，recyclerview有处理
	 * @param view
	 * @param viewType
	 * @return
	 */
	public abstract T getViewHolder(View view, int viewType);

	public interface OnItemClick <T>{

		void onItemClick(RecyclerBasePagerAdapter adapter, View itemView, T dataHolder);
	}
}
