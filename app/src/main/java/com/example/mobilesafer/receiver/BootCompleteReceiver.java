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
		//��ȡ���洢��sim,ע�ⶼ�Ǵ�context�л�ȡ
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
				System.out.println("��û�б�");
			} else {
				System.out.println("���ͱ�������");
				//��ȡ����ȫ����
				//ǿ�з��Ͷ���
				String phone =sp.getString("safe_phone", "") ;
				SmsManager smsManager = SmsManager.getDefault();
				/**
				 * ��һ����Ŀ�����
				 * �ڶ������������ģ���null��ʾĬ�Ϸ�������
				 * ����������Ϣ
				 * ���ĸ���sendIntent
				 * �������deliveryIntent
				 */
				smsManager.sendTextMessage(phone, null, "sim���Ѿ�������", null, null);
			}
		}
	}

}
