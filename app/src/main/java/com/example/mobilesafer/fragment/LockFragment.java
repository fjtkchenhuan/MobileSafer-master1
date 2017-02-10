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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilesafer.R;
import com.example.mobilesafer.bean.AppInfo;
import com.example.mobilesafer.bean.TaskInfo;
import com.example.mobilesafer.db.dao.AppLockDao;
import com.example.mobilesafer.engine.AppInfos;
import com.example.mobilesafer.engine.TaskInfoParser;
import com.example.mobilesafer.fragment.UnLockFragment.UnLockListAdapter.ViewHolder;

public class LockFragment extends Fragment {
	private TextView tv_lockapp_count;
	private ListView lv_lockAppList;
	private Context context;
	private AppLockDao dao;
	private List<AppInfo> lockAppList;
	private LockListAdapter adapter;
	private List<AppInfo> appInfos;

	/**
	 * ��ʼ��UI
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = View.inflate(getActivity(), R.layout.fragment_lock, null);

		// ������
		tv_lockapp_count = (TextView) view.findViewById(R.id.tv_lockapp_count);
		lv_lockAppList = (ListView) view.findViewById(R.id.lv_lockAppList);

		return view;
	}

	/**
	 * ��ʼ������
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		
		dao = new AppLockDao(context);
		appInfos = AppInfos.getAppInfos(context);
		lockAppList = new ArrayList<AppInfo>();
		for(AppInfo info : appInfos) {
			if(dao.query(info.getApkPackageName())) {
				lockAppList.add(info);
			}
		}
		adapter = new LockListAdapter();
		lv_lockAppList.setAdapter(adapter);
		
	}
	@Override
	public void onStart() {
		super.onStart();
		lockAppList.clear();
		for(AppInfo info : appInfos) {
			if(dao.query(info.getApkPackageName())) {
				lockAppList.add(info);
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
	class LockListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			//Ϊ���ܹ���̬�ı�ͷŵ���������ʼ��
			tv_lockapp_count.setText("��������("+ lockAppList.size() +")");
			return lockAppList.size();
		}

		@Override
		public Object getItem(int position) {
			return lockAppList.get(position);
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
				view = View.inflate(context, R.layout.item_lock, null);

				holder = new ViewHolder();
				holder.iv_app_icon = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				holder.tv_app_name = (TextView) view
						.findViewById(R.id.tv_app_name);
				holder.iv_lock = (ImageView) view
						.findViewById(R.id.iv_lock);

				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			info = lockAppList.get(position);
			holder.iv_app_icon.setBackgroundDrawable(info.getIcon());
			holder.tv_app_name.setText(info.getApkName());

			//��ӵ�����������
			holder.iv_lock.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dao.delete(info.getApkPackageName());
					// ��������
					TranslateAnimation animation = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, -1.0f,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0);
					animation.setDuration(2000);
					// ��ʼ����
					view.startAnimation(animation);
					new Thread() {
						public void run() {
							SystemClock.sleep(2000);
							lockAppList.remove(position);
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
			ImageView iv_lock;
		}
	}

}
