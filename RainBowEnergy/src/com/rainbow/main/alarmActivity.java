package com.rainbow.main;

import java.util.ArrayList;
import java.util.List;

import com.comutils.main.SharePreferences;
import com.comutils.rain_view.ViewPager;
import com.comutils.rain_view.ViewPager.OnPageChangeListener;
import com.rainbow.main.fragment.AlarmFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

@SuppressLint("NewApi")
public class alarmActivity extends FragmentActivity implements OnClickListener {
	SharePreferences isPrefences;
	TextView tv_back,tv_title,tv_untreated,tv_processed;
	ViewPager vp_main;
	int currpage = 1;
	List<Fragment> fragments;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm);
		initView();
		InitViewPager();
	}
	
	private void initView() {
		isPrefences = new SharePreferences(this);
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_untreated  = (TextView) findViewById(R.id.tv_untreated);
		tv_processed = (TextView) findViewById(R.id.tv_processed);
		tv_back.setOnClickListener(this);
		tv_title.setText(getString(R.string.tv_alarm_msg));
		
		tv_untreated.setOnClickListener(new MyOnClickListener(0));
		tv_processed.setOnClickListener(new MyOnClickListener(1));
		
		
	}
	
	
	private void InitViewPager() {
		vp_main = (ViewPager) findViewById(R.id.vp_main);
		fragments = new ArrayList<Fragment>();
		Fragment fragment1 = AlarmFragment.newInstance("0");
		Fragment fragment2 = AlarmFragment.newInstance("1");
		fragments.add(fragment1);
		fragments.add(fragment2);
		vp_main.setAdapter(new MyPagerAdapter(getSupportFragmentManager(),fragments));
		vp_main.setOffscreenPageLimit(0);
		vp_main.setCurrentItem(0);
		vp_main.setOnPageChangeListener(new MyOnPageChangeListener());
		
	}
	
	
	
	/** 页卡切换监听 **/
	public class MyOnPageChangeListener implements OnPageChangeListener {

		
	    @SuppressWarnings("deprecation")
		public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					tv_untreated.setBackgroundColor(getResources().getColor(R.color.white));
					tv_processed.setBackgroundColor(getResources().getColor(R.color.gray_2));
					
					break;
				case 1:
					
					tv_processed.setBackgroundColor(getResources().getColor(R.color.white));
					tv_untreated.setBackgroundColor(getResources().getColor(R.color.gray_2));
				
					break;
				
				}
				
				currpage = arg0;
				
//				initView(listViews.get(currpage),currpage);
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
		@SuppressWarnings("deprecation")
		public void onClick(View v) {
			vp_main.setCurrentItem(index);
			switch (index) {
			case 0:
				tv_untreated.setBackgroundColor(getResources().getColor(R.color.white));
				tv_processed.setBackgroundColor(getResources().getColor(R.color.gray_2));
				
				break;
			case 1:
				tv_processed.setBackgroundColor(getResources().getColor(R.color.white));
				tv_untreated.setBackgroundColor(getResources().getColor(R.color.gray_2));
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
			finish();
			break;
		default:
			break;
		}
	}

}
