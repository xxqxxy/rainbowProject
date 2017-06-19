package com.rainbow.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.comutils.main.Function;
import com.comutils.main.LogoDialog;
import com.comutils.main.SharePreferences;
import com.comutils.net.HttpUtil;
import com.comutils.net.UploadUtil;
import com.comutils.net.UploadUtil.OnUploadProcessListener;
import com.comutils.rain_view.RoundImageViewByXfermode;
import com.comutils.util.UrlUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class personalDesActivity extends Activity implements OnClickListener, OnUploadProcessListener {

	public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 101;
	SharePreferences isPrefences;
	TextView tv_back, tv_title, tv_right;
	EditText et_uname, et_phone;
	TextView tv_site;
	RoundImageViewByXfermode iv_person;
	RelativeLayout rl_uname, rl_phone, rl_site;
	private LogoDialog mLogoDialog;
	private String IMAGE_FILE_NAME = "", mapSavePath;
	PersonEditTask iPersonEditTask;
	LinearLayout ll_bar;
	UrlUtil mUrlUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_des);

		mUrlUtil = UrlUtil.getInstance();
		initView();

	}

	private void initView() {
		ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
		isPrefences = new SharePreferences(this);
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		et_uname = (EditText) findViewById(R.id.et_uname);
		et_phone = (EditText) findViewById(R.id.et_phone);
		tv_site = (TextView) findViewById(R.id.tv_site);
		iv_person = (RoundImageViewByXfermode) findViewById(R.id.iv_person);

		findViewById(R.id.rl_person).setOnClickListener(this);
		;
		et_uname.setText(isPrefences.getSp().getString("m_name", ""));
		tv_site.setText(isPrefences.getSp().getString("m_stations", ""));
		et_phone.setText(isPrefences.getSp().getString("m_connect", ""));

		Log.i("", "tag 1111 === " + isPrefences.getSp().getString("m_logo", ""));
		if (isPrefences.getSp().getString("m_logo", "").equals("")) {
			iv_person.setImageDrawable(getResources().getDrawable(R.drawable.icon_df_head));
		} else {
			float td = getResources().getDisplayMetrics().density;
			final int rd2 = (int) (80 * (td));
			Function.setCircleMap(((RainBowApplication) personalDesActivity.this.getApplication()), "personalDes",
					iv_person, isPrefences.getSp().getString("m_logo", ""), rd2);
		}

		iv_person.setOnClickListener(this);

		tv_right = (TextView) findViewById(R.id.tv_right);
		tv_right.setText(getString(R.string.tv_save));
		tv_right.setOnClickListener(this);

		tv_back.setOnClickListener(this);
		tv_title.setText(getString(R.string.tv_personal_msg));

		rl_uname = (RelativeLayout) findViewById(R.id.rl_uname);
		rl_phone = (RelativeLayout) findViewById(R.id.rl_phone);
		rl_site = (RelativeLayout) findViewById(R.id.rl_site);

		rl_uname.setOnClickListener(this);
		rl_phone.setOnClickListener(this);
		rl_site.setOnClickListener(this);

	};

	
	private void getEditPerson() {
		if (Function.isWiFi_3G(this)) {
			if (iPersonEditTask == null) {
				iPersonEditTask = new PersonEditTask();
				iPersonEditTask.execute();
			}
		} else {
			ll_bar.setVisibility(View.GONE);
			Function.toastMsg(this, getString(R.string.tv_not_netlink));
		}
	}

	private class PersonEditTask extends AsyncTask<String, Void, String> {
		List<NameValuePair> paramList = null;
		JSONObject jsonj = null;
		String errorString = null;
		int errorCode = 0;

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ll_bar.setVisibility(View.VISIBLE);
			errorString = null;
			paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("mtoken", isPrefences.getSp().getString("m_token", "")));
			paramList.add(new BasicNameValuePair("app_mid", isPrefences.getSp().getString("m_id", "")));
			paramList.add(new BasicNameValuePair("new_name", et_uname.getText().toString().trim()));
			// 电话号码联系方式
			paramList.add(new BasicNameValuePair("m_connect", et_phone.getText().toString().trim()));

			
			paramList.add(new BasicNameValuePair("logoname", IMAGE_FILE_NAME));
			Log.i("", "tag 111 ==" + IMAGE_FILE_NAME  +"et_phone ==  "+et_phone.getText().toString().trim());
		}

		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.personal_edit, paramList);
			Log.i("", "tag 111 == "+result);
			if (result.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
			try {
				jsonj = new JSONObject(result);
				if(jsonj.getInt("state") == 0){
					errorCode = jsonj.getInt("code");
					errorString = "err";
					if(errorCode == 101){
						errorString = getString(R.string.err_pe_101);
					}
					if(errorCode == 204){
						errorString= getString(R.string.err_204);
					}
					if(errorCode == 300){
						errorString = getString(R.string.err_300_2);
					}	
					return null;
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
			iPersonEditTask = null;
			ll_bar.setVisibility(View.GONE);
			if (errorString == null) {
				Function.toastMsg(personalDesActivity.this, getString(R.string.tv_edit_scuss));
				finish();
			} else {
				Function.toastMsg(personalDesActivity.this, errorString);
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
		case R.id.tv_right:
			getEditPerson();
			break;

		case R.id.iv_person:

			// android 6.0 获取摄像头权限
			showCamera();
			logoDialogShow();
			break;

		case R.id.rl_person:
			showCamera();
			logoDialogShow();
			break;
		default:
			break;
		}
	}

	public void showCamera() {
		// 检查摄像头权限是否已经有效
		if (ContextCompat.checkSelfPermission(personalDesActivity.this,
				Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			// 申请WRITE_EXTERNAL_STORAGE权限
			ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA },
					WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		// TODO Auto-generated method stub
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			// 授权成功 逻辑
		} else {
			// 没有授权 逻辑
			Function.toastMsg(personalDesActivity.this, "您将无法执行有关摄像头的操作！");
		}

	}

	/** 图片选择对话框 **/
	private void logoDialogShow() {
		mLogoDialog = new LogoDialog(this, getString(R.string.tv_p_logo_tab), new LogoDialog.OnBackClickListener() {
			@Override
			public void onBack(String path) {
				// path 获得的图片地址
				mapSavePath = path;

				int rd = (int) (70 * getResources().getDisplayMetrics().density);
				Bitmap iBitmap = Function.getZFBitmapByPath(path, rd);
				if (iBitmap != null)
					Function.saveBitmap(iBitmap, path);

				toUploadFile(path);

			}
		});
		mLogoDialog.show();
	}

	private void toUploadFile(String mapSavePath) {
		// 接口地址
		UploadUtil uploadUtil = UploadUtil.getInstance();
		;
		uploadUtil.setOnUploadProcessListener(this);
		Map<String, String> params = new HashMap<String, String>();
		params.put("file_param", "mlogo");
		params.put("app_mid", isPrefences.getSp().getString("m_id", ""));
		params.put("mtoken", isPrefences.getSp().getString("m_token", ""));
		// mapSavePath 图片地址
		uploadUtil.uploadFile(mapSavePath, "mlogo", mUrlUtil.upmlogo, params);
		// map path = /sdcard/comutils/cu_tmp_1474717347612.jpg
		System.out.println("map path = " + mapSavePath);
	}

	@Override
	public void onUploadDone(int responseCode, String message) {
		// TODO Auto-generated method stub
		try {
			Message msg = new Message();
			// System.out.println("tag 111 =msg = "+message);
			msg.what = responseCode;
			msg.obj = message;
			mhandler.sendMessage(msg);
		} catch (Exception e) {
		}
	}

	@Override
	public void onUploadProcess(int uploadSize) {
	}

	@Override
	public void initUpload(int fileSize) {
	}

	private Handler mhandler=new Handler(){public void handleMessage(Message msg){try{

	System.out.println("zhixing "+msg.what);if(msg.what==1){JSONObject jobj;jobj=new JSONObject(msg.obj.toString());
	// Log.i("", "tag returnback map==="+jobj);
	if(jobj.getInt("state")==1){IMAGE_FILE_NAME=jobj.getString("data");Function.toastMsg(personalDesActivity.this,getString(R.string.tv_map_upload_success));float td=getResources().getDisplayMetrics().density;final int rd=(int)(80*(td));Function.setCircleMap(((RainBowApplication)personalDesActivity.this.getApplication()),"personalDes",iv_person,mapSavePath,rd);

	isPrefences.updateSp("m_logo",mapSavePath);}else{Function.toastMsg(personalDesActivity.this,jobj.getString("msg"));}}else{Function.toastMsg(personalDesActivity.this,getString(R.string.tv_map_upload_failure));}}catch(JSONException e){}catch(Exception e){

	}}};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 类似iphone的图片选择
		if (mLogoDialog != null)
			mLogoDialog.onActivityResult(requestCode, resultCode, data);

		super.onActivityResult(requestCode, resultCode, data);
	}

}
