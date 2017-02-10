package com.example.mobilesafer.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
	public static String readStream2String(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 读取数据
		int len = 0;
		byte[] arr = new byte[1024];
		while( (len = is.read(arr)) != -1) {
			bos.write(arr, 0, len);
		}
		//将数据吐出
		return bos.toString();
	}
}
