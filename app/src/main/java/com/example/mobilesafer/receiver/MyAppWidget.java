package com.example.mobilesafer.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.example.mobilesafer.service.KillProcessWidgetService;
 
public class MyAppWidget extends AppWidgetProvider {

	private Intent i;

	/*
	 * ��һ�����ɿؼ���ʱ�����
	 */
	@Override
	public void onEnabled(Context context) {
		System.out.println("onEnabled");
		super.onEnabled(context);
		i = new Intent(context , KillProcessWidgetService.class);
		context.startService(i);
	}

	/*
	 * �����еĿؼ�����ɾ����ʱ�����
	 */
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		context.stopService(i);
	}

	

}
