package com.example.mobilesafer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mobilesafer.R;
import com.example.mobilesafer.db.dao.AddressDao;

public class AddressActivity extends Activity {
	private EditText etPhone;
	private TextView tvResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addressquery);
		
		etPhone = (EditText) findViewById(R.id.et_phone);
		tvResult = (TextView) findViewById(R.id.tv_result);
		
	}
	
	public void queryPhone(View v) {
		String phoneNum = etPhone.getText().toString();
		String reslut = AddressDao.getLocation(phoneNum);
		tvResult.setText(reslut);
	}
}
