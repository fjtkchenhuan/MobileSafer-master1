package com.example.mobilesafer.receiver;

import com.example.mobilesafer.db.dao.AddressDao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 *  ȥ�������
 * @author admin
 *
 */

public class OutCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//��ò�������
		String number = getResultData();
		//��ѯ������
		String location = AddressDao.getLocation(number);
		Toast.makeText(context, location, Toast.LENGTH_LONG).show();
	}
	
}
