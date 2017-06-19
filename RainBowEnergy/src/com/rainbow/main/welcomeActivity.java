package com.rainbow.main;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.comutils.main.Function;
import com.comutils.main.SharePreferences;
import com.comutils.net.HttpUtil;
import com.comutils.util.UrlUtil;
import com.rainbow.main.function.comFunction;
import com.rainbow.main.user.loginActivity;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class welcomeActivity extends Activity {
	private SharePreferences isPreferences;
	private String versionCode;
	private LoginAutoTask iLoginAutoTask = null;
	String token = "";
	Timer timer = new Timer();
	int timeout = 0;

	 LoginOtherTask iLoginOtherTask = null;
	
	 UrlUtil mUrlUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weclome);

		Log.i("tag" ,"tag ==== " +64%60);
		
		
		isPreferences = new SharePreferences(this);
		mUrlUtil = UrlUtil.getInstance();
		isPreferences.updateSp("wlact_from", "");
		isPreferences.updateSp("iswelcome", true);
		isPreferences.updateSp("mtoken", "");
		isPreferences.updateSp("vsn_name", "");
		isPreferences.updateSp("vsn_code", "");
		isPreferences.updateSp("vsn_apppath", "");
		 
		File nomedia = new File("sdcard/ComUtils/cache/" , ".nomedia");
	    try {
	        if (!nomedia.exists())
	        	Log.i("soso", "exception in createNewFile() method");
	        nomedia.createNewFile();
	        FileOutputStream nomediaFos = new FileOutputStream(nomedia);
	        nomediaFos.flush();
	        nomediaFos.close();
	        } catch (IOException e) {
	        Log.e("IOException", "exception in createNewFile() method");
	    }
		timer.schedule(task, 1000);
	}
	TimerTask task = new TimerTask() {

		@Override
		public void run() {
			setData();
		}
	};

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			// String logs ;
			switch (code) {
			case 0:
				break;
			case 6002:
				if (isConnected(getApplicationContext())) {
					mmHandler.sendMessageDelayed(mmHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
				} else {
					//// Log.i(TAG, "No network");
				}
				break;
			default:
			}
		}
	};

	public static boolean isConnected(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conn.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}

	private static final int MSG_SET_ALIAS = 1001;
	private final Handler mmHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SET_ALIAS:
				// Log.d(TAG, "Set alias in handler.");
				JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
				break;
			}
		}
	};

	
	private class LoginAutoTask extends AsyncTask<String, Void, String>{
		List<NameValuePair> paramsList;
		JSONObject jobj = null;
		String errorString,m_token="",m_id="",m_name="",mlogo="",m_rolelevel="0";
		private String m_ispush;
		int errCode;
		@SuppressWarnings("deprecation")
		protected void onPreExecute() {
			errorString = null;
			paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("app_mid",isPreferences.getSp().getString("m_id", "")));
			paramsList.add(new BasicNameValuePair("password",isPreferences.getSp().getString("m_pw", "")));
			paramsList.add(new BasicNameValuePair("lat",""));
			paramsList.add(new BasicNameValuePair("lng",""));
		
		}
    	@Override
		protected String doInBackground(String... params){
    		String requery = HttpUtil.queryStringForPost(mUrlUtil.auto_login,paramsList);
    		Log.i("", "tag sss111111="+requery);
    		if (requery.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
 			try {
 				jobj = new JSONObject(requery);
 				if(jobj.getInt("state") == 0){
 					errorString = "err";
 					errCode = jobj.getInt("code");
 					if(jobj.getInt("code")== 101)
 					    errorString = getString(R.string.err_login_101);
 					if(jobj.getInt("code")== 301)
 					    errorString = getString(R.string.err_301);
 					if(jobj.getInt("code")== 300)
 					    errorString = getString(R.string.err_300);
 					if(errCode== 201)
 					    errorString = getString(R.string.err_201);
					if(errCode == 202)
						errorString= getString(R.string.err_202);
 					if(errCode == 203)
 						errorString = getString(R.string.err_203);
 					if(errCode == 204)
						errorString= getString(R.string.err_204);
 					return null;
 					
 				}
 					
 				jobj = new JSONObject(jobj.getString("data"));
 				m_token = jobj.getString("m_token").replace("null", "");
 				m_name = jobj.getString("m_name").replace("null", "");
 				m_rolelevel = jobj.getString("m_rolelevel").replace("null", "");
 				mlogo = jobj.getString("mlogo").replace("null", "");
 				m_id = jobj.getString("m_id").replace("null", "");
 				
 				return null;
 			} catch (Exception e) {}finally{}
 			return null;
 			
		}
		@Override
		protected void onPostExecute(String result){
			try {
				iLoginAutoTask  = null;
			if(errorString == null){
				
				System.out.println("log_to");
				//					友盟
					MobclickAgent.onProfileSignIn(isPreferences.getSp().getString("m_id", ""));
					isPreferences.updateSp("m_token",m_token);
					isPreferences.updateSp("m_name",m_name);
					isPreferences.updateSp("m_id", m_id);
					isPreferences.updateSp("m_rolelevel", m_rolelevel);
					//调用JPush API设置Alias
					mmHandler.sendMessage(mmHandler.obtainMessage(MSG_SET_ALIAS, isPreferences.getSp().getString("m_id", "")));
					startActivity(new Intent(welcomeActivity.this,MainActivity.class));		
					
			}else{
				Function.toastMsg(welcomeActivity.this, errorString);
				if(isPreferences.getSp().getString("m_id", "").equals("")){
					startActivity(new Intent(welcomeActivity.this,loginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				}
				
				if(errCode == 300){
					startActivity(new Intent(welcomeActivity.this,loginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				}
				comFunction.outtoLogin(welcomeActivity.this, errorString, errCode,isPreferences);
				errorString = null;
				
			}
			} catch (Exception e) {
				Log.i("", "tag sss==repppp=  init"+e.getMessage());
			}
	    }
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		if (isPreferences.getSp().getString("wlact_from", "").equals("main")) {// 退出
			finish();
		}
		if (isPreferences.getSp().getString("wlact_from", "").equals("settings")) {// 退出
			startActivity(new Intent(welcomeActivity.this, loginActivity.class));
		}

		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		isPreferences.updateSp("iswelcome", false);
		System.exit(0);
		super.onDestroy();
	}

	private void setData() {
		if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			new AlertDialog.Builder(this).setMessage(getString(R.string.tip_no_sdcard))
					.setPositiveButton(getString(R.string.tv_yes), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								System.exit(0);
							} catch (Exception e) {
							}
						}
					}).create().show();
		}

		/*Function.mkFilePath(getString(R.string.app_cu_path));*/
		
		
		
	        
		//登录  萤石云
		if(Function.isWiFi_3G(this)){
			if(iLoginOtherTask == null){
//				Looper.prepare();
				iLoginOtherTask = new LoginOtherTask();
				iLoginOtherTask.execute();
//				Looper.loop();
			}
		}else{
			startActivity(new Intent(welcomeActivity.this, loginActivity.class));
		}

	}

	
	/**
	 * denglu  yinshiyun
	 * @author Administrator
	 *
	 */
	private  class LoginOtherTask extends AsyncTask<String, Void, String>{

		List<NameValuePair> paramList = null;
		JSONObject jsonj;
		String errorString = "";
		int errCode = 0;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			errorString = null;
			paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("appKey", RainBowApplication.APP_KEY));
			paramList.add(new BasicNameValuePair("appSecret", RainBowApplication.APP_SECRET));
	
		}
		
		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.YSY_API_URL+"token/get", paramList);
			
			Log.i("ceshicaihong", "result = "+result);
			if (result.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
			try {
				jsonj = new JSONObject(result);
				if( jsonj.getInt("code") == 200){
					jsonj = new JSONObject(jsonj.getString("data"));
					
					System.out.println("jsonj == "+jsonj);
					token = jsonj.getString("accessToken").toString();
					System.out.println("token == "+jsonj.getString("accessToken").toString());;
				}else{
					errCode = jsonj.getInt("code");
					errorString = errCode +"";
					}
				
				return null;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
/**
 * 
 * 如果不调用我们的openLoginPage ，那就按照上述流程图，获取到accessToken之后通过 setAccessToken 方法对SDK进行accessToken设置,
 * 退出登录之后需要调用setAccessToken 将sdk中的accessToken置为null。
 */			
			RainBowApplication.getOpenSDK().setAccessToken(token);
			getVsion();

		}
		
	}
	
	
	
	
	
	
	
	
	
	
	// 获取当前版本
	private void getVsion() {
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
		}
		//// Log.i("", "tag
		//// mlml="+packInfo.versionName+"=="+packInfo.versionCode);
		// versionName = packInfo.versionName;
		// versionCode = packInfo.versionCode+"";
		versionCode = packInfo.versionCode + "";
		isPreferences.updateSp("app_vname", packInfo.versionName);
		isPreferences.updateSp("app_vcode", packInfo.versionCode);
		new VersionTask().execute();
	}

	private class VersionTask extends AsyncTask<String, Void, String> {
		// final ProgressDialog pd = new ProgressDialog(welcomeActivity.this);
		List<NameValuePair> paramsList;
		JSONObject jobj = null;
		String errorString, vsn_name, vsn_code, vsn_apppath;

		protected void onPreExecute() {
			errorString = null;
			paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("version_type","1"));
			paramsList.add(new BasicNameValuePair("version_code", versionCode));
			paramsList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
			paramsList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
		}

		@Override
		protected String doInBackground(String... params) {
			String requery = HttpUtil.queryStringForPost(mUrlUtil.vsersion, paramsList);
			Log.i("", "tag 111 ==== "+requery);
			if (requery == "601") {
				errorString = "601";
				return null;
			}
			try {
				jobj = new JSONObject(requery);
				if (jobj.getInt("state") == 0) {
//					errorString = jobj.getString("msg");
					return null;
				}

				jobj = new JSONObject(jobj.getString("data"));
				vsn_name = jobj.getString("vsn_name").replace("null", "");
				vsn_code = jobj.getString("vsn_code").replace("null", "");
				vsn_apppath = jobj.getString("vsn_apppath").replace("null", "");
				return null;
			} catch (Exception e) {
			} finally {
			}
			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			try {
				// if(pd.isShowing())pd.dismiss();
				if (errorString == null) {
					if (!vsn_name.equals("")) {
						// Log.i("", "tag sss vsn=2"+vsn_name);
						isPreferences.updateSp("vsn_name", vsn_name);
						isPreferences.updateSp("vsn_code", vsn_code);
						isPreferences.updateSp("vsn_apppath", vsn_apppath);
					}
				} else {
					errorString = null;
				}
			} catch (Exception e) {
			}
				if (!isPreferences.getSp().getString("m_pw", "").equals("")) {// 没有点击退出账号按钮
					if(Function.isWiFi_3G(welcomeActivity.this)){
						if(iLoginAutoTask == null){
							iLoginAutoTask = new LoginAutoTask();
							iLoginAutoTask.execute();
						}
					}else{
						Function.toastMsg(welcomeActivity.this, getString(R.string.tv_not_netlink));
					}
					
				} else {
					startActivity(new Intent(welcomeActivity.this, loginActivity.class));
				}
			}
	}

}
