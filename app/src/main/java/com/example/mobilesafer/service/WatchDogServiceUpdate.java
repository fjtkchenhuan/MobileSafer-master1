package com.example.mobilesafer.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.example.mobilesafer.activity.EnterPwdActivity;
import com.example.mobilesafer.db.dao.AppLockDao;

/**
 * 看门狗的升级版
 * @author admin
 *
 */
public class WatchDogServiceUpdate extends Service {

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
	private List<String> packageNames;

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("WatchDogServiceUpdate is running");
		manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		dao = new AppLockDao(this);
		
		packageNames = dao.findAll();	//获得已经添加到数据库中的程序包名
		
		//内容观察者
		ContentObserver observer = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				packageNames = dao.findAll();
				System.out.println("data changed and update");
			}
		};
		/*
		 * 注册一个观察者
		 * param1:URI需要和notifyChange匹配
		 * param2:前缀匹配
		 * param3:观察者对象
		 */
		getContentResolver().registerContentObserver(Uri.parse("content://com.example.mobilesafer.change"), true, observer);

		// 注册广播接受者
		receiver = new WatchDogReceiver();
		IntentFilter filter = new IntentFilter();
		/**
		 * 三种停止的情况
		 */
		// 停止保护
		filter.addAction("com.itheima.mobileguard.stopprotect");
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(receiver, filter);

		//开启监控逻辑
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

				//  if (dao.query(packageName)) {
					if(packageNames.contains(packageName)) {
						if (packageName.equals(tempStopProtectPackageName)) {
							//如果是临时接触保护的程序则不需要再次输入密码
						} else {
							System.out.println("受保护程序");
							Intent i = new Intent(WatchDogServiceUpdate.this,
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
