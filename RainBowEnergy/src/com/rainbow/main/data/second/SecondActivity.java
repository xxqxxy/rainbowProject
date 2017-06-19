package com.rainbow.main.data.second;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.Poi;
import com.comutils.main.Function;
import com.comutils.main.SharePreferences;
import com.comutils.net.HttpUtil;
import com.comutils.rain_view.MyListView;
import com.comutils.rain_view.RoundImageViewByXfermode;
import com.comutils.util.UrlUtil;
import com.rainbow.main.LookMapActivity;
import com.rainbow.main.R;
import com.rainbow.main.RainBowApplication;
import com.rainbow.main.data.des.DataDesActivity;
import com.rainbow.main.function.comFunction;
import com.rainbow.main.widget.SiteOneSecAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SecondActivity extends Activity implements OnClickListener {
	private List<String> group;//父节点
	private List<List<String>>  child;//子
	int position,subposition;
	String st_id = "",st_latitudee6,st_longitudee6,st_name,st_type,st_maps,st_type2,st_name2;
	
	SiteInfoTask iSiteInfoTask;
	LeveloneTask iLeveloneTask;
	TextView tv_back,tv_title,tv_select_map;
	LinearLayout ll_main;
	RoundImageViewByXfermode iv_map;
	
	List<HashMap<String, Object>> olist1 =null;
	List<List<HashMap<String, Object>>> olist2 = null;

	LinearLayout ll_bar;
	 LocationClient mLocClient;
	    public MyLocationListenner myListener = new MyLocationListenner();
	    float city_x;
	    float city_y;
	    String addr ="";
	    SharePreferences isPreferences;
	    ListView mListView;
	    MyListView mPullListView;
	    SiteOneSecAdapter adapter = null;
	    UrlUtil mUrlUtil;
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.site_des);
		
		mUrlUtil = UrlUtil.getInstance();
		findViewById(R.id.iv_bg).setVisibility(View.VISIBLE);
		ll_main = (LinearLayout) findViewById(R.id.ll_main);
		ll_main.setFocusable(true);
		ll_main.setFocusableInTouchMode(true);
		ll_main.requestFocus();
		
		ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
		isPreferences = new SharePreferences(this);
		Intent i = getIntent();
		Bundle b = i.getExtras();
		st_id = b.getString("st_id");
		st_type2  = b.getString("st_type");
		st_name2 = b.getString("st_name");
		subposition = b.getInt("subposition");
		position = b.getInt("position");
		
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_back.setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(st_name2);
		tv_select_map = (TextView) findViewById(R.id.tv_select_map);
		tv_select_map.setOnClickListener(this);
		findViewById(R.id.rl_select_map).setOnClickListener(this);
		
		iv_map = (RoundImageViewByXfermode) findViewById(R.id.iv_map);
		
		getsiteInfo();
		getLevelone();

		initViewList();
		
		
		
		 mLocClient = new LocationClient(this);
		
		  // 定位初始化
       
        mLocClient.registerLocationListener(myListener);
        initLocation();
        mLocClient.start();
       
		
		
	}
	
	
	private void initViewList() {
		
		mPullListView = (MyListView) findViewById(R.id.pl_site);
		mPullListView.setDividerHeight(0);
		mPullListView.setDivider(new ColorDrawable(getResources().getColor(R.color.cr_gray1)));
		mPullListView.setDividerHeight((int)(getResources().getDisplayMetrics().density*5));
		mPullListView.setDivider(new ColorDrawable(R.color.cr_gray3));
		
		
		olist1 = new ArrayList<HashMap<String,Object>>();
		olist2 = new ArrayList<List<HashMap<String,Object>>>();
		if(st_type2.equals("2")){
			adapter = new SiteOneSecAdapter(SecondActivity.this, olist1, olist2, ((RainBowApplication)SecondActivity.this.getApplication()), onClickListener);
		}else{
			adapter = new SiteOneSecAdapter(SecondActivity.this, olist1, null, ((RainBowApplication)SecondActivity.this.getApplication()), onClickListener);
		}
		mPullListView.setAdapter(adapter);
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position=0,subposition=0;
			String[] tstring;
			if((v.getTag()).toString().indexOf("=") != -1){
				tstring = (v.getTag()).toString().split("=");
				position = Integer.parseInt(tstring[0]) ;
				subposition = Integer.parseInt(tstring[1]) ;
			}else{
				position = (Integer) v.getTag();
			}
			
			isPreferences.updateSp("site_name", st_name);
			isPreferences.updateSp("st_type", st_type2);
			isPreferences.updateSp("levlone_face", olist1.get(position).get("levlone_face")+"");
			if(st_type2.equals("2")){
				if(olist2.get(position).size() > 0){
					isPreferences.updateSp("dform_bg_img",  olist2.get(position).get(subposition).get("dform_bg_img")+"");
					isPreferences.updateSp("dform_show_states",  olist2.get(position).get(subposition).get("dform_show_states")+"");
					Log.i("soso", "isPreferences dform_show_states == "+olist2.get(position).get(subposition).get("dform_show_states")+"");
					isPreferences.updateSp("video_ip",  olist2.get(position).get(subposition).get("video_ip")+"");
					isPreferences.updateSp("video_login",  olist2.get(position).get(subposition).get("video_login")+"");
					isPreferences.updateSp("video_pass",  olist2.get(position).get(subposition).get("video_pass")+"");
					isPreferences.updateSp("video_channel",  olist2.get(position).get(subposition).get("video_channel")+"");
					isPreferences.updateSp("video_appport",  olist2.get(position).get(subposition).get("video_appport")+"");
					isPreferences.updateSp("video_appyschannel",  olist2.get(position).get(subposition).get("video_appyschannel")+"");
					isPreferences.updateSp("video_appyslicence",  olist2.get(position).get(subposition).get("video_appyslicence")+"");
					isPreferences.updateSp("video_appysvcode",  olist2.get(position).get(subposition).get("video_appysvcode")+"");
					isPreferences.updateSp("video_ip2",  olist2.get(position).get(subposition).get("video_ip2")+"");
					isPreferences.updateSp("video_login2",  olist2.get(position).get(subposition).get("video_login2")+"");
					isPreferences.updateSp("video_pass2",  olist2.get(position).get(subposition).get("video_pass2")+"");
					isPreferences.updateSp("video_channel2",  olist2.get(position).get(subposition).get("video_channel2")+"");
					isPreferences.updateSp("video_appport2",  olist2.get(position).get(subposition).get("video_appport2")+"");
				}
			}else{
				if(olist1.size()>0){
					isPreferences.updateSp("levlone_bg_img", olist1.get(position).get("levlone_bg_img")+"");
					isPreferences.updateSp("levlone_show_states", olist1.get(position).get("levlone_show_states")+"");
					Log.i("soso", "isPreferences levlone_show_states == "+olist1.get(position).get("levlone_show_states")+"");
					isPreferences.updateSp("video_ip",   olist1.get(position).get("video_ip")+"");
					isPreferences.updateSp("video_login",  olist1.get(position).get("video_login")+"");
					isPreferences.updateSp("video_pass",  olist1.get(position).get("video_pass")+"");
					isPreferences.updateSp("video_channel",  olist1.get(position).get("video_channel")+"");
					isPreferences.updateSp("video_appport",  olist1.get(position).get("video_appport")+"");
					isPreferences.updateSp("video_appyschannel", olist1.get(position).get("video_appyschannel")+"");
					isPreferences.updateSp("video_appyslicence",  olist1.get(position).get("video_appyslicence")+"");
					isPreferences.updateSp("video_appysvcode", olist1.get(position).get("video_appysvcode")+"");
					isPreferences.updateSp("video_ip2",   olist1.get(position).get("video_ip2")+"");
					isPreferences.updateSp("video_login2", olist1.get(position).get("video_login2")+"");
					isPreferences.updateSp("video_pass2",  olist1.get(position).get("video_pass2")+"");
					isPreferences.updateSp("video_channel2",  olist1.get(position).get("video_channel2")+"");
					isPreferences.updateSp("video_appport2",  olist1.get(position).get("video_appport2")+"");
				}
			}
			
			
			switch (v.getId()) {
			case R.id.ll_site:
				if(st_type2.equals("2")){
					//跳转到详情页面
					
					Intent intent = new Intent(SecondActivity.this, DataDesActivity.class);
					intent.putExtra("st_name", olist2.get(position).get(subposition).get("dform_measptname")+"");//详情 名
					intent.putExtra("dform_id", olist2.get(position).get(subposition).get("dform_id")+"");//详情类型
					/*intent.putExtra("position", subposition);
					intent.putExtra("groupPosition", position);*/
					Log.i("soso", "SecoundActivity dform_show_states == "+olist2.get(position).get(subposition).get("dform_show_states")+"");
					startActivity(intent);
				}
			
				break;
			case R.id.ll_site_one:
				if(st_type2.equals("1")){
					Intent intent = new Intent(SecondActivity.this, DataDesActivity.class);
					intent.putExtra("levlone_id", olist1.get(position).get("levlone_id")+"");//详情 名
					intent.putExtra("st_name", olist1.get(position).get("levlone_measptname")+"");
					
					/*intent.putExtra("position", position);*/
					startActivity(intent);
				}else if(st_type2.equals("2")){
					return;
				}
				break;
			default:
				break;
			}
		}
	};
	
	
	
	private void getsiteInfo() {
		if(Function.isWiFi_3G(this)){
			if(iSiteInfoTask == null){
				iSiteInfoTask = new SiteInfoTask();
				iSiteInfoTask.execute();
			}
		}else{
			ll_bar.setVisibility(View.GONE);
			Function.toastMsg(SecondActivity.this, getString(R.string.tv_not_netlink));
		}
		
	}
	
	private void getLevelone() {
		if(Function.isWiFi_3G(this)){
			if(iLeveloneTask == null){
				iLeveloneTask = new LeveloneTask();
				iLeveloneTask.execute();
			}
		}else{
			ll_bar.setVisibility(View.GONE);
			Function.toastMsg(SecondActivity.this, getString(R.string.tv_not_netlink));
		}
		
	}
	private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy
);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死  
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
mLocClient.setLocOption(option);
    }
	
	/**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null ) {
                return;
            }
            
            /**
             * 
             * 获取 经纬度
             * 
             */
            StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());//纬度
			city_y = (float) location.getLatitude();
			System.out.println("city_y="+city_y);
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());//经度
			city_x = (float) location.getLongitude();
			System.out.println("city_x="+city_x);
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());// 单位：公里每小时
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\nheight : ");
				sb.append(location.getAltitude());// 单位：米
				sb.append("\ndirection : ");
				sb.append(location.getDirection());// 单位度
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append("\ndescribe : ");
				sb.append("gps定位成功");

			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				addr = location.getAddrStr();
				// 运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
				sb.append("\ndescribe : ");
				sb.append("网络定位成功");
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				sb.append("\ndescribe : ");
				sb.append("离线定位成功，离线定位结果也是有效的");
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				sb.append("\ndescribe : ");
				sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				sb.append("\ndescribe : ");
				sb.append("网络不同导致定位失败，请检查网络是否通畅");
			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				sb.append("\ndescribe : ");
				sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
			}
			sb.append("\nlocationdescribe : ");
			sb.append(location.getLocationDescribe());// 位置语义化信息
			List<Poi> list = location.getPoiList();// POI数据
			if (list != null) {
				sb.append("\npoilist size = : ");
				sb.append(list.size());
				for (Poi p : list) {
					sb.append("\npoi= : ");
					sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
				}
			}
			
			Log.i("BaiduLocationApiDem", sb.toString());
            
            
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
	List<HashMap<String, Object>> wlist = null;
	

	
	private class LeveloneTask extends AsyncTask<String, Void, String>{
		List<NameValuePair> paramList = null;
		JSONObject jsonj = null;
		JSONArray jarray = null;
		JSONArray tarray = null;
		String errorString = "";
		int errorCode = 0;
		
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			errorString = null;
			paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("site_id", st_id));
			paramList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
			paramList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
			Log.i("SecondActivity", "st_id = "+st_id);
		}
		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.site_des, paramList);
			Log.i("", "tag 2222 = "+result);
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
				
				jarray = jsonj.getJSONArray("data");
				Log.i("", "tag 444444 = "+jarray);
				
				olist1.clear();
				olist2.clear();
				for (int i = 0; i < jarray.length(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("levlone_id", jarray.getJSONObject(i).getString("levlone_id").toString().replace("null", ""));
					map.put("levlone_measptname", jarray.getJSONObject(i).getString("levlone_measptname").toString().replace("null", ""));
					map.put("levlone_face",jarray.getJSONObject(i).getString("levlone_face").toString().replace("null", ""));
					map.put("levlone_form", jarray.getJSONObject(i).getString("levlone_form").toString().replace("null", ""));
					
					// 视频 IP
					map.put("video_ip", jarray.getJSONObject(i).getString("video_ip").toString().replace("null", ""));
				    //  视频账号
					map.put("video_login", jarray.getJSONObject(i).getString("video_login").toString().replace("null", ""));
					//   密码
					map.put("video_pass",jarray.getJSONObject(i).getString("video_pass").toString().replace("null", ""));
					//   视频通道
					map.put("video_channel", jarray.getJSONObject(i).getString("video_channel").toString().replace("null", ""));
					// app端口	
					map.put("video_appport", jarray.getJSONObject(i).getString("video_appport").toString().replace("null", ""));
					//   萤石云   通道
					map.put("video_appyschannel",jarray.getJSONObject(i).getString("video_appyschannel").toString().replace("null", ""));
					//   萤石云  序列号
					map.put("video_appyslicence", jarray.getJSONObject(i).getString("video_appyslicence").toString().replace("null", ""));
					// 萤石云  验证码
					map.put("video_appysvcode", jarray.getJSONObject(i).getString("video_appysvcode").toString().replace("null", ""));
					
					// 视频 IP
					map.put("video_ip2", jarray.getJSONObject(i).getString("video_ip2").toString().replace("null", ""));
				    //  视频账号
					map.put("video_login2", jarray.getJSONObject(i).getString("video_login2").toString().replace("null", ""));
					//   密码
					map.put("video_pass2",jarray.getJSONObject(i).getString("video_pass2").toString().replace("null", ""));
					//   视频通道
					map.put("video_channel2", jarray.getJSONObject(i).getString("video_channel2").toString().replace("null", ""));
					// app端口	
					map.put("video_appport2", jarray.getJSONObject(i).getString("video_appport2").toString().replace("null", ""));
					map.put("st_type", st_type2);
					if(st_type2.equals("2")){
					}else{
						map.put("levlone_bg_img", jarray.getJSONObject(i).getString("levlone_bg_img").toString().replace("null", ""));
						map.put("level_one_state", jarray.getJSONObject(i).getString("level_one_state").toString().replace("null", ""));
						map.put("levlone_show_states", jarray.getJSONObject(i).getString("levlone_show_states").toString().replace("null", ""));
					}
					olist1.add(map);
					
					wlist = new ArrayList<HashMap<String,Object>>();
					if(st_type2.equals("2")){
					tarray = new JSONArray(jarray.getJSONObject(i).getString("levltwo_info"));
						if(st_type2.equals("2")){
							for (int j = 0; j < tarray.length(); j++) {
								HashMap<String, Object> map2 = new HashMap<String, Object>();
								map2.put("dform_id", tarray.getJSONObject(j).getString("dform_id").toString().replace("null", ""));
								map2.put("dform_measptname", tarray.getJSONObject(j).getString("dform_measptname").toString().replace("null", ""));
								map2.put("dform_form", tarray.getJSONObject(j).getString("dform_form").toString().replace("null", ""));
								// 视频 IP
								map2.put("video_ip", tarray.getJSONObject(j).getString("video_ip").toString().replace("null", ""));
							    //  视频账号
								map2.put("video_login", tarray.getJSONObject(j).getString("video_login").toString().replace("null", ""));
								//   密码
								map2.put("video_pass",tarray.getJSONObject(j).getString("video_pass").toString().replace("null", ""));
								//   视频通道
								map2.put("video_channel", tarray.getJSONObject(j).getString("video_channel").toString().replace("null", ""));
								// app端口	
								map2.put("video_appport", tarray.getJSONObject(j).getString("video_appport").toString().replace("null", ""));
								//   萤石云   通道
								map2.put("video_appyschannel",tarray.getJSONObject(j).getString("video_appyschannel").toString().replace("null", ""));
								//   萤石云  序列号
								map2.put("video_appyslicence", tarray.getJSONObject(j).getString("video_appyslicence").toString().replace("null", ""));
								// 萤石云  验证码
								map2.put("video_appysvcode", tarray.getJSONObject(j).getString("video_appysvcode").toString().replace("null", ""));
								
								// 视频 IP
								map2.put("video_ip2", tarray.getJSONObject(j).getString("video_ip2").toString().replace("null", ""));
							    //  视频账号
								map2.put("video_login2", tarray.getJSONObject(j).getString("video_login2").toString().replace("null", ""));
								//   密码
								map2.put("video_pass2",tarray.getJSONObject(j).getString("video_pass2").toString().replace("null", ""));
								//   视频通道
								map2.put("video_channel2", tarray.getJSONObject(j).getString("video_channel2").toString().replace("null", ""));
								// app端口	
								map2.put("video_appport2", tarray.getJSONObject(j).getString("video_appport2").toString().replace("null", ""));
							
								map2.put("dform_bg_img", tarray.getJSONObject(j).getString("dform_bg_img").toString().replace("null", ""));
								map2.put("dform_show_states", tarray.getJSONObject(j).getString("dform_show_states").toString().replace("null", ""));
								map2.put("state_color", tarray.getJSONObject(j).getString("level_two_state").toString().replace("null", ""));
								
								wlist.add(map2);
							}
							olist2.add(wlist);
						}else{
							olist2 = null;
						}
					}else{
						olist2 = null;
					}
					
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
			iLeveloneTask  = null;
			if(errorString==null){
				Log.i("DataDesActivity", "olist1 = "+olist1);
				adapter.notifyDataSetChanged();
			}else{
				Function.toastMsg(SecondActivity.this, errorString);
				comFunction.outtoLogin(SecondActivity.this, errorString, errorCode,isPreferences);
				errorString = null;
			}
			ll_bar.setVisibility(View.GONE);
		}
	
	}
	
	
	private class SiteInfoTask extends AsyncTask<String, Void, String>{
		List<NameValuePair> paramList = null;
		JSONObject jsonj = null;
		JSONArray jarray = null;
		String errorString = "";
		int errorCode = 0;
		
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ll_bar.setVisibility(View.VISIBLE);
			errorString = null;
			paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("site_id", st_id));
			paramList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
			paramList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
		}
		
		
		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.site_des_info, paramList);
			Log.i("", "tag 111 = "+result);
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
				
				jarray = jsonj.getJSONArray("data");
				Log.i("", "tag 111 = "+jarray);
				st_name = jarray.getJSONObject(0).getString("st_name").toString().replace("null", "");
				st_type = jarray.getJSONObject(0).getString("st_type").toString().replace("null", "");
				st_latitudee6 = jarray.getJSONObject(0).getString("st_latitudee6").toString().replace("null", "");//纬度
				st_longitudee6 = jarray.getJSONObject(0).getString("st_longitudee6").toString().replace("null", "");//经度
				st_maps = jarray.getJSONObject(0).getString("st_maps").toString().replace("null", "");//大图
				
				return null;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			iSiteInfoTask = null;
			if(errorString==null){
				tv_title.setText(st_name);
//				if(isPreferences.getSp().getString("is_one_type", "").equals("")){
//					int wt =  (int)(getResources().getDisplayMetrics().widthPixels -100);
//					int ht = (int)((350*getResources().getDisplayMetrics().widthPixels)/640);
//					Function.setCKMap(((RainBowApplication)SecondActivity.this.getApplication()), "", iv_map, st_maps, wt, ht);
//					
//				}
				if(st_maps.equals("")){
					iv_map.setImageDrawable(getResources().getDrawable(R.drawable.icon_bg1));
				}else{
					int wt =  (int)(getResources().getDisplayMetrics().widthPixels -100);
					int ht = (int)((350*getResources().getDisplayMetrics().widthPixels)/640);
					Function.setCKMap(((RainBowApplication)SecondActivity.this.getApplication()), "", iv_map, st_maps, wt, ht);
				}
				
			}else{
				Function.toastMsg(SecondActivity.this, errorString);
				comFunction.outtoLogin(SecondActivity.this, errorString, errorCode,isPreferences);
				errorString = null;
			}
		}
		
	}
	
	
	@Override
	protected void onStop() {
		mLocClient.stop();
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		 mLocClient.stop();
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_back:
			finish();
			break;
		/*case R.id.tv_select_map:
			Intent intent = new Intent(SecondActivity.this, LookMapActivity.class);
			intent.putExtra("city_x",city_x);
			intent.putExtra("city_y", city_y);
			intent.putExtra("st_latitudee6",st_latitudee6);
			intent.putExtra("st_longitudee6", st_longitudee6);
			startActivity(intent);
			break;*/
		case R.id.rl_select_map:
			Intent intent1 = new Intent(SecondActivity.this, LookMapActivity.class);
			intent1.putExtra("city_x",city_x);
			intent1.putExtra("city_y", city_y);
			intent1.putExtra("st_latitudee6",st_latitudee6);
			intent1.putExtra("st_longitudee6", st_longitudee6);
			startActivity(intent1);
			break;
		default:
			break;
		}
		
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode()==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){ 
			finish();
	    	return true;
	    }
		return super.onKeyDown(keyCode, event);
	}

}
