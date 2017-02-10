package com.example.mobilesafer.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class SystemInfoUtils {
	/**
	 * �жϷ����Ƿ�������
	 * 
	 * @param context
	 * @param SN
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String SN) {

		// ��ȡ����
		ActivityManager manager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = manager
				.getRunningServices(100);

		System.out.println("���룺" + SN);

		for (RunningServiceInfo runningServiceInfo : runningServices) {
			String serviceName = runningServiceInfo.service.getClassName();
			System.out.println("�����ģ�" + serviceName);
			if (serviceName.equals(SN)) {
				System.out.println("����true");
				return true;
			}
		}
		System.out.println("����false");
		return false;
	}

	/**
	 * ��ѯ��ǰ�ж��ٽ���������
	 * 
	 * @param context
	 * @return ���ؽ�����
	 */
	public static int getProcessCount(Context context) {

		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// ����������еĳ������
		List<RunningAppProcessInfo> runningAppProcesses = manager
				.getRunningAppProcesses();
		return runningAppProcesses.size();
	}

	
	/**
	 * �鿴���ڴ��С
	 * @param context
	 * @return
	 */
	public static long getTotalMem(Context context) {
		/*
		 * �˷����������ڵͰ汾��
		 */
//		 ActivityManager manager = (ActivityManager)
//		 context.getSystemService(Context.ACTIVITY_SERVICE);
//		 MemoryInfo outInfo = new MemoryInfo();
//		 manager.getMemoryInfo(outInfo);
//		 return outInfo.totalMem;

		/*
		 * �����ڵͰ汾��
		 */
		long totalMem = 0;
		try {
			// /proc/meminfo �����ļ���·��
			FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			StringBuilder builder = new StringBuilder();
			
			String readLine = reader.readLine();
			for(char c : readLine.toCharArray()) {
				if(c <= '9' && c>= '0') {	//˵����һ������
					builder.append(c);
				}
			}
			//��Ϊ���ص���kb��ת����ʱ������byte������ת��
			totalMem = Long.parseLong(builder.toString()) * 1024;	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalMem;
	}
	
	/**
	 * ��ÿ����ڴ�
	 * @param context
	 * @return	���ؿ����ڴ�ֵ
	 */
	public static long getAvailMem(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		manager.getMemoryInfo(outInfo);
		
		//���ʣ���ڴ�
		long availMem = outInfo.availMem;
		return availMem;
	}
}
