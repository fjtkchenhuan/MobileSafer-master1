package com.example.mobilesafer.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafer.R;
import com.example.mobilesafer.utils.StreamUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class SplashActivity extends Activity {

	// 状态码
	protected static final int CODE_UPDATE_DIALOG = 0;
	protected static final int CODE_URL_ERROR = 1;
	protected static final int CODE_NET_ERROR = 2;
	protected static final int CODE_JSON_ERROR = 3;
	protected static final int CODE_START_HOME = 4;
	// 从服务器获取的数据
	private String mVersionName;
	private int mVersionCode;
	private String mDes;
	private String mDownUrl;

	private TextView tv;
	private TextView tvProgerss;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_UPDATE_DIALOG:
				showUpdataDialog();
				break;
			case CODE_URL_ERROR:
				Toast.makeText(SplashActivity.this, "URL错误", Toast.LENGTH_SHORT)
						.show();
				startHome();
				break;
			case CODE_NET_ERROR:
				Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_SHORT)
						.show();
				startHome();
				break;
			case CODE_JSON_ERROR:
				Toast.makeText(SplashActivity.this, "数据解析错误",
						Toast.LENGTH_SHORT).show();
				startHome();
				break;
			case CODE_START_HOME:
				startHome();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);

		tv = (TextView) findViewById(R.id.tv_version);
		tv.setText("版本号:" + getVersionName());

		tvProgerss = (TextView) findViewById(R.id.tv_progress);

		//拷贝数据库
		copyDataBase("address.db");
		copyDataBase("antivirus.db");
		
		//建立快捷方式
		createShortcut();
		
		// 获取配置文件，判断是否自动更新
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		Boolean autoUpdate = sp.getBoolean("auto_update", true);
		if (autoUpdate) {
			checkVersion();
		} else {
			// 发送延时信息
			handler.sendEmptyMessageDelayed(CODE_START_HOME, 2000);
		}
	}

	/**
	 * 创建快捷方式
	 */
	private void createShortcut() {
		Intent i = new Intent();
		i.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		i.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, R.drawable.ic_launcher);//设置图标
		i.putExtra(Intent.EXTRA_SHORTCUT_NAME , "手机卫士");
		
		Intent doWhat = new Intent();
		doWhat.setAction("com.dwb.mobilesafer");
		doWhat.addCategory(Intent.CATEGORY_DEFAULT);
		i.putExtra(Intent.EXTRA_SHORTCUT_INTENT, doWhat);
		// 防止重复生成快捷图标
		i.putExtra("duplicate", false);
		sendBroadcast(i);
	}

	/**
	 * 获得版本信息
	 * 
	 * @return
	 */
	String getVersionName() {
		try {
			// 获取信息
			PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), 0);

			int versionCode = info.versionCode;
			String versionName = info.versionName;
			System.out.println("versionCode=" + versionCode + ": versionName="
					+ versionName);

			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 获取版本Code
	 */
	int getVersionCode() {
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			int versionCode = packageInfo.versionCode;
			return versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 
	 * 从服务器获取版本信息，检查更新
	 */
	private void checkVersion() {
		final long startTime = System.currentTimeMillis();
		// 在子线程中进行网络连接获取数据。
		new Thread() {

			@Override
			public void run() {
				// 获得开启的时间

				Message msg = handler.obtainMessage();
				HttpURLConnection conn = null;

				try {

					URL url = new URL("http://172.18.114.184:8080/version.json");
					// 连接网络获取数据
					conn = (HttpURLConnection) url.openConnection();
					// 设置连接
					conn.setRequestMethod("GET");
					conn.setReadTimeout(5000);
					conn.setConnectTimeout(5000);
					if (conn.getResponseCode() == 200) {
						InputStream is = conn.getInputStream();
						String result = StreamUtils.readStream2String(is);

						// 解析json数据
						JSONObject jos = new JSONObject(result);
						mVersionName = jos.getString("versionName");
						mVersionCode = jos.getInt("versionCode");
						mDes = jos.getString("description");
						mDownUrl = jos.getString("downloadUrl");

						// 输出检测一下
						System.out.println(mDes);

						// 判断版本号
						if (mVersionCode > getVersionCode()) {
							// 说明有新的版本，发送状态吗
							msg.what = CODE_UPDATE_DIALOG;
						} else {
							// 如果没有新版本
							msg.what = CODE_START_HOME;
						}
					}

				} catch (MalformedURLException e) {
					msg.what = CODE_URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					msg.what = CODE_NET_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = CODE_JSON_ERROR;
					e.printStackTrace();
				} finally {
					// 保证停留时间
					long endTime = System.currentTimeMillis();
					long timeUsed = endTime - startTime;// 访问网络花费的时间
					if (timeUsed < 2000) {
						// 如果没有满2秒则睡眠线程
						try {
							Thread.sleep(2000 - timeUsed);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (conn != null) {
						conn.disconnect();
					}

					// 发送Message和断开连接
					handler.sendMessage(msg);
				}
			}
		}.start();
	}

	protected void showUpdataDialog() {
		AlertDialog.Builder builder = new Builder(this);

		builder.setTitle("最新版本" + mVersionName);
		builder.setMessage(mDes);

		builder.setPositiveButton("赶快升级吧", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载数据包
				download();
			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				startHome();
			}
		});

		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				startHome();
			}
		});

		builder.show();
	}

	/**
	 * 下载数据
	 */
	protected void download() {
		HttpUtils xutils = new HttpUtils();

		// 检测存储卡状态
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 将进度条变为可见
			tvProgerss.setVisibility(View.VISIBLE);
			// 构造路径
			String target = Environment.getExternalStorageDirectory()
					+ "/updata.apk";
			System.out.println(mDownUrl);
			System.out.println(target);
			// 开始下载
			xutils.download(mDownUrl, target, new RequestCallBack<File>() {

				// 下载成功的时候
				public void onSuccess(ResponseInfo<File> arg0) {
					Toast.makeText(SplashActivity.this, "下载成功",
							Toast.LENGTH_SHORT).show();

					// 实现自动打开安装页面
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					// type和data如果分开设置是没办法同时设置的
					intent.setDataAndType(Uri.fromFile(arg0.result),
							"application/vnd.android.package-archive");
					// 开启安装
					// startActivity(intent);
					startActivityForResult(intent, 0);
				}

				public void onLoading(long total, long current,
						boolean isUploading) {
					super.onLoading(total, current, isUploading);

					tvProgerss.setText("当前进度:" + current * 100 / total + "%");

					if (total == current) {
						// 如果下载完成则隐藏掉
						tvProgerss.setVisibility(View.GONE);
					}
				}

				// 下载失败的时候
				public void onFailure(HttpException arg0, String arg1) {

					Toast.makeText(SplashActivity.this, "下载失败",
							Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			// 存储卡有问题
			Toast.makeText(SplashActivity.this, "SD卡有问题", Toast.LENGTH_SHORT)
					.show();
			// 这里也可以添加其他逻辑
			startHome();
		}

	}

	/**
	 * 用于按安装界面回调
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println(resultCode);
		startHome();
	}

	void startHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// 一定要finish();
		finish();
	}

	/**
	 * 拷贝数据库
	 * @param dbName
	 */
	private void copyDataBase(String dbName) {
		InputStream in = null;
		FileOutputStream out = null;
		File targetFile = new File(getFilesDir(), dbName);
		
		if(targetFile.exists()) {
			System.out.println("数据库已经存在不用再次拷贝");
			return ;
		}
		
		// 获取到数据库文件
		try {
			in = getAssets().open(dbName);
			out = new FileOutputStream(targetFile);

			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null && out != null) {
				try {
					out.close();
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
