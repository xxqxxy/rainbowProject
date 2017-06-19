package com.rainbow.main.fragment;


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
import com.rainbow.main.R;
import com.rainbow.main.alarmDesActivity;
import com.rainbow.main.function.comFunction;
import com.rainbow.main.widget.AlarmAdapter;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

public class AlarmFragment extends Fragment {
	View alarmView;
	int page =1;
	String i = "0";
	
	WarnTask iWarnTask;
	SharePreferences isPreferences;
	PullToRefreshListView mPullListView;
	ListView mListView;
	LinearLayout ll_bar;
	AlarmAdapter alarmAdapter = null;
	List<HashMap<String, Object>> alarmlists;
	UrlUtil mUrlUtil;
	
	public static final Fragment newInstance(String io){
		Fragment fragment =  new  AlarmFragment();
		Bundle b = new Bundle();
		b.putString("alarm_type", io);
		fragment.setArguments(b);
		return fragment;
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		alarmView = inflater.inflate(R.layout.alarm_list, container, false);
		return alarmView;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		alarmlists = new ArrayList<HashMap<String,Object>>();
		i =  getArguments().getString("alarm_type");
		
		initView();
		
	}
	
	private void initView() {
		ll_bar = (LinearLayout) alarmView.findViewById(R.id.ll_bar);
		isPreferences = new SharePreferences(getActivity());
		mUrlUtil = UrlUtil.getInstance();
		mPullListView = (PullToRefreshListView) alarmView.findViewById(R.id.pl_alarm);
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
		
		alarmAdapter = new AlarmAdapter(getActivity(), alarmlists, itemOnClickListener);
		mListView.setAdapter(alarmAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position_2, long id) {
				startActivity(new Intent(getActivity(),alarmDesActivity.class)
						.putExtra("warn_sta", alarmlists.get(position_2).get("warn_sta")+"")
						.putExtra("warn_id", alarmlists.get(position_2).get("warn_id")+""));
			}
		});
		
		// 设置下拉刷新的listener  
					mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {  
			             @Override  
			             public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) { 
			            	 if(Function.isWiFi_3G(getActivity())){
			            		 if(iWarnTask == null){
					            		page = 1;
					            		iWarnTask = new WarnTask();
					            		iWarnTask.execute();
					    			}else{
					    				mPullListView.onPullDownRefreshComplete();
					    			}
			            	 }else{
			            		 ll_bar.setVisibility(View.GONE);
			            		 Function.toastMsg(getActivity(), getString(R.string.tv_not_netlink));
			            	 }
			             	
			             }  
			             @Override  
			             public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) { 
			            	if(Function.isWiFi_3G(getActivity())){
			            		if(iWarnTask == null){
				            		page += 1;
				            		iWarnTask = new WarnTask();
				            		iWarnTask.execute();
				    			}else{
				    				mPullListView.onPullDownRefreshComplete();
				    			}
			            	}else{
			            		ll_bar.setVisibility(View.GONE);
			            		Function.toastMsg(getActivity(), getString(R.string.tv_not_netlink));
			            	}
			            	 
			             }
			         }); 
					if(Function.isWiFi_3G(getActivity()))
						mPullListView.doPullRefreshing(true, 100);
					else
					    Function.toastMsg(getActivity(), getString(R.string.tv_not_netlink));
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(Function.isWiFi_3G(getActivity()))
			mPullListView.doPullRefreshing(true, 100);
		else
			 Function.toastMsg(getActivity(), getString(R.string.tv_not_netlink));
	}
	
	private OnClickListener itemOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			
			switch (v.getId()) {
			case R.id.iv_more:
				
				break;

			default:
				break;
			}
			
		}
	};
	
	private class WarnTask extends AsyncTask<String, Void, String>{
		List<NameValuePair> paramList = null;
		JSONObject jsonj= null;
		JSONArray jarray = null;
		String errorString = null;
		int errorCode = 0;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			errorString = null;
			paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("type", i));
			paramList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
			paramList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
			paramList.add(new BasicNameValuePair("page", page+""));
			
		}
		
		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.warning_list, paramList);
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
				if(page == 1)
				alarmlists.clear();
				
				jarray = jsonj.getJSONArray("data");
				
				for (int i = 0; i < jarray.length(); i++) {
					
					HashMap<String, Object> map = new HashMap<String, Object>();
					
					map.put("warn_sta", jarray.getJSONObject(i).getString("warn_sta").toString().replace("null",""));
					map.put("warn_id", jarray.getJSONObject(i).getString("warn_id").toString().replace("null",""));
					map.put("warn_evnt", jarray.getJSONObject(i).getString("warn_evnt").toString().replace("null",""));
					map.put("warn_time", jarray.getJSONObject(i).getString("warn_time").toString().replace("null",""));
					alarmlists.add(map);
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
			iWarnTask = null;
			if(errorString == null){
				alarmAdapter.notifyDataSetChanged();
			}else{
				Function.toastMsg(getContext(), errorString);
				comFunction.outtoLogin(getActivity(), errorString, errorCode,isPreferences);
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
	
	
	
}
