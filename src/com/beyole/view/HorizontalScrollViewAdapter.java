package com.beyole.view;

import java.util.List;

import com.beyole.customhorizontalscrollview.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class HorizontalScrollViewAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<Integer> mDatas;

	public HorizontalScrollViewAdapter(Context context, List<Integer> mDatas) {
		this.context = context;
		this.mDatas = mDatas;
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return mDatas.size();
	}

	public Object getItem(int position) {
		return mDatas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.activity_index_gallery_item, parent, false);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.id_index_gallery_item_image);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.id_index_gallery_item_text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.imageView.setImageResource(mDatas.get(position));
		viewHolder.textView.setText("Sword" + position);
		return convertView;
	}

	private class ViewHolder {
		ImageView imageView;
		TextView textView;
	}
}
