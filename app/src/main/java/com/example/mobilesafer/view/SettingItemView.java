package com.example.mobilesafer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobilesafer.R;

public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.example.mobilesafer";
	private TextView tvTitle;
	private TextView tvDesc;
	private CheckBox cb;
	private String mDescOn;
	private String mDescOff;
	private String mtitle;

	// 让所有的加载这个布局文件的都调用我的方法
	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initLayout();
	}

	// 如果有属性就调用这个方法
	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取属性值
		mtitle = attrs.getAttributeValue(NAMESPACE, "title");
		mDescOff = attrs.getAttributeValue(NAMESPACE, "desc_off");
		mDescOn = attrs.getAttributeValue(NAMESPACE, "desc_on");
		initLayout();
	}

	public SettingItemView(Context context) {
		super(context);
		initLayout();
	}

	public void initLayout() {
		// 指定this，因为加载的时候加载这个类的限定名，所以指定This，就指定将layout加载进来
		View.inflate(getContext(), R.layout.setting_item_layout, this);

		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvDesc = (TextView) findViewById(R.id.tv_desc);
		cb = (CheckBox) findViewById(R.id.cb_status);
		// 设置标题
		tvTitle.setText(mtitle);
		tvDesc.setText(mDescOff);
	}

	// 提供方法来改变内容
	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	public void setDesc(String desc) {
		tvDesc.setText(desc);
	}

	public boolean isChecked() {
		return cb.isChecked();
	}

	// 根据传入的数据自动更改
	public void setChecked(boolean flag) {
		if (flag) {
			tvDesc.setText(mDescOn);
		} else {
			tvDesc.setText(mDescOff);
		}
		cb.setChecked(flag);
	}
}
