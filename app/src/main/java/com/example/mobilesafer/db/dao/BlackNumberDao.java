package com.example.mobilesafer.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobilesafer.bean.BlackNumberInfo;

public class BlackNumberDao {
	private BlackNumberOpenHelper openHelper;

	public BlackNumberDao(Context context) {
		openHelper = new BlackNumberOpenHelper(context);
	}

	/**
	 * 添加黑名单
	 * 
	 * @param number
	 *            黑名单号码
	 * @param mode
	 *            拦截模式
	 * @return 返回是否成功改变
	 */
	public boolean add(String number, String mode) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		long rowId = db.insert("blacknumber", null, values);
		db.close();
		if (rowId == -1) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 通过电话号码删除黑名单
	 * 
	 * @param number
	 * @return 返回是否成功改变
	 */
	public boolean delete(String number) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int rows = db
				.delete("blacknumber", "number=?", new String[] { number });
		if (rows == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 通过电话号码改变拦截模式
	 * 
	 * @param number
	 * @param mode
	 * @return 返回是否成功改变
	 */
	public boolean changeNumberMode(String number, String mode) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		int rows = db.update("blacknumber", values, "number=?",
				new String[] { number });
		db.close();
		if (rows == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 通过电话号码查找拦截模式
	 * 
	 * @param number
	 * @return 返回当前拦截模式
	 */
	public String findMode(String number) {
		String mode = "";
		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor cursor = db.query("blacknumber", new String[] { "mode" },
				"number=?", new String[] { number }, null, null, null);
		if (cursor.moveToNext()) {
			mode = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return mode;
	}

	/**
	 * 查找所有的黑名单数据
	 * 
	 * @return 返回装有黑名单对象的List
	 */
	public List<BlackNumberInfo> findAll() {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor cursor = db
				.query("blacknumber", new String[] { "number", "mode" }, null,
						null, null, null, null);
		ArrayList<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			info.setNumber(cursor.getString(0));
			info.setMode(cursor.getString(1));
			list.add(info);
		}
		cursor.close();
		db.close();
		return list;
	}

	/**
	 * 分页获取数据
	 * 
	 * @param pageNumber
	 *            当前页码
	 * @param pageSize
	 *            每页的容量
	 * @return 返回装有黑名单对象的List
	 * 
	 *         limit 表示多少条数据 offset 表示从第几条开始返回
	 */
	public List<BlackNumberInfo> findPar(int pageNumber, int pageSize) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(
				"select number,mode from blacknumber limit ? offset ?",
				new String[] { String.valueOf(pageSize),
						String.valueOf(pageSize * pageNumber) });
		ArrayList<BlackNumberInfo> lists = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			info.setNumber(cursor.getString(0));
			info.setMode(cursor.getString(1));
			lists.add(info);
		}
		cursor.close();
		db.close();
		return lists;
	}

	/**
	 * 分批加载数据
	 * 
	 * @param offset
	 *            开始的位置
	 * @param size
	 *            每批的数量
	 * @return
	 */
	public List<BlackNumberInfo> findPar1(int offset, int size) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(
				"select number,mode from blacknumber limit ? offset ?",
				new String[] { String.valueOf(size), String.valueOf(offset) });
		ArrayList<BlackNumberInfo> lists = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			info.setNumber(cursor.getString(0));
			info.setMode(cursor.getString(1));
			lists.add(info);
		}
		cursor.close();
		db.close();
		return lists;
	}

	/**
	 * 获取到当前数据库数据的条数
	 * 
	 * @return
	 */
	public int countNumber() {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
		cursor.moveToNext();
		String str = cursor.getString(0);
		int number = Integer.parseInt(str);
		return number;
	}
}
