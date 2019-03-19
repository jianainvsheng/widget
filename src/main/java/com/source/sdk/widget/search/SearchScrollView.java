package com.source.sdk.widget.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.source.sdk.widget.search.array.IArray;
import com.source.sdk.widget.search.layout.SearchLayout;

/**
 * Created by yangjian on 2018/8/12.
 */

public class SearchScrollView extends HorizontalScrollView {

    private SearchLayout mSearchLayout;

    public SearchScrollView(Context context) {
        this(context,null);
    }

    public SearchScrollView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SearchScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        removeAllViews();
        mSearchLayout = new SearchLayout(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mSearchLayout.setLayoutParams(params);
        addView(mSearchLayout);
    }

    public void setAdapter(IArray adapter){

        mSearchLayout.setAdapter(adapter);
    }

    public SearchLayout getContentView(){

        return mSearchLayout;
    }

    public View getLastView(){

        if(mSearchLayout != null && mSearchLayout.getChildCount() > 0){

            return mSearchLayout.getChildAt(mSearchLayout.getChildCount() - 1);
        }
        return null;
    }
}
