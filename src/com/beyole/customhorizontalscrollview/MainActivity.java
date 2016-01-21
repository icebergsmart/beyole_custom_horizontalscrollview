package com.beyole.customhorizontalscrollview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.beyole.view.HorizontalScrollViewAdapter;
import com.beyole.view.MyHorizontalScrollView;
import com.beyole.view.MyHorizontalScrollView.CurrentImageChangedListener;
import com.beyole.view.MyHorizontalScrollView.OnItemClickListener;

public class MainActivity extends Activity {

	private MyHorizontalScrollView horizontalScrollView;
	private HorizontalScrollViewAdapter adapter;
	private ImageView imageView;
	private List<Integer> mDatas = new ArrayList<Integer>(Arrays.asList(R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h, R.drawable.l));

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		imageView = (ImageView) findViewById(R.id.id_content);
		horizontalScrollView = (MyHorizontalScrollView) findViewById(R.id.id_horizontalScrollview);
		adapter = new HorizontalScrollViewAdapter(this, mDatas);
		horizontalScrollView.setCurrentImageChangedListener(new CurrentImageChangedListener() {

			@Override
			public void currentImageChangedListener(int position, View viewIndicator) {
				imageView.setImageResource(mDatas.get(position));
				viewIndicator.setBackgroundColor(Color.parseColor("#AA024DA4"));
			}
		});
		horizontalScrollView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void click(View view, int pos) {
				imageView.setImageResource(mDatas.get(pos));
				view.setBackgroundColor(Color.parseColor("#AA024DA4"));
			}
		});
		horizontalScrollView.initDatas(adapter);
	}

}
