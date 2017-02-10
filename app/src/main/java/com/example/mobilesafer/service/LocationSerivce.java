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
		// 设置最佳提供者
		Criteria criteria = new Criteria();
		criteria.setCostAllowed(true);// 付费是否允许
		criteria.setAccuracy(Criteria.ACCURACY_FINE);// 精度要求
		String bestProvider = manager.getBestProvider(criteria, true);// true表示可用才返回回来
		// 提升为字段方便于销毁
		listener = new MyLocationListener();
		manager.requestLocationUpdates(bestProvider, 0, 0, listener);

	}

	class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// 获取到经纬度信息
			double longitude = location.getLongitude();
			double latitude = location.getLatitude();
			// 保存经纬度信息
			mPref.edit().putString("longitude", longitude + "").commit();
			mPref.edit().putString("latitude", latitude + "").commit();

			// 得到信息后停止服务
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
