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
 * 页面二
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

		// 判断是否已经绑定过sim,初始化CheckBox
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
					// CheckBox变为没有选中，并且将数据删掉
					sivSim.setChecked(false);
					sp.edit().remove("sim").commit();
				} else {
					// 将checkBox选中，并保存数据
					TelephonyManager mTelepnhoy = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String simSerialNumber = mTelepnhoy.getSimSerialNumber();
					sp.edit().putString("sim", simSerialNumber).commit();
					sivSim.setChecked(true);
				}
			}
		});
	}

	/**
	 * 显示上一页
	 */
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);
	}

	/**
	 * 显示下一页
	 */
	public void showNextPage() {
		//如果没有绑定则不能进行跳转
		String sim = sp.getString("sim", null);
		if(!TextUtils.isEmpty(sim)) {
			startActivity(new Intent(this, Setup3Activity1.class));
			finish();
			overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		} else {
			Toast.makeText(this, "必须绑定SIM卡！", Toast.LENGTH_SHORT).show();
		}
		
	}
}
