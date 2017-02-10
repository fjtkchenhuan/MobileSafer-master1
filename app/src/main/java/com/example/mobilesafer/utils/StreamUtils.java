package com.example.mobilesafer.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
	public static String readStream2String(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// ��ȡ����
		int len = 0;
		byte[] arr = new byte[1024];
		while( (len = is.read(arr)) != -1) {
			bos.write(arr, 0, len);
		}
		//�������³�
		return bos.toString();
	}
}
