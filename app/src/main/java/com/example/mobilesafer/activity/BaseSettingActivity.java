package com.example.mobilesafer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

/**
 * Settingҳ��Ļ��࣬�����˸��ֻ��������ĺ��� ���ڼ򻯸���ҳ�棬���Ӵ���ĸ����ԡ�
 * 
 * @author admin
 *
 */
public abstract class BaseSettingActivity extends Activity {
	private GestureDetector mGesture;
	public SharedPreferences sp;
	public Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		mGesture = new GestureDetector(this, new SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if(Math.abs(velocityX) < 100) {
					//̫��
					return true;
				}
				if (Math.abs(e1.getRawY() - e2.getRawY()) > 300) {
					//���·���̫��
					return true;
				}
				if (e1.getRawX() - e2.getRawX() > 100) {
					// ��ʾ��һҳ
					showNextPage();
				}
				if (e2.getRawX() - e1.getRawX() > 100) {
					// ��ʾ��һҳ
					showPreviousPage();
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}

	/**
	 * ��ʾ��һҳ
	 */
	public abstract void showPreviousPage();

	/**
	 * ��ʾ��һҳ
	 */
	public abstract void showNextPage();

	/**
	 * ������ʾ��һҳ
	 * 
	 * @param v
	 */
	public void next(View v) {
		showNextPage();
	}

	/**
	 * ������ʾ��һҳ
	 * 
	 * @param v
	 */
	public void previous(View v) {
		showPreviousPage();
	}

	/**
	 * ���廬���¼�
	 */
	public boolean onTouchEvent(MotionEvent event) {
		// ���������ݽ������������������
		mGesture.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

}
