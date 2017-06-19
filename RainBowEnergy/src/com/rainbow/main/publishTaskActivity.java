package com.rainbow.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.comutils.main.Function;
import com.comutils.main.PicSelectActivity;
import com.comutils.main.SharePreferences;
import com.comutils.net.HttpUtil;
import com.comutils.net.UploadUtil;
import com.comutils.net.UploadUtil.OnUploadProcessListener;
import com.comutils.rain_view.MyGridView;
import com.comutils.rain_view.MyGridView.OnTouchBlankPositionListener;
import com.comutils.util.UrlUtil;
import com.rainbow.main.function.comFunction;
import com.rainbow.main.widget.MapListAdapter;
import com.rainbow.main.widget.OprAdapter;
import com.umeng.analytics.MobclickAgent;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class publishTaskActivity   extends Activity implements OnClickListener,OnUploadProcessListener{
	public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 101;
	OprAdapter adapter = null;
	public static final int REQUES_CODE = 2;
	private SharePreferences isPreferences;
	private TextView tv_back,tv_title,tv_submit;
	private GridView gv_maplist;
	private List<HashMap<String, Object>> maplst;
	private MapListAdapter iMapListAdapter;
	private String TAG = "publishTaskActivity";
	private int dindex = -1;
    private String o_id="";//,o_position;
    //图片上传
    private List<HashMap<String, Object>> rtmaplst;//返回图片
    private int upindex = 0,mapsize = 0;
    private ProgressDialog iPDialog;
    private String IMAGE_FILE_NAME = "";

    private EditText et_content;
    private RelativeLayout rl_select;

    MyGridView gv_list;
    LinearLayout ll_bar;
	List<HashMap<String, Object>>  oplist_2 = null;
	String allmids="";
    private ConfirmTask iConfirmTask;
    UrlUtil mUrlUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.publish_task); 
		isPreferences = new SharePreferences(this);
		mUrlUtil = UrlUtil.getInstance();
		isPreferences.updateSp("map", "");
		isPreferences.updateSp("mids", "");
		isPreferences.updateSp("names", "");
		iPDialog = new ProgressDialog(this);
		iPDialog.setIndeterminate(true);
	    iPDialog.setMessage(getString(R.string.tv_map_uploading));
	    iPDialog.setCancelable(true);
		//o_id = getIntent().getExtras().getString("o_id");
		tv_back = (TextView) findViewById(R.id.tv_back);
	 	tv_back.setOnClickListener(this);
	 	
	 	tv_title = (TextView) findViewById(R.id.tv_title);
	 	tv_title.setText(getString(R.string.tv_publish_task));
	 	
	 	et_content = (EditText) findViewById(R.id.et_content);
	 	tv_submit = (TextView) findViewById(R.id.tv_submit);
	 	
	 	ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
	 	
	 	
	 	gv_list = (MyGridView) findViewById(R.id.gv_list);
	 	gv_list.setOnTouchBlankPositionListener(new OnTouchBlankPositionListener() {
			
			@Override
			public void onTouchBlank(MotionEvent event) {
				if(oplist_2!=null){
					allmids = "";
					for (int i = 0; i < oplist_2.size(); i++) {
						if(!allmids.equals(""))allmids+=",";
						allmids += oplist_2.get(i).get("mid")+"";
					}
				}
				System.out.println("oplist =  gv_list");
				
				startActivityForResult(new Intent(publishTaskActivity.this, selectOperationActivity.class)
					, 2);
				
//				startActivityForResult(new Intent(publishTaskActivity.this,selectOperationActivity.class), REQUES_CODE);
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
	 	
	 	
	 	rl_select = (RelativeLayout) findViewById(R.id.rl_select);
	 	
	 	
	 	tv_submit.setOnClickListener(this);
	 	rl_select.setOnClickListener(this);
	 	
	 	//isPreferences.updateSp("sl_oid","");
	 	
 	    //内容下的图片
 	    gv_maplist = (GridView) findViewById(R.id.gv_maplist);
	    gv_maplist.setSelector(new ColorDrawable(Color.TRANSPARENT));
	    maplst = new ArrayList<HashMap<String, Object>>();
	    iMapListAdapter = new MapListAdapter(this,((RainBowApplication)this.getApplication()),TAG,maplst,R.layout.map_list_item,new String[]{"fd_map"},
   			new int[]{R.id.iv_map});
	    gv_maplist.setAdapter(iMapListAdapter);
	    gv_maplist.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(maplst.get(arg2).get("fd_map_name").toString().equals("")){
					dindex = arg2;
					startActivityForResult(new Intent(publishTaskActivity.this, PicSelectActivity.class)
							.putExtra("pic_sel_limit", (6-dindex)),0xC110);
				}else{
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(new File(maplst.get(arg2).get("fd_map").toString())), "image/*");
					startActivity(intent);
					
				}
			}
		});
	    gv_maplist.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(!maplst.get(arg2).get("fd_map_name").toString().equals("")){
					deleteDialog(arg2);
				}
				return false;
			}
	    });
	    addDfMap();
	    
	    // 摄像头权限
	    showCamera();
	}
	
	
	
	
	public void showCamera() {
	    // 检查摄像头权限是否已经有效
    	if(ContextCompat.checkSelfPermission(publishTaskActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
			 //申请WRITE_EXTERNAL_STORAGE权限
	          ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
	                  WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
		}
	    	
	}
	
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		// TODO Auto-generated method stub
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		
		if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
			// 授权成功  逻辑
		}else{
			// 没有授权  逻辑
			 Function.toastMsg(publishTaskActivity.this, "您将无法执行有关摄像头的操作！");
		}
		
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
					String mids = "";
					adapter.notifyDataSetChanged();
				}
			}
		});
		mDialog.show();
	}
	
	/**
	 * 任务图片删除
	 * @param pt
	 */
	private void deleteDialog(final int pt){
		LayoutInflater mflater=LayoutInflater.from(this);
		View mView=mflater.inflate(R.layout.delete_dialog,null);
		((TextView)mView.findViewById(R.id.tv_title)).setText(getString(R.string.tv_del_map_tab));
		((TextView)mView.findViewById(R.id.tv_message)).setText(getString(R.string.tv_del_map_check_tab));
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
				if(!maplst.get(pt).get("fd_map_name").toString().equals("")){
					//新的删除
					maplst.remove(pt);
					int len = maplst.size();
					if(!maplst.get(len-1).get("fd_map_name").toString().equals("")){
						 addDfMap();
					}else{
						iMapListAdapter.notifyDataSetChanged();
						changeMaplistViewHeight();
					}
				}
			}
		});
		mDialog.show();
	}
	private void addDfMap(){
		 HashMap<String, Object> map; 
	     map = new HashMap<String, Object>();
	     map.put("fd_map_name", "");
	     map.put("fd_map",R.drawable.icon_map_add);
		 maplst.add(map);
		 iMapListAdapter.notifyDataSetChanged();
		 changeMaplistViewHeight();
	}
	private void changeMaplistViewHeight(){
		if(maplst.size() > 3){
			gv_maplist.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 
					(int)(240* getResources().getDisplayMetrics().density)));
		}else{
			gv_maplist.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
		}
	}

	/**
	 * 1maps === [{map=http://www.people.com.cn/mediafile/pic/20131224/88/8466783445076937632.jpg}, {map=http://www.people.com.cn/mediafile/pic/20131224/88/8466783445076937632.jpg}, {map=http://www.people.com.cn/mediafile/pic/20131224/88/8466783445076937632.jpg}, {map=http://www.people.com.cn/mediafile/pic/20131224/88/8466783445076937632.jpg}, {map=http://www.people.com.cn/mediafile/pic/20131224/88/8466783445076937632.jpg}]
	 */
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	
	//信息检测
		public boolean infoCheck(){
			if(Function.isNullorSpace(et_content.getText().toString().trim())){
				Function.toastMsg(publishTaskActivity.this, getString(R.string.tv_task_content_not_null));
				return false;
			}
			return true;
		}
	private void getPublicTask() {
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
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.tv_back:
				isPreferences.updateSp("map", "");
				isPreferences.updateSp("mids", "");
				isPreferences.updateSp("names", "");
				finish();break;
			case R.id.tv_submit:
				getPublicTask();
				break;
			case R.id.rl_select:
				System.out.println("oplist =  rl_select");
				
				isPreferences.updateSp("send", "2");
				allmids = "";
				if(oplist_2!=null){
					for (int i = 0; i < oplist_2.size(); i++) {
						if(!allmids.equals(""))allmids+=",";
						allmids += oplist_2.get(i).get("mid")+"";
					}
				}
				
				if( oplist_2 == null){
					startActivityForResult(new Intent(publishTaskActivity.this,selectOperationActivity.class)
						 , 2);		
				}else if(oplist_2.size() == 0 ){
					startActivityForResult(new Intent(publishTaskActivity.this,selectOperationActivity.class)
							, 2);	
				}
				
				break;
		}
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
			paramsList.add(new BasicNameValuePair("type",1+""));
			paramsList.add(new BasicNameValuePair("content",et_content.getText().toString().trim()));
			int len = maplst.size();
			for(int i = 0; i < len; i++){
				if(maplst.get(i).get("fd_map_name").toString().equals(""))continue;
				if(!maps.equals(""))maps +=",";
				maps += maplst.get(i).get("fd_map_name").toString();
			}
			paramsList.add(new BasicNameValuePair("logoname",maps));
			Log.i("", "soso mids = "+mids);
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
					Function.toastMsg(publishTaskActivity.this,getString(R.string.err_pt_scuss));
					finish();
				}else{
					Function.toastMsg(publishTaskActivity.this,errorString);
					comFunction.outtoLogin(publishTaskActivity.this, errorString, errcode,isPreferences);
					errorString = null;
				}
			} catch (Exception e) {}
	    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0xC110 && resultCode == RESULT_OK) {
			rtmaplst = (List<HashMap<String, Object>>) data.getSerializableExtra("maplist");
			mapsize = rtmaplst.size();
			if(mapsize != 0){
				upindex = 0;
				toUploadFile(rtmaplst.get(upindex).get("map_path").toString());
			}
		}else if(requestCode == REQUES_CODE && resultCode == RESULT_OK){
			//
			  oplist_2  =(List<HashMap<String, Object>>) data.getSerializableExtra("oplist"); 
			  System.out.println("oplist = "+oplist_2);
			  if(oplist_2.size()>0){
				  adapter = new OprAdapter(getApplicationContext(), oplist_2, (RainBowApplication)publishTaskActivity.this.getApplication());
				  gv_list.setAdapter(adapter);
			  }
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
    private void toUploadFile(String mapSavePath){
 		if(!iPDialog.isShowing())iPDialog.show();
		//接口地址
    	UploadUtil uploadUtil = UploadUtil.getInstance();;  
        uploadUtil.setOnUploadProcessListener(this); 
        Map<String, String> params = new HashMap<String, String>();  
        params.put("file_param","mlogo");  
        params.put("app_mid",isPreferences.getSp().getString("m_id", ""));
        params.put("mtoken",isPreferences.getSp().getString("m_token", ""));
        uploadUtil.uploadFile(mapSavePath,"mlogo", mUrlUtil.task_logo,params); 
    }

	@Override
	public void onUploadDone(int responseCode, String message) {
		// TODO Auto-generated method stub
		try {
		   ////新的图像处理   类似微信图片选择
			if((upindex+1) == mapsize)
				iPDialog.dismiss();
			Message msg = new Message();
			msg.what =responseCode;
			msg.obj =message;
			mhandler.sendMessage(msg);
		} catch (Exception e){
			// Log.i("", "tag sss dd="+e.getMessage());
		}	
	}
	@Override
	public void onUploadProcess(int uploadSize) {}
	@Override
	public void initUpload(int fileSize) {}
	private  Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				if(msg.what == 1){
					JSONObject jobj;
					jobj = new JSONObject(msg.obj.toString());
					System.out.println("tag 111 == "+jobj);
	 				if(jobj.getInt("state") == 1){
	 					IMAGE_FILE_NAME = jobj.getString("data");
	 					if (maplst.get(dindex).get("fd_map_name").equals(""))
	 					    maplst.remove(dindex);
	 					HashMap<String, Object> map; 
	 				    map = new HashMap<String, Object>();
	 				    map.put("fd_map_name", IMAGE_FILE_NAME);
	 				    map.put("fd_map",rtmaplst.get(upindex).get("map_path").toString());
	 					maplst.add(map);
	 					iMapListAdapter.notifyDataSetChanged();
 						changeMaplistViewHeight();
	 				    ////新的图像处理   类似微信图片选择
	 					if((upindex+1) == mapsize){//全部上传了
	 						if(maplst.size() < 6){
		 						addDfMap();
		 					}
	 					}
	 				}else{
	 					Function.toastMsg(publishTaskActivity.this,"图片上传失败"+jobj.getString("msg"));
	 				}
				}else{
					Function.toastMsg(publishTaskActivity.this,getString(R.string.tv_map_upload_failure));
				}
				
			    ////新的图像处理   类似微信图片选择  下一个
				if((upindex+1) != mapsize){
					upindex += 1;
					toUploadFile(rtmaplst.get(upindex).get("map_path").toString());
				}
			} catch (JSONException e) {}
		}
	};	

}
