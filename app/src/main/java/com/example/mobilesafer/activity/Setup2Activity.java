package com.example.mobilesafer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.mobilesafer.R;
import com.example.mobilesafer.view.SettingItemView;

/**
 * ҳ���
 * 
 * @author admin
 *
 */

public class Setup2Activity extends BaseSettingActivity {

	private SettingItemView sivSim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		sivSim = (SettingItemView) findViewById(R.id.siv_sim);

		// �ж��Ƿ��Ѿ��󶨹�sim,��ʼ��CheckBox
		String simNub = sp.getString("sim", null);
		if (TextUtils.isEmpty(simNub)) {
			sivSim.setChecked(false);
		} else {
			sivSim.setChecked(true);
		}

		sivSim.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sivSim.isChecked()) {
					// CheckBox��Ϊû��ѡ�У����ҽ�����ɾ��
					sivSim.setChecked(false);
					sp.edit().remove("sim").commit();
				} else {
					// ��checkBoxѡ�У�����������
					TelephonyManager mTelepnhoy = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String simSerialNumber = mTelepnhoy.getSimSerialNumber();
					sp.edit().putString("sim", simSerialNumber).commit();
					sivSim.setChecked(true);
				}
			}
		});
	}

	/**
	 * ��ʾ��һҳ
	 */
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);
	}

	/**
	 * ��ʾ��һҳ
	 */
	public void showNextPage() {
		//���û�а����ܽ�����ת
		String sim = sp.getString("sim", null);
		if(!TextUtils.isEmpty(sim)) {
			startActivity(new Intent(this, Setup3Activity1.class));
			finish();
			overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		} else {
			Toast.makeText(this, "�����SIM����", Toast.LENGTH_SHORT).show();
		}
		
	}
}
