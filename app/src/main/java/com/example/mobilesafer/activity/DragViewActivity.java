package com.example.mobilesafer.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.example.mobilesafer.R;

public class DragViewActivity extends Activity {

	private TextView tvTop;
	private TextView tvBottom;
	private ImageView ivDrag;

	private int startX;
	private int startY;
	private int endX;
	private int endY;
	private SharedPreferences mSprf;

	private long[] mHits = new long[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_view);

		mSprf = getSharedPreferences("config", MODE_PRIVATE);

		// ��ȡ�����
		tvTop = (TextView) findViewById(R.id.tv_top);
		tvBottom = (TextView) findViewById(R.id.tv_bottom);
		ivDrag = (ImageView) findViewById(R.id.iv_drag);

		// ��ȡ����Ļ�Ĵ�С
		final int winHeight = getWindowManager().getDefaultDisplay()
				.getHeight();
		final int winWidth = getWindowManager().getDefaultDisplay().getWidth();

		// ���϶���ͼ����ӵ����¼�
		ivDrag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// �ж�˫���¼�
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= SystemClock.uptimeMillis() - 500) {
					// Ϊ˫���¼������¶�λλ��
					
					int l = winWidth / 2 - ivDrag.getWidth() / 2;
					int t = ivDrag.getTop();
					// ����ʱ����Ҫ�ı�ģ����Ի�õ�ǰ��λ��
					ivDrag.layout(winWidth / 2 - ivDrag.getWidth() / 2,
							ivDrag.getTop(), winWidth / 2 + ivDrag.getWidth()
									/ 2, ivDrag.getBottom());
					
					// ������Ϣ,�������margin�ľ��룬������x,y����,��Ϊ��ʼ����ʱ�����õ�margin�����г�ʼ����
					mSprf.edit().putInt("lastLM", l).commit();
					mSprf.edit().putInt("lastTM", t).commit();
				}
			}
		});

		// ��ñ������Ϣ��Ȼ��ΪDrag��λ�ó�ʼ��
		int savedL = mSprf.getInt("lastLM", 0);
		int savedT = mSprf.getInt("lastTM", 0);

		// �ж�������ʾ�������
		if (savedT > winHeight / 2) {
			// �����±�
			tvTop.setVisibility(View.VISIBLE);
			tvBottom.setVisibility(View.INVISIBLE);
		} else {
			// �����ϱ�
			tvTop.setVisibility(View.INVISIBLE);
			tvBottom.setVisibility(View.VISIBLE);
		}

		// ΪImageView����λ�õĳ�ʼ��
		RelativeLayout.LayoutParams layoutparams = (LayoutParams) ivDrag
				.getLayoutParams();
		layoutparams.leftMargin = savedL;
		layoutparams.topMargin = savedT;
		ivDrag.setLayoutParams(layoutparams);

		// ��drag���һ��setOnTouchListener,���ڻ��λ��
		ivDrag.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// �жϵ�ǰ�����
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					// ����ָ���µ�ʱ�򣬻�ȡ��ǰλ����Ϊ��ʼλ��

					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				}
				case MotionEvent.ACTION_MOVE: {
					// ����ָ�ƶ���ʱ���ý��λ��

					endX = (int) event.getRawX();
					endY = (int) event.getRawY();

					// ����ƶ��ľ���
					int dx = endX - startX;
					int dy = endY - startY;

					// �µ�λ��
					int l = ivDrag.getLeft() + dx;
					int r = ivDrag.getRight() + dx;
					int t = ivDrag.getTop() + dy;
					int b = ivDrag.getBottom() + dy;

					// �ж��Ƿ����
					// ��Ϊ���϶����߽��ʱ��ͻᴥ���������������˵�Ͳ����ٸ���UI
					if (l < 0 || r > winWidth || t < 0 || b > winHeight - 20) {
						break;
					}

					// �ж�������ʾ�������
					if (t > winHeight / 2) {
						// �����±�
						tvTop.setVisibility(View.VISIBLE);
						tvBottom.setVisibility(View.INVISIBLE);
					} else {
						// �����ϱ�
						tvTop.setVisibility(View.INVISIBLE);
						tvBottom.setVisibility(View.VISIBLE);
					}

					// ����ͼ��λ��
					ivDrag.layout(l, t, r, b);

					// ����ͼ�����ʼλ��
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					// ������Ϣ,�������margin�ľ��룬������x,y����,��Ϊ��ʼ����ʱ�����õ�margin�����г�ʼ����
					mSprf.edit().putInt("lastLM", l).commit();
					mSprf.edit().putInt("lastTM", t).commit();
					System.out.println("l and t" + l + ":" + t);

					System.out.println(ivDrag.getLeft() + ":" + ivDrag.getTop());
					break;
				}
				case MotionEvent.ACTION_UP: {
					break;
				}
				}
				return false;
			}
		});

	}
}
