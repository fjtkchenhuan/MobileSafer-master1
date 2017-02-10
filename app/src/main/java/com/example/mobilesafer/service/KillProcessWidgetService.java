package com.example.mobilesafer.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.example.mobilesafer.R;
import com.example.mobilesafer.receiver.MyAppWidget;
import com.example.mobilesafer.utils.SystemInfoUtils;

public class KillProcessWidgetService extends Service {

	private AppWidgetManager widgetManager;
	private Timer timer;
	private TimerTask task;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		System.out.println("onCreate");
		super.onCreate();
		
		widgetManager = AppWidgetManager.getInstance(this);
		
		timer = new Timer();
		task = new TimerTask() {
			
			@Override
			public void run() {
				ComponentName provider = new ComponentName(getApplicationContext(), MyAppWidget.class);
				RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
				
				//获取到进程数
				int processCount = SystemInfoUtils.getProcessCount(KillProcessWidgetService.this);
				views.setTextViewText(R.id.process_count, "当前进程数:" + processCount);
				
				//获取到当前内存大小
				long totalMem = SystemInfoUtils.getTotalMem(KillProcessWidgetService.this);
				long availMem = SystemInfoUtils.getAvailMem(KillProcessWidgetService.this);
				String total = Formatter.formatFileSize(KillProcessWidgetService.this, totalMem);
				String avail = Formatter.formatFileSize(KillProcessWidgetService.this, availMem);
				views.setTextViewText(R.id.process_memory, "当前内存情况:"+ avail +"/" + total);
				
				//添加监听事件
				Intent intent = new Intent();
				intent.setAction("com.dwb.mobilesafer");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(KillProcessWidgetService.this, 0, intent, 0);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				
				widgetManager.updateAppWidget(provider, views);
			}
		};
		
		timer.schedule(task, 0 , 5000);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(timer != null ) {
			timer.cancel();
			timer = null;
			task = null;
		}
	}

}
