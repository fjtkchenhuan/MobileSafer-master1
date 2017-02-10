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
	 * ��Ӻ�����
	 * 
	 * @param number
	 *            ����������
	 * @param mode
	 *            ����ģʽ
	 * @return �����Ƿ�ɹ��ı�
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
	 * ͨ���绰����ɾ��������
	 * 
	 * @param number
	 * @return �����Ƿ�ɹ��ı�
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
	 * ͨ���绰����ı�����ģʽ
	 * 
	 * @param number
	 * @param mode
	 * @return �����Ƿ�ɹ��ı�
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
	 * ͨ���绰�����������ģʽ
	 * 
	 * @param number
	 * @return ���ص�ǰ����ģʽ
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
	 * �������еĺ���������
	 * 
	 * @return ����װ�к����������List
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
	 * ��ҳ��ȡ����
	 * 
	 * @param pageNumber
	 *            ��ǰҳ��
	 * @param pageSize
	 *            ÿҳ������
	 * @return ����װ�к����������List
	 * 
	 *         limit ��ʾ���������� offset ��ʾ�ӵڼ�����ʼ����
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
	 * ������������
	 * 
	 * @param offset
	 *            ��ʼ��λ��
	 * @param size
	 *            ÿ��������
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
	 * ��ȡ����ǰ���ݿ����ݵ�����
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
