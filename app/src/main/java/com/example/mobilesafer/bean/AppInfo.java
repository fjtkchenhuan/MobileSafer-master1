package com.example.mobilesafer.bean;

import android.graphics.drawable.Drawable;

/**
 * 存放应用信息的bean
 * @author admin
 *
 */
public class AppInfo {
	private String apkName;
	private long apkSize;
	/*
	 * 图标
	 */
	private Drawable icon;
	/*
	 * 是不是用户程序
	 */
	private boolean userApp;
	/*
	 * 存放位置
	 */
	private boolean inRom;
	/*
	 * 包名
	 */
	private String apkPackageName;
	/*
	 * uid
	 */
	private int uid;
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getApkName() {
		return apkName;
	}
	public void setApkName(String apkName) {
		this.apkName = apkName;
	}
	public long getApkSize() {
		return apkSize;
	}
	public void setApkSize(long apkSize) {
		this.apkSize = apkSize;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public boolean isUserApp() {
		return userApp;
	}
	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}
	public boolean isInRom() {
		return inRom;
	}
	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}
	public String getApkPackageName() {
		return apkPackageName;
	}
	public void setApkPackageName(String apkPackageName) {
		this.apkPackageName = apkPackageName;
	}
	@Override
	public String toString() {
		return "AppInfo [apkName=" + apkName + ", apkSize=" + apkSize
				+ ", icon=" + icon + ", userApp=" + userApp + ", inRom="
				+ inRom + ", apkPackageName=" + apkPackageName + "]";
	}
	
	
}
