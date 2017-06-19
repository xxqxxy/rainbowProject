package com.rainbow.main.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.comutils.main.Function;
import com.comutils.main.SharePreferences;
import com.comutils.net.HttpUtil;
import com.comutils.util.UrlUtil;
import com.rainbow.main.R;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class fotPwdActivity extends Activity implements OnClickListener {

	TextView tv_back,tv_title,tv_get_code,tv_next;
	EditText et_mobile,et_code;
	
	int timeint = 60, runtime = 0,agree=1;
	Timer timer = new Timer();
	
	GetCodeTask iGetCodeTesk;
	String get_Code;
	
	SharePreferences isPreferences;
	UrlUtil mUrlUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fot_pwd);
		isPreferences = new SharePreferences(this);
		mUrlUtil = UrlUtil.getInstance();
		initView();
	}
	
	private void initView() {
		findViewById(R.id.tv_back).setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getString(R.string.tv_back_pwd));
		tv_get_code = (TextView) findViewById(R.id.tv_get_code);
		findViewById(R.id.tv_next).setOnClickListener(this);
		
		findViewById(R.id.tv_get_code).setOnClickListener(this);
		et_mobile = (EditText) findViewById(R.id.et_mobile);
		et_code = (EditText) findViewById(R.id.et_code);
		
		
		if (timer != null){
		      if (task != null){
		    	  task.cancel();  //将原任务从队列中移除
		      }
		      
		     
		      task = new MyTask();  // 新建一个任务      
		      timer.schedule(task, 1000,1000);
		 }
	}
	
	MyTask task;
	class MyTask extends TimerTask{

		@Override
		public void run() {
			if (runtime == 0)
				return;
			if (timeint == 0) {// 秒数 更新完，重新获取验证码
				
				 Message message = new Message();  
		            message.what = 2;  
		            mHandler.sendMessage(message);  
			} else {// 正在获取验证码 秒数 一直递减
				
				  Message message = new Message();  
		            message.what = 1;  
		            mHandler.sendMessage(message);  
				
				
			}
		}
		
	}
	
	private Handler mHandler = new Handler()  
    {  
        public void handleMessage(Message msg)  
        {  
            //更新UI  
            switch (msg.what)  
            {  
                case 1:  
                	timeint--;
                	tv_get_code.setText(timeint + "秒");
    				tv_get_code.setEnabled(false);
                    break;
                case 2:
                	runtime = 0;
    				timeint = 60;
    				tv_get_code.setEnabled(true);
    				tv_get_code.setText(getString(R.string.tv_get_code));
                	break;
            }  
        };  
    };
 // 获取验证码时 手机 是否为空 格式判断
 	public boolean phoneJudge() {
 		if (TextUtils.isEmpty(et_mobile.getText().toString().trim())) {
 			Function.toastMsg(fotPwdActivity.this, getString((R.string.tv_phone_no_null)));
 			tv_get_code.setEnabled(true);
 			return false;
 		}
 		if (!Function.isMobile(et_mobile.getText().toString().trim())) {
 			tv_get_code.setEnabled(true);
 			Function.toastMsg(fotPwdActivity.this, getString((R.string.tv_phone_style_err)));
 			return false;
 		}
 		return true;
 	}
 	
 	
 	private void getCode(){
		if (phoneJudge()) {
			if(Function.isWiFi_3G(this)){
				if(iGetCodeTesk == null){
					iGetCodeTesk = new GetCodeTask();
					iGetCodeTesk.execute();
				}
			}else{
				Function.toastMsg(getApplicationContext(), getString(R.string.tv_not_netlink));
			}
		}
	}
	//获得验证码
	private class GetCodeTask extends AsyncTask<String, Void, String>{
		String errorString=null;
		int errorCode = 0;
		JSONObject jsonj=null;
		List<NameValuePair> paramList = null;
		JSONArray jarray =null;
		//预加载
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			errorString=null;
			paramList= new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("mobile", et_mobile.getText().toString().trim()) );

		}
		//处理过程
		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.fot_send_code, paramList);
			System.out.println("forgetpwd result  is "+ result);
			if (result.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
			try {
				jsonj = new JSONObject(result);
				System.out.println("forgetpwd data is "+jsonj.toString());
				
				//获取验证码失败
				if(jsonj.getInt("state")==0){
					errorCode = jsonj.getInt("code");
					errorString = "err";
					if(jsonj.getInt("code")==101){
						errorString = getString(R.string.err_fot_101);
					}
					if(jsonj.getInt("code")==102){
						errorString = getString(R.string.err_201);
					}
					
					if(jsonj.getInt("code")==300){
						errorString = getString(R.string.err_300);
					}
					
					if(jsonj.getInt("code")==302){
						errorString = getString(R.string.err_302);
					}
					
					return null;
				}
				jarray = new JSONArray(jsonj.getString("data"));
				get_Code =jsonj.getString("send_code").replace("null", "");
				System.out.println("验证码；；；"+get_Code);
				
			} catch (JSONException e) {
				System.out.println("tag is "+e.getMessage());
				e.printStackTrace();
			}
			return null;

		}
		//结果
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			iGetCodeTesk = null;
			//更新获取验证码的控件
			if(errorString==null){
				timeint=60;runtime=1;
				tv_get_code.setEnabled(false);
				tv_get_code.setText(timeint+"秒");
				
			}else{
				//获取验证码失败
				tv_get_code.setEnabled(true);
				Function.toastMsg(getApplicationContext(), errorString);
				errorString=null;
			}
			
		}
	}
	
	private boolean Judge() {
		if (TextUtils.isEmpty(et_mobile.getText().toString().trim())) {
 			Function.toastMsg(fotPwdActivity.this, getString((R.string.tv_phone_no_null)));
 			tv_get_code.setEnabled(true);
 			return false;
 		}
 		if (!Function.isMobile(et_mobile.getText().toString().trim())) {
 			tv_get_code.setEnabled(true);
 			Function.toastMsg(fotPwdActivity.this, getString((R.string.tv_phone_style_err)));
 			return false;
 		}
 		
 		if (TextUtils.isEmpty(et_code.getText().toString().trim())) {
 			Function.toastMsg(fotPwdActivity.this, getString((R.string.tv_code_no_null)));
 			return false;
 		}
 		
 		return true;
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
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_back:
			isPreferences.updateSp("iscancel", "1");
			finish();
			break;
		case R.id.tv_next:
			if(Judge()){
				startActivity( new Intent(this,newPwdActivity.class).putExtra("get_code", et_code.getText().toString().trim())
						.putExtra("phone", et_mobile.getText().toString().trim())
						);
			}
			break;
			
		case R.id.tv_get_code:
			getCode();
			break;
		default:
			break;
		}
	}

}
