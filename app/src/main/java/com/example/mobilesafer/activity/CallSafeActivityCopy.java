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
	//�ܵ�ҳ��
	private int pageNumber;
	private MyAdapter adapter;
	
	// ������Ϣ
	Handler handler = new Handler() {


		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			pbLayout.setVisibility(View.GONE);
			
			//�����������ҳ��

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
	 * ��ʼ������
	 * ��Ϊ��һ����ʱ�������Խ���ŵ����߳��н���
	 */
	private void initData() {
		pbLayout.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				//˯��3��ģ����������
//				SystemClock.sleep(3000);
				dao = new BlackNumberDao(CallSafeActivityCopy.this);
				int number = dao.countNumber();
				pageNumber = number / mPageSize;
				if(number%mPageSize != 0) {
					pageNumber += 1;
				}
//				blacknumberinfos = dao.findAll();
				//���Ĳ�ѯ����Ϊ��ҳ����
				blacknumberinfos = dao.findPar(mCurrentPage, mPageSize);
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		blackList = (ListView) findViewById(R.id.lv_black);
		pbLayout = (LinearLayout) findViewById(R.id.pb_layout);
		et_page_number = (EditText) findViewById(R.id.et_page_number);
		tv_page_number = (TextView) findViewById(R.id.tv_page_number);
		
	}

	class MyAdapter extends MyBaseAdapter<BlackNumberInfo> {
		//��ʼ��
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
			
			//��ȡ����ǰ����
			final BlackNumberInfo info = list.get(position);
			//��ɾ����Ӽ���
			holder.ivDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String number = info.getNumber();
					boolean flag = dao.delete(number);
					if(flag) {
						Toast.makeText(CallSafeActivityCopy.this, "ɾ���ɹ�", Toast.LENGTH_SHORT).show();
						//�Ƴ����ݣ�����UI
						list.remove(info);
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(CallSafeActivityCopy.this, "ɾ��ʧ��", Toast.LENGTH_SHORT).show();
					}
				}
			});
			
			String mode = blacknumberinfos.get(position).getMode();
			if(mode.equals("1")) {
				holder.tvMode.setText("�绰+��������");
			}else if(mode.equals("2")) {
				holder.tvMode.setText("�绰����");
			} else if(mode.equals("3")) {
				holder.tvMode.setText("��������");
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
	 * ��ת����һҳ
	 * @param view
	 */
	public void prePage(View view) {
		//���б߽����
		if(mCurrentPage == 0) {
			Toast.makeText(this, "��ǰ�Ѿ��ǵ�һҳ��", Toast.LENGTH_SHORT).show();
			return ;
		}
		//����һҳ
		mCurrentPage =mCurrentPage - 1;
		initData();
	}
	
	/**
	 * ��ת����һҳ
	 * @param view
	 */
	public void nextPage(View view) {
		if(mCurrentPage > pageNumber ) {
			Toast.makeText(this, "��ǰ�Ѿ������һҳ��", Toast.LENGTH_SHORT).show();
			return ;
		}
		System.out.println(pageNumber);
		mCurrentPage ++ ;
		initData();
	}
	
	/**
	 * ��ת��ָ��ҳ��
	 * @param view
	 */
	public void jump(View view) {
		//��ȡ����
		String page = et_page_number.getText().toString().trim();
		if(TextUtils.isEmpty(page)) {
			Toast.makeText(this, "��������ȷҳ��", Toast.LENGTH_SHORT).show();
			return;
		} else {
			int pnum = Integer.parseInt(page);
			if(pnum > pageNumber || pnum <= 0 ) {
				Toast.makeText(this, "��������ȷҳ��", Toast.LENGTH_SHORT).show();
				return ;
			}
			mCurrentPage = pnum - 1;
			initData();
		}
	}
	
}
