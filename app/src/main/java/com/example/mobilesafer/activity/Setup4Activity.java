package com.example.mobilesafer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.mobilesafer.R;

/**
 * 页面4
 * 
 * @author admin
 *
 */

public class Setup4Activity extends BaseSettingActivity {

	private CheckBox cb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setup4);
		
		cb = (CheckBox) findViewById(R.id.cb);
		//判断初始化状态
		boolean isChecked = sp.getBoolean("protect", false);
		if(isChecked) {
			sp.edit().putBoolean("protect", true).commit();
			cb.setText("防盗保护已经开启");
			cb.setChecked(true);
		} else {
			sp.edit().putBoolean("protect", false).commit();
			cb.setText("防盗保护没有开启");
		}
		//添加监听器，监听选中情况
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//根据isChecked来改变状态和文字
				if(isChecked) {
					sp.edit().putBoolean("protect", true).commit();
					cb.setText("防盗保护已经开启");
				} else {
					sp.edit().putBoolean("protect", false).commit();
					cb.setText("防盗保护没有开启");
				}
			}
		});
		
	}

	public void next(View v) {
		showNextPage();
	}

	// 返回页面3
	public void previous(View v) {
		showPreviousPage();
	}

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup3Activity1.class));
		finish();
		overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
	
	}

	@Override
	public void showNextPage() {
		startActivity(new Intent(this, LostFindActivity.class));
		finish();
		getSharedPreferences("config", MODE_PRIVATE).edit()
				.putBoolean("configed", true).commit();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	
	}

}
