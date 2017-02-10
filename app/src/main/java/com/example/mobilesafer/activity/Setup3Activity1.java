package com.example.mobilesafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobilesafer.R;

/**
 * 页面三
 * 
 * @author admin
 *
 */

public class Setup3Activity1 extends BaseSettingActivity {

	private EditText etNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setup3);

		etNum = (EditText) findViewById(R.id.et_num);
		// 进行默认填充
		String phone = sp.getString("safe_phone", "");
		etNum.setText(phone);

		// 给选择联系人添加监听事件
		Button chooseContact = (Button) findViewById(R.id.btn_choose_num);
		chooseContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				Setup3Activity1.this.startActivityForResult(intent, 1);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		// if (Activity.RESULT_OK == resultCode) {
		// String phone = data.getStringExtra("phone");
		// // 对字符串进行处理
		// phone = phone.replaceAll("-", " ").replaceAll(" ", "");
		// sp.edit().putString("safe_phone", phone).commit();
		// // 显示字符串
		// etNum.setText(phone);
		// }
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				Uri contactData = data.getData();
				Cursor cursor = managedQuery(contactData, null, null, null,
						null);
				cursor.moveToFirst();
				String num = this.getContactPhone(cursor);
				num = num.trim();
				etNum.setText(num);
			}
			break;

		default:
			break;
		}
	}

	public void next(View v) {
		showNextPage();
	}

	// 返回页面1
	public void previous(View v) {
		showPreviousPage();
	}

	@Override
	public void showPreviousPage() {

		startActivity(new Intent(this, Setup2Activity.class));
		finish();
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);
	}

	@Override
	public void showNextPage() {
		// 如果没有填写则不能向下跳转
		String phone = etNum.getText().toString().trim();
		// 对字符串进行处理
		if (!TextUtils.isEmpty(phone)) {
			sp.edit().putString("safe_phone", phone).commit();
			startActivity(new Intent(this, Setup4Activity.class));
			finish();
			overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		} else {
			Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
		}

	}

	private String getContactPhone(Cursor cursor) {
		// TODO Auto-generated method stub
		int phoneColumn = cursor
				.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
		int phoneNum = cursor.getInt(phoneColumn);
		String result = "";
		if (phoneNum > 0) {
			// 获得联系人的ID号
			int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			String contactId = cursor.getString(idColumn);
			// 获得联系人电话的cursor
			Cursor phone = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ contactId, null, null);
			if (phone.moveToFirst()) {
				for (; !phone.isAfterLast(); phone.moveToNext()) {
					int index = phone
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
					int typeindex = phone
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
					int phone_type = phone.getInt(typeindex);
					String phoneNumber = phone.getString(index);
					result = phoneNumber;
				}
				if (!phone.isClosed()) {
					phone.close();
				}
			}
		}
		return result;
	}

}
