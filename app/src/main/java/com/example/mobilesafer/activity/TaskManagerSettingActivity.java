package com.example.mobilesafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.mobilesafer.R;
import com.example.mobilesafer.service.KillProcessService;
import com.example.mobilesafer.utils.SystemInfoUtils;

public class TaskManagerSettingActivity extends Activity {
	private CheckBox cb_status;
	private SharedPreferences sp;
	private CheckBox cb_kill_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", 0);
		initUI();
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		setContentView(R.layout.activity_task_manager_setting);
		cb_status = (CheckBox) findViewById(R.id.cb_status);
		cb_kill_status = (CheckBox) findViewById(R.id.cb_kill_status);
		
		boolean flag = sp.getBoolean("show_system_process", true);
		if(flag) {
			cb_status.setChecked(true);
		} else {
			cb_status.setChecked(false);	
		}
		
		if(SystemInfoUtils.isServiceRunning(this, "com.example.mobilesafer.service.KillProcessService")) {
			cb_kill_status.setChecked(true);
		} else {
			cb_kill_status.setChecked(false);
		}
	}
	
	/**
	 * 显示系统进程
	 * @param v
	 */
	public void showSystemProcess(View v) {
		boolean isChecked = cb_status.isChecked();
		if(isChecked) {
			cb_status.setChecked(false);
			sp.edit().putBoolean("show_system_process", false).commit();
		} else {
			cb_status.setChecked(true);
			sp.edit().putBoolean("show_system_process", true).commit();
		}
	}
	
	/**
	 * 定时清理
	 * @param v
	 */
	public void setTimerKill(View v) {
		boolean isChecked = cb_kill_status.isChecked();
		if(isChecked) {
			cb_kill_status.setChecked(false);
		} else {
			cb_kill_status.setChecked(true);
		}
		
		cb_kill_status.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent i = new Intent(TaskManagerSettingActivity.this, KillProcessService.class);
				if(isChecked) {
					startService(i);
				} else{
					stopService(i);
				}
			}
		});
	}
}
