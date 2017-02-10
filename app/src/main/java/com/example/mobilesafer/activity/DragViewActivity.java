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

		// 获取到组件
		tvTop = (TextView) findViewById(R.id.tv_top);
		tvBottom = (TextView) findViewById(R.id.tv_bottom);
		ivDrag = (ImageView) findViewById(R.id.iv_drag);

		// 获取到屏幕的大小
		final int winHeight = getWindowManager().getDefaultDisplay()
				.getHeight();
		final int winWidth = getWindowManager().getDefaultDisplay().getWidth();

		// 给拖动的图标添加单击事件
		ivDrag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断双击事件
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= SystemClock.uptimeMillis() - 500) {
					// 为双击事件，重新定位位置
					
					int l = winWidth / 2 - ivDrag.getWidth() / 2;
					int t = ivDrag.getTop();
					// 上下时不需要改变的，所以获得当前的位置
					ivDrag.layout(winWidth / 2 - ivDrag.getWidth() / 2,
							ivDrag.getTop(), winWidth / 2 + ivDrag.getWidth()
									/ 2, ivDrag.getBottom());
					
					// 保存信息,保存的是margin的距离，而不是x,y坐标,因为初始化的时候是用的margin来进行初始化的
					mSprf.edit().putInt("lastLM", l).commit();
					mSprf.edit().putInt("lastTM", t).commit();
				}
			}
		});

		// 获得保存的信息，然后为Drag的位置初始化
		int savedL = mSprf.getInt("lastLM", 0);
		int savedT = mSprf.getInt("lastTM", 0);

		// 判断文字提示框的隐藏
		if (savedT > winHeight / 2) {
			// 隐藏下边
			tvTop.setVisibility(View.VISIBLE);
			tvBottom.setVisibility(View.INVISIBLE);
		} else {
			// 隐藏上边
			tvTop.setVisibility(View.INVISIBLE);
			tvBottom.setVisibility(View.VISIBLE);
		}

		// 为ImageView进行位置的初始化
		RelativeLayout.LayoutParams layoutparams = (LayoutParams) ivDrag
				.getLayoutParams();
		layoutparams.leftMargin = savedL;
		layoutparams.topMargin = savedT;
		ivDrag.setLayoutParams(layoutparams);

		// 给drag添加一个setOnTouchListener,用于获得位置
		ivDrag.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 判断当前的情况
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					// 当手指放下的时候，获取当前位置作为开始位置

					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				}
				case MotionEvent.ACTION_MOVE: {
					// 当手指移动的时候获得结果位置

					endX = (int) event.getRawX();
					endY = (int) event.getRawY();

					// 获得移动的距离
					int dx = endX - startX;
					int dy = endY - startY;

					// 新的位置
					int l = ivDrag.getLeft() + dx;
					int r = ivDrag.getRight() + dx;
					int t = ivDrag.getTop() + dy;
					int b = ivDrag.getBottom() + dy;

					// 判断是否出界
					// 因为当拖动到边界的时候就会触发这个条件，所以说就不会再更新UI
					if (l < 0 || r > winWidth || t < 0 || b > winHeight - 20) {
						break;
					}

					// 判断文字提示框的隐藏
					if (t > winHeight / 2) {
						// 隐藏下边
						tvTop.setVisibility(View.VISIBLE);
						tvBottom.setVisibility(View.INVISIBLE);
					} else {
						// 隐藏上边
						tvTop.setVisibility(View.INVISIBLE);
						tvBottom.setVisibility(View.VISIBLE);
					}

					// 更新图标位置
					ivDrag.layout(l, t, r, b);

					// 更新图标的起始位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					// 保存信息,保存的是margin的距离，而不是x,y坐标,因为初始化的时候是用的margin来进行初始化的
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
