package com.example.mobilesafer.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * �������ݿ��dao
 * 
 * @author admin
 *
 */

public class AntivirusDao {
	private static String PATH = "data/data/com.example.mobilesafer/files/antivirus.db";

	/**
	 * ����md5ֵ������Ƿ��в���
	 * 
	 * @param md5
	 * @return
	 */
	public static String checkFileVirus(String md5) {
		String desc = null;

		SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);
		
		Cursor cursor = db.rawQuery("select desc from datable where md5=? ", new String[]{md5});
		if(cursor.moveToNext()) {
			desc = cursor.getString(0);
		}
		cursor.close();

		return desc;
	}
}
