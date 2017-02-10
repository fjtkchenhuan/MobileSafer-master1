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
 * ���app��Ϣ
 * @author admin
 *
 */
public class AppInfos {
	
	/**
	 * ����ֻ����app��Ϣ
	 * @param context
	 * @return ����һ��app��Ϣ�б�
	 */

	public static List<AppInfo> getAppInfos(Context context) {
		//��ȡPackageManager
		PackageManager manager = context.getPackageManager();
		List<PackageInfo> installedPackages = manager.getInstalledPackages(0);
		
		//���APPInfo
		ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
		for(PackageInfo packageInfo : installedPackages) {
			AppInfo info = new AppInfo();
			//��ȡapk����
			info.setApkPackageName(packageInfo.packageName);
			
			//��ȡapk����
			String apkName = packageInfo.applicationInfo.loadLabel(manager).toString();
			info.setApkName(apkName);
			//��ȡͼ��
			Drawable icon = packageInfo.applicationInfo.loadIcon(manager);
			info.setIcon(icon);
			
			//��ȡ��С,��ȡ��Ŀ¼Ȼ�����Ŀ¼���ļ���С
			String sourceFile = packageInfo.applicationInfo.sourceDir;
			File file = new File(sourceFile);
			long apkSize = file.length();
			info.setApkSize(apkSize);
			
			//�ж�ϵͳӦ�û����û�Ӧ��
			int flags = packageInfo.applicationInfo.flags;
			
			if( (flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
				info.setUserApp(false);
			} else {
				info.setUserApp(true);
			}
			
			//�жϰ�װλ��
			if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) >0) {
				info.setInRom(false);
			} else {
				info.setInRom(true);
			}
			//��������ӵ�list��
			appList.add(info);
		}
		
		return appList;
	}
	
	/**
	 * ��ȡ���Ѿ���װ��Ӧ�ó����uid������
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
