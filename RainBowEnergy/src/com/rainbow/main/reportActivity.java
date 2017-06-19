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

public class reportActivity extends Activity implements OnClickListener,OnUploadProcessListener{
	public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 101;
	private SharePreferences isPreferences;
	private TextView tv_back,tv_title,tv_right,tv_submit;
	private GridView gv_maplist;
	private List<HashMap<String, Object>> maplst;
	private MapListAdapter iMapListAdapter;
	private String TAG = "reportActivity";
	private int dindex = -1;
    private String o_id="";//,o_position;
    //图片上传
    private List<HashMap<String, Object>> rtmaplst;//返回图片
    private int upindex = 0,mapsize = 0;
    private ProgressDialog iPDialog;
    private String IMAGE_FILE_NAME = "";
    String task_id;
    LinearLayout ll_bar;
    private Dialog mContentDialog;
    private DoTask idoTask;
    private EditText et_remark,et_cost_info,et_rest_problem;
    private RelativeLayout rl_select;
	List<HashMap<String, Object>>  oplist_2 = null;
	String allmids="";
	 MyGridView gv_list;
	 OprAdapter adapter = null;
	 public static final int REQUES_CODE = 2;
	 UrlUtil mUrlUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback); 
		isPreferences = new SharePreferences(this);
		mUrlUtil = UrlUtil.getInstance();
		iPDialog = new ProgressDialog(this);
		iPDialog.setIndeterminate(true);
	    iPDialog.setMessage(getString(R.string.tv_map_uploading));
	    iPDialog.setCancelable(true);
		
	    ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
	    
	    Intent i = getIntent();
	    Bundle b = i.getExtras();
	    task_id = b.getString("task_id");
		tv_back = (TextView) findViewById(R.id.tv_back);
	 	tv_back.setOnClickListener(this);
	 	
	 	tv_title = (TextView) findViewById(R.id.tv_title);
	 	tv_title.setText(getString(R.string.tv_report_task));
	 	
	 	tv_right = (TextView) findViewById(R.id.tv_right);
	 	tv_right.setText(getString(R.string.tv_send2));
	 	tv_right.setOnClickListener(this);
	 	
	 	
	 	et_remark = (EditText) findViewById(R.id.et_remark);//建议内容
	 	et_cost_info = (EditText) findViewById(R.id.et_cost_info);//建议内容
	 	et_rest_problem = (EditText) findViewById(R.id.et_rest_problem);//建议内容
	 	
	 	rl_select = (RelativeLayout) findViewById(R.id.rl_select);
	 	rl_select.setOnClickListener(this);
	 	
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
					startActivityForResult(new Intent(reportActivity.this, PicSelectActivity.class)
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
				
				startActivityForResult(new Intent(reportActivity.this, selectOperationActivity.class)
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
	 	
	    
	    
	    showCamera();
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
	
	public void showCamera() {
	    // 检查摄像头权限是否已经有效
    	if(ContextCompat.checkSelfPermission(reportActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
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
			 Function.toastMsg(reportActivity.this, "您将无法执行有关摄像头的操作！");
		}
		
	}
	
	
	
	
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

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tv_back:finish();break;
		case R.id.tv_right:
			getDoTask();
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
				startActivityForResult(new Intent(reportActivity.this,selectOperationActivity.class)
					 , 2);		
			}else if(oplist_2.size() == 0 ){
				startActivityForResult(new Intent(reportActivity.this,selectOperationActivity.class)
						, 2);	
			}
			
			break;
		
		
		}
	}
	
	private void getDoTask() {
		if(Function.isWiFi_3G(this)){
			if(infoCheck()){
				if(idoTask== null){
					idoTask = new DoTask();
					idoTask.execute();
				}
			}
		}else{
			ll_bar.setVisibility(View.GONE);
			Function.toastMsg(this, getString(R.string.tv_not_netlink));
		}
	}
	
	
	//信息检测
	public boolean infoCheck(){
		if(Function.isNullorSpace(et_remark.getText().toString().trim())){
			Function.toastMsg(reportActivity.this, getString(R.string.tv_input_all));
			return false;
		}
		if(Function.isNullorSpace(et_cost_info.getText().toString().trim())){
			Function.toastMsg(reportActivity.this, getString(R.string.tv_input_all));
			return false;
		}
		if(Function.isNullorSpace(et_rest_problem.getText().toString().trim())){
			Function.toastMsg(reportActivity.this, getString(R.string.tv_input_all));
			return false;
		}
		return true;
	}
	private class DoTask extends AsyncTask<String, Void, String>{
		List<NameValuePair> paramsList;
		JSONObject jobj = null;
		String errorString,maps="",mids="";int errcode=0;
		protected void onPreExecute() {
			ll_bar.setVisibility(View.VISIBLE);
			errorString = null;
			paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("app_mid",isPreferences.getSp().getString("m_id", "")));
			paramsList.add(new BasicNameValuePair("mtoken",isPreferences.getSp().getString("m_token", "")));
			paramsList.add(new BasicNameValuePair("task_id",task_id));
			paramsList.add(new BasicNameValuePair("content",et_remark.getText().toString().trim()));
			paramsList.add(new BasicNameValuePair("rest_problem",et_rest_problem.getText().toString().trim()));
			
			
			
			if(oplist_2!=null){
				if(oplist_2.size()>0){
					for(int i = 0; i < oplist_2.size(); i++){
						if(oplist_2.get(i).get("mid").toString().equals(""))continue;
						if(!mids.equals(""))mids +=",";
						mids += oplist_2.get(i).get("mid").toString();
					}
				}else{
					mids = allmids;
				}
			}
			paramsList.add(new BasicNameValuePair("member_ids",mids));
			
			paramsList.add(new BasicNameValuePair("cost_info",et_cost_info.getText().toString().trim()));
			int len = maplst.size();
			for(int i = 0; i < len; i++){
				if(maplst.get(i).get("fd_map_name").toString().equals(""))continue;
				if(!maps.equals(""))maps +=",";
				maps += maplst.get(i).get("fd_map_name").toString();
			}
			paramsList.add(new BasicNameValuePair("logoname",maps));
		}
    	@Override
		protected String doInBackground(String... params){
    		
    		String requery = HttpUtil.queryStringForPost( mUrlUtil.do_task,paramsList);
    		//Log.i("", "tag sss sucss=="+requery);
    		if (requery.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
 			try {
 				jobj = new JSONObject(requery);
 				if(jobj.getInt("state") == 0){
 					errcode = jobj.getInt("code");
 					errorString = "err";
 				
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
 				return null;
 			} catch (Exception e) {}finally{}
 			return null;
		}
		@Override
		protected void onPostExecute(String result){
			try {
				idoTask = null;
				ll_bar.setVisibility(View.GONE);
				if(errorString == null){
					Function.toastMsg(reportActivity.this, getString(R.string.tv_hb_scuesss4));
					finish();
				}else{
					Function.toastMsg(reportActivity.this,errorString);
					comFunction.outtoLogin(reportActivity.this, errorString, errcode,isPreferences);
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
				//String lpth = context.getString(R.string.app_img_path)+"mini_"+comFunction.getFileName(mUrl);
				//String sapath = getString(R.string.app_img_path)+"m500_"+comFunction.getFileName(rtmaplst.get(upindex).get("map_path").toString());
				// comFunction.getFileName(rtmaplst.get(upindex).get("map_path").toString())
				//Bitmap  photo  = comFunction.getBitmapByPath(this,rtmaplst.get(upindex).get("map_path").toString(),500,500);
	    		//comFunction.saveBitmap(photo, sapath);
				//toUploadFile(sapath);photo.recycle();
			}
		}else  if(requestCode == REQUES_CODE && resultCode == RESULT_OK){
			//
			  oplist_2  =(List<HashMap<String, Object>>) data.getSerializableExtra("oplist"); 
			  System.out.println("oplist = "+oplist_2);
			  if(oplist_2.size()>0){
				  adapter = new OprAdapter(getApplicationContext(), oplist_2, (RainBowApplication)reportActivity.this.getApplication());
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
        uploadUtil.uploadFile(mapSavePath,"mlogo",mUrlUtil.taskback_logo,params); 
    }

	@Override
	public void onUploadDone(int responseCode, String message) {
		// TODO Auto-generated method stub
		try {
			//iPDialog.dismiss();
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
				String errorString = "";
				if(msg.what == 1){
					JSONObject jobj;
					jobj = new JSONObject(msg.obj.toString());
					Log.i("tag", "tag image +"+jobj);
	 				
					if(jobj.getInt("state") == 0){
						if(jobj.getInt("code") == 101){
							errorString = getString(R.string.err_iv_101);
						}	
						if(jobj.getInt("code") == 202){
							errorString =getString(R.string.err_iv_202);
						}
					}
					
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
	 					Function.toastMsg(reportActivity.this,errorString);
	 				}
				}else{
					Function.toastMsg(reportActivity.this,getString(R.string.tv_map_upload_failure));
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
