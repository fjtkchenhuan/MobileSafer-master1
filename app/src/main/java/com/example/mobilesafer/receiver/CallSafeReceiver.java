package com.example.mobilesafer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import com.example.mobilesafer.db.dao.BlackNumberDao;

public class CallSafeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String number = "";
		String body = "";
		BlackNumberDao dao = new BlackNumberDao(context);
		
		Object[] objs = (Object[])intent.getExtras().get("pdus");
		for(Object obj : objs) {
			SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);
			number = message.getOriginatingAddress();
			body = message.getMessageBody();
			//≤È—Ø
			String mode = dao.findMode(number);
			System.out.println(mode);
			if(mode.equals("1") || mode.equals("3")) {
				System.out.println("in");
				//Ωÿ∂œ∂Ã–≈
				abortBroadcast();
			}
		}
	}

}
