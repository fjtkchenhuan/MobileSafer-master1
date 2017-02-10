package com.example.mobilesafer.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilesafer.R;
import com.example.mobilesafer.bean.TaskInfo;
import com.example.mobilesafer.engine.TaskInfoParser;
import com.example.mobilesafer.utils.SystemInfoUtils;
import com.example.mobilesafer.utils.UIUtils;

/**
 * ���̹���ҳ��
 * 
 * @author admin
 *
 */
public class TaskManagerActivity extends Activity {
	private TextView tv_task_count;
	private TextView tv_Mem;
	private ListView lv_tasklist;
	private ArrayList<TaskInfo> userTask;
	private ArrayList<TaskInfo> systemTask;
	private TextView tv_taskkind;
	private TaskListAdapter adapter;

	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (adapter == null) {
				adapter = new TaskListAdapter();
				lv_tasklist.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
		};
	};
	private int count;
	private String fTotalMem;
	private String fAvailMem;
	private long availMem;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", 0);
		initUI();
		initData();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(adapter != null) {
			adapter.notifyDataSetChanged();
		}
		boolean flag = sp.getBoolean("show_system_process", true);
		if(!flag) {
			tv_task_count.setText("��ǰ������:" + userTask.size());
		} else {
			tv_task_count.setText("��ǰ������:" + (userTask.size() + systemTask.size()));
			
		}
	}

	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		setContentView(R.layout.activity_task_manager);
		tv_task_count = (TextView) findViewById(R.id.tv_task_count);
		tv_Mem = (TextView) findViewById(R.id.tv_Mem);
		lv_tasklist = (ListView) findViewById(R.id.lv_tasklist);
		tv_taskkind = (TextView) findViewById(R.id.tv_taskkind);

		lv_tasklist.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (userTask != null && systemTask != null) {
					if (firstVisibleItem < userTask.size() + 1) {
						tv_taskkind.setText("�û�����(" + userTask.size() + ")");
					} else {
						tv_taskkind.setText("ϵͳ����(" + systemTask.size() + ")");
					}
				}
			}
		});
	}

	/**
	 * ��ʼ������
	 */
	private void initData() {
		count = SystemInfoUtils.getProcessCount(this);
		tv_task_count.setText("��ǰ������:" + count);
		/*
		 * �����ڴ����
		 */
		long totalMem = SystemInfoUtils.getTotalMem(this);
		availMem = SystemInfoUtils.getAvailMem(this);
		fTotalMem = Formatter.formatFileSize(this, totalMem);
		fAvailMem = Formatter.formatFileSize(this, availMem);
		tv_Mem.setText("����/�ܹ�:" + fAvailMem + "/" + fTotalMem);

		/*
		 * �Խ������ݽ��д���
		 */
		userTask = new ArrayList<TaskInfo>();
		systemTask = new ArrayList<TaskInfo>();
		List<TaskInfo> taskInfos = TaskInfoParser.getTaskInfos(this);
		for (TaskInfo taskInfo : taskInfos) {
			boolean flag = taskInfo.isUserApp();
			if (flag) {
				userTask.add(taskInfo);
			} else {
				systemTask.add(taskInfo);
			}
		}

		handler.sendEmptyMessage(0);

	}

	/*
	 * ���ڽ����б��adapter
	 */
	class TaskListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			boolean flag = sp.getBoolean("show_system_process", true);
			if(flag) {
				return userTask.size() + systemTask.size() + 2;
			} else{
				return userTask.size() + 1;
			}
		}

		@Override
		public Object getItem(int position) {
			// ���нǱ��ж�
			if (position == 0 || position == userTask.size() + 1) {
				return null;
			}
			// �����û�����
			if (position <= userTask.size() + 1) {
				System.out.println(userTask.get(position - 1).getAppName());
				return userTask.get(position - 1);
			} else {
				// System.out.println(systemTask.get(position - userTask.size()
				// - 2).getAppName());
				return systemTask.get(position - userTask.size() - 2);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (position == 0) {
				TextView textView = new TextView(TaskManagerActivity.this);
				textView.setText("�û����̣�" + userTask.size() + ")");
				textView.setTextColor(Color.WHITE);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextSize(15);
				return textView;
			}
			if (position == userTask.size() + 1) {
				TextView textView = new TextView(TaskManagerActivity.this);
				textView.setText("ϵͳ���̣�" + systemTask.size() + ")");
				textView.setTextColor(Color.WHITE);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextSize(15);
				return textView;
			}

			View view;
			final ViewHolder holder;
			if (convertView == null || convertView instanceof TextView) {
				view = View.inflate(TaskManagerActivity.this,
						R.layout.item_task_manager, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_task_name = (TextView) view
						.findViewById(R.id.tv_task_name);
				holder.tv_task_dirty_mem = (TextView) view
						.findViewById(R.id.tv_task_dirty_mem);
				holder.tv_app_status = (CheckBox) view
						.findViewById(R.id.tv_app_status);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) convertView.getTag();
			}
			TaskInfo info = (TaskInfo) getItem(position);
			holder.iv_icon.setImageDrawable(info.getIcon());
			holder.tv_task_name.setText(info.getAppName());
			holder.tv_task_dirty_mem.setText(Formatter.formatFileSize(
					TaskManagerActivity.this, info.getMemorySize() * 1024));
			

			// �ж�checkboxĬ��״̬
			boolean checked = info.isChecked();
			if (checked) {
				holder.tv_app_status.setChecked(true);
			} else {
				holder.tv_app_status.setChecked(false);
			}
			
			holder.tv_app_status.setVisibility(View.VISIBLE);
			//�����Լ���checkbox,��Ϊfalse
			if(info.getPackageName().equals(getPackageName())) {
				holder.tv_app_status.setVisibility(View.GONE);
				info.setChecked(false);
			}

			// ʵ�ֵ�����ѡcheckBox
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					TaskInfo info = (TaskInfo) lv_tasklist
							.getItemAtPosition(position);
					boolean flag = info.isChecked();
					if (flag) {
						info.setChecked(false);
						holder.tv_app_status.setChecked(false);
					} else {
						info.setChecked(true);
						holder.tv_app_status.setChecked(true);
					}
					
					if(info.getPackageName().equals(getPackageName())) {
						info.setChecked(false);
					}
				}
			});
			return view;
		}

	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_task_name;
		TextView tv_task_dirty_mem;
		CheckBox tv_app_status;
	}

	/**
	 * ȫѡ
	 * 
	 * @param v
	 */
	public void choseAll(View v) {
		// �ı�����TaskInfo��checked����
		if (userTask != null) {
			for (TaskInfo userTaskInfo : userTask) {
				userTaskInfo.setChecked(true);
				if(userTaskInfo.getPackageName().equals(getPackageName())) {
					userTaskInfo.setChecked(false);
				}
			}
		}
		if (systemTask != null) {
			for (TaskInfo systemTaskInfo : systemTask) {
				systemTaskInfo.setChecked(true);
			}
		}
		// ����UI
		handler.sendEmptyMessage(0);
	}

	/**
	 * ��ѡ
	 */
	public void selectOppsite(View v) {
		// �ı�����TaskInfo��checked����
		if (userTask != null) {
			for (TaskInfo userTaskInfo : userTask) {
				if(userTaskInfo.isChecked()) {
					userTaskInfo.setChecked(false);
				} else {
					userTaskInfo.setChecked(true);
				}
				if(userTaskInfo.getPackageName().equals(getPackageName())) {
					userTaskInfo.setChecked(false);
				}
			}
		}
		if (systemTask != null) {
			for (TaskInfo systemTaskInfo : systemTask) {
				if(systemTaskInfo.isChecked()) {
					systemTaskInfo.setChecked(false);
				} else {
					systemTaskInfo.setChecked(true);
				}
			}
		}
		// ����UI
		handler.sendEmptyMessage(0);
	}
	
	/**
	 * ɱ������
	 */
	public void killProcess(View v) {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		ArrayList<TaskInfo> killList = new ArrayList<TaskInfo>();
		long relessMem = 0;
		//�ж���Щ��ѡ����
		for(TaskInfo userInfo : userTask ) {
			if(userInfo.isChecked()) {
				killList.add(userInfo);
			}
		}
		for(TaskInfo systemInfo : systemTask) {
			if(systemInfo.isChecked()) {
				killList.add(systemInfo);
			}
		}
		// ɱ��������
		int totalCount = killList.size();
		//����Ҫ����Ľ��̽��д���
		for(TaskInfo willKillProcess : killList) {
			relessMem = relessMem +  willKillProcess.getMemorySize();
			//�����Ƴ�,��ɱ��
			if(willKillProcess.isUserApp()) {
				userTask.remove(willKillProcess);
				manager.killBackgroundProcesses(willKillProcess.getPackageName());
			} else {
				systemTask.remove(willKillProcess);
				manager.killBackgroundProcesses(willKillProcess.getPackageName());
			}
		}
		
		// �ı�������ʾ
		boolean flag = sp.getBoolean("show_system_process", true);
		int total = 0;
		if(flag) {
			total = userTask.size() + systemTask.size();
		} else {
			total = userTask.size();
		}
		tv_task_count.setText("��ǰ������:" + total);
		
		long f=  relessMem + availMem;
		String fRelessMem = Formatter.formatFileSize(TaskManagerActivity.this, f);
		tv_Mem.setText("����/�ܹ�:" + fRelessMem + "/" + fTotalMem);
		String sizeReless = Formatter.formatFileSize(TaskManagerActivity.this, relessMem *1024);
		UIUtils.showToast(TaskManagerActivity.this, "һ�����" + totalCount+"�����̣�һ���ͷ�"+sizeReless);
		handler.sendEmptyMessage(0);
	}

	/**
	 * ��������
	 */
	public void taskManagerSetting(View v) {
		Intent intent = new Intent(this , TaskManagerSettingActivity.class);
		startActivity(intent);
	}
}
