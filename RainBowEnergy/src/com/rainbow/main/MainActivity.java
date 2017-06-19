package com.rainbow.main;



import com.comutils.main.Function;
import com.comutils.main.SharePreferences;
import com.rainbow.main.map.LocationDemo;
import com.rainbow.main.service.UpdateService;

import android.Manifest;
import android.app.ActivityGroup;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MainActivity extends ActivityGroup implements OnClickListener {

	public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 101;
	private LinearLayout ll_main;
	private TextView tv_home,tv_site,tv_task,tv_personal;
	private SharePreferences isPreferences;
	private long exitTime = 0;
	protected Intent updateIntent;
//	private DownloadTask iDownloadTask;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		isPreferences = new SharePreferences(this);
		
		isPreferences.updateSp("update_from", "MainActivity");
		isPreferences.updateSp("wlact_from","main");
		initView();
		initListener();
		//初始化显示
		 onAddView(R.id.tv_home,LocationDemo.class);
		 
		 //获取外部存储的权限
		 RequestPermission();
		 
	        //版本更新问题
	        if(!(isPreferences.getSp().getString("vsn_code", "").toString()).equals(""))
	    		showNewApp();
	}
	
	private void RequestPermission(){
		if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
			 //申请WRITE_EXTERNAL_STORAGE权限
	          ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
	                  WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
		}
	}
	
	//获取权限允许
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		 if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
	          if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
	              // Permission Granted
//	        	  requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
	          } else {
	              // Permission Denied
	        	  Function.toastMsg(MainActivity.this, "您将无法执行有关存储空间的操作！");
	          }
	      }
		
	}
	
	
	
	private void initView(){
		ll_main = (LinearLayout) findViewById(R.id.ll_main);
		tv_home = (TextView) findViewById(R.id.tv_home);
		tv_site = (TextView) findViewById(R.id.tv_site);
		tv_task = (TextView) findViewById(R.id.tv_task);
		tv_personal  = (TextView) findViewById(R.id.tv_personal);
	}
	private void initListener(){
		tv_home.setOnClickListener(this);
		tv_site.setOnClickListener(this);
        tv_task.setOnClickListener(this);
        tv_personal.setOnClickListener(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(updateIntent!=null){
			stopService(updateIntent);
		}
		
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
		switch(v.getId()){
		case R.id.tv_home: 
		    onAddView(v.getId(),LocationDemo.class);
			break;
		case R.id.tv_site:
			 onAddView(v.getId(),SiteActivity.class);
			break;
		case R.id.tv_task:
		   onAddView(v.getId(),TaskActivity.class);
			break;
		case R.id.tv_personal:
			 onAddView(v.getId(),PersonalActivity.class);
			break;
		}
	}
	
	
	
    private void showNewApp(){
    	LayoutInflater mflater=LayoutInflater.from(this);//program_add_success_dialog.
		View mView=mflater.inflate(R.layout.version_dialog,null);
		TextView tv_v_d_title = (TextView)mView.findViewById(R.id.tv_v_d_title);
		TextView tv_v_d_content = (TextView)mView.findViewById(R.id.tv_v_d_content);
		
		tv_v_d_title.setText(getString(R.string.tv_version_updata2)
				.replace("%1$s", isPreferences.getSp().getString("app_vname", "").toString()));
		tv_v_d_content.setText(getString(R.string.tv_version_updata_question)
				.replace("%1$s", isPreferences.getSp().getString("vsn_name", "").toString()));
		TextView tv_cancel = (TextView)mView.findViewById(R.id.tv_cancel);
		TextView tv_yes = (TextView)mView.findViewById(R.id.tv_yes);
		final Dialog mDialog= new Dialog(this,R.style.iDialog2);
		mDialog.setContentView(mView); 
		mDialog.show(); 
		tv_cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
			}
		});
		tv_yes.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
				
				 updateIntent = new Intent(
							MainActivity.this,
							UpdateService.class);
					updateIntent.putExtra("titleId",
							R.string.app_name);
					startService(updateIntent);
				
				
				/*if(iDownloadTask == null){
					 iDownloadTask = new DownloadTask();
					 iDownloadTask.execute();
				}*/
			}
		});
}

    @Override
    protected void onStop() {
    	super.onStop();
    	if(updateIntent!=null){
    		stopService(updateIntent);
    	}
    }
    
    

	private void onAddView(int id, Class<?> cls){
		initfootbar(id);
		View view = null;
		ll_main.removeAllViews();

			view = getLocalActivityManager().startActivity(id+"",new Intent(MainActivity.this,cls)).getDecorView();

		view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		ll_main.addView(view);
	}
	private void initfootbar(int id){
		tv_home.setTextColor(getResources().getColor(R.color.cr_gray_2));
		tv_site.setTextColor(getResources().getColor(R.color.cr_gray_2));
		tv_task.setTextColor(getResources().getColor(R.color.cr_gray_2));
		tv_personal.setTextColor(getResources().getColor(R.color.cr_gray_2));
		tv_home.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_map_normal), null, null);
		tv_site.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_site_normal), null, null);
		tv_task.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_task_normal), null, null);
		tv_personal.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_personal_normal), null, null);
		switch(id){
		case R.id.tv_home: 
			tv_home.setTextColor(getResources().getColor(R.color.cr_blue_8));
			tv_home.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_map), null, null);
			break;
		case R.id.tv_site:
			tv_site.setTextColor(getResources().getColor(R.color.cr_blue_8));
			tv_site.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_site), null, null);
			break;
		case R.id.tv_task: 
			tv_task.setTextColor(getResources().getColor(R.color.cr_blue_8));
			tv_task.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_task), null, null);
			break;
		case R.id.tv_personal: 
			tv_personal.setTextColor(getResources().getColor(R.color.cr_blue_8));
			tv_personal.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_personal), null, null);
			break;
	    }
	}
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
	
//	
	
}
