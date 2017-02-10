package com.example.mobilesafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mobilesafer.R;

public class AToolsActivity extends Activity {

	
	/**
	 * �߼�����ҳ��
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}
	
	/**
	 * ��ѯ������
	 * @param v
	 */
	public void queryPhoneNum(View v) {
		Intent i = new Intent(this , AddressActivity.class);
		startActivity(i);
	}
	
	/**
	 * ���ű���
	 */
	public void backUpSms(View v) {
		Intent i = new Intent(this , BackUpSmsActivity.class);
		startActivity(i);
	}
	
	/**
	 *  ������
	 * @param v
	 */
	public void appLock(View v) {
		Intent i = new Intent(this , AppLockActivity.class);
		startActivity(i);
	}
}
