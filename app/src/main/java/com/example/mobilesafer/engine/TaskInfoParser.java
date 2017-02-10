package com.example.mobilesafer.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.example.mobilesafer.R;
import com.example.mobilesafer.bean.TaskInfo;

/**
 * 获取当前线程数据
 * @author admin
 *
 */

public class TaskInfoParser {

	/**
	 * 获取正在运行的程序的List集合
	 * @param context
	 * @return	返回一个装有TaskInfo的list集合
	 */
	public static List<TaskInfo> getTaskInfos(Context context) {
		//用于装taskInfo的集合
		ArrayList<TaskInfo> list = new ArrayList<TaskInfo>();
		
		//所需要的服务或者是管理器
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager packageManager = context.getPackageManager();

		List<RunningAppProcessInfo> runningAppProcesses = manager
				.getRunningAppProcesses();

		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			TaskInfo info = new TaskInfo();

			String processName = runningAppProcessInfo.processName; // 拿到的是包名
			info.setPackageName(processName);

			// 通过PackageManager来获取其他信息
			try {
				// 通过packageManager获取包的信息
				ApplicationInfo applicationInfo = packageManager
						.getApplicationInfo(processName, 0);

				/*
				 * 获取占用内存
				 */
				int[] pids = new int[1];
				pids[0] = runningAppProcessInfo.pid;
				android.os.Debug.MemoryInfo[] processMemoryInfo = manager
						.getProcessMemoryInfo(pids);
				int privateDirty = processMemoryInfo[0].getTotalPrivateDirty();
				info.setMemorySize(privateDirty);

				/*
				 * 判断进程
				 */
				int flags = packageManager.getApplicationInfo(processName, 0).flags;
				boolean userApp = false;
				if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
					userApp = false;
				} else {
					userApp = true;
				}
				info.setUserApp(userApp);

				/*
				 * 获取图标，和应用名 防止系统应用没有图标报错，所以放到最后，保证其他信息顺利记录
				 * 因为有些系统应用是没有名字的，所以会在lable就开始出错，所以在异常处理的时候相应进行处理
				 */
				String label = applicationInfo.loadLabel(packageManager)
						.toString();
				info.setAppName(label);

				Drawable icon = applicationInfo.loadIcon(packageManager);
				info.setIcon(icon);
				
			} catch (Exception e) {
				e.printStackTrace();
				info.setIcon(context.getResources().getDrawable(
						R.drawable.ic_launcher));
				info.setAppName(processName);
			}
			System.out.println(processName);

			list.add(info);
		}
		return list;
	}
}
