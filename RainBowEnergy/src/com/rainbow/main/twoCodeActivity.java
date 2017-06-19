package com.rainbow.main;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import com.comutils.main.Function;
import com.comutils.main.SharePreferences;
import com.comutils.net.HttpUtil;
import com.comutils.util.UrlUtil;
import com.rainbow.main.function.comFunction;

import android.annotation.SuppressLint;
import android.app.Activity;
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

public class twoCodeActivity extends Activity implements OnClickListener{
	private SharePreferences isPreferences;
	private TextView tv_back,tv_title;
	private WebView mWebView;
	private AboutTask iAboutTask;
	LinearLayout ll_bar;
	UrlUtil mUrlUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.two_code); 
    	isPreferences = new SharePreferences(this);
    	mUrlUtil = UrlUtil.getInstance();
		tv_back = (TextView)findViewById(R.id.tv_back);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_back.setOnClickListener(this);
		ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
		mWebView = (WebView) findViewById(R.id.wv_content);
	    initWeb();
		
		
		getTwoCode();
		
    }
	private void getTwoCode(){
		if(!Function.isWiFi_3G(this)){
			Function.toastMsg(this,getString(R.string.tv_not_netlink));
		}else{
			if(iAboutTask == null){
				iAboutTask = new AboutTask();
				iAboutTask.execute();
			}
		}
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.tv_back:finish(); break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	
	@SuppressLint("NewApi")
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
        ws.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
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
        //WebView硬件加速导致页面渲染闪烁问题解决方法
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }*/
        //mWebView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        mWebView.removeJavascriptInterface("searchBoxJavaBredge_");
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
	public void getAbout(){
		if(!Function.isWiFi_3G(this)){
			Function.toastMsg(this,getString(R.string.tv_not_netlink));
		}else{
			if(iAboutTask == null){
				iAboutTask = new AboutTask();
				iAboutTask.execute();
			}
		}
	}
	private class AboutTask extends AsyncTask<String, Void, String>{
		List<NameValuePair> paramsList;
		JSONObject jobj = null;
		String errorString,content,title;
		int errcode = 0;
		protected void onPreExecute() {
			ll_bar.setVisibility(View.VISIBLE);
			errorString = null;
			paramsList = new ArrayList<NameValuePair>();
		}
    	@Override
		protected String doInBackground(String... params){
    		String requery = HttpUtil.queryStringForPost(mUrlUtil.get_two_code,paramsList);
    		//String requery = httpUtil.queryStringForPost("base_info",paramsList);
    		Log.i("", "tag sss="+requery);
    		if (requery.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
 			try {
 				jobj = new JSONObject(requery);
 				errcode = jobj.getInt("code");
 				//code = jobj.getInt("code");
 				if(jobj.getInt("state") == 0)
 					{
 					if(errcode== 201)
 					    errorString = getString(R.string.err_201);
					if(errcode == 202)
						errorString= getString(R.string.err_202);
 					if(errcode == 203)
 						errorString = getString(R.string.err_203);
 					if(errcode == 204)
						errorString= getString(R.string.err_204);
 					return null;
 					}
// 				jobj = new JSONObject(jobj.getString("data"));
 				jobj = new JSONObject(jobj.getString("data"));
 				
 				 content = jobj.getString("content").toString();
 				 title = jobj.getString("title").toString();
 				
 				return null;
 			} catch (Exception e) {}finally{}
 			
 			return null;
		}
		@Override
		protected void onPostExecute(String result){
			iAboutTask = null;
			try {
				ll_bar.setVisibility(View.GONE);
				if(errorString == null){
				 mWebView.loadDataWithBaseURL(null, "<html><body>"+content+"</body></html>", "text/html", "UTF-8",null);  
				 tv_title.setText(title);
			}else{
				Function.toastMsg(twoCodeActivity.this,errorString);
				comFunction.outtoLogin(twoCodeActivity.this, errorString, errcode,isPreferences);
				errorString = null;
			}
			} catch (Exception e) {}
			
	    }
	}
}
