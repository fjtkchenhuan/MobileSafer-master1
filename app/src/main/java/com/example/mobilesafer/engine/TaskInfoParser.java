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
 * ��ȡ��ǰ�߳�����
 * @author admin
 *
 */

public class TaskInfoParser {

	/**
	 * ��ȡ�������еĳ����List����
	 * @param context
	 * @return	����һ��װ��TaskInfo��list����
	 */
	public static List<TaskInfo> getTaskInfos(Context context) {
		//����װtaskInfo�ļ���
		ArrayList<TaskInfo> list = new ArrayList<TaskInfo>();
		
		//����Ҫ�ķ�������ǹ�����
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager packageManager = context.getPackageManager();

		List<RunningAppProcessInfo> runningAppProcesses = manager
				.getRunningAppProcesses();

		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			TaskInfo info = new TaskInfo();

			String processName = runningAppProcessInfo.processName; // �õ����ǰ���
			info.setPackageName(processName);

			// ͨ��PackageManager����ȡ������Ϣ
			try {
				// ͨ��packageManager��ȡ������Ϣ
				ApplicationInfo applicationInfo = packageManager
						.getApplicationInfo(processName, 0);

				/*
				 * ��ȡռ���ڴ�
				 */
				int[] pids = new int[1];
				pids[0] = runningAppProcessInfo.pid;
				android.os.Debug.MemoryInfo[] processMemoryInfo = manager
						.getProcessMemoryInfo(pids);
				int privateDirty = processMemoryInfo[0].getTotalPrivateDirty();
				info.setMemorySize(privateDirty);

				/*
				 * �жϽ���
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
				 * ��ȡͼ�꣬��Ӧ���� ��ֹϵͳӦ��û��ͼ�걨�����Էŵ���󣬱�֤������Ϣ˳����¼
				 * ��Ϊ��ЩϵͳӦ����û�����ֵģ����Ի���lable�Ϳ�ʼ�����������쳣�����ʱ����Ӧ���д���
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
