package com.beyole.view;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class MyHorizontalScrollView extends HorizontalScrollView implements OnClickListener {

	/**
	 * 图片滚动时的接口
	 * 
	 * @author Iceberg
	 * 
	 */
	public interface CurrentImageChangedListener {
		void currentImageChangedListener(int position, View viewIndicator);
	}

	/**
	 * 条目被点击时的回调接口
	 * 
	 * @author Iceberg
	 * 
	 */
	public interface OnItemClickListener {
		void click(View view, int pos);
	}

	private CurrentImageChangedListener mListener;
	private OnItemClickListener mItemClickListener;
	private static final String TAG = "MyHorizontalScrollView";

	// HorizontalScrollView中的LinearLayout
	private LinearLayout mContainer;
	// 子元素的宽度
	private int mChildWidth;
	// 子元素的高度
	private int mChildHeight;
	// 当前最后一张图片的Index
	private int mCurrentIndex;
	// 当前第一张图片的下标
	private int mFirstIndex;
	// 当前第一个view
	private View mFirstView;
	// 适配器
	private HorizontalScrollViewAdapter mAdapter;
	// 每屏幕最多显示的个数
	private int mOneScreenCount;
	// 屏幕宽度
	private int mScreenWidth;
	// 保存view与位置的键值对
	private Map<View, Integer> mViewPos = new HashMap<View, Integer>();

	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获得屏幕的宽度
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		mScreenWidth = metrics.widthPixels;
	}

	/**
	 * 在测量方法中获取LinearLayout容器
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mContainer = (LinearLayout) getChildAt(0);
	}

	/**
	 * 加载下一张图片
	 */
	protected void loadNextImg() {
		// 数组边界值计算
		if (mCurrentIndex == mAdapter.getCount() - 1) {
			return;
		}
		// 移除第一张图片，水平滚动位置为0
		scrollTo(0, 0);
		mViewPos.remove(mContainer.getChildAt(0));
		mContainer.removeViewAt(0);
		// 获取下一张图片，并且设置点击事件，且加入容器
		View view = mAdapter.getView(++mCurrentIndex, null, mContainer);
		view.setOnClickListener(this);
		mContainer.addView(view);
		mViewPos.put(view, mCurrentIndex);

		// 当前第一张图片下标
		mFirstIndex++;
		// 如果设置了滚动监听则触发
		if (mListener != null) {
			notifyCurrentImageChanged();
		}
	}

	public void loadPreImage() {
		// 如果当前已经是第一张了，则返回
		if (mFirstIndex == 0) {
			return;
		}
		// 获得当前应该显示为第一张的下标
		int index = mCurrentIndex - mOneScreenCount;
		if (index >= 0) {
			// 移除最后一张
			int oldViewPos = mContainer.getChildCount() - 1;
			mViewPos.remove(mContainer.getChildAt(oldViewPos));
			mContainer.removeViewAt(oldViewPos);

			// 将此view放入第一个位置

			View view = mAdapter.getView(index, null, mContainer);
			mViewPos.put(view, index);
			mContainer.addView(view);
			view.setOnClickListener(this);
			// 水平滚动位置向左移动view的宽度个像素
			scrollTo(mChildWidth, 0);
			mCurrentIndex--;
			mFirstIndex--;
			// 回调
			if (mListener != null) {
				notifyCurrentImageChanged();
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (mItemClickListener != null) {
			for (int i = 0; i < mContainer.getChildCount(); i++) {
				mContainer.getChildAt(i).setBackgroundColor(Color.WHITE);
			}
			mItemClickListener.click(v, mViewPos.get(v));
		}
	}

	/**
	 * 滑动时的回调
	 */
	public void notifyCurrentImageChanged() {
		// 先清除所有的背景色，点击时会设置蓝色
		for (int i = 0; i < mContainer.getChildCount(); i++) {
			mContainer.getChildAt(i).setBackgroundColor(Color.WHITE);
		}
		mListener.currentImageChangedListener(mFirstIndex, mContainer.getChildAt(0));
	}

	/**
	 * 初始化数据，设置数据适配器
	 * 
	 * @param mAdapter
	 */
	public void initDatas(HorizontalScrollViewAdapter mAdapter) {
		this.mAdapter = mAdapter;
		mContainer = (LinearLayout) getChildAt(0);
		// 获得适配器中的第一个view
		final View view = mAdapter.getView(0, null, mContainer);
		mContainer.addView(view);
		// 强制计算当前view的宽度和高度
		if (mChildWidth == 0 && mChildHeight == 0) {
			int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			view.measure(w, h);
			mChildWidth = view.getMeasuredWidth();
			mChildHeight = view.getMeasuredHeight();
			// 计算每次加载多少个view
			mOneScreenCount = mScreenWidth / mChildWidth + 2;
		}
		// 初始化第一屏的元素
		initFirstScreenChild(mOneScreenCount);
	}

	private void initFirstScreenChild(int mOneScreenCount2) {
		mContainer = (LinearLayout) getChildAt(0);
		mContainer.removeAllViews();
		mViewPos.clear();
		for (int i = 0; i < mOneScreenCount2; i++) {
			View view = mAdapter.getView(i, null, mContainer);
			view.setOnClickListener(this);
			mContainer.addView(view);
			mViewPos.put(view, i);
			mCurrentIndex = i;
		}
		if (mListener != null) {
			notifyCurrentImageChanged();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			int scrollX = getScrollX();
			// 如果当前scrollx大于view的宽度，则加载下一张，移除第一张
			if (scrollX >= mChildWidth) {
				loadNextImg();
			}
			// 如果当前scrollX==0，往前设置一张，移除最后一张
			if (scrollX == 0) {
				loadPreImage();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	public void setOnItemClickListener(OnItemClickListener clickListener) {
		this.mItemClickListener = clickListener;
	}

	public void setCurrentImageChangedListener(CurrentImageChangedListener changedListener) {
		this.mListener = changedListener;
	}
}
