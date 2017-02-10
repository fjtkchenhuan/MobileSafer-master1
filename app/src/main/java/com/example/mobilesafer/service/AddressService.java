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

		// �����绰״̬
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		outcall = new OutCallReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(outcall, filter);
	}

	class MyListener extends PhoneStateListener {

		// ���绰״̬�ı��ʱ��
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING: {
				// ���绰����
				System.out.println("�绰����");

				String location = AddressDao.getLocation(incomingNumber);
				showToast(location);

				break;
			}
			case TelephonyManager.CALL_STATE_IDLE: { // �绰����״̬
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
	 * ȥ�������
	 * 
	 * @author admin
	 *
	 */
	public class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// ��ò�������
			String number = getResultData();
			// ��ѯ������
			String location = AddressDao.getLocation(number);
			// Toast.makeText(context, location, Toast.LENGTH_LONG).show();
			showToast(location);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// �������
		manager.listen(listener, PhoneStateListener.LISTEN_NONE);

		// ע��receiver
		unregisterReceiver(outcall);
	}

	/**
	 * ���������ؿ�
	 * ע��Ȩ�ޣ�android.permission.SYSTEM_ALERT_WINDOW
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
		
		//�������ĵ����Ϸ�
		params.gravity = Gravity.LEFT + Gravity.TOP;
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//��ȡ�����ݣ�����ƫ��
		int lastLM = sp.getInt("lastLM", 0);
		int lastTM = sp.getInt("lastTM", 0);
		//����ƫ����
		params.x = lastLM;
		params.y = lastTM;

		// tv = new TextView(this);
		// tv.setText(text);
		// tv.setTextColor(Color.RED);
		// styleͼƬ
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
					// ����ָ���µ�ʱ�򣬻�ȡ��ǰλ����Ϊ��ʼλ��
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				}
				case MotionEvent.ACTION_MOVE : {
					//��ȡ��Ļ����
					int winHeight = mWm.getDefaultDisplay().getHeight();
					int winWidth = mWm.getDefaultDisplay().getWidth();
					
					// ����ָ�ƶ���ʱ���ý��λ��
					endX = (int) event.getRawX();
					endY = (int) event.getRawY();

					// ����ƶ��ľ���
					int dx = endX - startX;
					int dy = endY - startY;
					
					//�������λ��
					params.x += dx;
					params.y += dy;
					
					//�жϱ߽�
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
					
					//ע���Ǹ���ui��ʹ�õ���updata��
					mWm.updateViewLayout(view, params);
					
					//������ʼλ��
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
