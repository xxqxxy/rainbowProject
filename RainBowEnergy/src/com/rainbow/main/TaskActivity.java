package com.rainbow.main;


import java.util.ArrayList;
import java.util.List;

import com.comutils.main.Function;
import com.comutils.main.SharePreferences;
import com.comutils.rain_view.ViewPager;
import com.comutils.rain_view.ViewPager.OnPageChangeListener;
import com.rainbow.main.fragment.TaskFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

@SuppressLint("NewApi")
public class TaskActivity extends FragmentActivity implements OnClickListener {

	TextView tv_back,tv_title,tv_right,tv_unfinish,tv_finish;
	ViewPager vp_main;
	int currpage = 1;
	
	List<Fragment> fragments;
	
	SharePreferences isPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task);
		isPreferences = new SharePreferences(this);
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_back.setVisibility(View.GONE);
		tv_title.setText(getString(R.string.tv_task_list));
		tv_unfinish = (TextView) findViewById(R.id.tv_unfinish);
		tv_finish = (TextView) findViewById(R.id.tv_finish);
		
		tv_unfinish.setOnClickListener(new MyOnClickListener(0));
		tv_finish.setOnClickListener(new MyOnClickListener(1));
		
		tv_right = (TextView) findViewById(R.id.tv_right);
		tv_right.setPadding(20,50, 20, 50);
		tv_right.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_publish_task), null, null, null);
		tv_right.setOnClickListener(this);
		
		
		InitViewPager();
		
	}
	private void InitViewPager() {
		vp_main = (ViewPager) findViewById(R.id.vp_main);
		fragments = new ArrayList<Fragment>();
		Fragment fragment1 = TaskFragment.newInstance("0");
		Fragment fragment2 = TaskFragment.newInstance("1");
		fragments.add(fragment1);
		fragments.add(fragment2);
		vp_main.setAdapter(new MyPagerAdapter(getSupportFragmentManager(),fragments));
		
		vp_main.setOffscreenPageLimit(0);
		vp_main.setCurrentItem(0);
		vp_main.setOnPageChangeListener(new MyOnPageChangeListener());
		
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();

	
	}
	
	/** 页卡切换监听 **/
	public class MyOnPageChangeListener implements OnPageChangeListener {
		
	    public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					tv_unfinish.setBackgroundColor(getResources().getColor(R.color.white));
					tv_finish.setBackgroundColor(getResources().getColor(R.color.gray_2));
					
					break;
				case 1:
					
					tv_finish.setBackgroundColor(getResources().getColor(R.color.white));
					tv_unfinish.setBackgroundColor(getResources().getColor(R.color.gray_2));
				
					break;
				
				}
				
				currpage = arg0;
				
		}
		public void onPageScrolled(int arg0, float arg1, int arg2) {}
		public void onPageScrollStateChanged(int arg0) {}
	}
	
	
	public class MyPagerAdapter extends FragmentPagerAdapter {
		public List<Fragment> mListViews;
		public MyPagerAdapter(FragmentManager fm,List<Fragment> listViews) {
			super(fm);
			this.mListViews= listViews;
		}
		
		@Override
		public Fragment getItem(int arg0) {
			return mListViews.get(arg0);
		}
		@Override
		public int getCount() {
			return mListViews.size();
		}
		
	}
	
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;
		public MyOnClickListener(int i) {
			index = i;
		}
		public void onClick(View v) {
			vp_main.setCurrentItem(index);
			switch (index) {
			case 0:
				tv_unfinish.setBackgroundColor(getResources().getColor(R.color.white));
				tv_finish.setBackgroundColor(getResources().getColor(R.color.gray_2));
				
				break;
			case 1:
				tv_finish.setBackgroundColor(getResources().getColor(R.color.white));
				tv_unfinish.setBackgroundColor(getResources().getColor(R.color.gray_2));
				break;
			

			default:
				break;
			}
			
		}
	};
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_right:
			
			if(isPreferences.getSp().getString("m_rolelevel", "").equals("1") ||
					isPreferences.getSp().getString("m_rolelevel", "").equals("2") ||
					isPreferences.getSp().getString("m_rolelevel", "").equals("3") || 
					isPreferences.getSp().getString("m_rolelevel", "").equals("4")||
					isPreferences.getSp().getString("m_rolelevel", "").equals("5") ||
					isPreferences.getSp().getString("m_rolelevel", "").equals("6")){
				
				startActivity(new Intent(this,publishTaskActivity.class));
				
			}else{
				Function.toastMsg(this, getString(R.string.tv_no_quanxian));
			}	
			break;

		default:
			break;
		}
	}

	private long exitTime = 0;
	//按两次退出程序
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getKeyCode()==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){ 
		if(isPreferences.getSp().getBoolean("ismsg", false)){
			finish();
			isPreferences.updateSp("ismsg", false);
		}else{
			if((System.currentTimeMillis()-exitTime) > 2000){ 
				Function.toastMsg(getApplicationContext(),getString(R.string.app_exit)); 
	    		exitTime = System.currentTimeMillis();    
	    	}else{
	    		finish();
			    startActivity(new Intent(this,welcomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	    	}
		}
			
	    	return true;
	    }
		return super.onKeyDown(keyCode, event);
	}
	
}
