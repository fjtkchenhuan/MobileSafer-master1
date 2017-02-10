package com.example.mobilesafer.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class MyBaseAdapter<T> extends BaseAdapter {
	// ×ÊÔ´List
	public List<T> list;
	public Context mcontext;
	
	
	public MyBaseAdapter(List<T> list, Context mcontext) {
		super();
		this.list = list;
		this.mcontext = mcontext;
	}

	public MyBaseAdapter() {}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	abstract public View getView(int position, View convertView, ViewGroup parent);

}
