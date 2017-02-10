package com.example.mobilesafer.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.example.mobilesafer.db.dao.BlackNumberDao;
import com.example.mobilesafer.receiver.CallSafeReceiver;

public class CallSafeService extends Service {

	public CallSafeReceiver receiver;
	private BlackNumberDao dao;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		/*
		 * 拦截短信
		 */
		blockSms();
		/*
		 * 拦截电话
		 */
		blockTelephone();

	}

	/**
	 * 拦截电话
	 */
	private void blockTelephone() {

		dao = new BlackNumberDao(this);

		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		MyListener listener = new MyListener();
		// 监听
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

	}

	private class MyListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			// 电话响起
			case TelephonyManager.CALL_STATE_RINGING:

				String mod = dao.findMode(incomingNumber);
				if ("1".equals(mod) || "2".equals(mod)) {
					System.out.println("黑名单号码，截断");
					// 截断电话
					stopCall();
				}

				// 清除通话记录
				Uri uri = Uri.parse("content://call_log/calls");
				// 监听变化
				getContentResolver().registerContentObserver(uri, true,
						new MyContentObserver(new Handler(), incomingNumber));

				break;
			}

		}

	}

	/**
	 * 监听内容提供者的数据变化
	 * 
	 * @author admin
	 *
	 */
	private class MyContentObserver extends ContentObserver {
		String incomingNumber;

		/**
		 * Creates a content observer.
		 *
		 * @param handler
		 *            The handler to run {@link #onChange} on, or null if none.
		 * @param incomingNumber
		 */
		public MyContentObserver(Handler handler, String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		// 当数据改变的时候调用的方法
		@Override
		public void onChange(boolean selfChange) {
			// 当有新的记录的时候，就可以把监听器给反注册掉了
			getContentResolver().unregisterContentObserver(this);
			// 删除数据
			deleteCallLog(incomingNumber);

			super.onChange(selfChange);
		}
	}

	/**
	 * 拦截短信
	 */
	private void blockSms() {
		receiver = new CallSafeReceiver();
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(2147483647);
		registerReceiver(receiver, filter);
	}

	/**
	 * 删除通话记录
	 * 
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {

		Uri uri = Uri.parse("content://call_log/calls");

		getContentResolver().delete(uri, "number=?",
				new String[] { incomingNumber });
	}

	/**
	 * 截断号码
	 */
	public void stopCall() {

		try {
			// 通过类加载器加载ServiceManager
			Class<?> clazz = getClassLoader().loadClass(
					"android.os.ServiceManager");
			// 通过反射得到当前的方法
			Method method = clazz.getDeclaredMethod("getService", String.class);

			// 如果方法是static的就可以传入null
			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);

			ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);

			iTelephony.endCall();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
