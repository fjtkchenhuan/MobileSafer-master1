package com.example.mobilesafer.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.example.mobilesafer.R;
import com.example.mobilesafer.service.LocationSerivce;

/**
 * ���ն��ţ������������ж�
 * 
 * @author admin
 *
 */
public class SmsReceiver extends BroadcastReceiver {

	private DevicePolicyManager mDPM;

	@Override
	public void onReceive(Context context, Intent intent) {

		mDPM = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);

		// ��ȡ����������
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		for (Object object : objects) {
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
			// ��ȡ��������Ϣ
			String originatingAddress = smsMessage.getOriginatingAddress();
			String messageBody = smsMessage.getMessageBody();
			System.out.println(originatingAddress + ":" + messageBody);
			if ("#*alarm*#".equals(messageBody)) {
				/*
				 * ���ű�������
				 */
				// ��ʼ��
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
				// ����
				player.setLooping(true);
				player.setVolume(1f, 1f);
				player.start();
				// �ж���Ϣ
				abortBroadcast();
			} else if ("#*location*#".equals(messageBody)) {
				/*
				 * ����ǻ�ȡ����λ��
				 */
				context.startService(new Intent(context, LocationSerivce.class));
				// ��ȡ����������ȡ����Ϣ
				SharedPreferences mPref = context.getSharedPreferences(
						"config", context.MODE_PRIVATE);
				String longitude = mPref.getString("longitude", "");
				String latitude = mPref.getString("latitude", "");
				System.out.println("longitude:" + longitude + " " + "latitude"
						+ latitude);

				// ������Ϣ
				SmsManager mSM = SmsManager.getDefault();
				mSM.sendTextMessage(originatingAddress, null, "longitude:"
						+ longitude + " " + "latitude" + latitude, null, null);
				// �ж���Ϣ
				abortBroadcast();
			} else if ("#*lockscreen*#".equals(messageBody)) {
				// ����
				mDPM.lockNow();
				// �ж���Ϣ
				abortBroadcast();
			} else if ("#*wipedata*#".equals(messageBody)) {
				// �������
				mDPM.wipeData(0);
				// �ж���Ϣ
				abortBroadcast();
			}

			
		}
	}

}
