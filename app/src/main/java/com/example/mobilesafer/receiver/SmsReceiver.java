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
 * 接收短信，并进行内容判断
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

		// 获取到短信内容
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		for (Object object : objects) {
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
			// 获取到各种信息
			String originatingAddress = smsMessage.getOriginatingAddress();
			String messageBody = smsMessage.getMessageBody();
			System.out.println(originatingAddress + ":" + messageBody);
			if ("#*alarm*#".equals(messageBody)) {
				/*
				 * 播放报警音乐
				 */
				// 初始化
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
				// 设置
				player.setLooping(true);
				player.setVolume(1f, 1f);
				player.start();
				// 切断信息
				abortBroadcast();
			} else if ("#*location*#".equals(messageBody)) {
				/*
				 * 如果是获取地理位置
				 */
				context.startService(new Intent(context, LocationSerivce.class));
				// 获取到服务所获取的信息
				SharedPreferences mPref = context.getSharedPreferences(
						"config", context.MODE_PRIVATE);
				String longitude = mPref.getString("longitude", "");
				String latitude = mPref.getString("latitude", "");
				System.out.println("longitude:" + longitude + " " + "latitude"
						+ latitude);

				// 返回信息
				SmsManager mSM = SmsManager.getDefault();
				mSM.sendTextMessage(originatingAddress, null, "longitude:"
						+ longitude + " " + "latitude" + latitude, null, null);
				// 切断信息
				abortBroadcast();
			} else if ("#*lockscreen*#".equals(messageBody)) {
				// 锁屏
				mDPM.lockNow();
				// 切断信息
				abortBroadcast();
			} else if ("#*wipedata*#".equals(messageBody)) {
				// 清除数据
				mDPM.wipeData(0);
				// 切断信息
				abortBroadcast();
			}

			
		}
	}

}
