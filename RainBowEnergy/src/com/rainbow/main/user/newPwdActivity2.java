package com.rainbow.main.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.comutils.main.Function;
import com.comutils.main.SharePreferences;
import com.comutils.net.HttpUtil;
import com.comutils.util.UrlUtil;
import com.rainbow.main.R;
import com.rainbow.main.function.comFunction;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class newPwdActivity2 extends Activity implements OnClickListener {
	
	TextView tv_back,tv_title,tv_next;
	EditText et_new_pwd,et_new_pwd_again;
	NewPwdTask iNewPwdTask;
	String phone,get_code;
	SharePreferences isPreferences;
	LinearLayout ll_bar;
	UrlUtil mUrlUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_pwd);
		
		isPreferences = new SharePreferences(this);
		mUrlUtil = UrlUtil.getInstance();
		initView();
	}
	
	private void initView() {
		findViewById(R.id.tv_back).setOnClickListener(this);
		findViewById(R.id.tv_next).setOnClickListener(this);
		((TextView)findViewById(R.id.tv_next)).setText(getString(R.string.tv_finish2));
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getString(R.string.tv_set_pwd));
		et_new_pwd = (EditText) findViewById(R.id.et_new_pwd);
		et_new_pwd.setHint(getString(R.string.tv_old_pass));
		et_new_pwd_again = (EditText) findViewById(R.id.et_new_pwd_again);
		et_new_pwd_again.setHint(getString(R.string.tv_new_pass));
		ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
	}
	
	
	private boolean FotJudge(){
		if(TextUtils.isEmpty(et_new_pwd.getText().toString().trim())){
			Function.toastMsg(getApplicationContext(), getString(R.string.tv_pwd_no_null));
			return false;
		}
		if(TextUtils.isEmpty(et_new_pwd_again.getText().toString().trim())){
			Function.toastMsg(getApplicationContext(), getString(R.string.tv_reg_pwd_no_null));
			return false;
		}
		if(!Function.isNumAndABC(et_new_pwd_again.getText().toString().trim())){
			Function.toastMsg(getApplicationContext(), getString(R.string.tv_pwd_style_err));
			return false;
		}
		
		return true;
	}
	


//设置 新密码
	private class NewPwdTask extends AsyncTask<String, Void, String>{

		String errorString =null;
		int errorCode ;
		JSONObject jsonj= null;
		List<NameValuePair> paramList = null;

		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ll_bar.setVisibility(View.VISIBLE);
			errorString = null;
			paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
			paramList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
			paramList.add(new BasicNameValuePair("old_pass", et_new_pwd.getText().toString().trim()));
			paramList.add(new BasicNameValuePair("new_pass", et_new_pwd_again.getText().toString().trim()));
		}
		
		
		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.person_set_pwd, paramList);
			if (result.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
			try {
				
				jsonj = new JSONObject(result);
				System.out.println("newpwd data is "+jsonj.toString());
				if(jsonj.getInt("state")==0){
					errorCode = jsonj.getInt("code");
					errorString = "err";
					
					if(jsonj.getInt("code")==101){
						errorString = getString(R.string.err_new_101);
					}
					if(jsonj.getInt("code")==102){
						errorString= getString(R.string.err_new_102);
					}	if(jsonj.getInt("code")==203){
						errorString= getString(R.string.err_203);
					}
					
					return null;
				}
				
			} catch (JSONException e) {
				System.out.println("tag is "+e.getMessage());
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			iNewPwdTask = null;
			ll_bar.setVisibility(View.GONE);
			if(errorString==null){
				Function.toastMsg(getApplicationContext(), getString(R.string.update_pwd_success));
				isPreferences.updateSp("m_pw",  et_new_pwd_again.getText().toString().trim());
				startActivity(new Intent(getApplicationContext(),loginActivity.class));
				finish();
			}else{
				Function.toastMsg(getApplicationContext(), errorString);
				comFunction.outtoLogin(newPwdActivity2.this, errorString, errorCode,isPreferences);
				errorString = null;
			}
			
		
		}
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_back:
			isPreferences.updateSp("iscancel", "1");
			finish();
			break;
		case R.id.tv_next:
			if(FotJudge()){
				if(Function.isWiFi_3G(this)){
					if(iNewPwdTask==null){
						iNewPwdTask = new NewPwdTask();
						iNewPwdTask.execute();
					}
				}else{
					Function.toastMsg(getApplicationContext(), getString(R.string.tv_not_netlink));
				}
				
			}
			break;

		default:
			break;
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

			isPreferences.updateSp("iscancel", "1");
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
}
