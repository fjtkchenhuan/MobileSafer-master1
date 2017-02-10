package com.example.mobilesafer.receiver;

import com.example.mobilesafer.db.dao.AddressDao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 *  去电接受者
 * @author admin
 *
 */

public class OutCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//获得播出号码
		String number = getResultData();
		//查询归属地
		String location = AddressDao.getLocation(number);
		Toast.makeText(context, location, Toast.LENGTH_LONG).show();
	}
	
}
