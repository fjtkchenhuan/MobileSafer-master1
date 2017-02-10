package com.example.mobilesafer.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.format.Formatter;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilesafer.R;
import com.example.mobilesafer.utils.UIUtils;

/**
 * ����������� APIԭ��������android4.2�������У������ܻ������
 * 
 * @author admin
 *
 */
public class CleanCacheActivity extends Activity {

	private ArrayList<TempCacheInfo> results;
	private PackageManager packageManager;
	private ListView lv_cache;
	private List<PackageInfo> installedPackages;
	
	/*
	 * loading������ͼ�Լ�����
	 */
	private ImageView iv_1;
	private ImageView iv_2;
	private ImageView iv_3;
	private ImageView iv_4;
	private FrameLayout fl_loading;
	

	private Handler handle = new Handler() {
		private MyCacheListAdapter adapter;

		public void handleMessage(android.os.Message msg) {
			
			int count = lv_cache.getCount();
			//�ж��Ƿ�������.����������ؼ��ز�
			if(count == results.size()) {
				fl_loading.setVisibility(View.GONE);
				System.out.println("gone");
			} else {
				fl_loading.setVisibility(View.VISIBLE);
			}
			
			System.out.println("count:" + count);
			System.out.println("the size of results :" + results.size());
			System.out.println("------------------------------");
			
			if (adapter == null) {
				adapter = new MyCacheListAdapter();
				lv_cache.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
		};
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initData();
		initUI();
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		// ˢ������
		new Thread() {
			@Override
			public void run() {
				super.run();
				for (PackageInfo packageInfo : installedPackages) {
					queryAppCache(packageInfo);
				}
				handle.sendEmptyMessage(0);
			}
		}.start();
	}

	/**
	 * ��ʼ������
	 */
	private void initData() {
		// ���ڴ洢����
		results = new ArrayList<TempCacheInfo>();

		packageManager = getPackageManager();

		installedPackages = packageManager.getInstalledPackages(0);

		new Thread() {
			@Override
			public void run() {
				super.run();
				for (PackageInfo packageInfo : installedPackages) {
					queryAppCache(packageInfo);
				}
				// handle.sendEmptyMessage(0);
			}
		}.start();
	}

	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		setContentView(R.layout.activity_clean_cache);
		lv_cache = (ListView) findViewById(R.id.lv_cache);
		//���Loading�õ�ͼ
		iv_1 = (ImageView) findViewById(R.id.iv_1);
		iv_2 = (ImageView) findViewById(R.id.iv_2);
		iv_3 = (ImageView) findViewById(R.id.iv_3);
		iv_4 = (ImageView) findViewById(R.id.iv_4);
		fl_loading = (FrameLayout) findViewById(R.id.fl_loading);
		
		setLoadingAnimation();
	}

	/**
	 * ����loading�Ķ���
	 */
	private void setLoadingAnimation() {
		Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotation);
		iv_1.startAnimation(rotation);
		iv_2.startAnimation(rotation);
		iv_3.startAnimation(rotation);
		iv_4.startAnimation(rotation);
	}

	/**
	 * ��ѯ�����Ӧ�õĻ������
	 * 
	 * @param packageName
	 *            ���ػ������
	 * @return
	 */
	private void queryAppCache(PackageInfo packageInfo) {
		if (!(packageInfo == null)) {
			// ��÷���
			int uid = 0;
			try {
				Method getPackageSizeInfoMethod = packageManager.getClass()
						.getDeclaredMethod("getPackageSizeInfo", String.class,
								int.class, IPackageStatsObserver.class);

				Method myUserId = UserHandle.class
						.getDeclaredMethod("myUserId");

				uid = (Integer) myUserId.invoke(packageManager, null);

				// װ����
				getPackageSizeInfoMethod.invoke(packageManager,
						packageInfo.packageName, uid,
						new MyIPackageStatsObserver(packageInfo));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * ���ڻ�ȡ���� ע��Ȩ�ޣ�android.permission.GET_PACKAGE_SIZE
	 */
	class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {
		private PackageInfo packageInfo;

		public MyIPackageStatsObserver(PackageInfo packageInfo) {
			this.packageInfo = packageInfo;
		}

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			if (pStats.cacheSize > 0 && packageInfo != null) {
//				System.out.println("1");
				
				TempCacheInfo info = new TempCacheInfo();

				String appName = packageInfo.applicationInfo.loadLabel(
						packageManager).toString();
				Drawable icon = packageInfo.applicationInfo
						.loadIcon(packageManager);
				String packageName = packageInfo.packageName;
				// ��ʽ��
				String cacheStats = Formatter.formatFileSize(
						CleanCacheActivity.this, pStats.cacheSize);

				info.appName = appName;
				info.icon = icon;
				info.cacheSize = cacheStats;
				info.packageName = packageName;

				results.add(info);
				handle.sendEmptyMessage(0);
			}
		}
	}

	/**
	 * ��������
	 * �Ժ����
	 * ע��Ȩ�ޣ�android.permission.CLEAR_APP_CACHE
	 * @param v
	 */
	public void cleanAll(View v) {
		//��ȡ��ǰӦ�ó������еķ���
		Method[] methods = packageManager.getClass().getMethods();
		for (Method method : methods) {
			if(method.getName().equals("freeStorageAndNotify")) {
				try {
					method.invoke(packageManager, Integer.MAX_VALUE , new MyPackageDataObserver());
					Log.e("test", "invok~~~~~");
				} catch(Exception e) {
					e.printStackTrace();
				}
			} 
		}
		UIUtils.showToast(this, "ȫ�����");
	}
	
	/**
	 * ������������õ���
	 * @author admin
	 *
	 */
	class MyPackageDataObserver implements IPackageDataObserver{

		@Override
		public IBinder asBinder() {
			return null;
		}

		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			Log.d("test", packageName + ":" + succeeded);
		}
		
	}

	/**
	 * ���ڳ�ʼ��ListView��Adapter
	 * 
	 * @author admin
	 *
	 */
	class MyCacheListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return results.size();
		}

		@Override
		public Object getItem(int position) {
			return results.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = View.inflate(CleanCacheActivity.this,
						R.layout.item_clean_cache, null);
				holder = new ViewHolder();

				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_app_name = (TextView) view
						.findViewById(R.id.tv_app_name);
				holder.tv_cache_size = (TextView) view
						.findViewById(R.id.tv_cache_size);
				holder.iv_clean_cache = (ImageView) view
						.findViewById(R.id.iv_clean_cache);

				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			TempCacheInfo adapterInfo = results.get(position);

			holder.iv_icon.setBackground(adapterInfo.icon);
			holder.tv_app_name.setText(adapterInfo.appName);
			holder.tv_cache_size.setText("�����С:" + adapterInfo.cacheSize);

			// ���ü����¼�
			holder.iv_clean_cache
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							TempCacheInfo item = (TempCacheInfo) getItem(position);
							// �Լ�����ɾ��
							Intent detail_intent = new Intent();
							detail_intent
									.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
							detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
							detail_intent.setData(Uri.parse("package:"
									+ item.packageName));
							startActivity(detail_intent);
						}
					});

			return view;
		}

		class ViewHolder {
			ImageView iv_icon;
			TextView tv_app_name;
			TextView tv_cache_size;
			ImageView iv_clean_cache;
		}

	}

	/*
	 * ��ʱװ�����ݶ���
	 */
	class TempCacheInfo {
		private Drawable icon;
		private String appName;
		private String cacheSize;
		private String packageName;
	}
}
