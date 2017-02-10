package com.example.mobilesafer.view;

import com.example.mobilesafer.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingClickView extends RelativeLayout {

	private TextView tvTitle;
	private TextView tvDesc;
	
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.example.mobilesafer";


	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}
	
	
	
	/*
	 * ��ʼ��
	 */
	public void init(AttributeSet attrs) {
		//���ز���
		View view = View.inflate(getContext(), R.layout.setting_address_style_layout, this);
		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		tvDesc = (TextView) view.findViewById(R.id.tv_desc);
		
		String title = attrs.getAttributeValue(NAMESPACE, "title");
		setTitle(title);
	}
	
	/*
	 * ���ñ���
	 */
	public void setTitle(String title) {
		tvTitle.setText(title);
	}
	
	/*
	 * ��������
	 */
	public void setDesc(String desc) {
		tvDesc.setText(desc);
	}
}
