package com.rainbow.main.map;

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
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;
import com.comutils.main.Function;
import com.comutils.main.SharePreferences;
import com.comutils.net.HttpUtil;
import com.comutils.util.UrlUtil;
import com.rainbow.main.R;
import com.rainbow.main.alarmDesActivity;
import com.rainbow.main.data.second.SecondActivity;
import com.rainbow.main.function.comFunction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * implements OnGetDistricSearchResultListener
 * 32:DC:70:FA:A8:A6:75:55:C1:C0:DF:C5:3F:8E:34:21:D4:9C:40:E1
 * 2E:07:FE:8E:CC:28:79:2A:9F:3D:DC:0B:8D:D5:A4:BD:57:6B:42:63
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 */
public class LocationDemo extends Activity  {

    // 定位相关
//    LocationClient mLocClient;
//    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;

    TextureMapView mTexturemap;
    BaiduMap mBaiduMap;
	TextView tv_reset;
    String warn_id,warn_time,warn_evnt,warn_sta;

    // UI相关
    boolean isFirstLoc = true; // 是否首次定位

    
    float city_x ,city_y;
    String addr ="";
    
    PopupWindow popupWindow = null;
    
    //覆盖物
    
    private Marker mMarkerA =null;
    private Marker mMarkerB;
    private Marker mMarkerC;
    private Marker mMarkerD;
    
    ArrayList<Marker> markerlist=null;
    ArrayList<LatLng> latlist = null;
    ArrayList<MarkerOptions> moptionlist = null;
    
    ArrayList<Marker> markerlist2=null;
    ArrayList<LatLng> latlist2 = null;
    ArrayList<MarkerOptions> moptionlist2 = null;
    
    
    
    AlarmTask iAlarmTask; 
    
 // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdA = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);
    BitmapDescriptor bdB = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);
    BitmapDescriptor bdC = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);
    BitmapDescriptor bdD = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);
    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);
    private InfoWindow mInfoWindow;
    
    ExpandableListView expand_listView;
    
    List<HashMap<String, Object>> olist1 = null;

    SharePreferences isPreferences;
    private DistrictSearch mDistrictSearch;
    
    Marker markk[] = new Marker[5];
    LinearLayout ll_bar;
    
    List<HashMap<String, Object>> prolist;
    List<HashMap<String, Object>> citylist;
    List<HashMap<String, Object>> sitelist;
    
    CityListTask iCityListTask =null;
    List<HashMap<String, Object>> citylist2 = null;
    
    SiteListTask iSiteListTask = null;
    List<HashMap<String, Object>> sitelist2= null;
    UrlUtil mUrlUtil;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
       
        mUrlUtil = UrlUtil.getInstance();
        isPreferences = new SharePreferences(this);
        mCurrentMode = LocationMode.NORMAL;
        // 地图初始化
        mTexturemap = (TextureMapView) findViewById(R.id.mTexturemap);
        mBaiduMap = mTexturemap.getMap();
        //隐藏 地图上的放大缩小按钮
        mTexturemap.showZoomControls(false);
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); 
//        mBaiduMap.setOnMapStatusChangeListener(this);
//        mDistrictSearch = DistrictSearch.newInstance();
//	    mDistrictSearch.setOnDistrictSearchListener(this);
        
        prolist = new ArrayList<HashMap<String,Object>>();
        citylist = new ArrayList<HashMap<String,Object>>();
        sitelist = new ArrayList<HashMap<String,Object>>();
        
        ll_bar  = (LinearLayout) findViewById(R.id.ll_bar);

        tv_reset = (TextView) findViewById(R.id.tv_reset);
        tv_reset.setOnClickListener(OnResetClickListeneer);

        citylist2 = new ArrayList<HashMap<String,Object>>();
		getCity2();
       
        //获取告警信息  是否有告警信息，如果有就显示，没有就隐藏
        getAlarm();
       
        findViewById(R.id.ll_pop).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LocationDemo.this,alarmDesActivity.class)
						.putExtra("warn_id", warn_id)
						.putExtra("warn_sta", warn_sta));
				
			}
		});
    
        
        //点击站点名 
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
        	@SuppressLint("NewApi")
			public boolean onMarkerClick(final Marker marker) {
        		final Button button = new Button(getApplicationContext());
                 button.setBackgroundResource(R.drawable.bg_cricle2);
                 button.setTextColor(getResources().getColor(R.color.black));
             		if(markerlist!=null && markerlist.size()!=0){
		            		for ( int i = 0; i < markerlist.size(); i++) {
		            			  OnInfoWindowClickListener listener = null;
		            			if(marker == markerlist.get(i)){
		                			Log.i("", "setOnMarkerClickListener :  markerlist ");
		                			final String city_name = citylist2.get(i).get("city_name")+""; 
		                			final double city_longitudee6 = Double.parseDouble(citylist2.get(i).get("city_longitudee6")+"");
		                			final double city_latitudee6 = Double.parseDouble(citylist2.get(i).get("city_latitudee6")+"");
		                			final float city_zoom = Float.parseFloat(citylist2.get(i).get("city_zoom")+"");
		                			final String city_id = citylist2.get(i).get("city_id")+"";
		                			button.setText(citylist2.get(i).get("city_name")+"");
		                			listener = new OnInfoWindowClickListener() {
										
										@Override
										public void onInfoWindowClick() {
											clearOverlay();
		                                	markerlist = null;
		                                	moptionlist = null;
		                                	latlist= null;
		                                	sitelist2 = new ArrayList<HashMap<String,Object>>();
		                                	sy_sitelist2(city_id);
		                                	
		                                	LatLng point = new LatLng(city_latitudee6, city_longitudee6);
		                      				MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(point);
		                      				mBaiduMap.setMapStatus(mapStatusUpdate);
		                      				MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(city_zoom);
		                      				mBaiduMap.animateMapStatus(u);
		                      				mBaiduMap.hideInfoWindow();
										}
									};
		                			
									 LatLng ll = marker.getPosition();
					                 mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -Function.dip2px(LocationDemo.this, 32),listener);
					                 mBaiduMap.showInfoWindow(mInfoWindow);		
		                		}
		                		
		    				}
		            	}else if(markerlist2!= null || markerlist2.size()!=0){
		            		for ( int i = 0; i < markerlist2.size(); i++) {
		            		
		            			if(marker == markerlist2.get(i)){
		                			 final int positon = i;
		                			button.setText(sitelist2.get(i).get("st_name")+"");
		                		 OnInfoWindowClickListener	listener = new OnInfoWindowClickListener() {
										
										@Override
										public void onInfoWindowClick() {
											startActivity(new Intent(LocationDemo.this,SecondActivity.class)
			                                   		   .putExtra("st_id", sitelist2.get(positon).get("st_id")+"")
			                                   		   .putExtra("st_type", sitelist2.get(positon).get("st_type")+""));
			                                 	mBaiduMap.hideInfoWindow();	
			                                 
										}
									};
	                                 	LatLng ll = marker.getPosition();
	    				                 mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -Function.dip2px(LocationDemo.this, 32),listener);
	    				                 mBaiduMap.showInfoWindow(mInfoWindow);	
		                		}
		    				} 
		            	}
				  return true;
            }
        });
    
		 MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(5f);
        mBaiduMap.animateMapStatus(u);
        
    } 
    
    
    private void sy_sitelist2(String city_id){
    	if(Function.isWiFi_3G(this)){
    		if(iSiteListTask== null){
    			iSiteListTask = new SiteListTask();
    			iSiteListTask.execute(city_id);
    		}
    	}else{
    		Function.toastMsg(this, getString(R.string.tv_not_netlink));
    	}
    }
   
    private class SiteListTask extends AsyncTask<String, Void, String>{
    	
    	JSONArray jarray = null;
    	JSONObject jsonj = null;
		List<NameValuePair> paramsList = null;
    	String errorString = "";
    	int errorCode = 0;
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		errorString = null;
    		paramsList = new ArrayList<NameValuePair>();
    		paramsList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
    		paramsList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));

		}
    	
		@Override
		protected String doInBackground(String... params) {
			paramsList.add(new BasicNameValuePair("city_id", params[0]));
			String result = HttpUtil.queryStringForPost(mUrlUtil.sitelist2, paramsList);
			Log.i("soso", "tag11 == "+result);
			if(result.equals("601")){
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
				jarray = new JSONArray(jsonj.getString("data"));
				for (int i = 0; i < jarray.length(); i++) {
					HashMap<String, Object> hashMap = new HashMap<String, Object>();
					hashMap.put("st_id", jarray.getJSONObject(i).getString("st_id").replace("null", ""));
					hashMap.put("st_name", jarray.getJSONObject(i).getString("st_name").replace("null", ""));
					hashMap.put("st_type", jarray.getJSONObject(i).getString("st_type").replace("null", ""));
					hashMap.put("st_latitudee6", jarray.getJSONObject(i).getString("st_latitudee6").replace("null", ""));
					hashMap.put("st_longitudee6", jarray.getJSONObject(i).getString("st_longitudee6").replace("null", ""));
					sitelist2.add(hashMap);
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
			
			iSiteListTask = null;
			if(errorString == null){
				initOverlay4();
			}else{
				Function.toastMsg(LocationDemo.this, errorString);
				comFunction.outtoLogin(LocationDemo.this, errorString, errorCode,isPreferences);
				errorString = null;
			}
			
		}
		
    }
    
    private void getCity2(){
    	if(Function.isWiFi_3G(this)){
    		if(iCityListTask== null){
    			iCityListTask = new CityListTask();
    			iCityListTask.execute();
    		}
    	}else{
    		Function.toastMsg(this, getString(R.string.tv_not_netlink));
    	}
    }
   
    private class CityListTask extends AsyncTask<String, Void, String>{

    	JSONArray jarray = null;  
    	JSONObject jsonj= null;
    	List<NameValuePair> paramsList = null;
    	 String errorString = "";
    	 int errorCode = 0;
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		errorString = null;
    		paramsList = new ArrayList<NameValuePair>();
    		paramsList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
    		paramsList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
    	}
    	
		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.getcity, paramsList);
			
			if(result == "601"){
				errorString = "601";
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

				jarray = new JSONArray(jsonj.getString("data"));
				for (int i = 0; i < jarray.length(); i++) {
					HashMap<String, Object> hashMap = new HashMap<String, Object>();
					hashMap.put("city_id", jarray.getJSONObject(i).getString("city_id").replace("null", ""));
					hashMap.put("city_zoom", jarray.getJSONObject(i).getString("city_zoom").replace("null", ""));
					hashMap.put("city_name", jarray.getJSONObject(i).getString("city_name").replace("null", ""));
					hashMap.put("city_longitudee6", jarray.getJSONObject(i).getString("city_longitudee6").replace("null", ""));
					hashMap.put("city_latitudee6", jarray.getJSONObject(i).getString("city_latitudee6").replace("null", ""));
					citylist2.add(hashMap);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
			return null;
		}
    	
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			iCityListTask = null;
			if(errorString == null){
				clearOverlay();
				initOverlay();
			}else{
				Function.toastMsg(LocationDemo.this ,errorString);
				comFunction.outtoLogin(LocationDemo.this, errorString, errorCode,isPreferences);
				errorString = null;
			}
		}
    }
    
    /**
     * 一键还原
     */
    OnClickListener OnResetClickListeneer = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			/**
			 * 1.地图缩放
			 * 2.清除之前的覆盖物
			 * 3.初始化 覆盖物
			 */
			clearOverlay();
			initOverlay();
			 MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(5f);
	         mBaiduMap.animateMapStatus(u);
		}
	};
    
	private void getAlarm() {
		if(Function.isWiFi_3G(this)){
			if(iAlarmTask == null){
				iAlarmTask  = new AlarmTask();
				iAlarmTask.execute();
			}
		}else{
			Function.toastMsg(this, getString(R.string.tv_not_netlink));
		}
	}
	
	
	private class AlarmTask extends AsyncTask<String, Void, String>{

		JSONObject jsonj;
		List<NameValuePair> paramlist = null;
		String errorString;int errorCode;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			errorString = null;
			paramlist = new ArrayList<NameValuePair>();
			paramlist.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
			paramlist.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
		}
		
		
		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.warning_one, paramlist);
			Log.i("", "alarm data = "+result);
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
				jsonj = new JSONObject(jsonj.getString("data"));
				Log.i("", "alarm "+jsonj);
				
				warn_id = jsonj.getString("warn_id").toString().replace("null", "");
				warn_time = jsonj.getString("warn_time").toString().replace("null", "");
				warn_evnt = jsonj.getString("warn_evnt").toString().replace("null", "");
				warn_sta = jsonj.getString("warn_sta").toString().replace("null", "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			iAlarmTask = null;
			if(errorString==null){
				if(warn_time!=null){
					findViewById(R.id.ll_pop).setVisibility(View.VISIBLE);
					((TextView)findViewById(R.id.tv_warn_time)).setText(warn_time);
					((TextView)findViewById(R.id.tv_warn_evnt)).setText(warn_evnt);
					
				}else{
					findViewById(R.id.ll_pop).setVisibility(View.GONE);
				}
			}else{
				Function.toastMsg(LocationDemo.this, errorString);
				comFunction.outtoLogin(LocationDemo.this, errorString, errorCode,isPreferences);
				errorString = null;
			}
			
		}
		
	}
	
    
    //初始化 覆盖物
    public void initOverlay() {
        // add marker overlay
    	latlist = new ArrayList<LatLng>();
    	
    	for (int i = 0; i < citylist2.size(); i++) {
    		  LatLng ll = new LatLng(Double.parseDouble(citylist2.get(i).get("city_latitudee6")+""),Double.parseDouble(citylist2.get(i).get("city_longitudee6")+""));//纬度  精度
    		  latlist.add(ll);
		}
    	
    	moptionlist = new ArrayList<MarkerOptions>();
    	markerlist = new ArrayList<Marker>();
    	for (int i = 0; i < latlist.size(); i++) {
    		MarkerOptions ooA = new MarkerOptions().position(latlist.get(i)).icon(bdA)
                    .zIndex(9).draggable(true);
    		moptionlist.add(ooA);
		}

    	
    	for (int i = 0; i < moptionlist.size(); i++) {
    	 Marker mMarkerA = (Marker) (mBaiduMap.addOverlay(moptionlist.get(i)));
    		markerlist.add(mMarkerA);
		}
    	
        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(bdA);
        giflist.add(bdB);
        giflist.add(bdC);
        giflist.add(bdD);
        // add ground overlay
        mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {
            }

            public void onMarkerDragEnd(Marker marker) {
            }

            public void onMarkerDragStart(Marker marker) {
            }
        });
    }
    
    
    
    //初始化 覆盖物
    public void initOverlay4() {
        // add marker overlay
    	latlist2 = new ArrayList<LatLng>();
    	
    	for (int i = 0; i < sitelist2.size(); i++) {
    		  LatLng ll = new LatLng(Double.parseDouble(sitelist2.get(i).get("st_latitudee6")+""),Double.parseDouble(sitelist2.get(i).get("st_longitudee6")+""));//纬度  精度
    		  latlist2.add(ll);
		}
    	
    	moptionlist2 = new ArrayList<MarkerOptions>();
    	markerlist2 = new ArrayList<Marker>();
    	for (int i = 0; i < latlist2.size(); i++) {
    		MarkerOptions ooA = new MarkerOptions().position(latlist2.get(i)).icon(bdA)
                    .zIndex(9).draggable(true);
    		moptionlist2.add(ooA);
		}

    	
    	for (int i = 0; i < moptionlist2.size(); i++) {
    	 Marker mMarkerA = (Marker) (mBaiduMap.addOverlay(moptionlist2.get(i)));
    		markerlist2.add(mMarkerA);
		}
    	
        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(bdA);
        giflist.add(bdB);
        giflist.add(bdC);
        giflist.add(bdD);
        // add ground overlay
        mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {
            }

            public void onMarkerDragEnd(Marker marker) {
            }

            public void onMarkerDragStart(Marker marker) {
            }
        });
    }
    

    /**
     * 清除所有Overlay
     *
     * @param view
     */
    public void clearOverlay() {
        mBaiduMap.clear();
        mMarkerA = null;
        mMarkerB = null;
        mMarkerC = null;
        mMarkerD = null;
    }

   
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mTexturemap == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            	mBaiduMap.setMyLocationData(locData);
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            
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
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());//经度
			city_x = (float) location.getLongitude();
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

    @Override
    protected void onPause() {
    	mTexturemap.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
    	mTexturemap.onResume();
//    	getAlarm();
    	/*
    	 MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(5f);
    	 mBaiduMap.animateMapStatus(u);
    	clearOverlay();
    	initOverlay();*/
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
//        mLocClient.stop();
        // 关闭定位图层
        bdA.recycle();
        bdB.recycle();
        bdC.recycle();
        bdD.recycle();
        bd.recycle();
        mTexturemap.onDestroy();
//        mTexturemap = null;
//        mDistrictSearch.destroy();
        super.onDestroy();
    }
	/*@Override
	public void onGetDistrictResult(DistrictResult districtResult) {
		if (districtResult.error == SearchResult.ERRORNO.NO_ERROR) {
            List<List<LatLng>> polyLines = districtResult.getPolylines();
          
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		
		for (List<LatLng> polyline : polyLines) {
             for (LatLng latLng : polyline) {
                 builder.include(latLng);
             }
         } 
		mBaiduMap.setMapStatus(MapStatusUpdateFactory
                 .newLatLngBounds(builder.build()));		
		}
	}*/
	
	/**
	 * 获取 缩放级别
	 */
	/*@Override
	public void onMapStatusChange(MapStatus arg0) {
		// arg0.zoom;
	}


	@Override
	public void onMapStatusChangeFinish(MapStatus arg0) {

	}


	@Override
	public void onMapStatusChangeStart(MapStatus arg0) {
	}*/
}
