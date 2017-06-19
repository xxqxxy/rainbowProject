package com.rainbow.main;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.comutils.main.Function;
import com.comutils.main.SharePreferences;
import com.comutils.net.HttpUtil;
import com.comutils.util.UrlUtil;
import com.rainbow.main.function.comFunction;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

public class msgDesActivity extends Activity implements OnClickListener{
	private TextView tv_back,tv_title,tv_next;
	private SharePreferences isPreferences;
	private WebView mWebView;
	private String n_title,n_id;
	private MsgTask iMsgTask;
	LinearLayout ll_bar;
	UrlUtil mUrlUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msgdes); 
		isPreferences = new SharePreferences(this);
		mUrlUtil = UrlUtil.getInstance();
		tv_back = (TextView)findViewById(R.id.tv_back);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_back.setOnClickListener(this);
		tv_title.setText(getString(R.string.tv_msg_des));
		
		Intent i = getIntent();
        Bundle b = i.getExtras();
        n_id = b.getString("n_id");
        n_title = b.getString("n_title");
        
		mWebView = (WebView) findViewById(R.id.web_context);
		ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
		initWeb();
	    
	    //读取消息
	  	getMessage();
	}
	

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	
	private void initWeb() {
		WebSettings ws = mWebView.getSettings();
        ws.setBlockNetworkImage(false);
        ws.setJavaScriptEnabled(true); // 设置支持javascript脚本
        ws.setAllowFileAccess(true); // 允许访问文件
        ws.setBuiltInZoomControls(false); // 设置显示缩放按钮
        ws.setSupportZoom(false); //支持缩放
        //* 1、LayoutAlgorithm.NARROW_COLUMNS ： 适应内容大小
        //* 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
        //ws.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        ws.setDefaultTextEncodingName("utf-8"); //设置文本编码
        ws.setAppCacheEnabled(false);
        //ws.setCacheMode(WebSettings.LOAD_DEFAULT);//设置缓存模式
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //加快HTML网页加载完成速度 网页 加载完了再加载图片
        if(Build.VERSION.SDK_INT >= 19) {
        	ws.setLoadsImagesAutomatically(true);
        } else {
        	ws.setLoadsImagesAutomatically(false);
        }
        mWebView.setAnimationCacheEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebViewClient(new WebViewClientDemo());
        mWebView.setWebChromeClient(new WebViewChromeClientDemo());
	}
	private class WebViewClientDemo extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			
			return true;
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			if(!mWebView.getSettings().getLoadsImagesAutomatically()) {
				mWebView.getSettings().setLoadsImagesAutomatically(true);
		    }
			super.onPageFinished(view, url);
		}
	}

	private class WebViewChromeClientDemo extends WebChromeClient {
		public void onProgressChanged(WebView view, int newProgress) {}
		public void onReceivedTitle(WebView view, String title) {}
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			return super.onJsAlert(view, url, message, result);
		}
		@Override
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue, JsPromptResult result) {
			return super.onJsPrompt(view, url, message, defaultValue, result);
		}
		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				JsResult result) {
			return super.onJsConfirm(view, url, message, result);
		}
	}
	public void getMessage(){
		if(!Function.isWiFi_3G(this)){
			ll_bar.setVisibility(View.GONE);
			Function.toastMsg(this,getString(R.string.tv_not_netlink));
		}else{
			if(iMsgTask == null){
				iMsgTask = new MsgTask();
				iMsgTask.execute();
			}
		}
	}
	String n_title2;
	String share_url;
	private class MsgTask extends AsyncTask<String, Void, String>{
		@SuppressWarnings("deprecation")
		List<NameValuePair> paramsList;
		JSONObject jobj = null;
		String errorString,n_content,n_addtime;
		int code = 0;
		@SuppressWarnings("deprecation")
		protected void onPreExecute() {
			
			ll_bar.setVisibility(View.VISIBLE);
			errorString = null;
			paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("app_mid",isPreferences.getSp().getString("m_id", "")));
			paramsList.add(new BasicNameValuePair("mtoken",isPreferences.getSp().getString("m_token", "")));
			paramsList.add(new BasicNameValuePair("n_id",n_id));
		}
    	@Override
		protected String doInBackground(String... params){
    		String requery = HttpUtil.queryStringForPost(mUrlUtil.system_ninfo,paramsList);
    		Log.i("", "tag sss ffff ddd="+requery);
    		if (requery.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
 			try {
 				jobj = new JSONObject(requery);
 				code = jobj.getInt("code");
 				if(jobj.getInt("state") == 0)
 				{
 					if(code== 201)
 					    errorString = getString(R.string.err_201);
					if(code == 202)
						errorString= getString(R.string.err_202);
 					if(code == 203)
 						errorString = getString(R.string.err_203);
 					if(code == 204)
						errorString= getString(R.string.err_204);
 					return null;
 				}
 				
 				n_content = jobj.getString("data").toString().replace("&lt;", "<").replace("&gt;", ">");
 				
 				return null;
 			} catch (Exception e) {}finally{}
 			
 			return null;
		}
		@Override
		protected void onPostExecute(String result){
			iMsgTask = null;
			try {

				ll_bar.setVisibility(View.GONE);
				if(errorString == null){
				
				if(n_content != null)
				   mWebView.loadDataWithBaseURL(null, "<html><body>"+n_content+"</body></html>", "text/html", "UTF-8",null);  
			}else{
				Function.toastMsg(msgDesActivity.this,errorString );
				comFunction.outtoLogin(msgDesActivity.this, errorString, code,isPreferences);
				errorString = null;
			}
			} catch (Exception e) {}
			
	    }
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() ==  R.id.tv_back)
			finish();
	}

}
