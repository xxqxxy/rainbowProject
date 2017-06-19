package com.rainbow.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.comutils.main.Function;
import com.comutils.main.SharePreferences;
import com.comutils.net.HttpUtil;
import com.comutils.pulltorefresh.PullToRefreshBase;
import com.comutils.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.comutils.pulltorefresh.PullToRefreshListView;
import com.comutils.util.UrlUtil;
import com.rainbow.main.function.comFunction;
import com.rainbow.main.widget.queListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class queBackActivity extends Activity implements OnClickListener {

	
	TextView tv_back,tv_title,tv_send;
	LinearLayout ll_bar;
	EditText et_content;
	int page = 1;
	PullToRefreshListView mPullListView;
	ListView mListView;
	SimpleAdapter iSimpleAdapter;
	queListAdapter iqueListAdapter;
	List<HashMap<String, Object>> quelists;
	AskListTask iAskListTask;
	SendQueTask iSendQueTask;
	SharePreferences isPreferences = null;
	UrlUtil mUrlUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.que_back);
		initView();
		initData();
	}
	
	
	private void initView() {
		isPreferences = new SharePreferences(this);
		mUrlUtil = UrlUtil.getInstance();
		ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_send = (TextView) findViewById(R.id.tv_send);
		tv_send.setOnClickListener(this);
		et_content = (EditText) findViewById(R.id.et_content);
		tv_back.setOnClickListener(this);
		tv_title.setText(getString(R.string.tv_exchange_of_skills));
		
	}
	private void initData() {
		mPullListView = (PullToRefreshListView) findViewById(R.id.pl_que);
		mPullListView.setPullLoadEnabled(true);  
        // 滚动到底自动加载可用  
		mPullListView.setScrollLoadEnabled(false);
        // 得到实际的ListView  
		mListView = mPullListView.getRefreshableView();  
		mListView.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);
		mListView.setFocusable(false);
		mListView.setVerticalScrollBarEnabled(true);
		mListView.setDividerHeight(0);
		mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.cr_gray1)));
		mListView.setDividerHeight((int)(getResources().getDisplayMetrics().density*5));
		mListView.setDivider(new ColorDrawable(R.color.cr_gray3));
		
		quelists = new ArrayList<HashMap<String,Object>>();
		
		iqueListAdapter = new queListAdapter(this, ((RainBowApplication)this.getApplication()), "queBack", null, quelists, R.layout.qus_item, 
				new String[]{"ask_mlogo","ask_name","qf_context","qf_addtime","ans_num"}, 
				new int[]{R.id.iv_nmap,R.id.tv_name,R.id.tv_content,R.id.tv_time,R.id.tv_num});
		
		mListView.setAdapter(iqueListAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position_2, long id) {
				startActivity(new Intent(queBackActivity.this,queDesActivity.class)
						.putExtra("qf_id", quelists.get(position_2).get("qf_id")+"")
						.putExtra("ask_mlogo", quelists.get(position_2).get("ask_mlogo")+"")
						.putExtra("ask_name", quelists.get(position_2).get("ask_name")+"")
						.putExtra("qf_context", quelists.get(position_2).get("qf_context")+"")
						.putExtra("qf_addtime", quelists.get(position_2).get("qf_addtime")+"")
						.putExtra("ans_num", quelists.get(position_2).get("ans_num")+"")
						
						);
			}
		});
		
		
		
		// 设置下拉刷新的listener  
					mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {  
			             @Override  
			             public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) { 
			            	if(Function.isWiFi_3G(queBackActivity.this)) {
			            		if(iAskListTask == null){
				            		page = 1;
				            		iAskListTask = new AskListTask();
				            		iAskListTask.execute();
				    			}else{
				    				mPullListView.onPullDownRefreshComplete();
				    			}
			            	}else{
			            		Function.toastMsg(queBackActivity.this, getString(R.string.tv_not_netlink));
			            	}
			            	
			             
			             }  
			             @Override  
			             public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) { 
			            	if(Function.isWiFi_3G(queBackActivity.this)){
			            		 if(iAskListTask == null){
					            		page += 1;
					            		iAskListTask = new AskListTask();
					            		iAskListTask.execute();
					    			}else{
					    				mPullListView.onPullDownRefreshComplete();
					    			}
			            	}else{
			            		Function.toastMsg(queBackActivity.this, getString(R.string.tv_not_netlink));
			            	}
			            	
			            	 
			            	 
			             	}
			         }); 
					if(Function.isWiFi_3G(queBackActivity.this))
						mPullListView.doPullRefreshing(true, 100);
					else
						Function.toastMsg(queBackActivity.this, getString(R.string.tv_not_netlink));
				
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(Function.isWiFi_3G(queBackActivity.this))
			mPullListView.doPullRefreshing(true, 100);
		else
			Function.toastMsg(queBackActivity.this, getString(R.string.tv_not_netlink));
	
	}
	
	private class AskListTask extends AsyncTask<String,Void,String>{
		
		List<NameValuePair> paramList = null;
		JSONObject jsonj = null;
		JSONArray jarray = null;
		String errorString = null;
		int errorCode = 0;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			errorString = null;
			paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("page", page+""));
			paramList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
			paramList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
		}
		
		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.quelist, paramList);
			Log.i("", "tag 111 ==== "+result);
			if (result.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
			
			try {
				jsonj = new JSONObject(result);
				if(jsonj.getInt("state") == 0 ){
					errorCode = jsonj.getInt("code");
					errorString = "err";
					if(errorCode== 201)
 					    errorString = getString(R.string.err_201);
					if(errorCode == 202)
						errorString= getString(R.string.err_202);
 					if(errorCode == 203)
 						errorString = getString(R.string.err_203);
 					if(errorCode == 204)
						errorString= getString(R.string.err_204);
					return null;
				}
				
				jarray = jsonj.getJSONArray("data");
				if(page == 1){
					quelists.clear();
				}
				for (int i = 0; i < jarray.length(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("qf_id", jarray.getJSONObject(i).getString("qf_id").toString().replace("null", ""));
					map.put("qf_context", jarray.getJSONObject(i).getString("qf_context").toString().replace("null", ""));
					map.put("qf_addtime", jarray.getJSONObject(i).getString("qf_addtime").toString().replace("null", ""));
					map.put("ask_name", jarray.getJSONObject(i).getString("ask_name").toString().replace("null", ""));
					map.put("ask_mlogo", jarray.getJSONObject(i).getString("ask_mlogo").toString().replace("null", ""));
					map.put("ans_num", jarray.getJSONObject(i).getString("ans_num").toString().replace("null", ""));
					quelists.add(map);
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
			iAskListTask = null;
			if(errorString == null){
				iqueListAdapter.notifyDataSetChanged();
			}else{
				Function.toastMsg(queBackActivity.this, errorString);
				comFunction.outtoLogin(queBackActivity.this, errorString, errorCode,isPreferences);
				errorString = null;
			}
			if(page == 1){
				mPullListView.onPullDownRefreshComplete();
				mPullListView.setLastUpdatedLabel( Function.getDateToString2());
			}else{
				mPullListView.onPullUpRefreshComplete();
			}
		}
	}
	
	
	
	private class SendQueTask extends AsyncTask<String, Void, String>{
		List<NameValuePair> paramList = null;
		JSONObject jsonj;
		String errorString = null;
		int errorCode = 0;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ll_bar.setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.tv_tishi)).setText(getString(R.string.tv_data_submiting));
			errorString = null;
			paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
			paramList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
			paramList.add(new BasicNameValuePair("content",et_content.getText().toString().trim()));
		}
		
		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.send_que, paramList);
			Log.i("", "tag 111 ==== "+result);
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
						errorString = getString(R.string.err_ans_101);
					}
					if(errorCode == 102){
						errorString = getString(R.string.err_ans_102);
					}
					if(errorCode == 301){
						errorString = getString(R.string.err_301);
					}
					if(errorCode == 300){
						errorString = getString(R.string.err_300_2);
					}
					
					if(errorCode== 201)
 					    errorString = getString(R.string.err_201);
					if(errorCode == 202)
						errorString= getString(R.string.err_202);
 					if(errorCode == 203)
 						errorString = getString(R.string.err_203);
 					if(errorCode == 204)
						errorString= getString(R.string.err_204);
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
			iSendQueTask = null;
			ll_bar.setVisibility(View.GONE);
			if(errorString == null){
				et_content.setText("");
				et_content.setHint(getString(R.string.tv_content_tab));
				getAskDes();
				
				
			}else{
				Function.toastMsg(queBackActivity.this, errorString);
				comFunction.outtoLogin(queBackActivity.this, errorString, errorCode,isPreferences);
				errorString = null;
			}
			
		}
	}
	
	
	private void getAskDes() {
		if(Function.isWiFi_3G(this)){
			if(iAskListTask == null){
				page = 1;
				iAskListTask = new AskListTask();
				iAskListTask.execute();
			}
		}else{
			Function.toastMsg(this, getString(R.string.tv_not_netlink));
		}
	}
	private void getSendComment() {
		if(Function.isWiFi_3G(this)){
			if(!Function.isNullorSpace(et_content.getText().toString().trim())){
				if(iSendQueTask == null){
					iSendQueTask = new SendQueTask();
					iSendQueTask.execute();
				}	
			}else{
				Function.toastMsg(queBackActivity.this, getString(R.string.tv_content_not_null));
			}
			
		}else{
			ll_bar.setVisibility(View.GONE);
			Function.toastMsg(this, getString(R.string.tv_not_netlink));
		}
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_back:
			finish();
			break;
		case R.id.tv_send:
			getSendComment();
			break;
		default:
			break;
		}
		
		
	}

}
