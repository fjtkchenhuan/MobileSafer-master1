package com.example.mobilesafer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.mobilesafer.R;
import com.example.mobilesafer.receiver.CallSafeReceiver;
import com.example.mobilesafer.receiver.OutCallReceiver;
import com.example.mobilesafer.service.AddressService;
import com.example.mobilesafer.service.CallSafeService;
import com.example.mobilesafer.service.WatchDogService;
import com.example.mobilesafer.service.WatchDogServiceUpdate;
import com.example.mobilesafer.utils.SystemInfoUtils;
import com.example.mobilesafer.view.SettingClickView;
import com.example.mobilesafer.view.SettingItemView;

public class SettingActivity extends Activity {

	private SettingItemView sivUpdate;
	private SharedPreferences sp;
	private SettingItemView sivAddress;
	private SettingItemView siv_callsafe;

	// private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.setting_layout);

		sp = getSharedPreferences("config", MODE_PRIVATE);

		// 设置自动更新
		sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
		// 设置来电显示服务
		sivAddress = (SettingItemView) findViewById(R.id.siv_address);
		// 设置来电显示服务
		siv_callsafe = (SettingItemView) findViewById(R.id.siv_callsafe);
		//设置看门狗
		siv_watch_dog = (SettingItemView) findViewById(R.id.siv_watch_dog);
		
		// 初始化自动更新
		initUpdata();

		// 初始化来电显示服务
		initAddressService();

		// 初始化来电显示样式
		initAddressStyle();

		// 初始化归属地显示位置选项
		initAddressLocation();

		// 初始化黑名单拦截
		initCallSafe();
		
		//初始化看门狗
		initWatchDog();
		
	}

	/**
	 * 初始化看门狗
	 */
	private void initWatchDog() {
		final Intent i = new Intent(this, WatchDogServiceUpdate.class);

		if (!SystemInfoUtils.isServiceRunning(this,
				"com.example.mobilesafer.service.WatchDogServiceUpdate")) {
			siv_watch_dog.setChecked(false);
		} else {
			siv_watch_dog.setChecked(true);
		}

		siv_watch_dog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (siv_watch_dog.isChecked()) {
					siv_watch_dog.setChecked(false);
					stopService(i);
				} else {
					System.out.println("start service");
					siv_watch_dog.setChecked(true);
					startService(i);
				}
			}
		});
	}

	/**
	 * 初始化黑名单
	 */
	private void initCallSafe() {
		final Intent i = new Intent(this, CallSafeService.class);

		if (!SystemInfoUtils.isServiceRunning(this,
				"com.example.mobilesafer.service.CallSafeService")) {
			siv_callsafe.setChecked(false);
		} else {
			siv_callsafe.setChecked(true);
		}

		siv_callsafe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_callsafe.isChecked()) {
					siv_callsafe.setChecked(false);
					stopService(i);

				} else {
					siv_callsafe.setChecked(true);
					startService(i);
				}
			}
		});

	}

	/**
	 * 初始化来电显示
	 */
	private void initAddressService() {
		final Intent i = new Intent(this, AddressService.class);

		if (!SystemInfoUtils.isServiceRunning(this,
				"com.example.mobilesafer.service.AddressService")) {
			sivAddress.setChecked(false);
		} else {
			sivAddress.setChecked(true);
		}

		sivAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sivAddress.isChecked()) {
					sivAddress.setChecked(false);
					stopService(i);

				} else {
					sivAddress.setChecked(true);
					startService(i);
				}
			}
		});

	}

	/**
	 * 初始化自动更新开关
	 */
	private void initUpdata() {

		sivUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sivUpdate.isChecked()) {
					sivUpdate.setChecked(false);
					// sivUpdate.setDesc("自动更新已关闭");
					// 注意commit
					sp.edit().putBoolean("auto_update", false).commit();

				} else {
					sivUpdate.setChecked(true);
					// sivUpdate.setDesc("自动更新已开启");
					sp.edit().putBoolean("auto_update", true).commit();
				}
			}
		});

		// 获取保存的信息
		Boolean autoUpdate = sp.getBoolean("auto_update", true);
		if (autoUpdate) {
			sivUpdate.setChecked(true);
			// sivUpdate.setDesc("自动更新已开启");
		} else {
			sivUpdate.setChecked(false);
			// sivUpdate.setDesc("自动更新已关闭");
		}
	}

	/*
	 * 初始化来电显示样式
	 */
	final String[] items = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };
	private SettingClickView scv;
	private SettingItemView siv_watch_dog;

	public void initAddressStyle() {
		scv = (SettingClickView) findViewById(R.id.scv_address);
		// 初始化
		int which = sp.getInt("address_style", 0);
		scv.setDesc(items[which]);

		scv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showSingleChooseDialog();

			}
		});
	}

	protected void showSingleChooseDialog() {
		// 建立一个对话框
		AlertDialog.Builder builder = new Builder(SettingActivity.this);
		// builder.setIcon(icon);设置标题
		builder.setTitle("归属地提示框风格");

		// 初始化
		int which = sp.getInt("address_style", 0);

		/*
		 * 1、资源 2、默认选中 3、监听器（注意要用DialogInterface.OnClickListener）
		 */
		builder.setSingleChoiceItems(items, which,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						String currentSytle = items[which];
						// 保存
						sp.edit().putInt("address_style", which).commit();
						// 更新desc
						scv.setDesc(currentSytle);
						// 让对话框消失
						dialog.dismiss();

					}
				});

		builder.setNegativeButton("取消", null);
		builder.show();
	}

	/**
	 * 修改归属地显示位置
	 */
	private void initAddressLocation() {
		// 获取对象
		SettingClickView addressLocation = (SettingClickView) findViewById(R.id.scv_addresslocation);
		addressLocation.setDesc("设置归属地提示框的显示位置");
		// 添加点击监听
		addressLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 启动位置设置activity
				Intent i = new Intent(SettingActivity.this,
						DragViewActivity.class);
				startActivity(i);
			}
		});

	}
}
