package com.example.mobilesafer.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Utils {
	/**
	 * 用于对密码进行md5加密
	 * @param password
	 * @return
	 */
	public static String encode(String password) {
		StringBuilder sb = new StringBuilder();
		try {
			MessageDigest instance = MessageDigest.getInstance("MD5");
			byte[] digest = instance.digest(password.getBytes());
			
			
			for(byte b : digest) {
				int i = b & 0xff;//取低八位
				String hexString = Integer.toHexString(i);
				if(hexString.length() < 2) {
					hexString = "0" + hexString;
				}
				sb.append(hexString);
			}
		} catch (NoSuchAlgorithmException e) {
			
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * 获得一个文件的md5值
	 * @param sourceDir
	 * @return
	 */
	public static String getFileMd5(String sourceDir) {
		String result = null;
		StringBuilder sb = new StringBuilder();
		
		File file = new File(sourceDir);
		try {
			FileInputStream fis = new FileInputStream(file);
			
			byte[] buffer = new byte[1024];
			int len = 0;
			
			MessageDigest md = MessageDigest.getInstance("md5");
			
			while( (len = fis.read(buffer)) != -1) {
				md.update(buffer, 0, len);
			}
			
			byte[] digest = md.digest();
			for(byte b : digest){
				int number = b&0xff; // 加盐 +1 ;
				String hex = Integer.toHexString(number);
				if(hex.length()==1){
					sb.append("0"+hex);
				}else{
					sb.append(hex);
				}
			}
			
			result = sb.toString();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
