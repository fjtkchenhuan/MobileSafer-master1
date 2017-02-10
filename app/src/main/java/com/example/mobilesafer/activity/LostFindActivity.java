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
		//�ж��Ƿ����ù�
		boolean configed = mSprfs.getBoolean("configed", false);
		if(configed) {
			// ���ù�
			setContentView(R.layout.activity_lost_find);
			//��ȡ�����ļ�����������иı�
			tvSafePhone = (TextView) findViewById(R.id.tv_safephone);
			lock = (ImageView) findViewById(R.id.lock);
			
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			String phone = sp.getString("safe_phone", "");
			boolean isProtect = sp.getBoolean("protect", false);
			//�ı䰲ȫ�������
			tvSafePhone.setText(phone);
			//�ı���ͼƬ,ʹ��api��setImageResource
			if(isProtect) {
//				lock.setImageResource(R.drawable.lock);
				lock.setBackgroundResource(R.drawable.lock);
			} else {
//				lock.setImageResource(R.drawable.unlock);
				lock.setBackgroundResource(R.drawable.unlock);
			}
		} else {
			// ˵��û�����ù�
			//��ת������
			startActivity(new Intent(LostFindActivity.this , Setup1Activity.class));
			
			// һ��Ҫд���finish����Ȼ���·��ؼ��Ϳ�����
			finish();
		}
		
		
	}
	
	public void reSet(View v) {
		startActivity(new Intent(this , Setup1Activity.class));
		finish();
	}
	
}
