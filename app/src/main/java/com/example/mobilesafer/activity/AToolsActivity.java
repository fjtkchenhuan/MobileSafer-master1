package com.example.mobilesafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mobilesafer.R;

public class AToolsActivity extends Activity {

	
	/**
	 * 高级工具页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}
	
	/**
	 * 查询归属地
	 * @param v
	 */
	public void queryPhoneNum(View v) {
		Intent i = new Intent(this , AddressActivity.class);
		startActivity(i);
	}
	
	/**
	 * 短信备份
	 */
	public void backUpSms(View v) {
		Intent i = new Intent(this , BackUpSmsActivity.class);
		startActivity(i);
	}
	
	/**
	 *  程序锁
	 * @param v
	 */
	public void appLock(View v) {
		Intent i = new Intent(this , AppLockActivity.class);
		startActivity(i);
	}
}
