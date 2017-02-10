package com.example.mobilesafer.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.example.mobilesafer.R;
import com.example.mobilesafer.bean.AppInfo;

/**
 * 获得app信息
 * @author admin
 *
 */
public class AppInfos {
	
	/**
	 * 获得手机里的app信息
	 * @param context
	 * @return 返回一个app信息列表
	 */

	public static List<AppInfo> getAppInfos(Context context) {
		//获取PackageManager
		PackageManager manager = context.getPackageManager();
		List<PackageInfo> installedPackages = manager.getInstalledPackages(0);
		
		//存放APPInfo
		ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
		for(PackageInfo packageInfo : installedPackages) {
			AppInfo info = new AppInfo();
			//获取apk包名
			info.setApkPackageName(packageInfo.packageName);
			
			//获取apk名称
			String apkName = packageInfo.applicationInfo.loadLabel(manager).toString();
			info.setApkName(apkName);
			//获取图标
			Drawable icon = packageInfo.applicationInfo.loadIcon(manager);
			info.setIcon(icon);
			
			//获取大小,获取到目录然后计算目录下文件大小
			String sourceFile = packageInfo.applicationInfo.sourceDir;
			File file = new File(sourceFile);
			long apkSize = file.length();
			info.setApkSize(apkSize);
			
			//判断系统应用还是用户应用
			int flags = packageInfo.applicationInfo.flags;
			
			if( (flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
				info.setUserApp(false);
			} else {
				info.setUserApp(true);
			}
			
			//判断安装位置
			if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) >0) {
				info.setInRom(false);
			} else {
				info.setInRom(true);
			}
			//将对象添加到list中
			appList.add(info);
		}
		
		return appList;
	}
	
	/**
	 * 获取到已经安装的应用程序的uid和名称
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getAppsUid(Context context) {
		PackageManager packageManager = context.getPackageManager();
		List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);
		
		ArrayList<AppInfo> uids = new ArrayList<AppInfo>();
		
		for (ApplicationInfo applicationInfo : installedApplications) {
			AppInfo info = new AppInfo();
			
			int uid = applicationInfo.uid;
			String label = applicationInfo.loadLabel(packageManager).toString();
			Drawable icon = applicationInfo.loadIcon(packageManager);
			
			info.setUid(uid);
			info.setApkName(label);
			if(icon == null) {
				icon = context.getResources().getDrawable(R.drawable.ic_launcher);
			}
			info.setIcon(icon);
			
			uids.add(info);
		}
		return uids;
	}
}
