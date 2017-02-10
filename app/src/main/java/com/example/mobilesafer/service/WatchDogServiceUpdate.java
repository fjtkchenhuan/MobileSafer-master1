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
 * ���Ź���������
 * @author admin
 *
 */
public class WatchDogServiceUpdate extends Service {

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
	private List<String> packageNames;

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("WatchDogServiceUpdate is running");
		manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		dao = new AppLockDao(this);
		
		packageNames = dao.findAll();	//����Ѿ���ӵ����ݿ��еĳ������
		
		//���ݹ۲���
		ContentObserver observer = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				packageNames = dao.findAll();
				System.out.println("data changed and update");
			}
		};
		/*
		 * ע��һ���۲���
		 * param1:URI��Ҫ��notifyChangeƥ��
		 * param2:ǰ׺ƥ��
		 * param3:�۲��߶���
		 */
		getContentResolver().registerContentObserver(Uri.parse("content://com.example.mobilesafer.change"), true, observer);

		// ע��㲥������
		receiver = new WatchDogReceiver();
		IntentFilter filter = new IntentFilter();
		/**
		 * ����ֹͣ�����
		 */
		// ֹͣ����
		filter.addAction("com.itheima.mobileguard.stopprotect");
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(receiver, filter);

		//��������߼�
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

				//  if (dao.query(packageName)) {
					if(packageNames.contains(packageName)) {
						if (packageName.equals(tempStopProtectPackageName)) {
							//�������ʱ�Ӵ������ĳ�������Ҫ�ٴ���������
						} else {
							System.out.println("�ܱ�������");
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
