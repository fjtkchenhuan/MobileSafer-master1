package com.example.mobilesafer.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafer.R;
import com.example.mobilesafer.adapter.MyBaseAdapter;
import com.example.mobilesafer.bean.BlackNumberInfo;
import com.example.mobilesafer.db.dao.BlackNumberDao;

public class CallSafeActivity extends Activity {
	private ListView blackList;
	private LinearLayout pbLayout;
	private List<BlackNumberInfo> blacknumberinfos;
	private int mPageSize = 20;
	private BlackNumberDao dao;
	// 总的页数
	private MyAdapter adapter;
	// 最后一条的角标
	int lastNumber;
	int offset = 0;

	// 处理信息
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			pbLayout.setVisibility(View.GONE);
			if (adapter == null) {
				adapter = new MyAdapter(blacknumberinfos, CallSafeActivity.this);
				blackList.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
			
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_safe);

		dao = new BlackNumberDao(CallSafeActivity.this);
		initUI();
		initData();

	}

	/**
	 * 初始化数据 因为是一个耗时操作所以将其放到子线程中进行
	 */
	private void initData() {
		pbLayout.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				// 睡眠3秒模仿数据下载
				// SystemClock.sleep(3000);

				if (blacknumberinfos == null) {
					blacknumberinfos = dao.findPar1(offset, mPageSize);
				} else {
					blacknumberinfos.addAll(dao.findPar1(offset, mPageSize));
				}
				// 更改角标，目的是表示现在整个listview中有多少数据，方便边界判定
				offset = blacknumberinfos.size();
				System.out.println("offset is " + offset);
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		blackList = (ListView) findViewById(R.id.lv_black);
		pbLayout = (LinearLayout) findViewById(R.id.pb_layout);

		// 添加滑动监听
		blackList.setOnScrollListener(new OnScrollListener() {

			// 当滑动停止的时候
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lastNumber = blackList.getLastVisiblePosition();
				System.out.println(lastNumber);
				// 判断
				if (lastNumber == offset - 1) {
					// 为最后一条
					if (offset == dao.countNumber()) {
						// 没有数据了
						Toast.makeText(CallSafeActivity.this, "已经是最后一条了",
								Toast.LENGTH_SHORT).show();
						;
						return;
					}
					initData();
				}
			}

			// 当正在滑动的时候
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}

	/**
	 * 添加黑名单 单击事件
	 */
	public void addBlackNumber(View view) {
		AlertDialog.Builder dbuiler = new AlertDialog.Builder(this);
		final AlertDialog alertDialog = dbuiler.create();

		View dialog_add_blacknumber = View.inflate(this,
				R.layout.dialog_add_blacknumber, null);

		// 获取组件
		Button bt_cancel = (Button) dialog_add_blacknumber
				.findViewById(R.id.bt_cancel);
		Button bt_commit = (Button) dialog_add_blacknumber
				.findViewById(R.id.bt_commit);
		final CheckBox cb_mode_phone = (CheckBox) dialog_add_blacknumber
				.findViewById(R.id.cb_mode_phone);
		final CheckBox cb_mode_sms = (CheckBox) dialog_add_blacknumber
				.findViewById(R.id.cb_mode_sms);
		final EditText et_add_number = (EditText) dialog_add_blacknumber
				.findViewById(R.id.et_add_number);

		/*
		 * 添加黑名单
		 */
		bt_commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 获取用户填写的数据
				String number = et_add_number.getText().toString();
				if (TextUtils.isEmpty(number)) {
					Toast.makeText(CallSafeActivity.this, "请填写号码",
							Toast.LENGTH_SHORT).show();
					return;
				}
				String mode = "";
				if (cb_mode_phone.isChecked() && cb_mode_sms.isChecked()) {
					mode = "1";
				} else if (cb_mode_phone.isChecked()) {
					mode = "2";
				} else if (cb_mode_sms.isChecked()) {
					mode = "3";
				} else {
					Toast.makeText(CallSafeActivity.this, "请勾选拦截模式",
							Toast.LENGTH_SHORT).show();
					return;
				}

				// 添加到数据库
				boolean flag = dao.add(number, mode);
				if (!flag) {
					Toast.makeText(CallSafeActivity.this, "添加到数据库失败 ",
							Toast.LENGTH_SHORT).show();
				}
				// 更新UI
				BlackNumberInfo info = new BlackNumberInfo();
				info.setNumber(number);
				info.setMode(mode);

				blacknumberinfos.add(0, info);
				adapter.notifyDataSetChanged();
				alertDialog.dismiss();
			}
		});
		/*
		 * 取消
		 */
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				return;
			}
		});

		alertDialog.setView(dialog_add_blacknumber);
		alertDialog.show();
	}

	class MyAdapter extends MyBaseAdapter<BlackNumberInfo> {
		// 初始化
		public MyAdapter(List<BlackNumberInfo> list, Context context) {
			super(list, context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = View.inflate(CallSafeActivity.this,
						R.layout.item_safe_call, null);
				holder.tvNumber = (TextView) convertView
						.findViewById(R.id.tv_number);
				holder.tvMode = (TextView) convertView
						.findViewById(R.id.tv_mode);
				holder.ivDelete = (ImageView) convertView
						.findViewById(R.id.iv_delete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tvNumber.setText(blacknumberinfos.get(position).getNumber());

			// 获取到当前对象
			final BlackNumberInfo info = list.get(position);
			// 给删除添加监听
			holder.ivDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String number = info.getNumber();
					boolean flag = dao.delete(number);
					if (flag) {
						Toast.makeText(CallSafeActivity.this, "删除成功",
								Toast.LENGTH_SHORT).show();
						// 移除数据，更新UI
						list.remove(info);
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(CallSafeActivity.this, "删除失败",
								Toast.LENGTH_SHORT).show();
					}
				}
			});

			String mode = blacknumberinfos.get(position).getMode();
			if (mode.equals("1")) {
				holder.tvMode.setText("电话+短信拦截");
			} else if (mode.equals("2")) {
				holder.tvMode.setText("电话拦截");
			} else if (mode.equals("3")) {
				holder.tvMode.setText("短信拦截");
			}
			return convertView;
		}
	}

	static class ViewHolder {
		TextView tvNumber;
		
		TextView tvMode;
		ImageView ivDelete;
	}
}
