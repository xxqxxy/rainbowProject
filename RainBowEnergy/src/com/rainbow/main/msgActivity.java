package com.rainbow.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.comutils.main.Function;
import com.comutils.main.SharePreferences;
import com.comutils.net.HttpUtil;
import com.comutils.pulltorefresh.PullToRefreshBase;
import com.comutils.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.comutils.pulltorefresh.PullToRefreshListView;
import com.comutils.util.UrlUtil;
import com.rainbow.main.function.comFunction;

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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class msgActivity extends Activity implements OnClickListener{
	private TextView tv_back,tv_title,tv_next;
	private SharePreferences isPreferences;
	private List<HashMap<String, Object>> nlst;
	private int page = 1;
    private ListView mListView;
    private PullToRefreshListView mPullListView;
    private SimpleAdapter iSimpleAdapter;
    private GetDataTask iGetDataTask;
    UrlUtil mUrlUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg); 
		isPreferences = new SharePreferences(this);
		mUrlUtil = UrlUtil.getInstance();
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
    	tv_title.setText(getString(R.string.tv_message));
    	isPreferences.updateSp("ismsg", true);
    	isPreferences.updateSp("isnews", false);
    	mPullListView = (PullToRefreshListView) findViewById(R.id.lv_list);
    	mPullListView.setPullLoadEnabled(true);  
        // 滚动到底自动加载可用  
		mPullListView.setScrollLoadEnabled(false);
        // 得到实际的ListView  
		mListView = mPullListView.getRefreshableView();  
		mListView.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);
		mListView.setFocusable(false);
		mListView.setVerticalScrollBarEnabled(true);
		/*mListView.setSelector(getResources().getColor(R.color.gray_3));*/
		mListView.setDividerHeight(0);
		mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.cr_gray1)));
		mListView.setDividerHeight((int)(getResources().getDisplayMetrics().density*5));
		mListView.setDivider(new ColorDrawable(R.color.cr_gray3));
        nlst = new ArrayList<HashMap<String, Object>>();
//        initData();
        iSimpleAdapter = new SimpleAdapter(this,nlst,R.layout.nlist_item,new String[]{"n_map","n_title","n_time"},
     			new int[]{R.id.iv_nmap,R.id.tv_ntitle,R.id.tv_ntime});
 		// 绑定数据  
 		mListView.setAdapter(iSimpleAdapter); 
 		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				if((nlst.get(arg2).get("n_type").toString().equals("0")) ||(nlst.get(arg2).get("n_type").toString().equals("3"))){
					startActivity(new Intent(msgActivity.this,msgDesActivity.class)
							.putExtra("n_id", nlst.get(arg2).get("n_id").toString()).putExtra("n_title", nlst.get(arg2).get("n_title").toString()) );
				}else if((nlst.get(arg2).get("n_type").toString().equals("1"))){//任务通知
					startActivity(new Intent(msgActivity.this,TaskActivity.class)
							);
				} else if((nlst.get(arg2).get("n_type").toString().equals("2"))){//警报通知
					isPreferences.updateSp("tk_type","2");
					startActivity(new Intent(msgActivity.this,alarmActivity.class)
							);
				}
			}
        });
 	    // 设置下拉刷新的listener  
 		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {  
            @Override  
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {  
            	if(iGetDataTask == null){
            		page = 1;
    				iGetDataTask = new GetDataTask();
    				iGetDataTask.execute();
    			}else{
    				mPullListView.onPullDownRefreshComplete();
    			}
            }  
  
            @Override  
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) { 
            	if(iGetDataTask == null){
            		page += 1;
    				iGetDataTask = new GetDataTask();
    				iGetDataTask.execute();
    			}else{
    				mPullListView.onPullUpRefreshComplete();
    			}
            }
        }); 
 	    // 自动刷新  
 		mPullListView.doPullRefreshing(true, 100);
    }
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() ==  R.id.tv_back)
			finish();
	}
	private class GetDataTask extends AsyncTask<String, Void, String>{
		List<NameValuePair> paramsList;
		JSONObject jobj = null;
		String errorString;int code = 0;
		JSONArray jArray = null;
    	
		protected void onPreExecute() {
			errorString = null;
			paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("app_mid",isPreferences.getSp().getString("m_id", "")));
			paramsList.add(new BasicNameValuePair("mtoken",isPreferences.getSp().getString("m_token", "")));
			paramsList.add(new BasicNameValuePair("page",page+""));
		}
    	@Override
		protected String doInBackground(String... params){
    		String requery = HttpUtil.queryStringForPost(mUrlUtil.message_list,paramsList);
    		Log.i("", "tag sss sucss=="+requery);
    		if (requery.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
 			try {
 				jobj = new JSONObject(requery);
 				if(jobj.getInt("state") == 0){code = jobj.getInt("code");
 					errorString = "err";
 					if(code == 203){
 						errorString = getString(R.string.err_203);
 					}
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
 				//jobj = new JSONObject(jobj.getString("data"));
 				jArray = jobj.getJSONArray("data");
 				System.out.println("jArray data is "+jArray);
 				int len = jArray.length();
 				HashMap<String, Object> map;
 				if(page == 1)
 					nlst.clear();
                for(int i = 0; i < len; i++){
                	map = new HashMap<String, Object>();
                	map.put("n_id", jArray.getJSONObject(i).getString("n_id").toString());
                	map.put("n_type", jArray.getJSONObject(i).getString("n_type").toString());
        			map.put("n_title", jArray.getJSONObject(i).getString("n_title").toString());
        			map.put("n_time", jArray.getJSONObject(i).getString("n_addtime").toString());
        			//map.put("n_url",  jArray.getJSONObject(i).getString("n_url").toString());
//        			map.put("n_bid", jArray.getJSONObject(i).getString("n_bid").toString());
        			if(jArray.getJSONObject(i).getString("n_type").toString().equals("0") ||jArray.getJSONObject(i).getString("n_type").toString().equals("3") ){
        				map.put("n_map",R.drawable.icon_system_msg);
        			}else{
        				map.put("n_map",R.drawable.icon_order_msg);
        			}
        			nlst.add(map);
                }
 				return null;
 			} catch (Exception e) {
 				//Log.i("", "tag sss sucss=="+e.getMessage());
 			}finally{}
 			return null;
 			
		}
		@Override
		protected void onPostExecute(String result){
			iGetDataTask=null;
			if(errorString == null){
				iSimpleAdapter.notifyDataSetChanged();
			}else{
				Function.toastMsg(msgActivity.this,errorString);
				comFunction.outtoLogin(msgActivity.this, errorString,code,isPreferences);
				errorString = null;
			}
			if(page == 1){
				mPullListView.onPullDownRefreshComplete();
				setLastUpdateTime();
			}else{
				mPullListView.onPullUpRefreshComplete();
			}
	    }
	}
	private void setLastUpdateTime() {
		mPullListView.setLastUpdatedLabel(Function.getDateToString2());
	}
}
