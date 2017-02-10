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
 * Setting页面的基类，包含了各种滑动操作的函数 用于简化各个页面，增加代码的复用性。
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
					//太慢
					return true;
				}
				if (Math.abs(e1.getRawY() - e2.getRawY()) > 300) {
					//上下幅度太大
					return true;
				}
				if (e1.getRawX() - e2.getRawX() > 100) {
					// 显示下一页
					showNextPage();
				}
				if (e2.getRawX() - e1.getRawX() > 100) {
					// 显示上一页
					showPreviousPage();
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}

	/**
	 * 显示上一页
	 */
	public abstract void showPreviousPage();

	/**
	 * 显示下一页
	 */
	public abstract void showNextPage();

	/**
	 * 单击显示下一页
	 * 
	 * @param v
	 */
	public void next(View v) {
		showNextPage();
	}

	/**
	 * 单击显示上一页
	 * 
	 * @param v
	 */
	public void previous(View v) {
		showPreviousPage();
	}

	/**
	 * 定义滑动事件
	 */
	public boolean onTouchEvent(MotionEvent event) {
		// 将滑动数据交给手势这个类来处理
		mGesture.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

}
