package com.source.sdk.widget.linearityview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.source.sdk.widget.R;
import com.source.sdk.widget.linearityview.adabpter.AbstractLinearityAdapter;


/**
 * @author @Created by yangjian on 2017/8/2. 横向不滑动的scrollView 加入布局
 */

public class LinearityView extends LinearLayout {

	private static final int[] ATTRS = new int[]{android.R.attr.textSize,
												 android.R.attr.textColor};

	private LayoutParams expandedTabLayoutParams;

	private LinearLayout tabsContainer;

	private int tabCount;

	private int selectedPosition = 0;
	private int currentPosition = 0;
	private float currentPositionOffset = 0f;

	private Paint rectPaint;
	private Paint dividerPaint;

	private int indicatorColor = 0xFF666666;
	private int underlineColor = 0x1A000000;
	private int dividerColor = 0x1A000000;

	private int scrollOffset = 52;
	private int indicatorHeight = 8;
	private int indicatorPadding = 0;
	private int underlineHeight = 2;
	private int dividerPadding = 12;
	private int tabPadding = 24;
	private int dividerWidth = 1;
	private boolean pstsIndicatorWidthOfText = false;
	private int lastScrollX = 0;
	private AbstractLinearityAdapter mAdapter;
	/**
	 * 是否显示全部底部线
	 */
	private boolean isShowUnderLine = true;

	private final static float ZERO_F = 0f;

	public LinearityView(Context context) {
		this(context, null);
	}

	public LinearityView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LinearityView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setWillNotDraw(false);
		tabsContainer = new LinearLayout(context);
		tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
		tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(tabsContainer);

		DisplayMetrics dm = getResources().getDisplayMetrics();

		scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
		indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
		underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
		dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
		tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
		dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
		TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
		a.recycle();
		a = context.obtainStyledAttributes(attrs, R.styleable.Widget_gu_orderlist_PagerSlidingTabStrip);
		pstsIndicatorWidthOfText = a.getBoolean(
			R.styleable.Widget_gu_orderlist_PagerSlidingTabStrip_widget_gu_orderlist_pstsIndicatorWidthOfText, false);
		indicatorColor = a.getColor(R.styleable.Widget_gu_orderlist_PagerSlidingTabStrip_widget_gu_orderlist_pstsIndicatorColor,
									indicatorColor);
		underlineColor = a.getColor(R.styleable.Widget_gu_orderlist_PagerSlidingTabStrip_widget_gu_orderlist_pstsUnderlineColor,
									underlineColor);
		dividerColor = a.getColor(R.styleable.Widget_gu_orderlist_PagerSlidingTabStrip_widget_gu_orderlist_pstsDividerColor,
								  dividerColor);
		indicatorHeight = a.getDimensionPixelSize(
			R.styleable.Widget_gu_orderlist_PagerSlidingTabStrip_widget_gu_orderlist_pstsIndicatorHeight, indicatorHeight);
		underlineHeight = a.getDimensionPixelSize(
			R.styleable.Widget_gu_orderlist_PagerSlidingTabStrip_widget_gu_orderlist_pstsUnderlineHeight, underlineHeight);
		indicatorPadding = a.getDimensionPixelSize(
			R.styleable.Widget_gu_orderlist_PagerSlidingTabStrip_widget_gu_orderlist_pstsIndicatorPadding, indicatorPadding);
		dividerPadding = a.getDimensionPixelSize(
			R.styleable.Widget_gu_orderlist_PagerSlidingTabStrip_widget_gu_orderlist_pstsDividerPadding, dividerPadding);
		tabPadding = a.getDimensionPixelSize(
			R.styleable.Widget_gu_orderlist_PagerSlidingTabStrip_widget_gu_orderlist_pstsTabPaddingLeftRight, tabPadding);
		scrollOffset = a.getDimensionPixelSize(
			R.styleable.Widget_gu_orderlist_PagerSlidingTabStrip_widget_gu_orderlist_pstsScrollOffset, scrollOffset);
		isShowUnderLine = a.getBoolean(R.styleable.Widget_gu_orderlist_PagerSlidingTabStrip_widget_gu_orderlist_pstsShowUnderLine,
									   true);
		a.recycle();

		rectPaint = new Paint();
		rectPaint.setAntiAlias(true);
		rectPaint.setStyle(Paint.Style.FILL);

