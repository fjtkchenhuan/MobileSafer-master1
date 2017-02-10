package com.example.mobilesafer.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.mobilesafer.R;
import com.example.mobilesafer.receiver.SimpleDeviceAdminReceive;

/**
 * 页面一
 * 
 * @author admin
 *
 */
public class Setup1Activity extends BaseSettingActivity {

	private ComponentName mDeviceAdminSample;
	private DevicePolicyManager mDPM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setup1);
		
		/*
		 * 用于设置设备管理器
		 * 还存在bug
		 */
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminSample = new ComponentName(this,
				SimpleDeviceAdminReceive.class);

		if(!mDPM.isAdminActive(mDeviceAdminSample)) {
			actionAdmin();
		}
	}

	@Override
	public void showPreviousPage() {
	}

	@Override
	public void showNextPage() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	/*
	 * 激活
	 */
	private void actionAdmin() {
		// 创建一个意图，用于打开设别管理器注册界面
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		// 指明想要打开哪一个设备管理器
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
				mDeviceAdminSample);
		// 添加解释信息
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "注册一键锁屏");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		// 启动设备管理器注册
//		startActivity(intent);
		startActivityForResult(intent, RESULT_OK);
		System.out.println("2");
	}

}
