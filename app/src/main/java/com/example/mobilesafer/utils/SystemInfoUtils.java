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
	 * 判断服务是否还在运行
	 * 
	 * @param context
	 * @param SN
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String SN) {

		// 获取服务
		ActivityManager manager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = manager
				.getRunningServices(100);

		System.out.println("传入：" + SN);

		for (RunningServiceInfo runningServiceInfo : runningServices) {
			String serviceName = runningServiceInfo.service.getClassName();
			System.out.println("遍历的：" + serviceName);
			if (serviceName.equals(SN)) {
				System.out.println("返回true");
				return true;
			}
		}
		System.out.println("返回false");
		return false;
	}

	/**
	 * 查询当前有多少进程在运行
	 * 
	 * @param context
	 * @return 返回进程数
	 */
	public static int getProcessCount(Context context) {

		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 获得正在运行的程序进程
		List<RunningAppProcessInfo> runningAppProcesses = manager
				.getRunningAppProcesses();
		return runningAppProcesses.size();
	}

	
	/**
	 * 查看总内存大小
	 * @param context
	 * @return
	 */
	public static long getTotalMem(Context context) {
		/*
		 * 此方法不能跑在低版本上
		 */
//		 ActivityManager manager = (ActivityManager)
//		 context.getSystemService(Context.ACTIVITY_SERVICE);
//		 MemoryInfo outInfo = new MemoryInfo();
//		 manager.getMemoryInfo(outInfo);
//		 return outInfo.totalMem;

		/*
		 * 适用于低版本的
		 */
		long totalMem = 0;
		try {
			// /proc/meminfo 配置文件的路径
			FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			StringBuilder builder = new StringBuilder();
			
			String readLine = reader.readLine();
			for(char c : readLine.toCharArray()) {
				if(c <= '9' && c>= '0') {	//说明是一个数字
					builder.append(c);
				}
			}
			//因为返回的是kb，转换的时候是以byte来进行转换
			totalMem = Long.parseLong(builder.toString()) * 1024;	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalMem;
	}
	
	/**
	 * 获得可用内存
	 * @param context
	 * @return	返回可用内存值
	 */
	public static long getAvailMem(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		manager.getMemoryInfo(outInfo);
		
		//获得剩余内存
		long availMem = outInfo.availMem;
		return availMem;
	}
}
