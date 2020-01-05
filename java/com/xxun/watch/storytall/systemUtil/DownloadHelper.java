package com.xxun.watch.storytall.systemUtil;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadHelper {
	public interface DownloadListener{
		void onRedirectURL(final String redirectURL);
		void onDownloadHelperOffset(long offset, long total);
	}
	/**
	 * 空实现
	 * @author arlen.dai(戴亚伟) */
	public static class EmptyDownloadListener implements DownloadListener{
		public void onRedirectURL(String redirectURL) {
		}
		public void onDownloadHelperOffset(long offset,long total) {
		}
	}
	
	public static boolean downloadFile(String urlString, File file, boolean redirect, boolean append, DownloadListener listener) {
		Log.i("downloadFile()","urlString="+urlString);
		Log.i("downloadFIle:",String.format("downloadFile: %s ==> %s",urlString, file.getPath()));
		
		boolean success = false,interrupted = false;
		HttpURLConnection con = null;
		DataOutputStream dos = null;
		DataInputStream dis = null;
		
		try {
			long offset = 0;
			boolean throwFirstByte = false;
			
			URL url = new URL(urlString);
			Log.i("openConnection()","YES");
			con = (HttpURLConnection) url.openConnection();
			Log.i("openConnection()","OK");
			con.setInstanceFollowRedirects(true);
			con.setConnectTimeout(50 * 1000);
			con.setReadTimeout(50 * 1000);
			
			con.setRequestMethod("GET") ;
			con.setRequestProperty(
	                "Accept",
	                "image/gif, image/jpeg, image/pjpeg, image/pjpeg, " +
	                "application/x-shockwave-flash, application/xaml+xml, " +
	                "application/vnd.ms-xpsdocument, application/x-ms-xbap, " +
	                "application/x-ms-application, application/vnd.ms-excel, " +
	                "application/vnd.ms-powerpoint, application/msword, */*");
			con.setRequestProperty("Accept-Language", "zh-CN");
			con.setRequestProperty("Charset", "UTF-8");
	        //设置浏览器类型和版本、操作系统，<STRONG>使用</STRONG>语言等信息
			con.setRequestProperty(
	                "User-Agent",
	                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.2; Trident/4.0; " +
	                ".NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; " +
	                ".NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
	        //设置为长连接
			con.setRequestProperty("Connection", "Keep-Alive");

			long fileSize = file.length();
			if (fileSize > 0 && append) {/* Range: bytes=0- */
				Log.i("old filesize:",fileSize+":");
				throwFirstByte = true;
				con.addRequestProperty("Range", String.format("bytes=%d-", fileSize-1));
			}
			
			offset = fileSize;
			
			if (redirect==true && listener != null) {
				URL redirecturl = con.getURL();
				if (redirecturl != null && redirecturl.toString() != null) {
					Log.i("redirect URL:",redirecturl.toString());
					listener.onRedirectURL(con.getURL().toString());
				}
			}
            //获取响应状态
            if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d("connect"," failed!");
            
            }else{		
			int contentLen = con.getContentLength();
			Log.i("ConnectLength: ",contentLen+" bytes");
			
			dis = new DataInputStream(con.getInputStream());
			dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file, append)));
			 
			if (listener != null && offset > 0) {
				listener.onDownloadHelperOffset(offset,contentLen);
			}
			int buffSize = 1*1024*1024;//不需要那么大的buff，一次读入通常读不满
			byte[] buffer = new byte[buffSize];
			int len = 0;
			while ((len = dis.read(buffer)) != -1){
				if (!Thread.currentThread().isInterrupted()) {
					if (throwFirstByte && len > 0) {
						dos.write(buffer, 1, len-1);
						throwFirstByte = false;
					} else if(len > 0){
						dos.write(buffer, 0, len);
					}
					offset += len;
					if (null!=listener) {
						listener.onDownloadHelperOffset(offset,contentLen);//报告当前进度
					}
				} else {
					interrupted = true;
					Log.e("upgradetask is ","interrupted!");
					break;
				}
			}
			if(!interrupted) {
				success = true;
			}
			if (null!=listener) {
				listener.onDownloadHelperOffset(offset,contentLen);//报告当前进度
			}
            }
		} catch (Exception e) {
			if(!(e instanceof MalformedURLException)){
				Log.e("====","I am error");
				Log.e("error" , e.toString());
			}
			success = false;
		} finally {
			try {
				if(null!=dos) {
					dos.close();
				}
				if(null!=dis) {
					dis.close();
				}
				if (con != null) {
					con.disconnect();
				}
			} catch (IOException e) {
			}
		}
		return success;
	}
}