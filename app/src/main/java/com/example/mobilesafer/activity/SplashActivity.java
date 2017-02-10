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

	// ״̬��
	protected static final int CODE_UPDATE_DIALOG = 0;
	protected static final int CODE_URL_ERROR = 1;
	protected static final int CODE_NET_ERROR = 2;
	protected static final int CODE_JSON_ERROR = 3;
	protected static final int CODE_START_HOME = 4;
	// �ӷ�������ȡ������
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
				Toast.makeText(SplashActivity.this, "URL����", Toast.LENGTH_SHORT)
						.show();
				startHome();
				break;
			case CODE_NET_ERROR:
				Toast.makeText(SplashActivity.this, "�������", Toast.LENGTH_SHORT)
						.show();
				startHome();
				break;
			case CODE_JSON_ERROR:
				Toast.makeText(SplashActivity.this, "���ݽ�������",
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
		tv.setText("�汾��:" + getVersionName());

		tvProgerss = (TextView) findViewById(R.id.tv_progress);

		//�������ݿ�
		copyDataBase("address.db");
		copyDataBase("antivirus.db");
		
		//������ݷ�ʽ
		createShortcut();
		
		// ��ȡ�����ļ����ж��Ƿ��Զ�����
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		Boolean autoUpdate = sp.getBoolean("auto_update", true);
		if (autoUpdate) {
			checkVersion();
		} else {
			// ������ʱ��Ϣ
			handler.sendEmptyMessageDelayed(CODE_START_HOME, 2000);
		}
	}

	/**
	 * ������ݷ�ʽ
	 */
	private void createShortcut() {
		Intent i = new Intent();
		i.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		i.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, R.drawable.ic_launcher);//����ͼ��
		i.putExtra(Intent.EXTRA_SHORTCUT_NAME , "�ֻ���ʿ");
		
		Intent doWhat = new Intent();
		doWhat.setAction("com.dwb.mobilesafer");
		doWhat.addCategory(Intent.CATEGORY_DEFAULT);
		i.putExtra(Intent.EXTRA_SHORTCUT_INTENT, doWhat);
		// ��ֹ�ظ����ɿ��ͼ��
		i.putExtra("duplicate", false);
		sendBroadcast(i);
	}

	/**
	 * ��ð汾��Ϣ
	 * 
	 * @return
	 */
	String getVersionName() {
		try {
			// ��ȡ��Ϣ
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
	 * ��ȡ�汾Code
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
	 * �ӷ�������ȡ�汾��Ϣ��������
	 */
	private void checkVersion() {
		final long startTime = System.currentTimeMillis();
		// �����߳��н����������ӻ�ȡ���ݡ�
		new Thread() {

			@Override
			public void run() {
				// ��ÿ�����ʱ��

				Message msg = handler.obtainMessage();
				HttpURLConnection conn = null;

				try {

					URL url = new URL("http://172.18.114.184:8080/version.json");
					// ���������ȡ����
					conn = (HttpURLConnection) url.openConnection();
					// ��������
					conn.setRequestMethod("GET");
					conn.setReadTimeout(5000);
					conn.setConnectTimeout(5000);
					if (conn.getResponseCode() == 200) {
						InputStream is = conn.getInputStream();
						String result = StreamUtils.readStream2String(is);

						// ����json����
						JSONObject jos = new JSONObject(result);
						mVersionName = jos.getString("versionName");
						mVersionCode = jos.getInt("versionCode");
						mDes = jos.getString("description");
						mDownUrl = jos.getString("downloadUrl");

						// ������һ��
						System.out.println(mDes);

						// �жϰ汾��
						if (mVersionCode > getVersionCode()) {
							// ˵�����µİ汾������״̬��
							msg.what = CODE_UPDATE_DIALOG;
						} else {
							// ���û���°汾
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
					// ��֤ͣ��ʱ��
					long endTime = System.currentTimeMillis();
					long timeUsed = endTime - startTime;// �������绨�ѵ�ʱ��
					if (timeUsed < 2000) {
						// ���û����2����˯���߳�
						try {
							Thread.sleep(2000 - timeUsed);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (conn != null) {
						conn.disconnect();
					}

					// ����Message�ͶϿ�����
					handler.sendMessage(msg);
				}
			}
		}.start();
	}

	protected void showUpdataDialog() {
		AlertDialog.Builder builder = new Builder(this);

		builder.setTitle("���°汾" + mVersionName);
		builder.setMessage(mDes);

		builder.setPositiveButton("�Ͽ�������", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// �������ݰ�
				download();
			}
		});
		builder.setNegativeButton("�´���˵", new OnClickListener() {

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
	 * ��������
	 */
	protected void download() {
		HttpUtils xutils = new HttpUtils();

		// ���洢��״̬
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// ����������Ϊ�ɼ�
			tvProgerss.setVisibility(View.VISIBLE);
			// ����·��
			String target = Environment.getExternalStorageDirectory()
					+ "/updata.apk";
			System.out.println(mDownUrl);
			System.out.println(target);
			// ��ʼ����
			xutils.download(mDownUrl, target, new RequestCallBack<File>() {

				// ���سɹ���ʱ��
				public void onSuccess(ResponseInfo<File> arg0) {
					Toast.makeText(SplashActivity.this, "���سɹ�",
							Toast.LENGTH_SHORT).show();

					// ʵ���Զ��򿪰�װҳ��
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					// type��data����ֿ�������û�취ͬʱ���õ�
					intent.setDataAndType(Uri.fromFile(arg0.result),
							"application/vnd.android.package-archive");
					// ������װ
					// startActivity(intent);
					startActivityForResult(intent, 0);
				}

				public void onLoading(long total, long current,
						boolean isUploading) {
					super.onLoading(total, current, isUploading);

					tvProgerss.setText("��ǰ����:" + current * 100 / total + "%");

					if (total == current) {
						// ���������������ص�
						tvProgerss.setVisibility(View.GONE);
					}
				}

				// ����ʧ�ܵ�ʱ��
				public void onFailure(HttpException arg0, String arg1) {

					Toast.makeText(SplashActivity.this, "����ʧ��",
							Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			// �洢��������
			Toast.makeText(SplashActivity.this, "SD��������", Toast.LENGTH_SHORT)
					.show();
			// ����Ҳ������������߼�
			startHome();
		}

	}

	/**
	 * ���ڰ���װ����ص�
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
		// һ��Ҫfinish();
		finish();
	}

	/**
	 * �������ݿ�
	 * @param dbName
	 */
	private void copyDataBase(String dbName) {
		InputStream in = null;
		FileOutputStream out = null;
		File targetFile = new File(getFilesDir(), dbName);
		
		if(targetFile.exists()) {
			System.out.println("���ݿ��Ѿ����ڲ����ٴο���");
			return ;
		}
		
		// ��ȡ�����ݿ��ļ�
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
