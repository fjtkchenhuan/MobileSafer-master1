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

		// �����Զ�����
		sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
		// ����������ʾ����
		sivAddress = (SettingItemView) findViewById(R.id.siv_address);
		// ����������ʾ����
		siv_callsafe = (SettingItemView) findViewById(R.id.siv_callsafe);
		//���ÿ��Ź�
		siv_watch_dog = (SettingItemView) findViewById(R.id.siv_watch_dog);
		
		// ��ʼ���Զ�����
		initUpdata();

		// ��ʼ��������ʾ����
		initAddressService();

		// ��ʼ��������ʾ��ʽ
		initAddressStyle();

		// ��ʼ����������ʾλ��ѡ��
		initAddressLocation();

		// ��ʼ������������
		initCallSafe();
		
		//��ʼ�����Ź�
		initWatchDog();
		
	}

	/**
	 * ��ʼ�����Ź�
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
	 * ��ʼ��������
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
	 * ��ʼ��������ʾ
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
	 * ��ʼ���Զ����¿���
	 */
	private void initUpdata() {

		sivUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sivUpdate.isChecked()) {
					sivUpdate.setChecked(false);
					// sivUpdate.setDesc("�Զ������ѹر�");
					// ע��commit
					sp.edit().putBoolean("auto_update", false).commit();

				} else {
					sivUpdate.setChecked(true);
					// sivUpdate.setDesc("�Զ������ѿ���");
					sp.edit().putBoolean("auto_update", true).commit();
				}
			}
		});

		// ��ȡ�������Ϣ
		Boolean autoUpdate = sp.getBoolean("auto_update", true);
		if (autoUpdate) {
			sivUpdate.setChecked(true);
			// sivUpdate.setDesc("�Զ������ѿ���");
		} else {
			sivUpdate.setChecked(false);
			// sivUpdate.setDesc("�Զ������ѹر�");
		}
	}

	/*
	 * ��ʼ��������ʾ��ʽ
	 */
	final String[] items = new String[] { "��͸��", "������", "��ʿ��", "������", "ƻ����" };
	private SettingClickView scv;
	private SettingItemView siv_watch_dog;

	public void initAddressStyle() {
		scv = (SettingClickView) findViewById(R.id.scv_address);
		// ��ʼ��
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
		// ����һ���Ի���
		AlertDialog.Builder builder = new Builder(SettingActivity.this);
		// builder.setIcon(icon);���ñ���
		builder.setTitle("��������ʾ����");

		// ��ʼ��
		int which = sp.getInt("address_style", 0);

		/*
		 * 1����Դ 2��Ĭ��ѡ�� 3����������ע��Ҫ��DialogInterface.OnClickListener��
		 */
		builder.setSingleChoiceItems(items, which,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						String currentSytle = items[which];
						// ����
						sp.edit().putInt("address_style", which).commit();
						// ����desc
						scv.setDesc(currentSytle);
						// �öԻ�����ʧ
						dialog.dismiss();

					}
				});

		builder.setNegativeButton("ȡ��", null);
		builder.show();
	}

	/**
	 * �޸Ĺ�������ʾλ��
	 */
	private void initAddressLocation() {
		// ��ȡ����
		SettingClickView addressLocation = (SettingClickView) findViewById(R.id.scv_addresslocation);
		addressLocation.setDesc("���ù�������ʾ�����ʾλ��");
		// ��ӵ������
		addressLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ����λ������activity
				Intent i = new Intent(SettingActivity.this,
						DragViewActivity.class);
				startActivity(i);
			}
		});

	}
}
