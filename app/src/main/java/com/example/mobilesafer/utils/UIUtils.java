package com.example.mobilesafer.utils;

import android.app.Activity;
import android.widget.Toast;

public class UIUtils {
	/**
	 * ������showToast���̰߳�ȫ
	 * @param context �������һ��activity
	 * @param str
	 */
	public static void showToast(final Activity context , final String str) {
		//�ж��߳�
		if("main".equals(Thread.currentThread().getName())) {
			Toast.makeText(context, str, 0).show();
		} else {
			//����acitivy�е�runonuithread
			context.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(context, str, 0).show();
				}
			});
		}
	}
}
