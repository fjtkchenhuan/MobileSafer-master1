package com.example.mobilesafer;

import java.util.List;
import java.util.Random;

import android.graphics.drawable.Drawable;
import android.test.AndroidTestCase;
import android.text.format.Formatter;

import com.example.mobilesafer.bean.AppInfo;
import com.example.mobilesafer.bean.BlackNumberInfo;
import com.example.mobilesafer.bean.TaskInfo;
import com.example.mobilesafer.db.dao.AppLockDao;
import com.example.mobilesafer.db.dao.BlackNumberDao;
import com.example.mobilesafer.engine.AppInfos;
import com.example.mobilesafer.engine.TaskInfoParser;
import com.example.mobilesafer.utils.SmsUtils;
import com.example.mobilesafer.utils.SystemInfoUtils;

public class Test extends AndroidTestCase {

//	public void testAdd() {
//		Random random = new Random();
//		long number = 1517885;
//		for(int i = 0; i < 200; i++) {
//			number = number + i;
//			BlackNumberDao dao = new BlackNumberDao(getContext());
//			int mode = random.nextInt(3) + 1;
//			dao.add(number + "", mode + "");
//		}
//	}
//	
//	public void testDelete() {
//		BlackNumberDao dao = new BlackNumberDao(getContext());
//		if(dao.delete("1517885")) {
//			System.out.println("success");
//			
//		} else {
//			System.out.println("failed");
//		}
//	}
//	
//	public void testChange() {
//		BlackNumberDao dao = new BlackNumberDao(getContext());
//		if(dao.changeNumberMode("1517886", 1+"") ) {
//			System.out.println("success");
//		}
//	}
//	
//	public void testFindMode() {
//		BlackNumberDao dao = new BlackNumberDao(getContext());
//		String i = dao.findMode("1517886");
//		System.out.println(i);
//	}
//	
//	
//	public void testFindAll() {
//		BlackNumberDao dao = new BlackNumberDao(getContext());
//		List<BlackNumberInfo> list = dao.findAll();
//		for(BlackNumberInfo b : list) {
//			System.out.println(b.getNumber() + " : " + b.getMode());
//		}
//	}
	
//	public void testAppInfo() {
//		List<AppInfo> appInfos = AppInfos.getAppInfos(getContext());
//		System.out.println("1");
//		for(AppInfo info : appInfos) {
//			System.out.println("程序名:" + info.getApkName());
//			System.out.println("程序大小:" + info.getApkSize());
//			System.out.println("程序包名:" + info.getApkPackageName());
//			System.out.println("用户应用::" + info.isUserApp());
//			System.out.println("在rom：" + info.isInRom());
//			System.out.println("----------------------------------");
//		}
//	}
	
//	public void testSmsUtils() {
//		boolean flag = SmsUtils.backUp(getContext());
//		System.out.println("flag:" + flag);
//	}
	
//	public void testGetProcessCount() {
//		int processCount = SystemInfoUtils.getProcessCount(getContext());
//		System.out.println(processCount);
//		long totalmem = SystemInfoUtils.getTotalMem(getContext());
//		System.out.println("mem : " + totalmem);
//		String fileSize = Formatter.formatFileSize(getContext(), totalmem);
//		System.out.println("fileSize : " + fileSize);
//		String avilSize = Formatter.formatFileSize(getContext(), SystemInfoUtils.getAvailMem(getContext()));
//		System.out.println("avilSize : " + avilSize);
//	}
	
//	public void testTaskInfoParser() {
//		List<TaskInfo> taskInfos = TaskInfoParser.getTaskInfos(getContext());
//		for(TaskInfo info : taskInfos) {
//			String appName = info.getAppName();
//			Drawable icon = info.getIcon();
//			long memorySize = info.getMemorySize();
//			String formatFileSize = Formatter.formatFileSize(getContext(), memorySize*1024);
//			String packageName = info.getPackageName();
//			System.out.println("appName : " +appName);
//			System.out.println("icon : " +icon);
//			System.out.println("formatFileSize : " +formatFileSize);
//			System.out.println("packageName : " +packageName);
//			System.out.println("------------------------------------");
//		}
//	}
	
//	public void testAppLockDao() {
		//add
//		String str = "com.dwb.test";
//		for(int count = 1; count < 10; count++) {
//			str = str + count;
//			System.out.println(str);
//			AppLockDao.addLockApp(getContext(), str);
//		}
		
//		//delete
//		boolean flag = AppLockDao.delete(getContext(), "com.dwb.test1");
//		System.out.println(flag);
		
//		//重复添加
//		boolean flag = AppLockDao.addLockApp(getContext(), "com.dwb.test12");
//		System.out.println(flag);
//	}
	
	public void testFindAll() {
		AppLockDao dao = new AppLockDao(getContext());
		dao.findAll();
	}
}
