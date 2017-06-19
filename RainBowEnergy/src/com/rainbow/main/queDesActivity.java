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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class queDesActivity extends Activity implements OnClickListener {

	TextView tv_back,tv_title,tv_num,tv_num2,tv_name,tv_time,tv_content;
	ImageView iv_map;
	PullToRefreshListView lv_que;
	ListView mListView;
	LinearLayout include_top2,ll_bar;
	List<HashMap<String, Object>> deslists;
	queListAdapter iSimpleAdapter = null;
	String qf_id,qf_context,qf_addtime,ask_name,ask_mlogo,ans_num;
//	ScrollView sc_que;
	int page =1 ;
	AskDesTask iAskDesTask;
	
	EditText et_content;
	TextView tv_send;
	SharePreferences isPreferences;
	SendCommentTask iSendCommentTask;
	UrlUtil mUrlUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.que_des);
		isPreferences = new SharePreferences(this);
		mUrlUtil =UrlUtil.getInstance();
		Intent i = getIntent();
		Bundle b = i.getExtras();
		qf_id = b.getString("qf_id");
		qf_context = b.getString("qf_context");
		qf_addtime = b.getString("qf_addtime");
		ask_name = b.getString("ask_name");
		ask_mlogo = b.getString("ask_mlogo");
		ans_num = b.getString("ans_num");
		initView();
