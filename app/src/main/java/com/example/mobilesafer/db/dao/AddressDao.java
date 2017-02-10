package com.example.mobilesafer.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * �����ز�ѯ
 * 
 * @author admin
 *
 */

public class AddressDao {
	private static String PATH = "data/data/com.example.mobilesafer/files/address.db";

	public static String getLocation(String phoneNum) {
		String location = "δ֪�绰";

		/*
		 * ���ڴ�һ�����ݿ� param1:���ݿ��·�� �����·��ֻ�ܸ�ֵΪdata/data/<package>/... ������ģʽ
		 * param2����ѯ��ģʽ param3:���ݿ�ģʽ��ֻ������д��������
		 */
		SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);

		// �ж��Ƿ�Ϊ�绰����
		if (phoneNum.matches("^1[3-8]\\d{9}$")) {// �ж��Ƿ�Ϊ�绰����

			// ���в�ѯ
			// select location from data2 where id=(select outkey from data1
			// where id=1300000)
			Cursor cursor = db
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[] { phoneNum.substring(0, 7) });

			if (cursor.moveToNext()) {// ���������
				location = cursor.getString(0);
				cursor.close(); // ��Լ��Դ
			}
		} else if (phoneNum.matches("^\\d+$")) { // ��������ֵĻ�
			// ����������ж�
			switch (phoneNum.length()) {
			case 3: {
				location = "�����绰";
				break;
			}
			case 4: {
				location = "ģ����";
				break;
			}
			case 5: {
				location = "�ͷ��绰";
				break;
			}
			case 7:
			case 8: {
				location = "���ص绰";
				break;
			}
			default: {
				if (phoneNum.startsWith("0") && phoneNum.length() > 10) { // �п����ǳ�;�绰
					// select location from data2 where area=931
					Cursor cursor = db.rawQuery(
							"select location from data2 where area=?",
							new String[] { phoneNum.substring(1, 4) });
					if (cursor.moveToNext()) {
						location = cursor.getString(0);
					} else {
						cursor.close();
						cursor = db.rawQuery(
								"select location from data2 where area=?",
								new String[] { phoneNum.substring(1, 3) });
						if (cursor.moveToNext()) {
							location = cursor.getString(0);
						}
					}
					cursor.close();
				}
			}
			}
		}
		db.close();
		return location;
	}
}
