package com.rainbow.main;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.comutils.main.Function;
import com.comutils.main.SharePreferences;
import com.comutils.net.HttpUtil;
import com.comutils.rain_view.RoundImageViewByXfermode;
import com.comutils.util.UrlUtil;
import com.rainbow.main.function.comFunction;
import com.rainbow.main.service.UpdateService;
import com.rainbow.main.user.newPwdActivity2;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class PersonalActivity extends Activity implements OnClickListener {

	SharePreferences isPreferences;
	ImageView iv_alarm,iv_msg;
	RoundImageViewByXfermode iv_person;
	TextView tv_user_name,tv_user_type,tv_msg_cnt;
	RelativeLayout rl_que_back,rl_update_pwd,rl_version,rl_code_dl,rl_exit;
	VersionTask iVersionTask;
	ExitTask iExitTask;
	ProfileTask iProfileTask;
	ProfileTask2 iProfileTask2;
	LinearLayout ll_bar;
	UrlUtil  mUrlUtil = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal);
		
		mUrlUtil =  UrlUtil.getInstance();
		initView();
		initListener();
		getVis();
		getProfile();
		IntentFilter filter = new IntentFilter("com.rainbow.main.PersonalActivity");
	 	registerReceiver(receiver, filter);
		
	}
	
	
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			
			if(isPreferences.getSp().getBoolean("isnews", false)){
				tv_msg_cnt.setVisibility(View.VISIBLE);
			}else{
				tv_msg_cnt.setVisibility(View.GONE);
			}
		}
	};
	private void initView(){
		isPreferences = new SharePreferences(this);
		isPreferences.updateSp("update_from", "PersonalActivity");
		iv_alarm = (ImageView) findViewById(R.id.iv_alarm);
		iv_msg = (ImageView) findViewById(R.id.iv_msg);
		iv_person = (RoundImageViewByXfermode) findViewById(R.id.iv_person);
		 ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
		 tv_msg_cnt = (TextView) findViewById(R.id.tv_msg_cnt);

		if(isPreferences.getSp().getString("m_logo", "").equals("")){
			iv_person.setImageDrawable(getResources().getDrawable(R.drawable.icon_df_head));
		}
		
		tv_user_name = (TextView) findViewById(R.id.tv_user_name);
		tv_user_type = (TextView) findViewById(R.id.tv_user_type);
		rl_que_back = (RelativeLayout) findViewById(R.id.rl_que_back);
		rl_update_pwd = (RelativeLayout) findViewById(R.id.rl_update_pwd);
		rl_version = (RelativeLayout) findViewById(R.id.rl_version_update);
		rl_code_dl = (RelativeLayout) findViewById(R.id.rl_code_dl);
		rl_exit = (RelativeLayout) findViewById(R.id.rl_exit);
	}
	
	private void initListener(){
		iv_alarm.setOnClickListener(this);
		iv_msg.setOnClickListener(this);
		iv_person.setOnClickListener(this);
		rl_que_back.setOnClickListener(this);
		rl_update_pwd.setOnClickListener(this);
		rl_version.setOnClickListener(this);
		rl_code_dl.setOnClickListener(this);
		rl_exit.setOnClickListener(this);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		 if(isPreferences.getSp().getBoolean("isnews", false)){
				tv_msg_cnt.setVisibility(View.VISIBLE);
			}else{
				tv_msg_cnt.setVisibility(View.GONE);
			}
		 
		getProfile2();
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(updateIntent!=null){
			stopService(updateIntent);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(updateIntent!=null){
			stopService(updateIntent);
		}
		if(receiver!=null){
			unregisterReceiver(receiver);
		}
	}
	private void getProfile2() {
		if(Function.isWiFi_3G(this)){
			if(iProfileTask2== null){
				iProfileTask2 = new ProfileTask2();
				iProfileTask2.execute();
			}
		}else{
			Function.toastMsg(this, getString(R.string.tv_not_netlink));
		}
	}
	
	private void getProfile() {
		if(Function.isWiFi_3G(this)){
			if(iProfileTask== null){
				iProfileTask = new ProfileTask();
				iProfileTask.execute();
			}
		}else{
			ll_bar.setVisibility(View.GONE);
			Function.toastMsg(this, getString(R.string.tv_not_netlink));
		}
	}
	private class ProfileTask extends AsyncTask<String, Void, String>{
		List<NameValuePair> paramsList =null;
		JSONObject jobj = null;
		String m_name,m_mobile,m_cardv,m_stations,m_logo,m_connect,errorString = null;
		
		int errcode=0;
		protected void onPreExecute() {
			ll_bar.setVisibility(View.VISIBLE);
			errorString = null;
			paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("app_mid",isPreferences.getSp().getString("m_id", "")));
			paramsList.add(new BasicNameValuePair("mtoken",isPreferences.getSp().getString("m_token", "")));
		
		}
    	@Override
		protected String doInBackground(String... params){
    		String requery = HttpUtil.queryStringForPost(mUrlUtil.getpersonal,paramsList);
    		Log.i("", "tag 111 ==="+requery);
    		if (requery.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
    		try {
 				jobj = new JSONObject(requery);
 				if(jobj.getInt("state") == 0){
 					errcode = jobj.getInt("code");
 					errorString = "err";
 					if(jobj.getInt("code")== 201)
 					    errorString = getString(R.string.err_201);
					if(errcode == 202)
						errorString= getString(R.string.err_202);
 					if(jobj.getInt("code") == 203)
 						errorString = getString(R.string.err_203);
 					if(jobj.getInt("code") == 204)
						errorString= getString(R.string.err_204);
 					return null;
 				}
 				jobj = new JSONObject(jobj.getString("data"));
 				
 				m_name = jobj.getString("m_name").toString().replace("null", "");
 				m_mobile =jobj.getString("m_mobile").toString().replace("null", "");
 				m_cardv = jobj.getString("m_cardv").toString().replace("null", "");
 				m_stations = jobj.getString("m_stations").toString().replace("null", "");
 				m_logo = jobj.getString("m_logo").toString().replace("null", "");
 				m_connect = jobj.getString("m_connect").toString().replace("null", "");
 				return null;
 			} catch (Exception e) {}finally{}
 			return null;
 			
		}
		@Override
		protected void onPostExecute(String result){
			iProfileTask = null;
			try {
				ll_bar.setVisibility(View.GONE);
				if(errorString == null){
							tv_user_name.setText(m_name);
							if(!m_logo.equals("")){
								Log.i("", "tag sss123="+m_logo);
								int rd = (int)(70*( getResources().getDisplayMetrics().density));
								Function.setCircleMap(((RainBowApplication)PersonalActivity.this.getApplication()),"personal", iv_person, m_logo, rd);
								tv_user_type.setText(m_cardv);
							}
							
							
							isPreferences.updateSp("m_name", m_name);
							isPreferences.updateSp("m_logo", m_logo);
							isPreferences.updateSp("m_cardv", m_cardv);
							isPreferences.updateSp("m_stations", m_stations);
							isPreferences.updateSp("m_connect", m_connect);
							
							isPreferences.updateSp("m_mobile", m_mobile);
		
				}else{
					Function.toastMsg(PersonalActivity.this,errorString);
					comFunction.outtoLogin(PersonalActivity.this, errorString, errcode,isPreferences);
					errorString = null;
				}
			} catch (Exception e) {}
			
	    }
	}
	
	private class ProfileTask2 extends AsyncTask<String, Void, String>{
		List<NameValuePair> paramsList =null;
		JSONObject jobj = null;
		String m_name,m_mobile,m_cardv,m_stations,m_logo,m_connect,errorString = null;
		
		int errcode=0;
		protected void onPreExecute() {
			errorString = null;
			paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("app_mid",isPreferences.getSp().getString("m_id", "")));
			paramsList.add(new BasicNameValuePair("mtoken",isPreferences.getSp().getString("m_token", "")));
		
		}
    	@Override
		protected String doInBackground(String... params){
    		String requery = HttpUtil.queryStringForPost(mUrlUtil.getpersonal,paramsList);
    		Log.i("", "tag 111 ==="+requery);
    		if(requery == "601"){
    			errorString = "601";
    			return null;
    		}
    		try {
 				jobj = new JSONObject(requery);
 				if(jobj.getInt("state") == 0){
 					errcode = jobj.getInt("code");
 					errorString = "err";
 					if(jobj.getInt("code")== 201)
 					    errorString = getString(R.string.err_201);
					if(errcode == 202)
						errorString= getString(R.string.err_202);
 					if(jobj.getInt("code") == 203)
 						errorString = getString(R.string.err_203);
 					if(jobj.getInt("code") == 204)
						errorString= getString(R.string.err_204);
 					return null;
 				}
 				jobj = new JSONObject(jobj.getString("data"));
 				
 				m_name = jobj.getString("m_name").toString().replace("null", "");
 				m_mobile =jobj.getString("m_mobile").toString().replace("null", "");
 				m_cardv = jobj.getString("m_cardv").toString().replace("null", "");
 				m_stations = jobj.getString("m_stations").toString().replace("null", "");
 				m_logo = jobj.getString("m_logo").toString().replace("null", "");
 				m_connect = jobj.getString("m_connect").toString().replace("null", "");
 				return null;
 			} catch (Exception e) {}finally{}
 			return null;
 			
		}
		@Override
		protected void onPostExecute(String result){
			iProfileTask2 = null;
			try {
				if(errorString == null){
							tv_user_name.setText(m_name);
							if(!m_logo.equals("")){
								Log.i("", "tag sss123="+m_logo);
								int rd = (int)(70*( getResources().getDisplayMetrics().density));
								Function.setCircleMap(((RainBowApplication)PersonalActivity.this.getApplication()),"personal", iv_person, m_logo, rd);
								tv_user_type.setText(m_cardv);
							}
							
							
							isPreferences.updateSp("m_name", m_name);
							isPreferences.updateSp("m_logo", m_logo);
							isPreferences.updateSp("m_cardv", m_cardv);
							isPreferences.updateSp("m_stations", m_stations);
							isPreferences.updateSp("m_connect", m_connect);
							
							isPreferences.updateSp("m_mobile", m_mobile);
		
				}else{
					Function.toastMsg(PersonalActivity.this,errorString);
					comFunction.outtoLogin(PersonalActivity.this, errorString, errcode,isPreferences);
					errorString = null;
				}
			} catch (Exception e) {}
			
	    }
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_alarm:
			startActivity(new Intent(this,alarmActivity.class));
			break;
		case R.id.iv_msg:
			startActivity(new Intent(this,msgActivity.class));		
			break;
		case R.id.iv_person:
			startActivity(new Intent(this,personalDesActivity.class));
			break;
		case R.id.rl_que_back:
			startActivity(new Intent(this,queBackActivity.class));
			break;
		case R.id.rl_update_pwd:
			startActivity(new Intent(this,newPwdActivity2.class)
					);
			break;
		case R.id.rl_version_update:
			getupdata();
			break;
		case R.id.rl_code_dl:
			startActivity(new Intent(this,twoCodeActivity.class));
			break;
		case R.id.rl_exit:
			exitShow();
			break;
			

		default:
			break;
		}
	}
	private String versionCode;
	protected Intent updateIntent;
	private void getVis(){
		// 获取packagemanager的实例
				PackageManager packageManager = getPackageManager();
				// getPackageName()是你当前类的包名，0代表是获取版本信息
				PackageInfo packInfo = null;
				try {
					packInfo = packageManager.getPackageInfo(getPackageName(), 0);
				} catch (NameNotFoundException e) {
				}
				versionCode = packInfo.versionCode + "";
				isPreferences.updateSp("app_vname", packInfo.versionName);
				isPreferences.updateSp("app_vcode", packInfo.versionCode);
	}
	//版本更新
	private void getupdata(){
		if(!Function.isWiFi_3G(this)){
			Function.toastMsg(this,getString(R.string.tv_not_netlink));
		}else{
			if(iVersionTask == null){
				iVersionTask = new VersionTask();
				iVersionTask.execute();
			}
		}
	}
	
		private class VersionTask extends AsyncTask<String, Void, String>{
			List<NameValuePair> paramsList;
			JSONObject jobj = null;
			String errorString = null,vsn_name = "",vsn_code ="",vsn_apppath = "";
	    	
			protected void onPreExecute() {
				ll_bar.setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.tv_tishi)).setText("检测版本中...");
				errorString = null;
				paramsList = new ArrayList<NameValuePair>();
				paramsList.add(new BasicNameValuePair("version_code", versionCode));
				paramsList.add(new BasicNameValuePair("version_type","1"));
				
			}
	    	@Override
			protected String doInBackground(String... params){
	    		String requery = HttpUtil.queryStringForPost(mUrlUtil.vsersion,paramsList);
	    		Log.i("", "tag sss="+requery);
	    		if (requery.equals("601")) {
					errorString = getString(R.string.tv_api_abnormal);
					return null;
				}
	 			try {
	 				jobj = new JSONObject(requery);
	 				if(jobj.getInt("state") == 0){
	 					errorString = jobj.getString("msg");
	 					return null;
	 				}
	 					
	 				jobj = new JSONObject(jobj.getString("data"));
	 				vsn_name = jobj.getString("vsn_name").replace("null", "");
	 				vsn_code = jobj.getString("vsn_code").replace("null", "");
	 				vsn_apppath = jobj.getString("vsn_apppath").replace("null", "");
	 				return null;
	 			} catch (Exception e) {}finally{}
	 			return null;
	 			
			}
			@Override
			protected void onPostExecute(String result){
				try {
				iVersionTask = null;
				ll_bar.setVisibility(View.GONE);
				if(errorString == null){
					System.out.println("vsn_name"+vsn_name);
					if(!vsn_name.equals("")){
						Log.i("tag", "tag === app_path; "+vsn_apppath);
						isPreferences.updateSp("vsn_name",vsn_name);
						isPreferences.updateSp("vsn_code",vsn_code);
						isPreferences.updateSp("vsn_apppath",vsn_apppath);
						showNewApp();
					}else{
						Function.toastMsg(PersonalActivity.this,getString(R.string.tv_version_no));
					}
				}else{
					errorString = null;
				}
				} catch (Exception e) {}
		    }
		}
		
		private void showNewApp(){
	    	LayoutInflater mflater=LayoutInflater.from(this);//program_add_success_dialog.
			View mView=mflater.inflate(R.layout.version_dialog,null);
			TextView tv_v_d_title = (TextView)mView.findViewById(R.id.tv_v_d_title);
			TextView tv_v_d_content = (TextView)mView.findViewById(R.id.tv_v_d_content);
			
			tv_v_d_title.setText(getString(R.string.tv_version_updata2)
					.replace("%1$s", isPreferences.getSp().getString("app_vname", "").toString()));
			tv_v_d_content.setText(getString(R.string.tv_version_updata_question)
					.replace("%1$s", isPreferences.getSp().getString("vsn_name", "").toString()));
			TextView tv_cancel = (TextView)mView.findViewById(R.id.tv_cancel);
			TextView tv_yes = (TextView)mView.findViewById(R.id.tv_yes);
			final Dialog mDialog= new Dialog(this,R.style.iDialog2);
			mDialog.setCanceledOnTouchOutside(false);
			int dWidth  = getResources().getDisplayMetrics().widthPixels - (int)
					(50* getResources().getDisplayMetrics().density);
			mDialog.setContentView(mView, new LayoutParams(dWidth, LayoutParams.WRAP_CONTENT)); 
			mDialog.show(); 
			tv_cancel.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mDialog.dismiss();
				}
			});
			tv_yes.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mDialog.dismiss();
					 updateIntent = new Intent(
							PersonalActivity.this,
							UpdateService.class);
					updateIntent.putExtra("titleId",
							R.string.app_name);
					startService(updateIntent);
					
				}
			});
	    }
		
		
		 
	
	//退出登录
	private void exitShow(){
		LayoutInflater mflater=LayoutInflater.from(this);
		View mView=mflater.inflate(R.layout.exit_dialog,null);
		   int dWidth  = getResources().getDisplayMetrics().widthPixels - (int)
	        		(50* getResources().getDisplayMetrics().density);
		final Dialog mDialog= new Dialog(this,R.style.iDialog2);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setContentView(mView, new LayoutParams(dWidth, LayoutParams.WRAP_CONTENT)); 
		((TextView)mView.findViewById(R.id.tv_cancel)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		((TextView)mView.findViewById(R.id.tv_yes)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
				toexitTask();
			}
		});
		mDialog.show();
	};
	
	
	private void toexitTask(){
		if(!Function.isWiFi_3G(this)){
			Function.toastMsg(this,getString(R.string.tv_not_netlink));
		}else{
			if(iExitTask == null){
				iExitTask = new ExitTask();
				iExitTask.execute();
			}
		}
	}
	private class ExitTask extends AsyncTask<String, Void, String>{
		List<NameValuePair> paramsList;
		JSONObject jobj = null;
		String errorString;int errcode = 0;
		protected void onPreExecute() {

			ll_bar.setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.tv_tishi)).setText("账号退出中...");
			errorString = null;
			paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("app_mid",isPreferences.getSp().getString("m_id", "")));
			paramsList.add(new BasicNameValuePair("mtoken",isPreferences.getSp().getString("m_token", "")));
		}
    	@Override
		protected String doInBackground(String... params){
    		String requery = HttpUtil.queryStringForPost(mUrlUtil.loginout,paramsList);
    		if (requery.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
 			try {
 				jobj = new JSONObject(requery);
 				if(jobj.getInt("state") == 0){
 					errcode = jobj.getInt("code");
 					errorString = "err";
 					if(jobj.getInt("code")== 201)
 					    errorString = getString(R.string.err_201);
 					if(jobj.getInt("code")== 202)
 					    errorString = getString(R.string.err_202);
 					if(jobj.getInt("code")== 203)
 					    errorString = getString(R.string.err_203);
 					if(jobj.getInt("code")== 204)
 					    errorString = getString(R.string.err_204);
 					if(jobj.getInt("code")== 301)
 					    errorString = getString(R.string.err_301);
 				}
 				return null;
 			} catch (Exception e) {}finally{}
 			return null;
 			
		}
		@Override
		protected void onPostExecute(String result){
			iExitTask = null;
			try {
				ll_bar.setVisibility(View.GONE);
				if(errorString == null){
					isPreferences.updateSp("m_token","");
					isPreferences.updateSp("m_id","");
					isPreferences.updateSp("m_pw","");
					isPreferences.updateSp("m_logo","");
					JPushInterface.setAliasAndTags(getApplicationContext(), (String) "", null, mAliasCallback);
					isPreferences.updateSp("wlact_from","settings");
					startActivity(new Intent(PersonalActivity.this,welcomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
					
				}else{
					Function.toastMsg(PersonalActivity.this,errorString);
					comFunction.outtoLogin(PersonalActivity.this,errorString,errcode,isPreferences);
					errorString = null;
				}
			} catch (Exception e) {}
			
	    }
	}
	
	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
           // String logs ;
            switch (code) {
            //Set tag and alias success
            case 0:break;
            //Failed to set     
            case 6002:
                //logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                ////Log.i(TAG, logs);
                if (isConnected(getApplicationContext())) {
                	mmHandler.sendMessageDelayed(mmHandler.obtainMessage(1001, alias), 1000 * 60);
                } else {
                	////Log.i(TAG, "No network");
                }
                break;
            }
        }
	};
    private final Handler mmHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case 1001:
               // Log.d(TAG, "Set alias in handler.");
                JPushInterface.setAliasAndTags(getApplicationContext(), (String)"", null, mAliasCallback);
                break;
            }
        }
    };
    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }
	
	private long exitTime = 0;
	//按两次退出程序
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getKeyCode()==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){ 
			if((System.currentTimeMillis()-exitTime) > 2000){ 
				Function.toastMsg(getApplicationContext(),getString(R.string.app_exit)); 
	    		exitTime = System.currentTimeMillis();    
	    	}else{
	    		finish();
			    startActivity(new Intent(this,welcomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	    	}
	    	return true;
	    }
		return super.onKeyDown(keyCode, event);
	}
	

}
