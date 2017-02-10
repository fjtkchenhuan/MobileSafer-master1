package com.example.mobilesafer.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.mobilesafer.R;
import com.example.mobilesafer.db.dao.AntivirusDao;
import com.example.mobilesafer.utils.Md5Utils;

import java.util.List;

public class AntivirusActivity extends Activity {
	protected static final int BEGIN = 1;
	protected static final int DOING = 2;
	protected static final int DONE = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initUI();
		initData();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			// �жϵ�ǰ������һ����
			switch (what) {
			case BEGIN: {
				tv_status.setText("���ڳ�ʼ��XX����");
				break;
			}
			case DOING: {
				CheckInfo info = (CheckInfo) msg.obj;
				String name = info.AppName;

				tv_status.setText("���ڼ�� :" + name);

				TextView tv = new TextView(AntivirusActivity.this);
				if (info.isVirus) {
					tv.setTextColor(Color.RED);
					tv.setText(name + "����ȫ��������");
				} else {
					tv.setTextColor(Color.BLACK);
					tv.setText(name + "��ȫ��������");
				}

				ll.addView(tv);
				break;
			}
			case DONE: {
				tv_status.setText("������");
				scanning.clearAnimation();
				break;
			}
			}
		};
	};
	private TextView tv_status;
	private ProgressBar pb;
	private LinearLayout ll;
	private ImageView scanning;

	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		setContentView(R.layout.activity_antivirus);
		// ��õ����
		tv_status = (TextView) findViewById(R.id.tv_status);
		pb = (ProgressBar) findViewById(R.id.pb_progress);
		ll = (LinearLayout) findViewById(R.id.ll_contenter);
		final ScrollView sv = (ScrollView) findViewById(R.id.sv);

		scanning = (ImageView) findViewById(R.id.scanning);
		RotateAnimation rA = new RotateAnimation(0, 360,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rA.setDuration(500);
		rA.setRepeatCount(-1);
		scanning.startAnimation(rA);
		
		sv.post(new Runnable() {
			@Override
			public void run() {
				sv.fullScroll(sv.FOCUS_DOWN);
			}
		});
	}

	/**
	 * ��ʼ������
	 */
	private void initData() {
		new Thread() {
			@Override
			public void run() {
				super.run();
				// ��ʼ��飬ɱ��ǰ
				Message message = Message.obtain();
				message.what = BEGIN;
				handler.sendMessage(message);

				PackageManager packageManager = getPackageManager();
				List<PackageInfo> infos = packageManager
						.getInstalledPackages(0);

				// ����ProgressBar
				pb.setMax(infos.size());
				int current = 0;
				for (PackageInfo packageInfo : infos) {
					// ��ǰ�����Ӧ�õĸ�����Ϣ�������װ��һ�������н��д���
					CheckInfo info = new CheckInfo();

					String packageName = packageInfo.applicationInfo.packageName;
					info.packageName = packageName;

					String AppName = packageInfo.applicationInfo.loadLabel(
							packageManager).toString();
					info.AppName = AppName;

					String sourceDir = packageInfo.applicationInfo.sourceDir;
					String fileMd5 = Md5Utils.getFileMd5(sourceDir);
					String Desc = AntivirusDao.checkFileVirus(fileMd5);
					info.Desc = Desc;

					if (!TextUtils.isEmpty(Desc)) {
						info.isVirus = true;
					} else {
						info.isVirus = false;
					}
					current = current + 1;
					pb.setProgress(current);
					SystemClock.sleep(200);
					// ������ϣ�ɱ����
					message = Message.obtain();
					message.what = DOING;
					message.obj = info;
					handler.sendMessage(message);
				}

				// ��ʾ�Ѿ�����
				message = Message.obtain();
				message.what = DONE;
				handler.sendMessage(message);
				
			}
		}.start();
	}

	/**
	 * ���ڼ�¼һ��Ӧ�õ���Ϣ����ʱjavabean ����������Ϣ���ݵ�ʱ�򴫵ݹ�ȥ��ǰ�������������
	 * 
	 * @author admin
	 *
	 */
	static class CheckInfo {
		String packageName;
		String AppName;
		boolean isVirus;
		String Desc;
	}
}
