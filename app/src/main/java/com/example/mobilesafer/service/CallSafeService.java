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
		 * ���ض���
		 */
		blockSms();
		/*
		 * ���ص绰
		 */
		blockTelephone();

	}

	/**
	 * ���ص绰
	 */
	private void blockTelephone() {

		dao = new BlackNumberDao(this);

		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		MyListener listener = new MyListener();
		// ����
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

	}

	private class MyListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			// �绰����
			case TelephonyManager.CALL_STATE_RINGING:

				String mod = dao.findMode(incomingNumber);
				if ("1".equals(mod) || "2".equals(mod)) {
					System.out.println("���������룬�ض�");
					// �ضϵ绰
					stopCall();
				}

				// ���ͨ����¼
				Uri uri = Uri.parse("content://call_log/calls");
				// �����仯
				getContentResolver().registerContentObserver(uri, true,
						new MyContentObserver(new Handler(), incomingNumber));

				break;
			}

		}

	}

	/**
	 * ���������ṩ�ߵ����ݱ仯
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

		// �����ݸı��ʱ����õķ���
		@Override
		public void onChange(boolean selfChange) {
			// �����µļ�¼��ʱ�򣬾Ϳ��԰Ѽ���������ע�����
			getContentResolver().unregisterContentObserver(this);
			// ɾ������
			deleteCallLog(incomingNumber);

			super.onChange(selfChange);
		}
	}

	/**
	 * ���ض���
	 */
	private void blockSms() {
		receiver = new CallSafeReceiver();
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(2147483647);
		registerReceiver(receiver, filter);
	}

	/**
	 * ɾ��ͨ����¼
	 * 
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {

		Uri uri = Uri.parse("content://call_log/calls");

		getContentResolver().delete(uri, "number=?",
				new String[] { incomingNumber });
	}

	/**
	 * �ضϺ���
	 */
	public void stopCall() {

		try {
			// ͨ�������������ServiceManager
			Class<?> clazz = getClassLoader().loadClass(
					"android.os.ServiceManager");
			// ͨ������õ���ǰ�ķ���
			Method method = clazz.getDeclaredMethod("getService", String.class);

			// ���������static�ľͿ��Դ���null
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
