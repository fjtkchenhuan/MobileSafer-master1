package com.example.mobilesafer.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.example.mobilesafer.bean.TaskInfo;
import com.example.mobilesafer.engine.TaskInfoParser;

public class KillProcessService extends Service {

	private ScreenLockReceiver receiver;
	private ActivityManager manager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	@Override
	public void onCreate() {
		super.onCreate();
		manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		receiver = new ScreenLockReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
	private class ScreenLockReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			//清除进程
			List<TaskInfo> taskInfos = TaskInfoParser.getTaskInfos(KillProcessService.this);
			for(TaskInfo info : taskInfos) {
				//防止杀掉自己
				if(info.getPackageName().equals(getPackageName())) {
					continue;
				} else {
					manager.killBackgroundProcesses(info.getPackageName());
				}
			}
		}
		
	}
}
