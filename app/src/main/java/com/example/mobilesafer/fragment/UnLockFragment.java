package com.example.mobilesafer.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilesafer.R;
import com.example.mobilesafer.bean.AppInfo;
import com.example.mobilesafer.db.dao.AppLockDao;
import com.example.mobilesafer.engine.AppInfos;

public class UnLockFragment extends Fragment {

	private TextView tv_unlockapp_count;
	private ListView lv_unlockAppList;
	private List<AppInfo> appInfos;
	private Context context;
	private List<AppInfo> unLockList;
	private UnLockListAdapter adapter;
	private AppLockDao dao;

	/**
	 * 初始化UI
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = View.inflate(getActivity(), R.layout.fragment_unlock, null);

		// 获得组件
		tv_unlockapp_count = (TextView) view
				.findViewById(R.id.tv_unlockapp_count);
		lv_unlockAppList = (ListView) view.findViewById(R.id.lv_unlockAppList);
		return view;
	}

	/**
	 * 初始化数据
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		
		dao = new AppLockDao(getActivity());
		unLockList = new ArrayList<AppInfo>();
		appInfos = AppInfos.getAppInfos(context);
		// 判断哪些是未加锁的程序
		for (AppInfo appInfo : appInfos) {
			if (!dao.query(appInfo.getApkPackageName())) {
				unLockList.add(appInfo);
			}
		}
		adapter = new UnLockListAdapter();
		lv_unlockAppList.setAdapter(adapter);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// 每次重新进入都重新分类数据,达到刷新的目的
		unLockList.clear();
		for (AppInfo appInfo : appInfos) {
			if (!dao.query(appInfo.getApkPackageName())) {
				unLockList.add(appInfo);
			}
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 未加锁程序列表adapter
	 * 
	 * @author Administrator
	 *
	 */
	class UnLockListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			//为了能够动态改变就放到这里来初始化
			tv_unlockapp_count.setText("未加锁程序("+ unLockList.size() +")");
			return unLockList.size();
		}

		@Override
		public Object getItem(int position) {
			return unLockList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final View view;
			ViewHolder holder = null;
			final AppInfo info;

			if (convertView == null) {
				view = View.inflate(context, R.layout.item_unlock, null);

				holder = new ViewHolder();
				holder.iv_app_icon = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				holder.tv_app_name = (TextView) view
						.findViewById(R.id.tv_app_name);
				holder.iv_unlock = (ImageView) view
						.findViewById(R.id.iv_unlock);

				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			info = unLockList.get(position);
			holder.iv_app_icon.setBackgroundDrawable(info.getIcon());
			holder.tv_app_name.setText(info.getApkName());

			//添加到加锁程序中
			holder.iv_unlock.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dao.addLockApp(info.getApkPackageName());
					// 创建动画
					TranslateAnimation animation = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 1.0f,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0);
					animation.setDuration(2000);
					// 开始动画
					view.startAnimation(animation);
					new Thread() {
						public void run() {
							SystemClock.sleep(2000);
							unLockList.remove(position);
							// 返回主线程刷新ui
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									adapter.notifyDataSetChanged();
								}
							});
						};
					}.start();
				}
			});
			return view;
		}

		class ViewHolder {
			ImageView iv_app_icon;
			TextView tv_app_name;
			ImageView iv_unlock;
		}
	}
}
