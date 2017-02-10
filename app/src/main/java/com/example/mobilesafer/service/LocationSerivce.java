package com.example.mobilesafer.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationSerivce extends Service {

	private SharedPreferences mPref;
	private LocationManager manager;
	private MyLocationListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mPref = getSharedPreferences("config", MODE_PRIVATE);
		manager = (LocationManager) getSystemService(LOCATION_SERVICE);
		// ��������ṩ��
		Criteria criteria = new Criteria();
		criteria.setCostAllowed(true);// �����Ƿ�����
		criteria.setAccuracy(Criteria.ACCURACY_FINE);// ����Ҫ��
		String bestProvider = manager.getBestProvider(criteria, true);// true��ʾ���òŷ��ػ���
		// ����Ϊ�ֶη���������
		listener = new MyLocationListener();
		manager.requestLocationUpdates(bestProvider, 0, 0, listener);

	}

	class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// ��ȡ����γ����Ϣ
			double longitude = location.getLongitude();
			double latitude = location.getLatitude();
			// ���澭γ����Ϣ
			mPref.edit().putString("longitude", longitude + "").commit();
			mPref.edit().putString("latitude", latitude + "").commit();

			// �õ���Ϣ��ֹͣ����
			stopSelf();

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		manager.removeUpdates(listener);
	}
}
