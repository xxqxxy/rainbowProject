package com.rainbow.main.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.comutils.main.SharePreferences;
import com.rainbow.main.MainActivity;
import com.rainbow.main.PersonalActivity;
import com.rainbow.main.R;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

public class UpdateService extends Service {
	private int titleId = 0;

	private File updateDir = null;
	private File updateFile = null;
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;
	private NotificationManager updateNotificationManager = null;
	private Notification updateNotification = null;
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;
	RemoteViews contentView;
	int downloadCount = 0;
	int currentSize = 0;
	long totalSize = 0;
	int updateTotalSize = 0;
	SharePreferences isPreferences;
	String savefile = null;
	Notification.Builder builder = null;
	@SuppressLint("NewApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		titleId = intent.getIntExtra("titleId", 0);
		isPreferences = new SharePreferences(this);
		savefile = getString(R.string.app_cu_path)+"RainBowEnergy_"+isPreferences.getSp().getString("vsn_name", "").toString().replace(".", "_")+".apk";
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			updateDir = new File(Environment.getExternalStorageDirectory(),
					savefile);
			updateFile = new File(updateDir.getPath(),   isPreferences.getSp().getString("vsn_name", "").toString().replace(".", "_")+".apk");
		}

		this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		this.updateNotification = new Notification();

		builder =  new Notification.Builder(this).setTicker("彩虹能源新版本下载")
				.setSmallIcon(R.drawable.icon_login_logo);
		if(isPreferences.getSp().getString("update_from", "").equals("PersonalActivity")){
			updateIntent = new Intent(this, PersonalActivity.class);
		}else{
			updateIntent = new Intent(this, MainActivity.class);
		}
		updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent,
				0);
		updateNotification.icon = R.drawable.icon_login_logo;
		updateNotification.tickerText = "开始下载";
//		updateNotification.setLatestEventInfo(this, "QQ", "0%",
//				updatePendingIntent);
		
		updateNotification = builder.setContentIntent(updatePendingIntent).setContentTitle("彩虹能源").setContentText("0%").build();
		updateNotificationManager.notify(0, updateNotification);

		new Thread(new updateRunnable()).start();// ��������ص��ص㣬�����صĹ���

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private Handler updateHandler = new Handler() {
		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				
			case DOWNLOAD_COMPLETE:
				Uri uri = Uri.fromFile(updateFile);
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				installIntent.setDataAndType(uri,
						"application/vnd.android.package-archive");

				updatePendingIntent = PendingIntent.getActivity(
						UpdateService.this, 0, installIntent, 0);

				updateNotification.defaults = Notification.DEFAULT_SOUND;// ��������
//				updateNotification.setLatestEventInfo(UpdateService.this,
//						"QQ", "下载完成,点击安装", updatePendingIntent);
				
				updateNotification = builder.setContentIntent(updatePendingIntent).setContentTitle("彩虹能源").setContentText("下载完成,点击安装。").build();
				
				updateNotificationManager.notify(0, updateNotification);

				startActivity(installIntent);
				stopService(updateIntent);
			case DOWNLOAD_FAIL:
//				updateNotification.setLatestEventInfo(UpdateService.this,
//						"QQ", "下载完成,点击安装", updatePendingIntent);
				updateNotification = builder.setContentIntent(updatePendingIntent).setContentTitle("彩虹能源").setContentText("下载完成,点击安装。").build();
				
				updateNotificationManager.notify(0, updateNotification);
			default:
				stopService(updateIntent);
			}
		}
	};

	@SuppressLint("NewApi")
	public long downloadUpdateFile(String downloadUrl, File saveFile)
			throws Exception {

		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection
					.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes="
						+ currentSize + "-");
			}
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();
			if (httpConnection.getResponseCode() == 404) {
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile, false);
			byte buffer[] = new byte[4096];
			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;
				if ((downloadCount == 0)
						|| (int) (totalSize * 100 / updateTotalSize) - 10 > downloadCount) {
					downloadCount += 10;

//					updateNotification.setLatestEventInfo(UpdateService.this,
//							"彩虹能源", (int) totalSize * 100 / updateTotalSize
//									+ "%", updatePendingIntent);
					updateNotification = builder.setContentIntent(updatePendingIntent).setContentTitle("彩虹能源").setContentText("彩虹能源下载进度："+(int) totalSize * 100 / updateTotalSize+ "%").build();
					
					updateNotification.contentView = new RemoteViews(
							getPackageName(), R.layout.notification_item);
					updateNotification.contentView.setTextViewText(
							R.id.notificationTitle, "彩虹能源新版本下载");
					updateNotification.contentView.setProgressBar(
							R.id.notificationProgress, 100, downloadCount, false);
					
					updateNotificationManager.notify(0, updateNotification);
				}
			}
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return totalSize;
	}

	class updateRunnable implements Runnable {
		Message message = updateHandler.obtainMessage();

		public void run() {
			message.what = DOWNLOAD_COMPLETE;
			
			
			try {
				// android:name="android.permission.WRITE_EXTERNAL_STORAGE">;
				if (!updateDir.exists()) {
					updateDir.mkdirs();
				}
				if (!updateFile.exists()) {
					updateFile.createNewFile();
				}
				//"http://softfile.3g.qq.com:8080/msoft/179/1105/10753/MobileQQ1.0(Android)_Build0198.apk"
				// android:name="android.permission.INTERNET">;
				long downloadSize = downloadUpdateFile(
						isPreferences.getSp().getString("vsn_apppath", "").toString(),
						updateFile);
				if (downloadSize > 0) {
					updateHandler.sendMessage(message);
				} 
			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				updateHandler.sendMessage(message);
			}
		}
	}
}