		dividerPaint = new Paint();
		dividerPaint.setAntiAlias(true);
		dividerPaint.setStrokeWidth(dividerWidth);
		expandedTabLayoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
	}

	public void setViewAdapter(AbstractLinearityAdapter adapter) {

		if (adapter == null) {
			throw new IllegalStateException("ScrollAdapter does not have adapter instance.");
		}

		this.mAdapter = adapter;
		notifyDataSetChanged();
	}

	public void notifyDataSetChanged() {
		if (mAdapter == null || mAdapter.getCount() <= 0) {
			return;
		}
		tabsContainer.removeAllViews();
		tabCount = mAdapter.getCount();
		for (int i = 0; i < tabCount; i++) {
			View tab = mAdapter.getContentView(i, this);
			if (tab != null) {
				addTab(i, tab);
			}
		}
		updateTabStyles();
		getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			@Override
			public void onGlobalLayout() {

				getViewTreeObserver().removeOnGlobalLayoutListener(this);
				currentPosition = 0;
				mAdapter.initChildView(LinearityView.this, currentPosition);
				scrollToChild(currentPosition, 0);
				invalidate();
			}
		});

	}

	private void addTab(final int position, View tab) {
		tab.setFocusable(true);
		tab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mAdapter.setCurrentItem(LinearityView.this, position);
				currentPosition = mAdapter.getCurrentItem();
				selectedPosition = position;
				scrollToChild(currentPosition, 0);
				invalidate();
			}
		});

		if (tabPadding > 0) {
			tab.setPadding(tabPadding, 0, tabPadding, 0);
		}
		tabsContainer.addView(tab, position, expandedTabLayoutParams);
	}

	private void updateTabStyles() {

		for (int i = 0; i < tabCount; i++) {
			View v = tabsContainer.getChildAt(i);
			if (v == null) {
				return;
			}
			v.setTag(i);
			v.setSelected(i == selectedPosition);
		}
	}

	public void scrollToChild(int position, int offset) {

		if (tabCount == 0) {
			return;
		}

		int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

		if (position > 0 || offset > 0) {
			newScrollX -= scrollOffset;
		}

		if (newScrollX != lastScrollX) {
			lastScrollX = newScrollX;
			//            scrollTo(newScrollX, 0);
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (isInEditMode() || tabCount == 0) {
			return;
		}

		final int height = getHeight();

		//TODO 【此处置前】draw underline
		if (isShowUnderLine) {
			rectPaint.setColor(underlineColor);
			canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);
		}
		rectPaint.setColor(indicatorColor);
		float lineLeft;
		float lineRight;
		// default: line below current tab
		View currentTab = tabsContainer.getChildAt(currentPosition);
		if (TextView.class.isInstance(currentTab) && pstsIndicatorWidthOfText) {
			TextView textView = (TextView) currentTab;
			Paint paint = textView.getPaint();
			int textWidth = (int) paint.measureText(textView.getText().toString());
			lineLeft = textView.getLeft() + ((textView.getRight() - textView.getLeft() - textWidth) >> 1) - 24;
			lineRight = textView.getRight() - ((textView.getRight() - textView.getLeft() - textWidth) >> 1) + 24;
		} else {
			lineLeft = currentTab.getLeft() + indicatorPadding;
			lineRight = currentTab.getRight() - indicatorPadding;
		}
		// if there is an offset, start interpolating left and right coordinates between current and next tab
		if (currentPositionOffset > ZERO_F && currentPosition < tabCount - 1) {

			View nextTab = tabsContainer.getChildAt(currentPosition + 1);
			float nextTabLeft;
			float nextTabRight;
			if (TextView.class.isInstance(nextTab) && pstsIndicatorWidthOfText) {
				TextView nextTabView = (TextView) nextTab;
				Paint paint = nextTabView.getPaint();
				int textWidth = (int) paint.measureText(nextTabView.getText().toString());
				nextTabLeft =
					nextTabView.getLeft() + ((nextTabView.getRight() - nextTabView.getLeft() - textWidth) >> 1) - 24;
				nextTabRight =
					nextTabView.getRight() - ((nextTabView.getRight() - nextTabView.getLeft() - textWidth) >> 1) + 24;
			} else {
				nextTabLeft = nextTab.getLeft() + indicatorPadding;
				nextTabRight = nextTab.getRight() - indicatorPadding;
			}
			lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
			lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
		}

		canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, rectPaint);

		// draw divider

		dividerPaint.setColor(dividerColor);
		for (int i = 0; i < tabCount - 1; i++) {
			View tab = tabsContainer.getChildAt(i);
			canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
		}
	}

	public void setIndicatorColor(int indicatorColor) {
		this.indicatorColor = indicatorColor;
		invalidate();
	}

	public void setIndicatorColorResource(int resId) {
		this.indicatorColor = ContextCompat.getColor(getContext(), resId);
		invalidate();
	}

	public int getIndicatorColor() {
		return this.indicatorColor;
	}

	public void setIndicatorHeight(int indicatorLineHeightPx) {
		this.indicatorHeight = indicatorLineHeightPx;
		invalidate();
	}

	public int getIndicatorHeight() {
		return indicatorHeight;
	}

	public void setUnderlineColor(int underlineColor) {
		this.underlineColor = underlineColor;
		invalidate();
	}

	public void setUnderlineColorResource(int resId) {
		this.underlineColor = ContextCompat.getColor(getContext(), resId);
		invalidate();
	}

	public int getUnderlineColor() {
		return underlineColor;
	}

	public void setDividerColor(int dividerColor) {
		this.dividerColor = dividerColor;
		invalidate();
	}

	public void setDividerColorResource(int resId) {
		this.dividerColor = ContextCompat.getColor(getContext(), resId);
		invalidate();
	}

	public int getDividerColor() {
		return dividerColor;
	}

	public void setUnderlineHeight(int underlineHeightPx) {
		this.underlineHeight = underlineHeightPx;
		invalidate();
	}

	public int getUnderlineHeight() {
		return underlineHeight;
	}

	@Override
	public void setDividerPadding(int dividerPaddingPx) {
		this.dividerPadding = dividerPaddingPx;
		invalidate();
	}

	@Override
	public int getDividerPadding() {
		return dividerPadding;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		currentPosition = savedState.currentPosition;
		requestLayout();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.currentPosition = currentPosition;
		return savedState;
	}

	static class SavedState extends BaseSavedState {

		int currentPosition;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentPosition = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(currentPosition);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	public ViewGroup getChildParentView() {

		return tabsContainer;
	}
}
