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
import com.rainbow.main.reportActivity;
import com.rainbow.main.taskDesActivity;
import com.rainbow.main.function.comFunction;
import com.rainbow.main.widget.TaskAdapter;

import android.app.Dialog;
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
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TaskFragment extends Fragment {

	String i = "0";
	int page = 1;
	
	LinearLayout ll_bar;
	View task_View;
	SharePreferences isPreferences;
	PullToRefreshListView mPullListView;
	ListView mListView;
	List<HashMap<String, Object>> tasklists;	
	TaskAdapter adapter = null;
	WarningDesTask iWarningDesTask;
	private DoTask iDotask;
	
	UrlUtil mUrlUtil;
	public static final Fragment newInstance(String io){
		Fragment fragment = new TaskFragment();
		Bundle b = new Bundle();
		b.putString("task_type", io);;
		fragment.setArguments(b);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		task_View = inflater.inflate(R.layout.alarm_list, container, false);
		return task_View;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		i = getArguments().getString("task_type");
		initView();
	
	}
	
	private void initView() {
		tasklists = new ArrayList<HashMap<String,Object>>();
		isPreferences = new SharePreferences(getActivity());
		mUrlUtil = UrlUtil.getInstance();
		ll_bar = (LinearLayout) task_View.findViewById(R.id.ll_bar);
		mPullListView = (PullToRefreshListView) task_View.findViewById(R.id.pl_alarm);
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
		
		adapter = new TaskAdapter(getActivity(), tasklists, onClickListener);
		
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position_2, long id) {
				isPreferences.updateSp("tk_state_bool", true);
				startActivity(new Intent(getActivity(),taskDesActivity.class)
						.putExtra("task_id", tasklists.get(position_2).get("tk_id")+"")
						.putExtra("tk_type", tasklists.get(position_2).get("tk_type")+"")//1  警报  2  图文
						.putExtra("tk_state", tasklists.get(position_2).get("tk_state")+""));
			
			}
		});
		
		
		
		// 设置下拉刷新的listener  
					mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {  
			             @Override  
			             public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) { 
			            	if(Function.isWiFi_3G(getActivity())){
			            		if(iWarningDesTask == null){
				            		page = 1;
				            		iWarningDesTask = new WarningDesTask();
				            		iWarningDesTask.execute();
				    			}else{
				    				mPullListView.onPullDownRefreshComplete();
				    			}
			            	} else{
			            		ll_bar.setVisibility(View.GONE);
			            		Function.toastMsg(getActivity(), getString(R.string.tv_not_netlink));
			            	}
			             	
			             }  
			             @Override  
			             public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) { 
			            	 if(Function.isWiFi_3G(getActivity())){
			            		 if(iWarningDesTask == null){
					            		page += 1;
					            		iWarningDesTask = new WarningDesTask();
					            		iWarningDesTask.execute();
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
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			switch (v.getId()) {
			case R.id.tv_task_type:
				//接收任务   完成任务  汇报任务   已汇报
				if((tasklists.get(position).get("tk_state")+"").equals("0")){
					if((tasklists.get(position).get("is_owner")+"").equals("0")){
						isPreferences.updateSp("tk_state", "0");
						Receiver_Task(tasklists.get(position).get("tk_id")+"");
					}else{
						startActivity(new Intent(getActivity(),taskDesActivity.class)
								.putExtra("task_id", tasklists.get(position).get("tk_id")+"")
								.putExtra("tk_type", tasklists.get(position).get("tk_type")+"")
								.putExtra("tk_state", tasklists.get(position).get("tk_state")+""));
					}
					
				}else if((tasklists.get(position).get("tk_state")+"").equals("1")){
					
					if((tasklists.get(position).get("is_owner")+"").equals("0")){
						isPreferences.updateSp("tk_state", "1");
						Receiverd_Task(tasklists.get(position).get("tk_id")+"");
					}else{
						startActivity(new Intent(getActivity(),taskDesActivity.class)
								.putExtra("task_id", tasklists.get(position).get("tk_id")+"")
								.putExtra("tk_type", tasklists.get(position).get("tk_type")+"")
								.putExtra("tk_state", tasklists.get(position).get("tk_state")+""));
					}
					
				}else if((tasklists.get(position).get("tk_state")+"").equals("2")){//汇报任务
					
					if((tasklists.get(position).get("is_owner")+"").equals("0")){
						isPreferences.updateSp("tk_state", "2");
						startActivity(new Intent(getActivity(),reportActivity.class)
								.putExtra("task_id", tasklists.get(position).get("tk_id")+"")
								.putExtra("task_from", "2"));
					}else{
						startActivity(new Intent(getActivity(),taskDesActivity.class)
								.putExtra("task_id", tasklists.get(position).get("tk_id")+"")
								.putExtra("tk_type", tasklists.get(position).get("tk_type")+"")
								.putExtra("tk_state", tasklists.get(position).get("tk_state")+""));
					}
				}else if((tasklists.get(position).get("tk_state")+"").equals("3")){//查看任务
					isPreferences.updateSp("tk_state_bool", true);
					startActivity(new Intent(getActivity(),taskDesActivity.class)
							.putExtra("task_id", tasklists.get(position).get("tk_id")+"")
							.putExtra("tk_type", tasklists.get(position).get("tk_type")+"")
							.putExtra("tk_state", tasklists.get(position).get("tk_state")+""));
				}
				
				break;

			default:
				break;
			}
			
		}
	};
	
	private void getDoTask(String task_id){
		if(Function.isWiFi_3G(getActivity())){
			if(iDotask == null){
				iDotask = new DoTask();
				iDotask.execute(task_id);
			}
		}else{
			ll_bar.setVisibility(View.GONE);
			Function.toastMsg(getActivity(), getString(R.string.tv_not_netlink));
		}
	}
	
 	private class WarningDesTask extends AsyncTask<String, Void, String>{
		List<NameValuePair> paramList = null;
		JSONObject jsonj = null;
		JSONArray jarray = null;
		String errorString = null;
		int errCode = 0;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			errorString = null;
			paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("type", i));
			paramList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
			paramList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
			paramList.add(new BasicNameValuePair("page", page+""));
			Log.i("", "tag ===== "+isPreferences.getSp().getString("m_id", "")+" \t\t tag === "+isPreferences.getSp().getString("m_token", ""));
			System.out.println("type == "+ i + ",, page  == "+page);
		}
		
		
		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.get_task, paramList);
			Log.i("", "tag 1111 == "+result);
			if (result.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
			try {
				jsonj = new JSONObject(result);
				if(jsonj.getInt("state") == 0){
					errCode = jsonj.getInt("code");
					errorString = "err";
					if(errCode == 301){
						errorString = getString(R.string.err_301);
					}
					if(errCode== 201)
 					    errorString = getString(R.string.err_201);
					if(errCode == 203)
						errorString= getString(R.string.err_203);
					if(errCode == 202)
						errorString= getString(R.string.err_202);
					if(errCode == 102){
						errorString = getString(R.string.tv_no_quanxian);
					}
					if(errCode== 204)
						errorString= getString(R.string.err_204);
					return null;
				}
				
				jarray = jsonj.getJSONArray("data");
				Log.i("", "tag 111 == "+jarray);
				if(page == 1)tasklists.clear();
				for (int i = 0; i < jarray.length(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("tk_id", jarray.getJSONObject(i).getString("tk_id").toString().replace("null", ""));
					map.put("task_des", jarray.getJSONObject(i).getString("task_des").toString().replace("null", ""));
					map.put("tk_state", jarray.getJSONObject(i).getString("tk_state").toString().replace("null", ""));
					map.put("tk_context", jarray.getJSONObject(i).getString("tk_context").toString().replace("null", ""));
					map.put("tk_type", jarray.getJSONObject(i).getString("tk_type").toString().replace("null", ""));
					map.put("is_owner", jarray.getJSONObject(i).getString("is_owner").toString().replace("null", ""));
					
					tasklists.add(map);
						
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
			iWarningDesTask = null;
			if(errorString == null){
				adapter.notifyDataSetChanged();
			}else{
				Function.toastMsg(getContext(), errorString);
				comFunction.outtoLogin(getActivity(), errorString, errCode,isPreferences);
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
	
	private class DoTask extends AsyncTask<String, Void, String>{
		List<NameValuePair> paramsList;
		JSONObject jobj = null;
		String errorString,maps="";int errcode=0;
		protected void onPreExecute() {
			ll_bar.setVisibility(View.VISIBLE);
			((TextView)task_View.findViewById(R.id.tv_tishi)).setText(getString(R.string.tv_operating_waitting));
			errorString = null;
			paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("app_mid",isPreferences.getSp().getString("m_id", "")));
			paramsList.add(new BasicNameValuePair("mtoken",isPreferences.getSp().getString("m_token", "")));
			
		}
    	@Override
		protected String doInBackground(String... params){
    		String taskid = params[0];
    		paramsList.add(new BasicNameValuePair("task_id",taskid));
    		String requery = HttpUtil.queryStringForPost(mUrlUtil.do_task,paramsList);
    		Log.i("", "tag sss sucss=="+requery);
    		if (requery.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
 			try {
 				jobj = new JSONObject(requery);
 				if(jobj.getInt("state") == 0){
 					errcode = jobj.getInt("code");
 					errorString = "err"+errcode;
 				
 					if(jobj.getInt("code")== 301)
 					    errorString = getString(R.string.err_301);
 					if(jobj.getInt("code")== 102)
 					    errorString = getString(R.string.tv_no_quanxian);
 					if(jobj.getInt("code")== 101)
 					    errorString = getString(R.string.err_hb_101);
 					if(jobj.getInt("code")== 103)
 					    errorString = getString(R.string.err_pt_103);
 					if(jobj.getInt("code")== 104)
 					    errorString = getString(R.string.err_hb_104);
 					if(jobj.getInt("code")== 201)
 					    errorString = getString(R.string.err_201);
 					if(jobj.getInt("code") == 203){
 						errorString = getString(R.string.err_203);
 					}
 					if(jobj.getInt("code") == 202){
 						 errorString = getString(R.string.err_202);
 					}
 					if(jobj.getInt("code") == 204)
						errorString= getString(R.string.err_204);
 					return null;
 				}
 				return null;
 			} catch (Exception e) {}finally{}
 			return null;
		}
		@Override
		protected void onPostExecute(String result){
			try {
				iDotask = null;
				ll_bar.setVisibility(View.GONE);
				if(errorString == null){
					if(isPreferences.getSp().getString("tk_state","").equals("0")){
						Function.toastMsg(getActivity(), getString(R.string.tv_hb_scuesss2));
					}else if(isPreferences.getSp().getString("tk_state","").equals("1")){
						Function.toastMsg(getActivity(), getString(R.string.tv_hb_scuesss3));
					} 
					mPullListView.doPullRefreshing(true, 500);
					
				}else{
					
					Function.toastMsg(getActivity(),errorString);
					comFunction.outtoLogin(getContext(), errorString, errcode,isPreferences);
					errorString = null;
				}
			} catch (Exception e) {}
	    }
	}
	
	
	private void Receiver_Task(final String task_id){
    	LayoutInflater mflater=LayoutInflater.from(getActivity());//program_add_success_dialog.
		View mView=mflater.inflate(R.layout.simple_dialog,null);
		TextView tv_v_d_title = (TextView)mView.findViewById(R.id.tv_v_d_title);
		TextView tv_v_d_content = (TextView)mView.findViewById(R.id.tv_v_d_content);
		
		tv_v_d_title.setText(getString(R.string.tv_ok));
		tv_v_d_content.setText(getString(R.string.tv_receiver_task));
		TextView tv_cancel = (TextView)mView.findViewById(R.id.tv_cancel);
		TextView tv_yes = (TextView)mView.findViewById(R.id.tv_yes);
		final Dialog mDialog= new Dialog(getActivity(),R.style.iDialog2);
		mDialog.setCanceledOnTouchOutside(false);
		int dWidth  = getResources().getDisplayMetrics().widthPixels - (int)
				(50* getResources().getDisplayMetrics().density);
		mDialog.setContentView(mView, new LayoutParams(dWidth, LayoutParams.WRAP_CONTENT));  
		mDialog.show(); 
		tv_cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		tv_yes.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				getDoTask(task_id);
				mDialog.dismiss();
			}
		});
    }
	private void Receiverd_Task(final String task_id){
    	LayoutInflater mflater=LayoutInflater.from(getActivity());//program_add_success_dialog.
		View mView=mflater.inflate(R.layout.simple_dialog,null);
		TextView tv_v_d_title = (TextView)mView.findViewById(R.id.tv_v_d_title);
		TextView tv_v_d_content = (TextView)mView.findViewById(R.id.tv_v_d_content);
		
		tv_v_d_title.setText(getString(R.string.tv_ok));
		tv_v_d_content.setText(getString(R.string.tv_receiverd_task));
		TextView tv_cancel = (TextView)mView.findViewById(R.id.tv_cancel);
		
		 
		TextView tv_yes = (TextView)mView.findViewById(R.id.tv_yes);
		final Dialog mDialog= new Dialog(getActivity(),R.style.iDialog2);
		mDialog.setCanceledOnTouchOutside(false);
		int dWidth  = getResources().getDisplayMetrics().widthPixels - (int)
				(50* getResources().getDisplayMetrics().density);
		mDialog.setContentView(mView, new LayoutParams(dWidth, LayoutParams.WRAP_CONTENT)); 
		mDialog.show(); 
		tv_cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		tv_yes.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				getDoTask(task_id);
				mDialog.dismiss();
			}
		});
    }
	
	
	
}
