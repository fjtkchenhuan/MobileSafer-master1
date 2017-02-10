package com.example.mobilesafer.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.mobilesafer.R;
import com.example.mobilesafer.fragment.LockFragment;
import com.example.mobilesafer.fragment.UnLockFragment;

public class AppLockActivity extends FragmentActivity implements
		OnClickListener {
	private TextView tv_top_unlock;
	private TextView tv_top_lock;
	private Resources resources;
	private FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		fragmentManager = getSupportFragmentManager();
		resources = getResources();
		initUI();
		
		//����unlock
		UnLockFragment unlock = new UnLockFragment();
		fragmentManager.beginTransaction().replace(R.id.fl_content, unlock).commit();
	}

	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		setContentView(R.layout.activity_app_lock);
		
		tv_top_unlock = (TextView) findViewById(R.id.tv_top_unlock);
		tv_top_lock = (TextView) findViewById(R.id.tv_top_lock);

		// ��ӵ����¼�
		tv_top_unlock.setOnClickListener(this);
		tv_top_lock.setOnClickListener(this);
	}

	/**
	 * �����߼�
	 */
	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
			case R.id.tv_top_unlock: {
				//�ı���ʽ
				tv_top_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
				tv_top_unlock.setTextColor(resources.getColor(R.color.white));
				tv_top_lock.setBackgroundResource(R.drawable.tab_right_default);
				tv_top_lock.setTextColor(resources.getColor(R.color.pressedgray));
				
				//������ת
				//����fragment
				UnLockFragment unlock = new UnLockFragment();
				fragmentManager.beginTransaction().replace(R.id.fl_content, unlock).commit();
				break;
			}
			case R.id.tv_top_lock: {
				//�ı���ʽ
				tv_top_unlock.setBackgroundResource(R.drawable.tab_left_default);
				tv_top_unlock.setTextColor(resources.getColor(R.color.pressedgray));
				tv_top_lock.setBackgroundResource(R.drawable.tab_right_pressed);
				tv_top_lock.setTextColor(resources.getColor(R.color.white));
				
				//������ת
				//����fragment
				LockFragment lock = new LockFragment();
				fragmentManager.beginTransaction().replace(R.id.fl_content, lock).commit();
				break;
			}
		}
	}

}
