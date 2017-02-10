package com.example.mobilesafer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafer.R;
import com.example.mobilesafer.utils.SmsUtils;
import com.example.mobilesafer.utils.SmsUtils.ISmsDate;
import com.example.mobilesafer.utils.UIUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 短信备份页面
 * @author admin
 *
 */
public class BackUpSmsActivity extends Activity {
	
	@ViewInject(R.id.tv_result)
	TextView tv_result;
	
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int current = msg.what;
			tv_result.setText("一共备份了" + current +"条短信" );
		};
	};


	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backup_sms);
		
		com.lidroid.xutils.ViewUtils.inject(this);
	}
	
	public void startBackUp(View v) {
		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setTitle("提示：");
		dialog.setMessage("短信正在备份中，稍安勿躁...");
		dialog.show();
		//开始备份短信
		new Thread(){
			public void run() {
				
				boolean flag = SmsUtils.backUp(BackUpSmsActivity.this, new ISmsDate() {
					// 保存总数
					int count = 0;
					@Override
					public void getCount(int count) {
						this.count = count;
						dialog.setMax(count);
					}
					
					@Override
					public void currentProgress(int current) {
						dialog.setProgress(current);
						
						if(current == count) {
							dialog.dismiss();
							//交回主线程刷新ui
							handler.sendEmptyMessage(count);
						}
					}
				});
				if(flag == true) {
					UIUtils.showToast(BackUpSmsActivity.this, "备份完成");
				}
			};
		}.start();
		
	}
	
	/**
	 * 恢复短信
	 */
	public void startRecovery(View v) {
		/*
		 * 1、检测有没有备份好的文件
		 * 2、使用contentProvider进行插入
		 * 3、插入过程中通过query检测是否已经有了这条短信，如果存在则不插入
		 * 4、插入完成弹出提示
		 */
		Toast.makeText(this, "暂时还没实现", 0).show();
	}
}
