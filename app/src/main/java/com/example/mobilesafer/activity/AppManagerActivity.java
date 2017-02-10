package com.example.mobilesafer.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.cardemulation.OffHostApduService;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobilesafer.R;
import com.example.mobilesafer.adapter.MyBaseAdapter;
import com.example.mobilesafer.bean.AppInfo;
import com.example.mobilesafer.engine.AppInfos;

public class AppManagerActivity extends Activity implements
		View.OnClickListener {
	private TextView tv_rom;
	private TextView tv_sd;
	private ListView list;
	private List<AppInfo> appInfoList;
	private List<AppInfo> systemApps;
	private List<AppInfo> userApps;
	private TextView tv_app;
	private AppInfo itemAppInfo;
	private PopupWindow pop;
	private AppAdapter adapter;

	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (adapter == null) {

				adapter = new AppAdapter();
				list.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
		};
	};
	private View contentView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);

		initUI();
		initData();
	}

	/**
	 * ��ʼ������
	 */
	private void initData() {
		// ��������
		setFreeSpace();

		/*
		 * ����list����Ϊ�Ǻ�ʱ�������ŵ����߳���
		 */
		new Thread() {

			public void run() {
				appInfoList = AppInfos.getAppInfos(AppManagerActivity.this);
				userApps = new ArrayList<AppInfo>();
				systemApps = new ArrayList<AppInfo>();
				// �����ݽ��з���
				for (AppInfo info : appInfoList) {
					if (info.isUserApp()) {
						userApps.add(info);
					} else {
						systemApps.add(info);
					}
				}
				handler.sendEmptyMessage(0);
			};
		}.start();

		/*
		 * ΪListView��ӹ������������ڸı����textView
		 */
		list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			/**
			 * @param firstVisibleItem
			 *            ��һ���ɼ���Ŀ��positon
			 * @param visibleItemCount
			 *            һҳ�ɼ���Ŀ������
			 * @param totalItemCount
			 *            һ���ж�����Ŀ
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				if (userApps != null && systemApps != null) {
					if (firstVisibleItem < (userApps.size() + 1)) {
						// �û�����
						tv_app.setText("�û�����" + userApps.size() + ")");
					} else {
						// ϵͳ����
						tv_app.setText("ϵͳ����" + systemApps.size() + ")");
					}
				}
			}
		});

		/*
		 * ��ӵ�������
		 */
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// ����Ѿ�����һ��popup��
				dismissPopupWindow();
				// ��õ�ǰ�Ķ���,�ж�����
				Object item = list.getItemAtPosition(position);
				// ������onclick�л�ȡ����
				itemAppInfo = (AppInfo) item;
				if (item != null && item instanceof AppInfo) {

					/*
					 * ����һ��popupWindow -2��ʾΪ��������
					 */
					// View contentView = View.inflate(AppManagerActivity.this,
					// R.layout.item_popup, null);
					pop = new PopupWindow(contentView, -2, -2);
					// ��Ҫע�⣺ʹ��PopupWindow �������ñ�������Ȼû�ж���
					pop.setBackgroundDrawable(new ColorDrawable(
							Color.TRANSPARENT));

					// ���ö���
					ScaleAnimation sa = new ScaleAnimation(0.5f, 1f, 0.5f, 1f,
							Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					sa.setDuration(500);
					contentView.setAnimation(sa);

					/*
					 * ������ʾλ�� ��ǰview��Ҳ����item����������Ϣ
					 */
					int[] location = new int[2];
					view.getLocationInWindow(location);
					pop.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 50,
							location[1]);

				}
			}
		});
	}

	/**
	 * ���Ѿ����ڵ���ʧ
	 */
	protected void dismissPopupWindow() {
		if (pop != null) {
			pop.dismiss();
			pop = null;
		}
	}

	/**
	 * ��ʼ��ui
	 */
	private void initUI() {
		tv_rom = (TextView) findViewById(R.id.tv_rom);
		tv_sd = (TextView) findViewById(R.id.tv_sd);

		tv_app = (TextView) findViewById(R.id.tv_app);
		list = (ListView) findViewById(R.id.list);

		/*
		 * ����popupwindow��view ���� ��Ϊ��ҪΪ���ڲ�������ӵ����¼���������ǰ�ó��� һ����ɼ�������
		 */
		contentView = View.inflate(AppManagerActivity.this,
				R.layout.item_popup, null);
		// ���popupWindow�еİ�ť�����¼�
		LinearLayout ll_unInstall = (LinearLayout) contentView
				.findViewById(R.id.ll_unInstall);
		LinearLayout ll_run = (LinearLayout) contentView
				.findViewById(R.id.ll_run);
		LinearLayout ll_share = (LinearLayout) contentView
				.findViewById(R.id.ll_share);
		LinearLayout ll_detail = (LinearLayout) contentView
				.findViewById(R.id.ll_detail);
		ll_unInstall.setOnClickListener(this);
		ll_run.setOnClickListener(this);
		ll_share.setOnClickListener(this);
		ll_detail.setOnClickListener(this);
	}

	/*
	 * adapter
	 */

	class AppAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// ������Ҫ��������textView
			return appInfoList.size() + 2;
		}

		@Override
		public Object getItem(int position) {
			// ��Ҫ���ݽǱ���б仯
			if (position == 0 || position == (userApps.size() + 1)) {
				return null; // ���ʱ�򷵻ص���textview
			}
			AppInfo appInfo = null;
			if (position <= userApps.size()) {
				appInfo = userApps.get(position - 1);
			} else if (position >= userApps.size() + 2) {
				appInfo = systemApps.get(position - userApps.size() - 2);
			}
			return appInfo;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// �жϽǱ꣬����Ǳ귵��textView
			if (position == 0) {
				// ��һ��λ��
				TextView textView = new TextView(AppManagerActivity.this);
				textView.setText("�û�����" + userApps.size() + ")");
				textView.setTextColor(Color.WHITE);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextSize(15);
				return textView;
			} else if (position == userApps.size() + 1) {
				// ��һ������λ��
				TextView textView = new TextView(AppManagerActivity.this);
				textView.setText("ϵͳ����" + systemApps.size() + ")");
				textView.setTextColor(Color.WHITE);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextSize(15);
				return textView;
			}

			View view = null;
			ViewHolder holder = null;

			// ��ֹtextView
			if (convertView != null && convertView instanceof RelativeLayout) {

				view = convertView;
				holder = (ViewHolder) convertView.getTag();

			} else {

				view = View.inflate(AppManagerActivity.this,
						R.layout.item_app_manager, null);
				holder = new ViewHolder();

				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_appname = (TextView) view
						.findViewById(R.id.tv_appname);
				holder.tv_applocation = (TextView) view
						.findViewById(R.id.tv_applocation);
				holder.tv_appsize = (TextView) view
						.findViewById(R.id.tv_appsize);

				view.setTag(holder);
			}
			// �жϸ�ȡ�ĸ�list
			AppInfo info;
			if (position <= userApps.size()) {
				info = userApps.get(position - 1);
			} else {
				info = systemApps.get(position - userApps.size() - 2);
			}
			holder.iv_icon.setImageDrawable(info.getIcon());
			holder.tv_appname.setText(info.getApkName());
			holder.tv_appsize.setText(Formatter.formatFileSize(
					AppManagerActivity.this, info.getApkSize()).toString());
			if (info.isInRom()) {
				holder.tv_applocation.setText("�ֻ�");
			} else {
				holder.tv_applocation.setText("�ֻ��ڴ�");
			}
			return view;
		}
	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_appname;
		TextView tv_applocation;
		TextView tv_appsize;
	}

	/**
	 * ��ȡʣ��ռ䣬��������text����
	 */

	private void setFreeSpace() {
		// ��ȡromʣ��ռ�
		long rom_space = Environment.getDataDirectory().getFreeSpace();
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// ��ȡsd��ʣ��ռ�
			long sd_space = Environment.getExternalStorageDirectory()
					.getFreeSpace();
			tv_sd.setText("SD���ռ�ʣ�ࣺ" + Formatter.formatFileSize(this, sd_space));
		} else {
			tv_sd.setText("�ⲿ�洢������");
		}
		// �����ݽ��и�ʽ��,������UI
		tv_rom.setText("�ֻ��洢�ռ�ʣ�ࣺ" + Formatter.formatFileSize(this, rom_space));

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissPopupWindow();
	}

	/**
	 * �����ж�ж�أ����飬�������У���ʵ���߼�
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/*
		 * ж��
		 */
		case R.id.ll_unInstall:

			Intent uninstall_localIntent = new Intent(
					"android.intent.action.DELETE", Uri.parse("package:"
							+ itemAppInfo.getApkPackageName()));
			startActivityForResult(uninstall_localIntent , 0);
			adapter.notifyDataSetChanged();
			dismissPopupWindow();
			break;

		/*
		 * ����
		 */
		case R.id.ll_detail:

			Intent detail_intent = new Intent();
			detail_intent
					.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
			detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
			detail_intent.setData(Uri.parse("package:"
					+ itemAppInfo.getApkPackageName()));
			startActivity(detail_intent);
			

			dismissPopupWindow();
			break;

		/*
		 * ����
		 */
		case R.id.ll_run:

			Intent start_localIntent = this.getPackageManager()
					.getLaunchIntentForPackage(itemAppInfo.getApkPackageName());
			startActivity(start_localIntent);
			
			dismissPopupWindow();
			break;

		/*
		 * ����
		 */
		case R.id.ll_share:

			Intent share_localIntent = new Intent("android.intent.action.SEND");
			share_localIntent.setType("text/plain");
			share_localIntent.putExtra("android.intent.extra.SUBJECT", "f����");
			share_localIntent.putExtra("android.intent.extra.TEXT",
					"Hi���Ƽ���ʹ�������" + itemAppInfo.getApkName() + "���ص�ַ:"
							+ "https://play.google.com/store/apps/details?id="
							+ itemAppInfo.getApkPackageName());
			startActivity(Intent.createChooser(share_localIntent, "����"));

			dismissPopupWindow();
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("1");
		if(requestCode == 0) {
			adapter.notifyDataSetChanged();
		}
	}
	
}
