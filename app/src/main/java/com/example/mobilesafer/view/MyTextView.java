package com.example.mobilesafer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {

	// ��style��ʽ�Ļ����ߴ˷���
	public MyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	//�����Ե�ʱ�������������
	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	// �ô���new����ʱ,�ߴ˷���
	public MyTextView(Context context) {
		super(context);
	}

	
	/**
	 * ��ʾ�����л�ȡ����
	 * 
	 * �����Ҫ����,���ȵ��ô˺����ж��Ƿ��н���,��true�Ļ�,����ƲŻ���Ч�� �������ǲ���ʵ����textview��û�н���,
	 * ���Ƕ�ǿ�Ʒ���true, ���������Ϊ�н���
	 */
	@Override
	public boolean isFocused() {
		return true;
	}
}
