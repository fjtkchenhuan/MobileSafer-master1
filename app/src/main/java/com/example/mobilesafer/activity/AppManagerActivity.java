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
	 * 初始化数据
	 */
	private void initData() {
		// 设置容量
		setFreeSpace();

		/*
		 * 设置list，因为是耗时操作，放到子线程中
		 */
		new Thread() {

			public void run() {
				appInfoList = AppInfos.getAppInfos(AppManagerActivity.this);
				userApps = new ArrayList<AppInfo>();
				systemApps = new ArrayList<AppInfo>();
				// 对数据进行分类
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
		 * 为ListView添加滚动监听，用于改变类别textView
		 */
		list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			/**
			 * @param firstVisibleItem
			 *            第一个可见条目的positon
			 * @param visibleItemCount
			 *            一页可见条目的数量
			 * @param totalItemCount
			 *            一共有多少条目
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				if (userApps != null && systemApps != null) {
					if (firstVisibleItem < (userApps.size() + 1)) {
						// 用户程序
						tv_app.setText("用户程序（" + userApps.size() + ")");
					} else {
						// 系统程序
						tv_app.setText("系统程序（" + systemApps.size() + ")");
					}
				}
			}
		});

		/*
		 * 添加单击监听
		 */
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 如果已经存在一个popup了
				dismissPopupWindow();
				// 获得当前的对象,判断类型
				Object item = list.getItemAtPosition(position);
				// 用于在onclick中获取数据
				itemAppInfo = (AppInfo) item;
				if (item != null && item instanceof AppInfo) {

					/*
					 * 生成一个popupWindow -2表示为包裹内容
					 */
					// View contentView = View.inflate(AppManagerActivity.this,
					// R.layout.item_popup, null);
					pop = new PopupWindow(contentView, -2, -2);
					// 需要注意：使用PopupWindow 必须设置背景。不然没有动画
					pop.setBackgroundDrawable(new ColorDrawable(
							Color.TRANSPARENT));

					// 设置动画
					ScaleAnimation sa = new ScaleAnimation(0.5f, 1f, 0.5f, 1f,
							Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					sa.setDuration(500);
					contentView.setAnimation(sa);

					/*
					 * 设置显示位置 当前view（也就是item）的坐标信息
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
	 * 让已经存在的消失
	 */
	protected void dismissPopupWindow() {
		if (pop != null) {
			pop.dismiss();
			pop = null;
		}
	}

	/**
	 * 初始化ui
	 */
	private void initUI() {
		tv_rom = (TextView) findViewById(R.id.tv_rom);
		tv_sd = (TextView) findViewById(R.id.tv_sd);

		tv_app = (TextView) findViewById(R.id.tv_app);
		list = (ListView) findViewById(R.id.list);

		/*
		 * 用于popupwindow的view 对象 因为需要为其内部对象添加单击事件，所以提前拿出来 一次完成监听设置
		 */
		contentView = View.inflate(AppManagerActivity.this,
				R.layout.item_popup, null);
		// 添加popupWindow中的按钮单击事件
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
			// 这里需要加上两个textView
			return appInfoList.size() + 2;
		}

		@Override
		public Object getItem(int position) {
			// 需要根据角标进行变化
			if (position == 0 || position == (userApps.size() + 1)) {
				return null; // 这个时候返回的是textview
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
			// 判断角标，特殊角标返回textView
			if (position == 0) {
				// 第一个位置
				TextView textView = new TextView(AppManagerActivity.this);
				textView.setText("用户程序（" + userApps.size() + ")");
				textView.setTextColor(Color.WHITE);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextSize(15);
				return textView;
			} else if (position == userApps.size() + 1) {
				// 下一个特殊位置
				TextView textView = new TextView(AppManagerActivity.this);
				textView.setText("系统程序（" + systemApps.size() + ")");
				textView.setTextColor(Color.WHITE);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextSize(15);
				return textView;
			}

			View view = null;
			ViewHolder holder = null;

			// 防止textView
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
			// 判断该取哪个list
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
				holder.tv_applocation.setText("手机");
			} else {
				holder.tv_applocation.setText("手机内存");
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
	 * 获取剩余空间，并对两个text设置
	 */

	private void setFreeSpace() {
		// 获取rom剩余空间
		long rom_space = Environment.getDataDirectory().getFreeSpace();
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 获取sd卡剩余空间
			long sd_space = Environment.getExternalStorageDirectory()
					.getFreeSpace();
			tv_sd.setText("SD卡空间剩余：" + Formatter.formatFileSize(this, sd_space));
		} else {
			tv_sd.setText("外部存储不可用");
		}
		// 对数据进行格式化,并设置UI
		tv_rom.setText("手机存储空间剩余：" + Formatter.formatFileSize(this, rom_space));

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissPopupWindow();
	}

	/**
	 * 用于判断卸载，详情，分享，运行，并实现逻辑
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/*
		 * 卸载
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
		 * 详情
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
		 * 运行
		 */
		case R.id.ll_run:

			Intent start_localIntent = this.getPackageManager()
					.getLaunchIntentForPackage(itemAppInfo.getApkPackageName());
			startActivity(start_localIntent);
			
			dismissPopupWindow();
			break;

		/*
		 * 分享
		 */
		case R.id.ll_share:

			Intent share_localIntent = new Intent("android.intent.action.SEND");
			share_localIntent.setType("text/plain");
			share_localIntent.putExtra("android.intent.extra.SUBJECT", "f分享");
			share_localIntent.putExtra("android.intent.extra.TEXT",
					"Hi！推荐您使用软件：" + itemAppInfo.getApkName() + "下载地址:"
							+ "https://play.google.com/store/apps/details?id="
							+ itemAppInfo.getApkPackageName());
			startActivity(Intent.createChooser(share_localIntent, "分享"));

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
