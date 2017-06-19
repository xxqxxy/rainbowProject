package com.rainbow.main;

import java.io.Serializable;
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
import com.rainbow.main.widget.OperationAdapter;

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
import android.widget.ListView;
import android.widget.TextView;

public class selectOperationActivity extends Activity implements OnClickListener {

	TextView tv_title,tv_right;

	int page = 1;
	PullToRefreshListView mPullListView;
	ListView mListView;
	List<HashMap<String, Object>> opelists;
	OperationAdapter adapter;
	SharePreferences isPreferences;
	String task_id;
	TaskExcmembersTask iTaskExcmembersTask;
	UrlUtil mUrlUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.operation);
		isPreferences = new SharePreferences(this);
		mUrlUtil = UrlUtil.getInstance();
		opelists = new ArrayList<HashMap<String,Object>>();
		Intent i = getIntent();
		Bundle b = i.getExtras();

		isPreferences.updateSp("maps", "");
		isPreferences.updateSp("mids","");
		isPreferences.updateSp("names", "");
		
		mapslist =  new ArrayList<HashMap<String,Object>>();
		
		if(isPreferences.getSp().getString("send", "").equals("1")){
			task_id = b.getString("task_id");
		}
		findViewById(R.id.tv_back).setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getString(R.string.tv_yunwei_person));
		tv_right = (TextView) findViewById(R.id.tv_right);
		tv_right.setOnClickListener(this);
//		tv_right.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_search), null, null, null);
		
		initView();
	}
	
	private void initView() {
		mPullListView = (PullToRefreshListView) findViewById(R.id.pl_operation);
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
//		initData();
		adapter = new OperationAdapter(((RainBowApplication)this.getApplication()),selectOperationActivity.this, opelists, itemOnClickListener);
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
			            	 
			             	if(iTaskExcmembersTask == null){
			            		page = 1;
			            		iTaskExcmembersTask = new TaskExcmembersTask();
			            		iTaskExcmembersTask.execute();
			    			}else{
			    				mPullListView.onPullDownRefreshComplete();
			    			}
			             }  
			             @Override  
			             public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) { 
			            	 if(iTaskExcmembersTask == null){
				            		page += 1;
				            		iTaskExcmembersTask = new TaskExcmembersTask();
				            		iTaskExcmembersTask.execute();
				    			}else{
				    				mPullListView.onPullDownRefreshComplete();
				    			}
			             }
			         }); 
			  	    
					mPullListView.doPullRefreshing(true, 300);
				
	}
	
	private class TaskExcmembersTask extends AsyncTask<String, Void, String>{
		List<NameValuePair> paramList = null;
		JSONObject jsonj = null;
		JSONArray jarray = null;
		String errorString =null;
		int errorCode = 0;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			errorString = null;
			paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
			paramList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
			paramList.add(new BasicNameValuePair("page", page+""));
			paramList.add(new BasicNameValuePair("site_id", task_id));
			Log.i("", "site_id| = "+task_id);
			
		}

		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.task_exc_people, paramList);
			Log.i("", "tag 111 === "+result);
			if (result.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
			
			try {
				jsonj = new JSONObject(result);
				if(jsonj.getInt("state") == 0){
					errorCode = jsonj.getInt("code");
					errorString = "err";
					if(errorCode == 301){
						errorString = getString(R.string.err_301);
					}
					if(errorCode == 202){
						errorString = getString(R.string.err_202);
					}
					if(errorCode== 201)
 					    errorString = getString(R.string.err_201);
					if(errorCode == 202)
						errorString= getString(R.string.err_202);
 					if(errorCode == 203)
 						errorString = getString(R.string.err_203);
 					if(errorCode == 204)
						errorString= getString(R.string.err_204);
					if(errorCode == 102){
						errorString = getString(R.string.tv_no_quanxian);
					}
					return null;
				}
			
				jarray = jsonj.getJSONArray("data");
				if(page == 1)opelists.clear();
				for (int i = 0; i < jarray.length(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("m_id",jarray.getJSONObject(i).getString("m_id").toString().replace("null", ""));
					map.put("m_name",jarray.getJSONObject(i).getString("m_name").toString().trim().replace("null", ""));
					map.put("m_logo",jarray.getJSONObject(i).getString("m_logo").toString().replace("null", ""));
					map.put("m_cardv",jarray.getJSONObject(i).getString("m_cardv").toString().replace("null", ""));
					map.put("busy_state",jarray.getJSONObject(i).getString("busy_state").toString().replace("null", ""));
					map.put("task_des",jarray.getJSONObject(i).getString("task_des").toString().replace("null", ""));
					map.put("select", "1");
					opelists.add(map);
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
			iTaskExcmembersTask = null;
			if(errorString == null){
				
				adapter.notifyDataSetChanged();
			}else{
				Function.toastMsg(selectOperationActivity.this, errorString);
				comFunction.outtoLogin(selectOperationActivity.this, errorString, errorCode,isPreferences);
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
	
	String maps = "";
	String mids = "";
	String names = "";
	
	List<HashMap<String, Object>> mapslist = null;
	
	OnClickListener itemOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			switch (v.getId()) {
			case R.id.iv_select:
				if((opelists.get(position).get("select")+"").equals("1")){
					opelists.get(position).put("select", "2");
					//图片
					HashMap<String, Object> map = new HashMap<String, Object>();
					if((opelists.get(position).get("m_logo")+"").equals("")){
						map.put("map", "123456");
					}else{
						map.put("map", opelists.get(position).get("m_logo")+"");
					}
					map.put("mid", opelists.get(position).get("m_id")+"");
					map.put("name", opelists.get(position).get("m_name")+"");
					mapslist.add(map);
					
					System.out.println("maplist = "+mapslist);
					
				}else{
					opelists.get(position).put("select", "1");
					String mid = opelists.get(position).get("m_id")+""; //对应的ID
					for (int i = 0; i < mapslist.size(); i++) {
						String m_id = mapslist.get(i).get("mid")+"";
						if(mid.equals(m_id)){
							mapslist.remove(i);
						}
					}
					System.out.println("maplist 3  = "+mapslist);
				}
				adapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
			
		}
	};
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_back:
			Intent intent = new Intent();
			intent.putExtra("oplist", (Serializable) mapslist);
			setResult(RESULT_OK, intent);
			
			for (int i = 0; i < mapslist.size(); i++) {
				if(!mids.equals(""))mids+=",";
				mids+=mapslist.get(i).get("mid")+"";
				isPreferences.updateSp("mids",mids);
			}
			System.out.println("maps == "+isPreferences.getSp().getString("maps", ""));
			System.out.println("maps = "+maps);
			isPreferences.updateSp("send", "");
			finish();
			break;
		case R.id.tv_right:
			
			//搜索

		default:
			break;
		}
	}
	
	//按两次退出程序
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if(event.getKeyCode()==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){ 
				Intent intent = new Intent();
				intent.putExtra("oplist", (Serializable) mapslist);
				setResult(RESULT_OK, intent);
				for (int i = 0; i < mapslist.size(); i++) {
					if(!mids.equals(""))mids+=",";
					mids+=mapslist.get(i).get("mid")+"";
					
					isPreferences.updateSp("mids",mids);
				}
				
				isPreferences.updateSp("list", mapslist);
				System.out.println("maps = "+maps);
				isPreferences.updateSp("send", "");
				finish();
		    	return true;
		    }
			return super.onKeyDown(keyCode, event);
		}
	
	
	
	
	
}
