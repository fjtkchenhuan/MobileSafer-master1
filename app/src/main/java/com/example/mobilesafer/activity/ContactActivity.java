package com.example.mobilesafer.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.mobilesafer.R;

public class ContactActivity extends Activity {
	
	private ListView lv;
	ArrayList<HashMap<String , String>> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		
		list = new ArrayList<HashMap<String , String>>();
		lv = (ListView) findViewById(R.id.lv_contact);
		lv.setAdapter(new SimpleAdapter(this, list, R.layout.contact_item, new String[]{"name" ,"phone"},  new int[]{R.id.tv_name , R.id.tv_num}));
		getContect();
		
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//获取点击的对象
				String phone = list.get(position).get("phone");
				Intent i = new Intent();
				i.putExtra("phone", phone);
				setResult(Activity.RESULT_OK , i);
				finish();
			}
		});
	}
	
	
private void getContect() {
		
		// 查询raw_contacts表，获取到联系人id
		Uri rawContactsUri = Uri
				.parse("content://com.android.contacts/raw_contacts");
		Cursor contactsCursor = getContentResolver().query(rawContactsUri,
				new String[] { "contact_id" }, null, null, null);
		if (contactsCursor != null) {
			while (contactsCursor.moveToNext()) {
				// 获取到联系人ID
				String contactId = contactsCursor.getString(0);
				System.out.println(contactId);
				// 查询data表，获取到数据
				Uri DataUri = Uri.parse("content://com.android.contacts/data");
				Cursor dataCursor = getContentResolver().query(DataUri,
						new String[] { "data1", "mimetype" }, "contact_id=?",
						new String[] { contactId }, null);
				if (dataCursor != null) {
					HashMap<String, String> map = new HashMap<String, String>();
					while (dataCursor.moveToNext()) {
						String data1 = dataCursor.getString(dataCursor
								.getColumnIndex("data1"));
						String mimetype = dataCursor.getString(dataCursor
								.getColumnIndex("mimetype"));
						System.out.println(contactId + ":" + data1 + ":"
								+ mimetype);
						if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
							map.put("phone", data1);
						} else if ("vnd.android.cursor.item/name"
								.equals(mimetype)) {
							map.put("name", data1);
						}
						
					}
					list.add(map);
					dataCursor.close();
				}
			}
		}
		contactsCursor.close();
	}
}
