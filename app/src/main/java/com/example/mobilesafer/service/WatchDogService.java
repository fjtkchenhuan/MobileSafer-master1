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

	// ��ʱֹͣ�����İ���
	private String tempStopProtectPackageName;

	private class WatchDogReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction()
					.equals("com.itheima.mobileguard.stopprotect")) {
				// ��ȡ��ֹͣ�����Ķ���

				tempStopProtectPackageName = intent
						.getStringExtra("packageName");
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				tempStopProtectPackageName = null;
				// �ù���Ϣ
				flag = false;
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				// �ù������ɻ�
				if (flag == false) {
					startWatchDog();
				}
			}
		}
	}

	// ���ڱ�ʾ���Ź��Ƿ���
	boolean flag = false;
	private ActivityManager manager;
	private AppLockDao dao;
	private WatchDogReceiver receiver;

	@Override
	public void onCreate() {
		super.onCreate();
		manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		dao = new AppLockDao(this);

		// ע��㲥������

		receiver = new WatchDogReceiver();
		IntentFilter filter = new IntentFilter();
		/**
		 * ����ֹͣ�����
		 */
		// ֹͣ����
		filter.addAction("com.itheima.mobileguard.stopprotect");
		// ע��һ�������Ĺ㲥
		/**
		 * ����Ļ��ס��ʱ�򡣹�����Ϣ ��Ļ������ʱ���ù������
		 */
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(receiver, filter);

		startWatchDog();
	}

	/**
	 * ���Ź��߼� ��Ϊ��һ����̨���������Էŵ����߳���
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
							//�������ʱ�Ӵ������ĳ�������Ҫ�ٴ���������
						} else {
							System.out.println("�ܱ�������");
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
