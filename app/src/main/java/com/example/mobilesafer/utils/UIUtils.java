package com.example.mobilesafer.utils;

import android.app.Activity;
import android.widget.Toast;

public class UIUtils {
	/**
	 * 方便与showToast，线程安全
	 * @param context 传入的是一个activity
	 * @param str
	 */
	public static void showToast(final Activity context , final String str) {
		//判断线程
		if("main".equals(Thread.currentThread().getName())) {
			Toast.makeText(context, str, 0).show();
		} else {
			//调用acitivy中的runonuithread
			context.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(context, str, 0).show();
				}
			});
		}
	}
}
