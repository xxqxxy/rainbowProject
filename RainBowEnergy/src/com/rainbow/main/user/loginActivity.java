package com.rainbow.main.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.comutils.main.Function;
import com.comutils.main.SharePreferences;
import com.comutils.net.HttpUtil;
import com.comutils.util.UrlUtil;
import com.rainbow.main.MainActivity;
import com.rainbow.main.R;
import com.rainbow.main.welcomeActivity;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class loginActivity extends Activity implements OnClickListener {

	SharePreferences isPreferences;
	EditText et_mobile,et_password;
	TextView tv_fotpwd,tv_login,tv_register;
	LoginTask iLoginTask;
	LinearLayout ll_bar;
	UrlUtil mUrlUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		isPreferences = new SharePreferences(this);
		mUrlUtil  = UrlUtil.getInstance();
		isPreferences.updateSp("wlact_from","main");
		initView();
		if(!Function.isWiFi_3G(this))
			Function.toastMsg(this, getString(R.string.tv_not_netlink));
	}
	
	
	private void initView() {
		
		ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
		et_mobile = (EditText) findViewById(R.id.et_mobile);
		et_password = (EditText) findViewById(R.id.et_password);
		
		tv_fotpwd = (TextView) findViewById(R.id.tv_fot_pwd);
		tv_login = (TextView) findViewById(R.id.tv_login);
		tv_register = (TextView) findViewById(R.id.tv_register);
		
		tv_fotpwd.setOnClickListener(this);
		tv_login.setOnClickListener(this);
		tv_register.setOnClickListener(this);
		
	}
	
	public boolean infoCheck(){
		if(Function.isNullorSpace(et_mobile.getText().toString().trim())){
			Function.toastMsg(this,getString(R.string.tv_mobile_tab2) );
			return false;
		}
		if(!Function.isMobile(et_mobile.getText().toString().trim())){
			Function.toastMsg(this, getString(R.string.err_mobile));
			return false;
		}
		if(Function.isNullorSpace(et_password.getText().toString().trim())){
			Function.toastMsg(this,getString(R.string.tv_password_tab));
			return false;
		}
		
		//密码格式为  数字和字母
		if(!Function.isNumAndABC(et_password.getText().toString().trim())){
			Function.toastMsg(getApplicationContext() , getString(R.string.tv_pwd_style_err));
			return false;
		}
		
		return true;
	}
	private void getLogin() {
		
		if(infoCheck()){
			if(Function.isWiFi_3G(this)){
				if(iLoginTask==null){
					iLoginTask = new LoginTask();
					iLoginTask.execute();
				}
			}else{
				ll_bar.setVisibility(View.GONE);
				Function.toastMsg(getApplicationContext(), getString(R.string.tv_not_netlink));
			}
		}
		
		
		
		
	}
	
	private class LoginTask extends AsyncTask<String, Void, String>{
		String errorString,errorCode="";
		String m_id,m_token,m_pw,ryun_token,m_name,m_logo="",m_rolelevel="";
		JSONObject jsonj= null;
		@SuppressWarnings("deprecation")
		List<NameValuePair> paramList = null;
		
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ll_bar.setVisibility(View.VISIBLE);
			errorString = null;  //不能写为 errorString = "";    不会进入  onPostExeute的errorString==null中    ""  和  null的区别
			paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("mobile",et_mobile.getText().toString().trim()));
			paramList.add(new BasicNameValuePair("password",et_password.getText().toString().trim()));
			paramList.add(new BasicNameValuePair("lat",""));//纬度
			paramList.add(new BasicNameValuePair("lng",""));//经度
			
		}
		
		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.m_login, paramList);
			if (result.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
			Log.i("LoginActivity", "login data is "+result);
		
			try {
				jsonj = new JSONObject(result);
				//登录  判断状态
				if(jsonj.getInt("state")==0){
					errorCode = jsonj.getString("code");
					errorString="err";
					if(jsonj.getInt("code")==101){
						errorString = getString(R.string.err_login_101);
					}
					if(jsonj.getInt("code")==301){
						errorString = getString(R.string.err_reg_301);
					}
					if(jsonj.getInt("code")==300){
						errorString = getString(R.string.err_300);
					}if(jsonj.getInt("code")==204){
						errorString = getString(R.string.err_login_204);
					}
					if(jsonj.getInt("code")==201){
						errorString = getString(R.string.err_login_201);
					}
					return null;
				}
				
				jsonj = new JSONObject(jsonj.getString("data"));
				m_id = jsonj.getString("m_id").replace("null", "");
				m_token = jsonj.getString("m_token").replace("null", "");
				m_name = jsonj.getString("m_name").replace("null", "");
				m_logo = jsonj.getString("mlogo").replace("null", "");
				m_rolelevel = jsonj.getString("m_rolelevel").replace("null", "");
				return null;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			iLoginTask= null;
			//关闭
			ll_bar.setVisibility(View.GONE);
			if(errorString==null){
				if(m_token!=null){
					
					isPreferences.updateSp("m_name", m_name);
					isPreferences.updateSp("m_token", m_token);
					isPreferences.updateSp("m_id", m_id);
					System.out.println("m_id  "+m_id);
					isPreferences.updateSp("m_rolelevel", m_rolelevel);
					isPreferences.updateSp("switch", true);//外网连接  false 内网连接
					isPreferences.updateSp("m_pw", et_password.getText().toString().trim());
					MobclickAgent.onProfileSignIn(m_id);
					//调用JPush API设置Alias
					mmHandler.sendMessage(mmHandler.obtainMessage(MSG_SET_ALIAS, isPreferences.getSp().getString("m_id", "")));
					
					//跳转到 首页
					Function.toastMsg(getApplicationContext() , getString(R.string.tv_login_scuesss));
					finish();
					startActivity(new Intent(loginActivity.this,MainActivity.class));
				}
			}else{
				Function.toastMsg(getApplicationContext() , errorString);
				errorString=null;
			}
				
				
		}
		
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
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_fot_pwd:
			startActivity(new Intent(loginActivity.this,fotPwdActivity.class)
					.putExtra("act_from", "login"));		
			break;
		case R.id.tv_login:
			getLogin();
			break;
		case R.id.tv_register:
			startActivity(new Intent(loginActivity.this,registerActivity.class));	
			break;
		default:
			break;
		}
	}
	
	private long exitTime = 0;
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
