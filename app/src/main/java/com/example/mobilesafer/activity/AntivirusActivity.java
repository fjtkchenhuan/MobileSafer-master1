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
			// 判断当前处理到哪一步了
			switch (what) {
			case BEGIN: {
				tv_status.setText("正在初始化XX引擎");
				break;
			}
			case DOING: {
				CheckInfo info = (CheckInfo) msg.obj;
				String name = info.AppName;

				tv_status.setText("正在检查 :" + name);

				TextView tv = new TextView(AntivirusActivity.this);
				if (info.isVirus) {
					tv.setTextColor(Color.RED);
					tv.setText(name + "不安全！！！！");
				} else {
					tv.setTextColor(Color.BLACK);
					tv.setText(name + "安全！！！！");
				}

				ll.addView(tv);
				break;
			}
			case DONE: {
				tv_status.setText("检查完毕");
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
	 * 初始化UI
	 */
	private void initUI() {
		setContentView(R.layout.activity_antivirus);
		// 获得到组件
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
	 * 初始化数据
	 */
	private void initData() {
		new Thread() {
			@Override
			public void run() {
				super.run();
				// 开始检查，杀毒前
				Message message = Message.obtain();
				message.what = BEGIN;
				handler.sendMessage(message);

				PackageManager packageManager = getPackageManager();
				List<PackageInfo> infos = packageManager
						.getInstalledPackages(0);

				// 设置ProgressBar
				pb.setMax(infos.size());
				int current = 0;
				for (PackageInfo packageInfo : infos) {
					// 当前检验的应用的各种信息，将其封装在一个对象中进行传递
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
					// 数据完毕，杀毒中
					message = Message.obtain();
					message.what = DOING;
					message.obj = info;
					handler.sendMessage(message);
				}

				// 表示已经结束
				message = Message.obtain();
				message.what = DONE;
				handler.sendMessage(message);
				
			}
		}.start();
	}

	/**
	 * 用于记录一个应用的信息的临时javabean 方便与在消息传递的时候传递过去当前检验的数据内容
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
