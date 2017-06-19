package com.rainbow.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.comutils.main.Function;
import com.comutils.main.SharePreferences;
import com.comutils.net.HttpUtil;
import com.comutils.rain_view.MyGridView;
import com.comutils.rain_view.MyGridView.OnTouchBlankPositionListener;
import com.comutils.util.UrlUtil;
import com.rainbow.main.function.comFunction;
import com.rainbow.main.widget.OprAdapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class sendTaskActivity extends Activity implements OnClickListener {
	OprAdapter adapter = null;
	TextView tv_title,tv_right;
	
	TextView tv_site,tv_device,tv_fault;
	
	EditText et_need_order,et_work_order;
	
	RelativeLayout rl_select;
	ImageView iv_map;
	 MyGridView gv_list;
	SharePreferences isPreferences;
	String measpoint_id,warn_sta,devname,warn_evnt,warn_staid;
	ConfirmTask iConfirmTask;
	List<HashMap<String, Object>>  oplist_2 = null;
	LinearLayout ll_bar ;
	String allmids = "";
	UrlUtil mUrlUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_task);
		isPreferences = new SharePreferences(this);
		mUrlUtil = UrlUtil.getInstance();
		isPreferences.updateSp("maps", "");
		isPreferences.updateSp("mids", "");
		isPreferences.updateSp("names", "");
		Intent i = getIntent();
		Bundle b = i.getExtras();
		measpoint_id = b.getString("measpoint_id");
		warn_sta = b.getString("warn_sta");
		devname = b.getString("devname");
		warn_evnt = b.getString("warn_evnt");
		warn_staid = b.getString("warn_staid");
		initView();
		ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
		tv_site.setText(warn_sta);
		tv_device.setText(devname);
		tv_fault.setText(warn_evnt);
	}
	
	
	private void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getString(R.string.tv_send_task));
		tv_right = (TextView) findViewById(R.id.tv_right);
	 	tv_right.setText(getString(R.string.tv_send2));
		findViewById(R.id.tv_back).setOnClickListener(this);
		tv_right.setOnClickListener(this);
	 	
		gv_list = (MyGridView) findViewById(R.id.gv_list);
		gv_list.setOnTouchBlankPositionListener(new OnTouchBlankPositionListener() {
			
			@Override
			public void onTouchBlank(MotionEvent event) {
				isPreferences.updateSp("send", "1");
				allmids = "";
				if(oplist_2!=null){
					for (int i = 0; i < oplist_2.size(); i++) {
						if(!allmids.equals(""))allmids+=",";
						allmids += oplist_2.get(i).get("mid")+"";
					}
				}
				startActivityForResult(new Intent(sendTaskActivity.this,selectOperationActivity.class).putExtra("task_id", warn_staid) ,2);
			}
		});
		
		gv_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(!(oplist_2.get(arg2).get("name")+"").equals("")){
					deleteOperationDialog(arg2);
				}
				return false;
			}
		});
		
		
		tv_right.setText(getString(R.string.tv_send));
		tv_right.setOnClickListener(this);
		
		tv_site=  (TextView) findViewById(R.id.tv_site);//站点
		tv_device=  (TextView) findViewById(R.id.tv_device);//设备
		tv_fault=  (TextView) findViewById(R.id.tv_fault);//故障
		
		et_need_order = (EditText) findViewById(R.id.et_need_order);
		et_work_order = (EditText) findViewById(R.id.et_work_order);
		
		rl_select = (RelativeLayout) findViewById(R.id.rl_select);
		rl_select.setOnClickListener(this);
		iv_map = (ImageView) findViewById(R.id.iv_map);
		
	}
	
	
	/**
	 * 运维人员删除
	 * @param pt
	 */
	private void deleteOperationDialog(final int pt){
		LayoutInflater mflater=LayoutInflater.from(this);
		View mView=mflater.inflate(R.layout.delete_dialog,null);
		((TextView)mView.findViewById(R.id.tv_title)).setText(getString(R.string.tv_del_opera_tab));
		((TextView)mView.findViewById(R.id.tv_message)).setText(getString(R.string.tv_del_opera_check_tab));
		final Dialog mDialog= new Dialog(this,R.style.iDialog2);
		mDialog.setContentView(mView); 
		((TextView)mView.findViewById(R.id.tv_cancel)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		((TextView)mView.findViewById(R.id.tv_yes)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
				if(!oplist_2.get(pt).get("name").toString().equals("")){
					//新的删除
					oplist_2.remove(pt);
					adapter.notifyDataSetChanged();
				}
			}
		});
		mDialog.show();
	}
	
	
	
	private void sendTask(){
		if(Function.isWiFi_3G(this)){
			if(infoCheck()){
				if(iConfirmTask == null){
					iConfirmTask = new ConfirmTask();
					iConfirmTask.execute();
				}
			}
		}else{
			ll_bar.setVisibility(View.GONE);
			Function.toastMsg(this, getString(R.string.tv_not_netlink));
		}
	}
	
	public boolean infoCheck(){
		if(Function.isNullorSpace(et_need_order.getText().toString().trim())){
			Function.toastMsg(sendTaskActivity.this, getString(R.string.tv_need_order_not_null));
			return false;
		}
		if(Function.isNullorSpace(et_work_order.getText().toString().trim())){
			Function.toastMsg(sendTaskActivity.this, getString(R.string.tv_work_order_not_null));
			return false;
		}
		return true;
	}
	private class ConfirmTask extends AsyncTask<String, Void, String>{
		List<NameValuePair> paramsList;
		JSONObject jobj = null;
		String errorString,maps="",mids="";int errcode=0;
		@SuppressWarnings("deprecation")
		protected void onPreExecute() {
			ll_bar.setVisibility(View.VISIBLE);
			errorString = null;
			paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("app_mid",isPreferences.getSp().getString("m_id", "")));
			paramsList.add(new BasicNameValuePair("mtoken",isPreferences.getSp().getString("m_token", "")));
			if(oplist_2!=null){
				if(oplist_2.size()>0){
					for(int i = 0; i < oplist_2.size(); i++){
						if(oplist_2.get(i).get("mid").toString().equals(""))continue;
						if(!mids.equals(""))mids +=",";
						mids += oplist_2.get(i).get("mid").toString();
					}
				}else{
					Log.i("", "soso allmids = "+allmids);
					mids = allmids;
				}
			}
			paramsList.add(new BasicNameValuePair("member_ids",mids));
			paramsList.add(new BasicNameValuePair("type",2+""));
			paramsList.add(new BasicNameValuePair("order_des",et_work_order.getText().toString().trim()));
			paramsList.add(new BasicNameValuePair("tool_des",et_need_order.getText().toString().trim()));
			paramsList.add(new BasicNameValuePair("measpoint_id",measpoint_id));
			
			Log.i("send", "send = "+isPreferences.getSp().getString("m_id", "")+":"+isPreferences.getSp().getString("m_token", "")
					+":"+isPreferences.getSp().getString("mids", "")+":"+et_work_order.getText().toString().trim()+":"
					+et_need_order.getText().toString().trim()+":"
					+measpoint_id);
		}
    	@Override
		protected String doInBackground(String... params){
    		String requery = HttpUtil.queryStringForPost(mUrlUtil.publish_task,paramsList);
    		Log.i("", "tag sss sucss=="+requery);
    		if (requery.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
 			try {
 				jobj = new JSONObject(requery);
 				if(jobj.getInt("state") == 0){
 					errcode = jobj.getInt("code");
 					errorString = "err";
 					if(jobj.getInt("code")== 103)
 					    errorString = getString(R.string.err_pt_103);
 					if(jobj.getInt("code")== 104)
 					    errorString = getString(R.string.err_pt_104);
 					if(jobj.getInt("code")== 102)
 					    errorString = getString(R.string.err_pt_102);
 					if(jobj.getInt("code")== 105)
 					    errorString = getString(R.string.tv_no_quanxian);
 					if(errcode== 201)
 					    errorString = getString(R.string.err_201);
					if(errcode == 202)
						errorString= getString(R.string.err_202);
 					if(errcode == 203)
 						errorString = getString(R.string.err_203);
 					if(errcode == 204)
						errorString= getString(R.string.err_204);
 					if(jobj.getInt("code") == 300)
 						errorString= getString(R.string.err_300_2);
 					return null;
 				}
 				return null;
 			} catch (Exception e) {}finally{}
 			return null;
		}
		@Override
		protected void onPostExecute(String result){
			try {
				iConfirmTask = null;
				ll_bar.setVisibility(View.GONE);
				if(errorString == null){
					Function.toastMsg(sendTaskActivity.this, getString(R.string.tv_send_suess));
					finish();
				}else{
					Function.toastMsg(sendTaskActivity.this,errorString);
					comFunction.outtoLogin(sendTaskActivity.this, errorString, errcode,isPreferences);
					errorString = null;
				}
			} catch (Exception e) {}
	    }
	}
	
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tv_back:
			isPreferences.updateSp("send", "");
			finish();
			
			break;
		case R.id.tv_right:
			sendTask();
			isPreferences.updateSp("send", "");
			break;
			
		case R.id.rl_select:
			allmids = "";
			if(oplist_2!=null){
				for (int i = 0; i < oplist_2.size(); i++) {
					if(!allmids.equals(""))allmids+=",";
					allmids += oplist_2.get(i).get("mid")+"";
				}
			}
			if( oplist_2 == null){
				startActivityForResult(new Intent(sendTaskActivity.this,selectOperationActivity.class).putExtra("task_id", warn_staid) ,2);	
			}else if(oplist_2.size() == 0 ){
				startActivityForResult(new Intent(sendTaskActivity.this,selectOperationActivity.class).putExtra("task_id", warn_staid) ,2);
			}
			break;
		default:
			break;
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	 if(requestCode == 2 && resultCode == RESULT_OK){
			//
			 oplist_2 =(List<HashMap<String, Object>>) data.getSerializableExtra("oplist"); 
			System.out.println("oplist = "+oplist_2);
			 if(oplist_2.size()>0){
				  adapter = new OprAdapter(getApplicationContext(), oplist_2, (RainBowApplication)sendTaskActivity.this.getApplication());
				  gv_list.setAdapter(adapter);
			  }
			 }
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	

}
