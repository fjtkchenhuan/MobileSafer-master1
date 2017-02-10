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
	 * ��ʼ��UI
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = View.inflate(getActivity(), R.layout.fragment_unlock, null);

		// ������
		tv_unlockapp_count = (TextView) view
				.findViewById(R.id.tv_unlockapp_count);
		lv_unlockAppList = (ListView) view.findViewById(R.id.lv_unlockAppList);
		return view;
	}

	/**
	 * ��ʼ������
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		
		dao = new AppLockDao(getActivity());
		unLockList = new ArrayList<AppInfo>();
		appInfos = AppInfos.getAppInfos(context);
		// �ж���Щ��δ�����ĳ���
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
		// ÿ�����½��붼���·�������,�ﵽˢ�µ�Ŀ��
		unLockList.clear();
		for (AppInfo appInfo : appInfos) {
			if (!dao.query(appInfo.getApkPackageName())) {
				unLockList.add(appInfo);
			}
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * δ���������б�adapter
	 * 
	 * @author Administrator
	 *
	 */
	class UnLockListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			//Ϊ���ܹ���̬�ı�ͷŵ���������ʼ��
			tv_unlockapp_count.setText("δ��������("+ unLockList.size() +")");
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

			//��ӵ�����������
			holder.iv_unlock.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dao.addLockApp(info.getApkPackageName());
					// ��������
					TranslateAnimation animation = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 1.0f,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0);
					animation.setDuration(2000);
					// ��ʼ����
					view.startAnimation(animation);
					new Thread() {
						public void run() {
							SystemClock.sleep(2000);
							unLockList.remove(position);
							// �������߳�ˢ��ui
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
