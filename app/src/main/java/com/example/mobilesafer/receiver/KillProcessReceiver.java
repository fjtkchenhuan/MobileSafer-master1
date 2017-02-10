package com.example.mobilesafer.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.mobilesafer.bean.TaskInfo;
import com.example.mobilesafer.engine.TaskInfoParser;

public class KillProcessReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 清除进程
		List<TaskInfo> taskInfos = TaskInfoParser.getTaskInfos(context);
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for(TaskInfo info : taskInfos) {
			manager.killBackgroundProcesses(info.getPackageName());
		}
		int count = taskInfos.size();
		Toast.makeText(context,  "一共清除" + count + "个进程" ,0).show();;
	}

}
