package com.example.mobilesafer.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.mobilesafer.R;
import com.example.mobilesafer.receiver.SimpleDeviceAdminReceive;

/**
 * ҳ��һ
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
		 * ���������豸������
		 * ������bug
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
	 * ����
	 */
	private void actionAdmin() {
		// ����һ����ͼ�����ڴ���������ע�����
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		// ָ����Ҫ����һ���豸������
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
				mDeviceAdminSample);
		// ��ӽ�����Ϣ
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "ע��һ������");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		// �����豸������ע��
//		startActivity(intent);
		startActivityForResult(intent, RESULT_OK);
		System.out.println("2");
	}

}
