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

	// �����еļ�����������ļ��Ķ������ҵķ���
	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initLayout();
	}

	// ��������Ծ͵����������
	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// ��ȡ����ֵ
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
		// ָ��this����Ϊ���ص�ʱ������������޶���������ָ��This����ָ����layout���ؽ���
		View.inflate(getContext(), R.layout.setting_item_layout, this);

		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvDesc = (TextView) findViewById(R.id.tv_desc);
		cb = (CheckBox) findViewById(R.id.cb_status);
		// ���ñ���
		tvTitle.setText(mtitle);
		tvDesc.setText(mDescOff);
	}

	// �ṩ�������ı�����
	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	public void setDesc(String desc) {
		tvDesc.setText(desc);
	}

	public boolean isChecked() {
		return cb.isChecked();
	}

	// ���ݴ���������Զ�����
	public void setChecked(boolean flag) {
		if (flag) {
			tvDesc.setText(mDescOn);
		} else {
			tvDesc.setText(mDescOff);
		}
		cb.setChecked(flag);
	}
}
