package com.example.mobilesafer.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 归属地查询
 * 
 * @author admin
 *
 */

public class AddressDao {
	private static String PATH = "data/data/com.example.mobilesafer/files/address.db";

	public static String getLocation(String phoneNum) {
		String location = "未知电话";

		/*
		 * 用于打开一个数据库 param1:数据库的路径 这里的路径只能赋值为data/data/<package>/... 这样的模式
		 * param2：查询的模式 param3:数据库模式（只读，读写。。。）
		 */
		SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);

		// 判断是否为电话号码
		if (phoneNum.matches("^1[3-8]\\d{9}$")) {// 判断是否为电话号码

			// 进行查询
			// select location from data2 where id=(select outkey from data1
			// where id=1300000)
			Cursor cursor = db
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[] { phoneNum.substring(0, 7) });

			if (cursor.moveToNext()) {// 如果有数据
				location = cursor.getString(0);
				cursor.close(); // 节约资源
			}
		} else if (phoneNum.matches("^\\d+$")) { // 如果是数字的话
			// 进行种类的判断
			switch (phoneNum.length()) {
			case 3: {
				location = "报警电话";
				break;
			}
			case 4: {
				location = "模拟器";
				break;
			}
			case 5: {
				location = "客服电话";
				break;
			}
			case 7:
			case 8: {
				location = "本地电话";
				break;
			}
			default: {
				if (phoneNum.startsWith("0") && phoneNum.length() > 10) { // 有可能是长途电话
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
