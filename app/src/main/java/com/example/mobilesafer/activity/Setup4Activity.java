package com.example.mobilesafer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.mobilesafer.R;

/**
 * ҳ��4
 * 
 * @author admin
 *
 */

public class Setup4Activity extends BaseSettingActivity {

	private CheckBox cb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setup4);
		
		cb = (CheckBox) findViewById(R.id.cb);
		//�жϳ�ʼ��״̬
		boolean isChecked = sp.getBoolean("protect", false);
		if(isChecked) {
			sp.edit().putBoolean("protect", true).commit();
			cb.setText("���������Ѿ�����");
			cb.setChecked(true);
		} else {
			sp.edit().putBoolean("protect", false).commit();
			cb.setText("��������û�п���");
		}
		//��Ӽ�����������ѡ�����
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//����isChecked���ı�״̬������
				if(isChecked) {
					sp.edit().putBoolean("protect", true).commit();
					cb.setText("���������Ѿ�����");
				} else {
					sp.edit().putBoolean("protect", false).commit();
					cb.setText("��������û�п���");
				}
			}
		});
		
	}

	public void next(View v) {
		showNextPage();
	}

	// ����ҳ��3
	public void previous(View v) {
		showPreviousPage();
	}

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup3Activity1.class));
		finish();
		overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
	
	}

	@Override
	public void showNextPage() {
		startActivity(new Intent(this, LostFindActivity.class));
		finish();
		getSharedPreferences("config", MODE_PRIVATE).edit()
				.putBoolean("configed", true).commit();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	
	}

}
