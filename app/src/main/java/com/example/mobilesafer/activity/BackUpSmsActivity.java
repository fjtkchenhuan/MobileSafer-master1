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
 * ���ű���ҳ��
 * @author admin
 *
 */
public class BackUpSmsActivity extends Activity {
	
	@ViewInject(R.id.tv_result)
	TextView tv_result;
	
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int current = msg.what;
			tv_result.setText("һ��������" + current +"������" );
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
		dialog.setTitle("��ʾ��");
		dialog.setMessage("�������ڱ����У��԰�����...");
		dialog.show();
		//��ʼ���ݶ���
		new Thread(){
			public void run() {
				
				boolean flag = SmsUtils.backUp(BackUpSmsActivity.this, new ISmsDate() {
					// ��������
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
							//�������߳�ˢ��ui
							handler.sendEmptyMessage(count);
						}
					}
				});
				if(flag == true) {
					UIUtils.showToast(BackUpSmsActivity.this, "�������");
				}
			};
		}.start();
		
	}
	
	/**
	 * �ָ�����
	 */
	public void startRecovery(View v) {
		/*
		 * 1�������û�б��ݺõ��ļ�
		 * 2��ʹ��contentProvider���в���
		 * 3�����������ͨ��query����Ƿ��Ѿ������������ţ���������򲻲���
		 * 4��������ɵ�����ʾ
		 */
		Toast.makeText(this, "��ʱ��ûʵ��", 0).show();
	}
}