//		initData();
		include_top2 =(LinearLayout) findViewById(R.id.include_top2);
		
		tv_name.setText(ask_name);
		tv_time.setText(qf_addtime);
		tv_content.setText(qf_context);
		tv_num.setText("已解答"+ans_num);
		tv_num2.setText("全部解答"+ans_num);
		if(ask_mlogo.equals("")){
			iv_map.setImageDrawable(getResources().getDrawable(R.drawable.icon_df_head));
		}else{
			float td =  getResources().getDisplayMetrics().density;  
			final int rd2 = (int)(50*(td));
			Function.setCircleMap(((RainBowApplication)this.getApplication()),"personalDes", iv_map, 
					ask_mlogo, rd2);
		}
	}
	private void initView() {
		ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_num = (TextView) findViewById(R.id.tv_num);
	
		iv_map = (ImageView) findViewById(R.id.iv_nmap);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_num2 = (TextView) findViewById(R.id.tv_num2);

		tv_back.setOnClickListener(this);
		tv_title.setText(getString(R.string.tv_exchange_detail));
		
		et_content = (EditText) findViewById(R.id.et_content);
		tv_send = (TextView) findViewById(R.id.tv_send);
		tv_send.setOnClickListener(this);
		
		
		lv_que = (PullToRefreshListView) findViewById(R.id.lv_que);
		lv_que.setPullLoadEnabled(true);  
        // 滚动到底自动加载可用  
		lv_que.setScrollLoadEnabled(false);
        // 得到实际的ListView  
		mListView = lv_que.getRefreshableView();  
		mListView.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);
		mListView.setFocusable(false);
		mListView.setVerticalScrollBarEnabled(true);
		mListView.setDividerHeight(0);
		mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.cr_gray1)));
		mListView.setDividerHeight((int)(getResources().getDisplayMetrics().density*5));
		mListView.setDivider(new ColorDrawable(R.color.cr_gray3));

		deslists = new ArrayList<HashMap<String,Object>>();
		iSimpleAdapter = new queListAdapter(this, ((RainBowApplication)this.getApplication()),"queDes", null, deslists, R.layout.que_item
				, new String[]{"ans_mlogo","ans_name","qa_addtime","qa_context"},
				new int[]{R.id.iv_nmap,R.id.tv_name,R.id.tv_time,R.id.tv_content});
		mListView.setAdapter(iSimpleAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position_2, long id) {
			}
		});
		
		// 设置下拉刷新的listener  
					lv_que.setOnRefreshListener(new OnRefreshListener<ListView>() {  
			             @Override  
			             public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) { 
			            	 if(Function.isWiFi_3G(queDesActivity.this)){ 
			            	 if(iAskDesTask == null){
				            		page = 1;
				            		iAskDesTask = new AskDesTask();
				            		iAskDesTask.execute();
				    			}else{
				    				lv_que.onPullDownRefreshComplete();
				    			}
			            	 }else{
			            		 Function.toastMsg(queDesActivity.this, getString(R.string.tv_not_netlink));
			            	 }
			             }  
			             @Override  
			             public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) { 
			            	 if(Function.isWiFi_3G(queDesActivity.this)){
			            	 if(iAskDesTask == null){
				            		page += 1;
				            		iAskDesTask = new AskDesTask();
				            		iAskDesTask.execute();
				    			}else{
				    				lv_que.onPullDownRefreshComplete();
				    			}
			            	 }else{
			            		 Function.toastMsg(queDesActivity.this, getString(R.string.tv_not_netlink));
			            	 }
			             }
			         }); 
					 if(Function.isWiFi_3G(queDesActivity.this))
						 lv_que.doPullRefreshing(true, 100);
					 else
						 Function.toastMsg(queDesActivity.this, getString(R.string.tv_not_netlink)); 
				
	}
	
	private void getSendComment() {
		if(Function.isWiFi_3G(this)){
			if(!Function.isNullorSpace(et_content.getText().toString().trim())){
				if(iSendCommentTask == null){
					iSendCommentTask = new SendCommentTask();
					iSendCommentTask.execute();
				}
			}else{
				Function.toastMsg(queDesActivity.this, getString(R.string.tv_content_not_null));
			}
			
		}else{
			ll_bar.setVisibility(View.GONE);
			Function.toastMsg(this, getString(R.string.tv_not_netlink));
		}
	}
	
	
	
	private class SendCommentTask extends AsyncTask<String, Void, String>{
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
			paramList.add(new BasicNameValuePair("qf_id", qf_id));
			paramList.add(new BasicNameValuePair("content",et_content.getText().toString().trim()));
		}
		
		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.send_ans, paramList);
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
			iSendCommentTask = null;
			ll_bar.setVisibility(View.GONE);
			if(errorString == null){
				lv_que.doPullRefreshing(true, 100);
				 ans_num =String.valueOf(Integer.parseInt(ans_num) +1) ;
				et_content.setText("");
				et_content.setHint(getString(R.string.tv_content_tab));
				isPreferences.updateSp("reflsh", "1");
				
			}else{
				Function.toastMsg(queDesActivity.this, errorString);
				comFunction.outtoLogin(queDesActivity.this, errorString, errorCode,isPreferences);
				errorString = null;
			}
			
		}
	}
	
	private class AskDesTask extends AsyncTask<String, Void, String>{
		List<NameValuePair> paramList = null;
		JSONObject jsonj;
		JSONArray jarray;
		String errorString = null;
		int errorCode = 0;
		int length = 0;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			errorString=null;
			paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("page",page+""));
			paramList.add(new BasicNameValuePair("qf_id", qf_id));
			paramList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
			paramList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
			System.out.println("question page = "+page);
		}
		
		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.que_des, paramList);
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
				if(page == 1)deslists.clear();
				jarray = jsonj.getJSONArray("data");
				length = jarray.length();
				isPreferences.updateSp(page+"", jarray.length());
				
				System.out.println("question length = "+length);
				
				for (int i = 0; i < jarray.length(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("qa_id", jarray.getJSONObject(i).getString("qa_id").toString().replace("null", ""));
					map.put("qa_context", jarray.getJSONObject(i).getString("qa_context").toString().replace("null", ""));
					map.put("qa_addtime", jarray.getJSONObject(i).getString("qa_addtime").toString().replace("null", ""));
					map.put("ans_name", jarray.getJSONObject(i).getString("ans_name").toString().replace("null", ""));
					map.put("ans_mlogo", jarray.getJSONObject(i).getString("ans_mlogo").toString().replace("null", ""));
					deslists.add(map);
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
			iAskDesTask = null;
			if(errorString== null){
				System.out.println("jinlai le _"+deslists);
				iSimpleAdapter.notifyDataSetChanged();
				int length2 = 0 ;
				for (int i = 1; i <= page; i++) {
					length2 +=  isPreferences.getSp().getInt(i+"",1);
				
				}
				
				if(Integer.parseInt(ans_num) >length2){
					tv_num.setText("已解答"+ans_num);
					tv_num2.setText("全部解答"+ans_num);
				}else{
					tv_num.setText("已解答"+length2);
					tv_num2.setText("全部解答"+length2);
				}
			}else{
				Function.toastMsg(queDesActivity.this, errorString);
				comFunction.outtoLogin(queDesActivity.this, errorString, errorCode,isPreferences);
				errorString = null;
			}
			if(page == 1){
				lv_que.onPullDownRefreshComplete();
				lv_que.setLastUpdatedLabel(Function.getDateToString2());
			}else{
				lv_que.onPullUpRefreshComplete();
			}
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
