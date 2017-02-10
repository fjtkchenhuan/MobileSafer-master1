package com.example.mobilesafer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class BootCompleteReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		//获取到存储的sim,注意都是从context中获取
		SharedPreferences sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		boolean isChecked = sp.getBoolean("protect", false);
		if(!isChecked) {
			return;
		}
		String sim = sp.getString("sim", null);
		
		
		if(!TextUtils.isEmpty(sim)) {
			TelephonyManager te  = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
			String currentSim = te.getSimSerialNumber() + "dafdaa";
			if(currentSim.equals(sim)) {
				System.out.println("卡没有变");
			} else {
				System.out.println("发送报警短信");
				//获取到安全号码
				//强行发送短信
				String phone =sp.getString("safe_phone", "") ;
				SmsManager smsManager = SmsManager.getDefault();
				/**
				 * 第一个：目标号码
				 * 第二个：服务中心，传null表示默认服务中心
				 * 第三个：信息
				 * 第四个：sendIntent
				 * 第五个：deliveryIntent
				 */
				smsManager.sendTextMessage(phone, null, "sim卡已经被更换", null, null);
			}
		}
	}

}
