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
	 * ͼƬ����ʱ�Ľӿ�
	 * 
	 * @author Iceberg
	 * 
	 */
	public interface CurrentImageChangedListener {
		void currentImageChangedListener(int position, View viewIndicator);
	}

	/**
	 * ��Ŀ�����ʱ�Ļص��ӿ�
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

	// HorizontalScrollView�е�LinearLayout
	private LinearLayout mContainer;
	// ��Ԫ�صĿ��
	private int mChildWidth;
	// ��Ԫ�صĸ߶�
	private int mChildHeight;
	// ��ǰ���һ��ͼƬ��Index
	private int mCurrentIndex;
	// ��ǰ��һ��ͼƬ���±�
	private int mFirstIndex;
	// ��ǰ��һ��view
	private View mFirstView;
	// ������
	private HorizontalScrollViewAdapter mAdapter;
	// ÿ��Ļ�����ʾ�ĸ���
	private int mOneScreenCount;
	// ��Ļ���
	private int mScreenWidth;
	// ����view��λ�õļ�ֵ��
	private Map<View, Integer> mViewPos = new HashMap<View, Integer>();

	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// �����Ļ�Ŀ��
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		mScreenWidth = metrics.widthPixels;
	}

	/**
	 * �ڲ��������л�ȡLinearLayout����
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mContainer = (LinearLayout) getChildAt(0);
	}

	/**
	 * ������һ��ͼƬ
	 */
	protected void loadNextImg() {
		// ����߽�ֵ����
		if (mCurrentIndex == mAdapter.getCount() - 1) {
			return;
		}
		// �Ƴ���һ��ͼƬ��ˮƽ����λ��Ϊ0
		scrollTo(0, 0);
		mViewPos.remove(mContainer.getChildAt(0));
		mContainer.removeViewAt(0);
		// ��ȡ��һ��ͼƬ���������õ���¼����Ҽ�������
		View view = mAdapter.getView(++mCurrentIndex, null, mContainer);
		view.setOnClickListener(this);
		mContainer.addView(view);
		mViewPos.put(view, mCurrentIndex);

		// ��ǰ��һ��ͼƬ�±�
		mFirstIndex++;
		// ��������˹��������򴥷�
		if (mListener != null) {
			notifyCurrentImageChanged();
		}
	}

	public void loadPreImage() {
		// �����ǰ�Ѿ��ǵ�һ���ˣ��򷵻�
		if (mFirstIndex == 0) {
			return;
		}
		// ��õ�ǰӦ����ʾΪ��һ�ŵ��±�
		int index = mCurrentIndex - mOneScreenCount;
		if (index >= 0) {
			// �Ƴ����һ��
			int oldViewPos = mContainer.getChildCount() - 1;
			mViewPos.remove(mContainer.getChildAt(oldViewPos));
			mContainer.removeViewAt(oldViewPos);

			// ����view�����һ��λ��

			View view = mAdapter.getView(index, null, mContainer);
			mViewPos.put(view, index);
			mContainer.addView(view);
			view.setOnClickListener(this);
			// ˮƽ����λ�������ƶ�view�Ŀ�ȸ�����
			scrollTo(mChildWidth, 0);
			mCurrentIndex--;
			mFirstIndex--;
			// �ص�
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
	 * ����ʱ�Ļص�
	 */
	public void notifyCurrentImageChanged() {
		// ��������еı���ɫ�����ʱ��������ɫ
		for (int i = 0; i < mContainer.getChildCount(); i++) {
			mContainer.getChildAt(i).setBackgroundColor(Color.WHITE);
		}
		mListener.currentImageChangedListener(mFirstIndex, mContainer.getChildAt(0));
	}

	/**
	 * ��ʼ�����ݣ���������������
	 * 
	 * @param mAdapter
	 */
	public void initDatas(HorizontalScrollViewAdapter mAdapter) {
		this.mAdapter = mAdapter;
		mContainer = (LinearLayout) getChildAt(0);
		// ����������еĵ�һ��view
		final View view = mAdapter.getView(0, null, mContainer);
		mContainer.addView(view);
		// ǿ�Ƽ��㵱ǰview�Ŀ�Ⱥ͸߶�
		if (mChildWidth == 0 && mChildHeight == 0) {
			int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			view.measure(w, h);
			mChildWidth = view.getMeasuredWidth();
			mChildHeight = view.getMeasuredHeight();
			// ����ÿ�μ��ض��ٸ�view
			mOneScreenCount = mScreenWidth / mChildWidth + 2;
		}
		// ��ʼ����һ����Ԫ��
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
			// �����ǰscrollx����view�Ŀ�ȣ��������һ�ţ��Ƴ���һ��
			if (scrollX >= mChildWidth) {
				loadNextImg();
			}
			// �����ǰscrollX==0����ǰ����һ�ţ��Ƴ����һ��
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
