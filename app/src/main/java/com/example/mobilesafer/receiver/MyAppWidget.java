package com.example.mobilesafer.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.example.mobilesafer.service.KillProcessWidgetService;
 
public class MyAppWidget extends AppWidgetProvider {

	private Intent i;

	/*
	 * 第一次生成控件的时候调用
	 */
	@Override
	public void onEnabled(Context context) {
		System.out.println("onEnabled");
		super.onEnabled(context);
		i = new Intent(context , KillProcessWidgetService.class);
		context.startService(i);
	}

	/*
	 * 当所有的控件都被删除的时候调用
	 */
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		context.stopService(i);
	}

	

}
