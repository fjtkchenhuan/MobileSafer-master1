package com.example.mobilesafer.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * 应用加锁的dao
 * @author admin
 *
 */
public class AppLockDao {

	private Context context;

	public AppLockDao(Context context) {
		this.context = context;
	}

	/**
	 * 添加加锁应用
	 * 
	 * @param packageName
	 *            被加锁APP包名
	 * @return 如果是-1表示没有能添加成功
	 */
	public boolean addLockApp(String packageName) {
		AppLockOpenHelper dao = new AppLockOpenHelper(context);
		SQLiteDatabase db = dao.getWritableDatabase();

		// 判断是否已经有了
		boolean flag = query(packageName);
		if (flag) {
			return false;
		}

		ContentValues values = new ContentValues();
		values.put("packagename", packageName);
		long insert = db.insert("addedappinfo", null, values);

		db.close();
		dao.close();

		// 注册一个内容提醒
		context.getContentResolver().notifyChange(Uri.parse("com.example.mobilesafer.change"), null);
		if (insert == -1) {
			return false;
		}
		
		return true;
	}

	/**
	 * 删除数据库中的数据
	 * 
	 * @param packageName
	 * @return
	 */
	public boolean delete(String packageName) {
		AppLockOpenHelper dao = new AppLockOpenHelper(context);
		SQLiteDatabase db = dao.getWritableDatabase();

		int delete = db.delete("addedappinfo", "packagename = ?",
				new String[] { packageName });

		db.close();
		dao.close();
		
		// 注册一个内容提醒
		context.getContentResolver().notifyChange(Uri.parse("com.example.mobilesafer.change"), null);

		if (delete == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 查询当前包名是否已经加密
	 * 
	 * @param packageName
	 * @return
	 */
	public boolean query(String packageName) {
		AppLockOpenHelper dao = new AppLockOpenHelper(context);
		SQLiteDatabase db = dao.getWritableDatabase();

		Cursor cursor = db.rawQuery(
				"select packagename from addedappinfo where packagename = ?",
				new String[] { packageName });
		boolean flag = cursor.moveToNext();

		cursor.close();
		db.close();
		dao.close();
		if (flag) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 拿到所有已经添加保护的程序
	 * @return 返回一个装有所有已经保护的程序的packagename的list
	 */
	public List<String> findAll() {
		AppLockOpenHelper dao = new AppLockOpenHelper(context);
		SQLiteDatabase db = dao.getWritableDatabase();
		
		ArrayList<String> results = new ArrayList<String>();
		
		Cursor cursor = db.rawQuery("select packagename from addedappinfo", null);
			
		while(cursor.moveToNext()) {
			String packageName = cursor.getString(0);
			results.add(packageName);
		}
		System.out.println(results);
		return results;
	}
}
