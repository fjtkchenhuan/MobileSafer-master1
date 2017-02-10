package com.example.mobilesafer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafer.R;
import com.example.mobilesafer.utils.Md5Utils;

public class HomeActivity extends Activity {
	// 获取组件
	GridView gvHome = null;

	// 设置数据
	private String[] mItems = new String[] { "手机防盗", "通讯卫士", "软件管理", "进程管理",
			"流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心" };

	// 设置图片id
	private int[] mPics = new int[] { R.drawable.home_safe,
			R.drawable.home_callmsgsafe, R.drawable.home_apps,
			R.drawable.home_taskmanager, R.drawable.home_netmanager,
			R.drawable.home_trojan, R.drawable.home_sysoptimize,
			R.drawable.home_tools, R.drawable.home_settings };

	private EditText etPassword;

	private EditText etPasswordConfirm;

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home_layout);

		gvHome = (GridView) findViewById(R.id.gv_home);
		gvHome.setAdapter(new MyAdapter());
		gvHome.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 判断当前单击对象
				switch (position) {

				// 手机防盗,判断是否设置了密码
				case 0: {
					String pwd = sp.getString("password", null);
					if (TextUtils.isEmpty(pwd)) {
						showPasswordDialog();
					} else {
						showPasswordCheckDialog();
					}
					break;
				}

				// 手机通讯卫士
				case 1: {
					Intent i = new Intent(HomeActivity.this,
							CallSafeActivity.class);
					startActivity(i);
					break;
				}

				// 软件管理
				case 2: {
					Intent i = new Intent(HomeActivity.this,
							AppManagerActivity.class);
					startActivity(i);
					break;
				}

				// 进程管理
				case 3: {
					Intent i = new Intent(HomeActivity.this,
							TaskManagerActivity.class);
					startActivity(i);
					break;
				}
				
				// 进程管理
				case 4: {
					Intent i = new Intent(HomeActivity.this,
							TrafficManagerActivity.class);
					startActivity(i);
					break;
				}

				// 手机杀毒
				case 5: {
					Intent i = new Intent(HomeActivity.this,
							AntivirusActivity.class);
					startActivity(i);
					break;
				}
				
				// 缓存清理
				case 6: {
					Intent i = new Intent(HomeActivity.this,
							CleanCacheActivity.class);
					startActivity(i);
					break;
				}

				// 高级工具
				case 7: {
					Intent i = new Intent(HomeActivity.this,
							AToolsActivity.class);
					startActivity(i);
					break;
				}
				// 设置中心
				case 8:
					Intent i = new Intent(HomeActivity.this,
							SettingActivity.class);
					startActivity(i);
					break;

				default:
					break;
				}
			}
		});

		// 获取配置文件
		sp = getSharedPreferences("config", MODE_PRIVATE);
	}

	/**
	 * 前置：如果设置了密码 功能： 检验密码
	 */
	protected void showPasswordCheckDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(HomeActivity.this,
				R.layout.dailog_input_password, null);

		// 添加逻辑事件
		final EditText etP = (EditText) view.findViewById(R.id.et_password);
		Button btn_Ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

		btn_Ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String passw = etP.getText().toString();
				String savedPassword = sp.getString("password", null);

				if (!TextUtils.isEmpty(passw)) {
					if (savedPassword.equals(Md5Utils.encode(passw))) {
						// Toast.makeText(HomeActivity.this, "登陆成功",
						// Toast.LENGTH_SHORT).show();
						// 开启手机找寻页面
						startActivity(new Intent(HomeActivity.this,
								LostFindActivity.class));

						dialog.dismiss();
					} else {
						Toast.makeText(HomeActivity.this, "密码错误",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(HomeActivity.this, "不能为空",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.setView(view);
		dialog.show();
	}

	/**
	 * 前置:没有设置密码 功能：设置密码
	 */
	protected void showPasswordDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		// 将弹窗填充到dialog当中
		View view = View.inflate(this, R.layout.dailog_set_password, null);
		// 设置边距
		dialog.setView(view, 0, 0, 0, 0);

		etPassword = (EditText) view.findViewById(R.id.et_password);
		etPasswordConfirm = (EditText) view
				.findViewById(R.id.et_passwordConfirm);
		Button btnOk = (Button) view.findViewById(R.id.btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		// 确认按钮的逻辑
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 获取数据并检验
				String password = etPassword.getText().toString();
				String passwordConfirm = etPasswordConfirm.getText().toString();
				// 检验是否为空
				if (!TextUtils.isEmpty(password)
						&& !TextUtils.isEmpty(passwordConfirm)) {
					if (password.equals(passwordConfirm)) {
						// Toast.makeText(HomeActivity.this, "成功",
						// Toast.LENGTH_SHORT).show();
						// 保存配置文件
						sp.edit()
								.putString("password",
										Md5Utils.encode(password)).commit();

						dialog.dismiss();
						startActivity(new Intent(HomeActivity.this,
								LostFindActivity.class));// 打开下个Activity
					} else {
						Toast.makeText(HomeActivity.this, "两次密码不相同",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(HomeActivity.this, "不能为空",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		// 取消按钮的逻辑
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	class MyAdapter extends BaseAdapter {

		// 返回item的数量
		@Override
		public int getCount() {
			return mItems.length;
		}

		@Override
		public Object getItem(int position) {
			return mItems[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder viewHolder;
			if (convertView == null) {
				view = View.inflate(HomeActivity.this,
						R.layout.home_item_layout, null);
				viewHolder = new ViewHolder();
				viewHolder.ivItem = (ImageView) view.findViewById(R.id.iv_item);
				viewHolder.tvItem = (TextView) view.findViewById(R.id.tv_item);
				// 保存viewHolder
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.ivItem.setImageResource(mPics[position]);
			viewHolder.tvItem.setText(mItems[position]);
			return view;
		}

		class ViewHolder {
			TextView tvItem;
			ImageView ivItem;
		}
	}

}
