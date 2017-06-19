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
import com.rainbow.main.data.second.SecondActivity;
import com.rainbow.main.function.comFunction;
import com.rainbow.main.widget.SiteAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SiteActivity extends Activity implements OnClickListener {

	List<HashMap<String, Object>> olist_1;
	List<List<HashMap<String, Object>>> olist_2;
	List<HashMap<String, Object>> wlist = null;
	SiteAdapter adapter;
	TextView tv_back,tv_title;
	PullToRefreshListView mPullListView;
	ListView mListView;
	SharePreferences isPreferences;
	SiteTask iSiteTask;
	int page =1 ;
	LinearLayout ll_bar;
	UrlUtil mUrlUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.site);
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_back.setVisibility(View.GONE);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getString(R.string.tv_site));
		
		ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
		initView();
	}
	
	private void initView() {
		isPreferences = new SharePreferences(this);
		mUrlUtil = UrlUtil.getInstance();
		mPullListView = (PullToRefreshListView) findViewById(R.id.pl_site);
		mPullListView.setPullLoadEnabled(false);  
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
		
		olist_1 = new ArrayList<HashMap<String,Object>>();
		olist_2 = new ArrayList<List<HashMap<String,Object>>>();
		adapter = new SiteAdapter(SiteActivity.this, olist_1, olist_2, ((RainBowApplication)SiteActivity.this.getApplication()), onClickListener);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position_2, long id) {
			
			}
		});
		
		// 设置下拉刷新的listener  
					mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {  
			             @Override  
			             public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) { 
			            	if(Function.isWiFi_3G(SiteActivity.this)){
			            		if(iSiteTask == null){
				            		page = 1;
				            		iSiteTask = new SiteTask();
				            		iSiteTask.execute();
				    			}else{
				    				mPullListView.onPullDownRefreshComplete();
				    			}
			            	} else{
			            		ll_bar.setVisibility(View.GONE);
			            		Function.toastMsg(SiteActivity.this, getString(R.string.tv_not_netlink));
			            	}
			             	
			             }  
			             @Override  
			             public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) { 
			             }
			         }); 
					if(Function.isWiFi_3G(this))
						mPullListView.doPullRefreshing(true, 500);
					else
						Function.toastMsg(SiteActivity.this, getString(R.string.tv_not_netlink));
	}
	
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position=0,subposition=0;
			String[] tstring;
			if((v.getTag()).toString().indexOf("=") != -1){
				tstring = (v.getTag()).toString().split("=");
				position = Integer.parseInt(tstring[0]) ;
				subposition = Integer.parseInt(tstring[1]) ;
			}else{
				position = (Integer) v.getTag();
			}
			
			switch (v.getId()) {
			case R.id.ll_site:
				startActivity(new Intent(SiteActivity.this,SecondActivity.class)
						.putExtra("st_id",olist_2.get(position).get(subposition).get("st_id")+"")
						.putExtra("st_type", olist_2.get(position).get(subposition).get("st_type")+"")
						.putExtra("st_name", olist_2.get(position).get(subposition).get("st_name")+"")
						.putExtra("position", position)
						.putExtra("subposition", subposition)
						
						);
				break;

			default:
				break;
			}
		}
	};
	
	
	private  class  SiteTask extends AsyncTask<String, Void, String>{

		List<NameValuePair> paramList = null;
		JSONObject jsonj = null;
		JSONArray jarray = null;
		JSONArray tarray= null;
		String errorString ;
		int errorCode = 0;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			errorString = null;
			paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
			paramList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
			
			Log.i("", "tag llll =  = "+ isPreferences.getSp().getString("m_id", "")+"  .. "+isPreferences.getSp().getString("m_token", ""));
		}
		
		@SuppressLint("NewApi")
		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.site_list, paramList);
			Log.i("", "tag llll = "+result);
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
						errorString= getString(R.string.err_203);
					if(errorCode == 204)
						errorString= getString(R.string.err_204);
					if(errorCode == 300)
						errorString = getString(R.string.err_301);
					return null;
				}
				jarray = jsonj.getJSONArray("data");
				Log.i("", "tag 111 == "+jarray);
				if(page ==1 ){
					olist_1.clear();
					olist_2.clear();
				}
				
				for (int i = 0; i < jarray.length(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("dpt_name", jarray.getJSONObject(i).get("dpt_name").toString().replace("null", ""));
					isPreferences.updateSp("dpt_name"+i, jarray.getJSONObject(i).get("dpt_name").toString().replace("null", ""));
					olist_1.add(map);
					
					wlist = new ArrayList<HashMap<String,Object>>();
					
					tarray = new JSONArray(jarray.getJSONObject(i).getString("dpt_info"));
					Log.i("", "tag 111 = "+tarray);
					
					for (int j = 0; j < tarray.length(); j++) {
						HashMap<String, Object> map2 = new HashMap<String, Object>();
						isPreferences.updateSp("st_name"+j, tarray.getJSONObject(j).getString("st_name").toString().replace("null", ""));
						System.out.println("dddd   = === "+isPreferences.getSp().getString("ddd0", ""));
						map2.put("st_id", tarray.getJSONObject(j).getString("st_id").toString().replace("null", ""));
						map2.put("st_name", tarray.getJSONObject(j).getString("st_name").toString().replace("null", ""));
						map2.put("facemap", tarray.getJSONObject(j).getString("facemap").toString().replace("null", ""));
						map2.put("st_type", tarray.getJSONObject(j).getString("st_type").toString().replace("null", ""));
						wlist.add(map2);
					}
					olist_2.add(wlist);
					
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
			iSiteTask = null;
			if(errorString == null){
				adapter.notifyDataSetChanged();
			}else{
				Function.toastMsg(SiteActivity.this, errorString);
				comFunction.outtoLogin(SiteActivity.this, errorString, errorCode,isPreferences);
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
	
	@Override
	public void onClick(View v) {

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
