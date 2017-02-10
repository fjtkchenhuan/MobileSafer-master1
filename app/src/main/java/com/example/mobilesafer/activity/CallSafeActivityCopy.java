package com.example.mobilesafer.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafer.R;
import com.example.mobilesafer.adapter.MyBaseAdapter;
import com.example.mobilesafer.bean.BlackNumberInfo;
import com.example.mobilesafer.db.dao.BlackNumberDao;

public class CallSafeActivityCopy extends Activity{
	private ListView blackList;
	private LinearLayout pbLayout;
	private EditText et_page_number;
	private TextView tv_page_number;
	private List<BlackNumberInfo> blacknumberinfos;
	private int mCurrentPage = 0;
	private int mPageSize = 20;
	private BlackNumberDao dao;
	//总的页数
	private int pageNumber;
	private MyAdapter adapter;
	
	// 处理信息
	Handler handler = new Handler() {


		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			pbLayout.setVisibility(View.GONE);
			
			//并在这里更新页数

			tv_page_number.setText((mCurrentPage+1) + "/" + pageNumber);
			adapter = new MyAdapter(blacknumberinfos, CallSafeActivityCopy.this);
			blackList.setAdapter(adapter);
		}
		
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_safe);
		initUI();
		initData();
		
	}

	/**
	 * 初始化数据
	 * 因为是一个耗时操作所以将其放到子线程中进行
	 */
	private void initData() {
		pbLayout.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				//睡眠3秒模仿数据下载
//				SystemClock.sleep(3000);
				dao = new BlackNumberDao(CallSafeActivityCopy.this);
				int number = dao.countNumber();
				pageNumber = number / mPageSize;
				if(number%mPageSize != 0) {
					pageNumber += 1;
				}
//				blacknumberinfos = dao.findAll();
				//更改查询方法为分页查找
				blacknumberinfos = dao.findPar(mCurrentPage, mPageSize);
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
		et_page_number = (EditText) findViewById(R.id.et_page_number);
		tv_page_number = (TextView) findViewById(R.id.tv_page_number);
		
	}

	class MyAdapter extends MyBaseAdapter<BlackNumberInfo> {
		//初始化
		public MyAdapter(List<BlackNumberInfo> list , Context context) {
			super(list , context);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if(convertView == null) {
				convertView = View.inflate(CallSafeActivityCopy.this, R.layout.item_safe_call, null);
				holder.tvNumber = (TextView) convertView.findViewById(R.id.tv_number);
				holder.tvMode = (TextView) convertView.findViewById(R.id.tv_mode);
				holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.tvNumber.setText( blacknumberinfos.get(position).getNumber());
			
			//获取到当前对象
			final BlackNumberInfo info = list.get(position);
			//给删除添加监听
			holder.ivDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String number = info.getNumber();
					boolean flag = dao.delete(number);
					if(flag) {
						Toast.makeText(CallSafeActivityCopy.this, "删除成功", Toast.LENGTH_SHORT).show();
						//移除数据，更新UI
						list.remove(info);
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(CallSafeActivityCopy.this, "删除失败", Toast.LENGTH_SHORT).show();
					}
				}
			});
			
			String mode = blacknumberinfos.get(position).getMode();
			if(mode.equals("1")) {
				holder.tvMode.setText("电话+短信拦截");
			}else if(mode.equals("2")) {
				holder.tvMode.setText("电话拦截");
			} else if(mode.equals("3")) {
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
	
	/**
	 * 跳转到上一页
	 * @param view
	 */
	public void prePage(View view) {
		//进行边界检验
		if(mCurrentPage == 0) {
			Toast.makeText(this, "当前已经是第一页了", Toast.LENGTH_SHORT).show();
			return ;
		}
		//减少一页
		mCurrentPage =mCurrentPage - 1;
		initData();
	}
	
	/**
	 * 跳转到下一页
	 * @param view
	 */
	public void nextPage(View view) {
		if(mCurrentPage > pageNumber ) {
			Toast.makeText(this, "当前已经是最后一页了", Toast.LENGTH_SHORT).show();
			return ;
		}
		System.out.println(pageNumber);
		mCurrentPage ++ ;
		initData();
	}
	
	/**
	 * 跳转到指定页面
	 * @param view
	 */
	public void jump(View view) {
		//获取数据
		String page = et_page_number.getText().toString().trim();
		if(TextUtils.isEmpty(page)) {
			Toast.makeText(this, "请输入正确页码", Toast.LENGTH_SHORT).show();
			return;
		} else {
			int pnum = Integer.parseInt(page);
			if(pnum > pageNumber || pnum <= 0 ) {
				Toast.makeText(this, "请输入正确页码", Toast.LENGTH_SHORT).show();
				return ;
			}
			mCurrentPage = pnum - 1;
			initData();
		}
	}
	
}
