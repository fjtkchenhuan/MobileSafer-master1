package com.example.mobilesafer.service;

import java.util.List;

import com.example.mobilesafer.activity.EnterPwdActivity;
import com.example.mobilesafer.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class WatchDogService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// 临时停止保护的包名
	private String tempStopProtectPackageName;

	private class WatchDogReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction()
					.equals("com.itheima.mobileguard.stopprotect")) {
				// 获取到停止保护的对象

				tempStopProtectPackageName = intent
						.getStringExtra("packageName");
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				tempStopProtectPackageName = null;
				// 让狗休息
				flag = false;
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				// 让狗继续干活
				if (flag == false) {
					startWatchDog();
				}
			}
		}
	}

	// 用于标示看门狗是否工作
	boolean flag = false;
	private ActivityManager manager;
	private AppLockDao dao;
	private WatchDogReceiver receiver;

	@Override
	public void onCreate() {
		super.onCreate();
		manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		dao = new AppLockDao(this);

		// 注册广播接受者

		receiver = new WatchDogReceiver();
		IntentFilter filter = new IntentFilter();
		/**
		 * 三种停止的情况
		 */
		// 停止保护
		filter.addAction("com.itheima.mobileguard.stopprotect");
		// 注册一个锁屏的广播
		/**
		 * 当屏幕锁住的时候。狗就休息 屏幕解锁的时候。让狗活过来
		 */
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(receiver, filter);

		startWatchDog();
	}

	/**
	 * 看门狗逻辑 因为是一个后台动作，所以放到子线程中
	 */
	private void startWatchDog() {
		new Thread() {
			public void run() {
				flag = true;
				while (flag) {
					List<RunningTaskInfo> runningTasks = manager
							.getRunningTasks(1);
					RunningTaskInfo runningTaskInfo = runningTasks.get(0);
					String packageName = runningTaskInfo.topActivity
							.getPackageName();

					if (dao.query(packageName)) {
						if (packageName.equals(tempStopProtectPackageName)) {
							//如果是临时接触保护的程序则不需要再次输入密码
						} else {
							System.out.println("受保护程序");
							Intent i = new Intent(WatchDogService.this,
									EnterPwdActivity.class);
							i.putExtra("packageName", packageName);
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(i);
						}
					}
				}
			};
		}.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		flag = false;
	}

}
