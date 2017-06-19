package com.rainbow.main.user;

import java.util.ArrayList;
import java.util.List;
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
import com.rainbow.main.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class registerActivity extends Activity implements OnClickListener {

	TextView tv_back, tv_title, tv_get_code, tv_submit;
	EditText et_mobile, et_code, et_password;

	int timeint = 60, runtime = 0, agree = 1;
	Timer timer = new Timer();

	SharePreferences isPreferences;
	private String get_Code = "";
	SendCodeTask iSendCodeTask;
	RegisterTask iRegisterTask;
	LinearLayout ll_bar;
	UrlUtil mUrlUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		isPreferences = new SharePreferences(this);
		mUrlUtil = UrlUtil.getInstance();
		initView();
		if (timer != null) {
			if (task != null) {
				task.cancel(); // 将原任务从队列中移除
			}
			task = new MyTask(); // 新建一个任务
			timer.schedule(task, 1000, 1000);
		}
	}

	MyTask task;

	class MyTask extends TimerTask {

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

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			// 更新UI
			switch (msg.what) {
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

	private void initView() {
		findViewById(R.id.tv_back).setOnClickListener(this);
		ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getString(R.string.tv_register));
		tv_get_code = (TextView) findViewById(R.id.tv_get_code);
		tv_get_code.setOnClickListener(this);
		tv_submit = (TextView) findViewById(R.id.tv_submit);
		tv_submit.setOnClickListener(this);
		et_mobile = (EditText) findViewById(R.id.et_mobile);
		et_code = (EditText) findViewById(R.id.et_code);
		et_password = (EditText) findViewById(R.id.et_password);

	}

	// 获取验证码时 手机 是否为空 格式判断
	public boolean phoneJudge() {
		if (TextUtils.isEmpty(et_mobile.getText().toString().trim())) {
			Function.toastMsg(registerActivity.this, getString((R.string.tv_phone_no_null)));
			tv_get_code.setEnabled(true);
			return false;
		}
		if (!Function.isMobile(et_mobile.getText().toString().trim())) {
			tv_get_code.setEnabled(true);
			Function.toastMsg(registerActivity.this, getString((R.string.tv_phone_style_err)));
			return false;
		}
		return true;
	}

	private void getCode() {
		// 获取注册的验证码
		if (phoneJudge()) {
			if (Function.isWiFi_3G(this)) {
				if (iSendCodeTask == null) {
					iSendCodeTask = new SendCodeTask();
					iSendCodeTask.execute();
				}
			} else {
				Function.toastMsg(this, getString(R.string.tv_not_netlink));
			}
		}
	}

	private class SendCodeTask extends AsyncTask<String, Void, String> {
		String errorString = null;
		int errorCode = 0;
		JSONObject jsonj = null;
		@SuppressWarnings("deprecation")
		List<NameValuePair> paramList = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			errorString = null;
			paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("mobile", et_mobile.getText().toString().trim()));

		}

		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.register_send_code, paramList);
			Log.e("RegisterActivity", "RegisterActivity  " + result);
			if (result.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
			try {
				jsonj = new JSONObject(result);
				Log.e("RegisterActivity", jsonj.toString());
				// 获取验证码失败
				if (jsonj.getInt("state") == 0) {
					errorCode = jsonj.getInt("code");
					errorString = "err";
					// code === 302
					if (jsonj.getInt("code") == 101) {
						errorString = getString(R.string.err_101);
					} else if (jsonj.getInt("code") == 102) {
						errorString = getString(R.string.err_102);
					} else if (jsonj.getInt("code") == 300) {
						errorString = getString(R.string.err_300);
					} else if (jsonj.getInt("code") == 302) {
						errorString = getString(R.string.err_302);
					}
					return null;
				}
				jsonj = new JSONObject(jsonj.getString("data"));

			} catch (JSONException e) {

				e.printStackTrace();
			}
			return null;
		}

		// 结果
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			iSendCodeTask = null;
			// 更新获取验证码的控件
			if (errorString == null) {
				timeint = 60;
				runtime = 1;
				tv_get_code.setEnabled(false);
				tv_get_code.setText(timeint + "秒");

			} else {
				// 获取验证码失败
				tv_get_code.setEnabled(true);
				Function.toastMsg(registerActivity.this, errorString);
				errorString = null;
			}

		}
	}

	public boolean RegisterJudge() {
		// 手机号码不为空 手机号码格式
		if (TextUtils.isEmpty(et_mobile.getText().toString().trim())) {
			Function.toastMsg(getApplicationContext(), getString(R.string.tv_phone_no_null));
			tv_get_code.setEnabled(true);
			return false;
		}
		if (!Function.isMobile(et_mobile.getText().toString().trim())) {
			tv_get_code.setEnabled(true);
			Function.toastMsg(getApplicationContext(), getString(R.string.tv_phone_style_err));
			return false;
		}

		// 验证码不能为空
		if (TextUtils.isEmpty(et_code.getText().toString().trim())) {
			Function.toastMsg(getApplicationContext(), getString(R.string.tv_reg_code_no_null));
			return false;
		}

		// 密码不能为空
		if (TextUtils.isEmpty(et_password.getText().toString().trim())) {
			Function.toastMsg(getApplicationContext(), getString(R.string.tv_reg_pwd_no_null));
			return false;
		}

		// 密码格式为 数字和字母
		if (!Function.isNumAndABC(et_password.getText().toString().trim())) {
			Function.toastMsg(getApplicationContext(), getString(R.string.tv_pwd_style_err));
			return false;
		}

		return true;
	}

	private void getReg() {
		if (RegisterJudge()) {
			if (Function.isWiFi_3G(this)) {
				if (iRegisterTask == null) {
					iRegisterTask = new RegisterTask();
					iRegisterTask.execute();
				}
			} else {
				Function.toastMsg(this, getString(R.string.tv_not_netlink));
			}
		}
	}

	private class RegisterTask extends AsyncTask<String, Void, String> {
		String errorString = null;
		int errorCode;
		JSONObject jsonj = null;
		String m_token = "", m_id = "", m_pw = "", m_name = "", m_logo = "", m_ispush = "", m_rolelevel = "";

		@SuppressWarnings("deprecation")
		List<NameValuePair> paramsList = null;

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			ll_bar.setVisibility(View.VISIBLE);
			errorString = null;
			paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("mobile", et_mobile.getText().toString().trim()));
			paramsList.add(new BasicNameValuePair("password", et_password.getText().toString().trim()));
			paramsList.add(new BasicNameValuePair("send_code", et_code.getText().toString().trim()));
		}

		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.m_register, paramsList);
			Log.i("RegisterActivity", "register data is " + result);
			if (result == "601") {
				errorString = "601";
				return null;
			}

			try {

				jsonj = new JSONObject(result);
				// 注册成功与否
				if (jsonj.getInt("state") == 0) {
					errorCode = jsonj.getInt("code");
					errorString = "err";
					if (jsonj.getInt("code") == 101) {
						errorString = getString(R.string.err_reg_101);
					}
					if (jsonj.getInt("code") == 102) {
						errorString = getString(R.string.err_reg_102);
					}
					if (jsonj.getInt("code") == 103) {
						errorString = getString(R.string.err_reg_103);
					}
					if (jsonj.getInt("code") == 104) {
						errorString = getString(R.string.err_reg_104);
					}
					if (jsonj.getInt("code") == 105) {
						errorString = getString(R.string.err_reg_105);
					}
					if (jsonj.getInt("code") == 301) {
						errorString = getString(R.string.err_reg_301);
					}
					if (jsonj.getInt("code") == 300) {
						errorString = getString(R.string.err_300);
					}
					return null;
				}

				// 解析data
				jsonj = new JSONObject(jsonj.getString("data"));
				// 得到 m_token m_id ryun_token
				m_logo = jsonj.getString("mlogo").replace("null", "");
				m_token = jsonj.getString("m_token").replace("null", "");
				m_id = jsonj.getString("m_id").replace("null", "");
				m_rolelevel = jsonj.getString("m_rolelevel").replace("null", "");
				m_name = jsonj.getString("m_name").replace("null", "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			iRegisterTask = null;
			ll_bar.setVisibility(View.GONE);
			if (errorString == null) {
				// 保存到 sharedPreferences中
				isPreferences.updateSp("m_token", m_token);
				isPreferences.updateSp("m_id", m_id);
				isPreferences.updateSp("m_name", m_name);
				isPreferences.updateSp("m_rolelevel", m_rolelevel);
				isPreferences.updateSp("m_pw", et_password.getText().toString().trim());
				MobclickAgent.onProfileSignIn(m_id);
				Function.toastMsg(getApplicationContext(), getString(R.string.tv_register_scuess));

				finish();

			} else {
				Function.toastMsg(getApplicationContext(), errorString);
				errorString = null;
			}

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_back:
			finish();
			break;
		case R.id.tv_submit:
			getReg();
			break;
		case R.id.tv_get_code:
			getCode();
			break;
		default:
			break;
		}
	}

}
