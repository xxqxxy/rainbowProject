package com.rainbow.main;

import org.json.JSONException;
import org.json.JSONObject;

import com.comutils.main.SharePreferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";
	private SharePreferences isPreferences;

	@Override
	public void onReceive(Context context, Intent intent) {
		isPreferences = new SharePreferences(context);
		Bundle bundle = intent.getExtras();
		// Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ",
		// extras: " + printBundle(bundle));
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Log.i("JPUSH", "附加字段:" + extras);
		String type = "", warn_sta = "", link_id = "", task_type = "", task_state = "";
		try {
			JSONObject jsonj = new JSONObject(extras);
			type = jsonj.getString("type").toString().replace("null", "");
			warn_sta = jsonj.getString("warn_sta").toString().replace("null", "");
			link_id = jsonj.getString("link_id").toString().replace("null", "");
			task_type = jsonj.getString("task_type").toString().replace("null", "");
			task_state = jsonj.getString("task_state").toString().replace("null", "");
			Log.i("JPUSH", "附加详细字段:" + type + ", " + warn_sta + ", " + link_id);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			// Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
			// send the Registration Id to your server...

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			// Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " +
			// bundle.getString(JPushInterface.EXTRA_MESSAGE));
			// processCustomMessage(context, bundle);

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			// Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
			// int notifactionId =
			// bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			// Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
			isPreferences.updateSp("isnews", true);
			context.sendBroadcast(new Intent("com.rainbow.main.PersonalActivity"));

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			// Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

			// 打开自定义的Activity
			if (type.equals("1")) {// 任务详情
				context.startActivity(new Intent(context, taskDesActivity.class).putExtra("task_id", link_id)
						.putExtra("tk_type", task_type + "")// 1警报 // 2// 图文
						.putExtra("tk_state", task_state + "")
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
			} else if (type.equals("2")) {// 告警详情
				context.startActivity(new Intent(context, alarmDesActivity.class).putExtra("warn_sta", warn_sta + "")
						.putExtra("warn_id", link_id + "")
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
			} else {
				Intent i = new Intent(context, msgActivity.class);
				i.putExtras(bundle);
				// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(i);
			}

		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
			// Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " +
			// bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..

		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
			boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			// Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state
			// change to "+connected);
		} else {
			// Log.d(TAG, "[MyReceiver] Unhandled intent - " +
			// intent.getAction());
		}
	}

	// 打印所有的 intent extra 数据
	/*
	 * private static String printBundle(Bundle bundle) { StringBuilder sb = new
	 * StringBuilder(); for (String key : bundle.keySet()) { if
	 * (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) { sb.append("\nkey:" +
	 * key + ", value:" + bundle.getInt(key)); }else
	 * if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
	 * sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key)); } else {
	 * sb.append("\nkey:" + key + ", value:" + bundle.getString(key)); } }
	 * return sb.toString(); }
	 * 
	 * //send msg to MainActivity private void processCustomMessage(Context
	 * context, Bundle bundle) { if (mainActivity.isForeground) { String message
	 * = bundle.getString(JPushInterface.EXTRA_MESSAGE); String extras =
	 * bundle.getString(JPushInterface.EXTRA_EXTRA); Intent msgIntent = new
	 * Intent(mainActivity.MESSAGE_RECEIVED_ACTION);
	 * msgIntent.putExtra(mainActivity.KEY_MESSAGE, message); if
	 * (!ExampleUtil.isEmpty(extras)) { try { JSONObject extraJson = new
	 * JSONObject(extras); if (null != extraJson && extraJson.length() > 0) {
	 * msgIntent.putExtra(mainActivity.KEY_EXTRAS, extras); } } catch
	 * (JSONException e) {
	 * 
	 * }
	 * 
	 * } context.sendBroadcast(msgIntent); } }
	 */
}
