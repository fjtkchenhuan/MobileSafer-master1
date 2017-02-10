package com.example.mobilesafer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.mobilesafer.R;
import com.example.mobilesafer.db.dao.AddressDao;

public class AddressService extends Service {

	private TelephonyManager manager;
	private MyListener listener;
	private OutCallReceiver outcall;
	private WindowManager mWm;
	private View view;
	
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	private WindowManager.LayoutParams params;
	private SharedPreferences sp;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();

		// 监听电话状态
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		outcall = new OutCallReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(outcall, filter);
	}

	class MyListener extends PhoneStateListener {

		// 当电话状态改变的时候
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING: {
				// 当电话响起
				System.out.println("电话响起");

				String location = AddressDao.getLocation(incomingNumber);
				showToast(location);

				break;
			}
			case TelephonyManager.CALL_STATE_IDLE: { // 电话闲置状态
				if (mWm != null && view != null) {
					mWm.removeView(view);
					view = null;
				}
			}
			default:
				break;
			}
		}
	}

	/**
	 * 去电接受者
	 * 
	 * @author admin
	 *
	 */
	public class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 获得播出号码
			String number = getResultData();
			// 查询归属地
			String location = AddressDao.getLocation(number);
			// Toast.makeText(context, location, Toast.LENGTH_LONG).show();
			showToast(location);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 解除监听
		manager.listen(listener, PhoneStateListener.LISTEN_NONE);

		// 注销receiver
		unregisterReceiver(outcall);
	}

	/**
	 * 弹出归属地框
	 * 注意权限：android.permission.SYSTEM_ALERT_WINDOW
	 * 
	 */

	private void showToast(String text) {
		mWm = (WindowManager) getSystemService(WINDOW_SERVICE);

		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
//		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		
		params.setTitle("Toast");
		
		//更改中心到左上方
		params.gravity = Gravity.LEFT + Gravity.TOP;
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//获取到数据，进行偏移
		int lastLM = sp.getInt("lastLM", 0);
		int lastTM = sp.getInt("lastTM", 0);
		//设置偏移量
		params.x = lastLM;
		params.y = lastTM;

		// tv = new TextView(this);
		// tv.setText(text);
		// tv.setTextColor(Color.RED);
		// style图片
		int[] bgs = new int[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		SharedPreferences mSpref = getSharedPreferences("config", MODE_PRIVATE);
		int which = mSpref.getInt("address_style", 0);

		view = View.inflate(this, R.layout.toast_address, null);
		view.setBackgroundResource(bgs[which]);
		TextView tvLocation = (TextView) view.findViewById(R.id.tv_location);
		tvLocation.setText(text);
		
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN : {
					// 当手指放下的时候，获取当前位置作为开始位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				}
				case MotionEvent.ACTION_MOVE : {
					//获取屏幕长宽
					int winHeight = mWm.getDefaultDisplay().getHeight();
					int winWidth = mWm.getDefaultDisplay().getWidth();
					
					// 当手指移动的时候获得结果位置
					endX = (int) event.getRawX();
					endY = (int) event.getRawY();

					// 获得移动的距离
					int dx = endX - startX;
					int dy = endY - startY;
					
					//更新组件位置
					params.x += dx;
					params.y += dy;
					
					//判断边界
					if(params.x < 0) {
						params.x = 0;
					}
					if(params.y <0) {
						params.y =0 ;
					}
					if(params.x > winWidth - view.getWidth()) {
						params.x = winWidth - view.getWidth();
					}
					if(params.y > winHeight - view.getHeight()) {
						params.y = winHeight - view.getHeight();
					}
					
					//注意是更新ui（使用的是updata）
					mWm.updateViewLayout(view, params);
					
					//更新起始位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
					break;
				}
				case MotionEvent.ACTION_UP: {
					sp.edit().putInt("lastLM", params.x).commit();
					sp.edit().putInt("lastTM", params.y).commit();
					break;
				}
				}
				return false;
			}
		});

		mWm.addView(view, params);
	}
}
