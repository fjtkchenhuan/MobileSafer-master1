package com.example.mobilesafer.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilesafer.R;
import com.example.mobilesafer.bean.AppInfo;
import com.example.mobilesafer.engine.AppInfos;


/**
 * 流量管理界面
 * @author admin
 *
 */
public class TrafficManagerActivity extends Activity {
	private ListView lv_apps;
	private List<TempApp> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initUI();
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		TrafficStats trafficStats = new TrafficStats();
		
		list = new ArrayList<TrafficManagerActivity.TempApp>();
		List<AppInfo> appsUid = AppInfos.getAppsUid(this);
		
		for (AppInfo appInfo : appsUid) {
			TempApp ta = new TempApp();
			
			long uidRxBytes = trafficStats.getUidRxBytes(appInfo.getUid());
			long uidTxBytes = trafficStats.getUidTxBytes(appInfo.getUid());
			Drawable icon = appInfo.getIcon();
			
			ta.AppName = appInfo.getApkName();
			ta.rx = uidRxBytes;
			ta.tx = uidTxBytes;
			ta.icon = icon;
			
			list.add(ta);
		}
		
		//对list进行排序
		Comparator<TempApp> comparator = new Comparator<TrafficManagerActivity.TempApp>() {
			@Override
			public int compare(TempApp lhs, TempApp rhs) {
				int lhst = (int) (lhs.rx + lhs.tx);
				int rhst = (int) (rhs.rx + rhs.tx);
				if(lhst >= rhst) {
					return lhst;
				}
				return rhst;
			}
		};
		Collections.sort(list, comparator);
		
		for (TempApp appInfo : list) {
			System.out.println(appInfo.AppName + ":" + appInfo.rx + "," + appInfo.tx);
			System.out.println("------------------------------------------------------");
		}
	}
	

	/**
	 * 初始化UI
	 */
	private void initUI() {
		
		setContentView(R.layout.activity_traffic);
		lv_apps = (ListView) findViewById(R.id.lv_apps);
		
		TrafficListAdapter adapter = new TrafficListAdapter();
		lv_apps.setAdapter(adapter);
	}
	
	class TempApp{
		private String AppName;
		private Drawable icon;
		private long rx;
		private long tx;
	}
	
	/**
	 * 用于初始化ListView的Adapter
	 * @author admin
	 *
	 */
	class TrafficListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if(convertView == null) {
				view = View.inflate(TrafficManagerActivity.this, R.layout.item_traffic_manager, null);
				
				holder = new ViewHolder();
				holder.iv_icon = (ImageView)view.findViewById(R.id.iv_icon);
				holder.tv_appname = (TextView) view.findViewById(R.id.tv_appname);
				holder.tv_rx = (TextView) view.findViewById(R.id.tv_rx);
				holder.tv_tx = (TextView) view.findViewById(R.id.tv_tx);
				
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			TempApp info = (TempApp) getItem(position);
			
			holder.iv_icon.setBackground(info.icon);
			holder.tv_appname.setText(info.AppName);
			String rx = Formatter.formatFileSize(TrafficManagerActivity.this, info.rx);
			String tx = Formatter.formatFileSize(TrafficManagerActivity.this, info.tx);
			holder.tv_rx.setText("上传:" + rx);
			holder.tv_tx.setText("下载:" + tx);
			
			return view;
		}
		
		class ViewHolder {
			ImageView iv_icon;
			TextView tv_appname;
			TextView tv_rx;
			TextView tv_tx;
		}
	}
	
}
