package com.example.mobilesafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobilesafer.R;

public class LostFindActivity extends Activity{

	private TextView tvSafePhone;
	private ImageView lock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences mSprfs = getSharedPreferences("config", MODE_PRIVATE);
		//判断是否设置过
		boolean configed = mSprfs.getBoolean("configed", false);
		if(configed) {
			// 设置过
			setContentView(R.layout.activity_lost_find);
			//读取配置文件来对组件进行改变
			tvSafePhone = (TextView) findViewById(R.id.tv_safephone);
			lock = (ImageView) findViewById(R.id.lock);
			
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			String phone = sp.getString("safe_phone", "");
			boolean isProtect = sp.getBoolean("protect", false);
			//改变安全号码组件
			tvSafePhone.setText(phone);
			//改变锁图片,使用api：setImageResource
			if(isProtect) {
//				lock.setImageResource(R.drawable.lock);
				lock.setBackgroundResource(R.drawable.lock);
			} else {
//				lock.setImageResource(R.drawable.unlock);
				lock.setBackgroundResource(R.drawable.unlock);
			}
		} else {
			// 说明没有设置过
			//跳转到设置
			startActivity(new Intent(LostFindActivity.this , Setup1Activity.class));
			
			// 一定要写这个finish，不然按下返回键就卡死了
			finish();
		}
		
		
	}
	
	public void reSet(View v) {
		startActivity(new Intent(this , Setup1Activity.class));
		finish();
	}
	
}
