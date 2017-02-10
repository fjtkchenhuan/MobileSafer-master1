package com.example.mobilesafer.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

public class SmsUtils {

	public interface ISmsDate {
		//将count传出去让实现者使用
		void getCount(int count);
		void currentProgress(int current);
	}
	
	/**
	 * 备份短信
	 * 
	 * @param context
	 *            传入一个上下文
	 * @return 返回备份成功与否
	 */
	public static boolean backUp(Context context , ISmsDate datePasser) {
		// 检查存储卡的状态
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			//初始化
			ContentResolver resolver = context.getContentResolver();
			Uri uri = Uri.parse("content://sms/");
			Cursor cursor = resolver.query(uri, new String[] { "address",
					"date", "type", "body" }, null, null, null);
			
			int current = 0;
			int count = cursor.getCount();	//总共的短信条数
			datePasser.getCount(count);	//将总的条数传出
			System.out.println("count" + count);
				try {
					// xml输出设置
					File file = new File(
							Environment.getExternalStorageDirectory(),
							"backup.xml");
					XmlSerializer serializer = Xml.newSerializer();
					FileOutputStream fos = new FileOutputStream(file);
					serializer.setOutput(fos, "utf-8");
					
					//开始输出xml文档
					serializer.startDocument("utf-8", true);

					serializer.startTag(null, "smss");
					serializer.attribute(null, "size", String.valueOf(count));	//设置一共备份的短信条数属性
					while (cursor.moveToNext()) {
						// 获取数据
						String address = cursor.getString(0);
						String date = cursor.getString(1);
						String type = cursor.getString(2);
						String body = cursor.getString(3);
						// 生成xml文件
						serializer.startTag(null, "sms");

						serializer.startTag(null, "address");
						serializer.text(address);
						serializer.endTag(null, "address");
						
						serializer.startTag(null, "date");
						serializer.text(date);
						serializer.endTag(null, "date");

						serializer.startTag(null, "type");
						serializer.text(type);
						serializer.endTag(null, "type");
						
						serializer.startTag(null, "body");
						serializer.text(body);
						serializer.endTag(null, "body");

						serializer.endTag(null, "sms");
						
						System.out.println(address);
						current++;
						datePasser.currentProgress(current);
						System.out.println("0");
					}
					serializer.endTag(null, "smss");
System.out.println("1");
					serializer.endDocument();
					fos.close();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		return false;
	}
} 
