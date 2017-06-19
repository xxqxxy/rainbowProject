package com.rainbow.main;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;
import com.comutils.main.Function;
import com.rainbow.main.map.BNDemoGuideActivity;
import com.rainbow.main.map.overlayutil.DrivingRouteOverlay;
import com.rainbow.main.map.overlayutil.OverlayManager;
import com.rainbow.main.map.overlayutil.WalkingRouteOverlay;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class LookMapActivity extends Activity implements OnClickListener , OnGetRoutePlanResultListener,BaiduMap.OnMapClickListener{

	TextView tv_gps;
	    BaiduMap mBaiduMap;
	    MapView mMapView;
	    LocationClient mLocClient;
	    public MyLocationListenner myListener = new MyLocationListenner();
	    
	    boolean isFirstLoc = true;
	    float city_x;
	    float city_y;
	    String st_longitudee6,st_latitudee6;
	    String addr ="";
	    private static final String APP_FOLDER_NAME = "RainBowDemo";
	    private String mSDCardPath = null;
	    public static final String ROUTE_PLAN_NODE = "routePlanNode";
	    public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
	    public static final String RESET_END_NODE = "resetEndNode";
	    public static final String VOID_MODE = "voidMode";

	    private final static String authBaseArr[] =
	            { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION };
	    private final static String authComArr[] = { Manifest.permission.READ_PHONE_STATE };
	    private final static int authBaseRequestCode = 1;
	    private final static int authComRequestCode = 2;
	    
	    private boolean hasInitSuccess = false;
	    private boolean hasRequestComAuth = false;
	    public static List<Activity> activityList = new LinkedList<Activity>();
	    
	    //搜索路线
	    // 搜索相关
	    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
	    int nodeIndex = -1; // 节点索引,供浏览节点时使用
	    DrivingRouteResult nowResultd  = null;//驾车结果
	    
	   WalkingRouteResult nowReslutw = null;
	    
	    RouteLine route = null;
	    OverlayManager routeOverlay = null;
	    TextView popupText = null;
	    
	    TextView tv_detail;
	    
	   public static final List<DrivingRouteLine> drivingRouteLines = new ArrayList<DrivingRouteLine>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityList.add(this);
		setContentView(R.layout.home2);
		
		
		Intent  i = getIntent();
		Bundle b =  i.getExtras();
		
		city_x = b.getFloat("city_x");
		city_y = b.getFloat("city_y");
		st_latitudee6 =b.getString("st_latitudee6");
		st_longitudee6 = b.getString("st_longitudee6");
		
		Log.i("soso", "地理坐标："+city_x+","+city_y+","+st_latitudee6+","+st_longitudee6);
		findViewById(R.id.tv_back).setOnClickListener(this);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getString(R.string.tv_select_map));
		
		tv_gps = (TextView) findViewById(R.id.tv_gps);
		
		tv_gps.setOnClickListener(this);
		
		
		 // 地图初始化
		mMapView = (MapView) findViewById(R.id.mMapView);
        
        mBaiduMap = mMapView.getMap();
        
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); 
        
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        
        
        
        /**
         * 路线规划
         * 
         * */
        LatLng latLng = new LatLng(city_y, city_x);//开始纬度  精度   
        LatLng latLng2 = new LatLng(Double.parseDouble(st_latitudee6),Double.parseDouble(st_longitudee6));////结束纬度  精度
        PlanNode sNode = PlanNode.withLocation(latLng);
        PlanNode eNode = PlanNode.withLocation(latLng2);
        //驾车搜索WalkingRoutePlanOption
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(sNode).to(eNode));
//        
//        //导航初始化
        BNOuterLogUtil.setLogSwitcher(true);
        if (initDirs()) {
            initNavi();
        }
//        
//        
        
        
        tv_detail = (TextView) findViewById(R.id.tv_detail);
        tv_detail.setOnClickListener(this);
	}
	

	
	 
	public class DemoRoutePlanListener implements RoutePlanListener {
	 
	    private BNRoutePlanNode mBNRoutePlanNode = null;
	 
	    
	    public DemoRoutePlanListener(BNRoutePlanNode node) {
	        mBNRoutePlanNode = node;
	    }
	    @Override
	    public void onJumpToNavigator() {
	        /*
	         * 设置途径点以及resetEndNode会回调该接口
	         */
	 
	        for (Activity ac : activityList) {
	 
	            if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {
	 
	                return;
	            }
	        }
	        System.out.println("经纬度 BD09LL 2");
	        Intent intent = new Intent(LookMapActivity.this, BNDemoGuideActivity.class);
	        Bundle bundle = new Bundle();
	        bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
	        intent.putExtras(bundle);
	        startActivity(intent);
	 
	    }
	 
	    @Override
	    public void onRoutePlanFailed() {
	        Toast.makeText(LookMapActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
	    }
	}
	
	
	/**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {
        if (route == null || route.getAllStep() == null) {
            return;
        }
        if (nodeIndex == -1 && v.getId() == R.id.pre) {
            return;
        }
        // 设置节点索引
        if (v.getId() == R.id.next) {
            if (nodeIndex < route.getAllStep().size() - 1) {
                nodeIndex++;
            } else {
                return;
            }
        } else if (v.getId() == R.id.pre) {
            if (nodeIndex > 0) {
                nodeIndex--;
            } else {
                return;
            }
        }
        // 获取节结果信息
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = route.getAllStep().get(nodeIndex);
        if (step instanceof DrivingRouteLine.DrivingStep) {
            nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
            nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
        } else if (step instanceof WalkingRouteLine.WalkingStep) {
            nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
            nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
        } else if (step instanceof TransitRouteLine.TransitStep) {
            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
        } else if (step instanceof BikingRouteLine.BikingStep) {
            nodeLocation = ((BikingRouteLine.BikingStep) step).getEntrance().getLocation();
            nodeTitle = ((BikingRouteLine.BikingStep) step).getInstructions();
        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }
        // 移动节点至中心
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        popupText = new TextView(LookMapActivity.this);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);
        mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));

    }

	
	
	
    private boolean hasBasePhoneAuth() {
        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean hasCompletePhoneAuth() {
        PackageManager pm = this.getPackageManager();
        for (String auth : authComArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
	/**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    break;
                }
                default:
                    break;
            }
        }
    };
	  private boolean initDirs() {
	        mSDCardPath = getSdcardDir();
	        if (mSDCardPath == null) {
	            return false;
	        }
	        File f = new File(mSDCardPath, APP_FOLDER_NAME);
	        if (!f.exists()) {
	            try {
	                f.mkdir();
	            } catch (Exception e) {
	                e.printStackTrace();
	                return false;
	            }
	        }
	        return true;
	    }

	    String authinfo = null;
    @SuppressLint("NewApi")
		private void initNavi() {

	        BNOuterTTSPlayerCallback ttsCallback = null;

	        // 申请权限
	        if (android.os.Build.VERSION.SDK_INT >= 23) {

	            if (!hasBasePhoneAuth()) {

	                this.requestPermissions(authBaseArr, authBaseRequestCode);
	                return;

	            }
	        }

	        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new NaviInitListener() {
	            @Override
	            public void onAuthResult(int status, String msg) {
	                if (0 == status) {
//	                    authinfo = "key校验成功!";
	                } else {
//	                    authinfo = "key校验失败, " + msg;
	                }
	                LookMapActivity.this.runOnUiThread(new Runnable() {

	                    @Override
	                    public void run() {
	                    }
	                });
	            }

	            public void initSuccess() {
	                hasInitSuccess = true;
	                initSetting();
	            }

	            public void initStart() {
	            }

	            public void initFailed() {
	            }

	        }, null, ttsHandler, ttsPlayStateListener);

	    }
	    
	    
	    /**
	     * 内部TTS播报状态回调接口
	     */
	    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

	        @Override
	        public void playEnd() {
	        }

	        @Override
	        public void playStart() {
	        }
	    };

	    public void showToastMsg(final String msg) {
	        LookMapActivity.this.runOnUiThread(new Runnable() {

	            @Override
	            public void run() {
	            }
	        });
	    }

	    
	    
	    private String getSdcardDir() {
	        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
	            return Environment.getExternalStorageDirectory().toString();
	        }
	        return null;
	    }

	    private CoordinateType mCoordinateType = null;

    @SuppressLint("NewApi")
		private void routeplanToNavi(CoordinateType coType) {
	        mCoordinateType = coType;
	        if (!hasInitSuccess) {
//	            Toast.makeText(LookMapActivity.this, "还未初始化!", Toast.LENGTH_SHORT).show();
	        }
	        // 权限申请
	        if (android.os.Build.VERSION.SDK_INT >= 23) {
	            // 保证导航功能完备
	            if (!hasCompletePhoneAuth()) {
	                if (!hasRequestComAuth) {
	                    hasRequestComAuth = true;
	                    this.requestPermissions(authComArr, authComRequestCode);
	                    return;
	                } else {
//	                    Toast.makeText(LookMapActivity.this, "没有完备的权限!", Toast.LENGTH_SHORT).show();
	                }
	            }

	        }
	        BNRoutePlanNode sNode = null;
	        BNRoutePlanNode eNode = null;
	        switch (coType) {
	            case GCJ02: {
	                sNode = new BNRoutePlanNode(116.30142, 40.05087, "百度大厦", null, coType);
	                eNode = new BNRoutePlanNode(116.39750, 39.90882, "北京天安门", null, coType);
	                break;
	            }
	            case WGS84: {
	                sNode = new BNRoutePlanNode(116.300821, 40.050969, "百度大厦", null, coType);
	                eNode = new BNRoutePlanNode(116.397491, 39.908749, "北京天安门", null, coType);
	                break;
	            }
	            case BD09_MC: {
	                sNode = new BNRoutePlanNode(12947471, 4846474, "百度大厦", null, coType);
	                eNode = new BNRoutePlanNode(12958160, 4825947, "北京天安门", null, coType);
	                break;
	            }
	            case BD09LL: {//30.253968, 120.208980
	            	System.out.println("经纬度 BD09LL");
	                sNode = new BNRoutePlanNode(city_x, city_y, addr, null, coType);
	                eNode = new BNRoutePlanNode(Double.parseDouble(st_longitudee6),Double.parseDouble(st_latitudee6), "重庆永川", null, coType);
	                break;
	            }
	            default:
	                ;
	        }
	        if (sNode != null && eNode != null) {
	            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
	            list.add(sNode);
	            list.add(eNode);
	            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
	        }
	    }

	    private void initSetting() {
	        // BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
	        BNaviSettingManager
	                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
	        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
	        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
	        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
	    }

	    private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

	        @Override
	        public void stopTTS() {
	            Log.e("test_TTS", "stopTTS");
	        }

	        @Override
	        public void resumeTTS() {
	            Log.e("test_TTS", "resumeTTS");
	        }

	        @Override
	        public void releaseTTSPlayer() {
	            Log.e("test_TTS", "releaseTTSPlayer");
	        }

	        @Override
	        public int playTTSText(String speech, int bPreempt) {
	            // TODO Auto-generated method stub
	            Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);

	            return 1;
	        }

	        @Override
	        public void phoneHangUp() {
	            Log.e("test_TTS", "phoneHangUp");
	        }

	        @Override
	        public void phoneCalling() {
	            Log.e("test_TTS", "phoneCalling");
	        }

	        @Override
	        public void pauseTTS() {
	            Log.e("test_TTS", "pauseTTS");
	        }

	        @Override
	        public void initTTSPlayer() {
	            Log.e("test_TTS", "initTTSPlayer");
	        }

	        @Override
	        public int getTTSState() {
	            Log.e("test_TTS", "getTTSState");
	            return 1;
	        }
	    };

		@SuppressLint("NewApi")
		@Override
	    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
	        // TODO Auto-generated method stub
	        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	        if (requestCode == authBaseRequestCode) {
	            for (int ret : grantResults) {
	                if (ret == 0) {
	                    continue;
	                } else {
//	                    Toast.makeText(LookMapActivity.this, "缺少导航基本的权限!", Toast.LENGTH_SHORT).show();
	                    return;
	                }
	            }
	            initNavi();
	        } else if (requestCode == authComRequestCode) {
	            for (int ret : grantResults) {
	                if (ret == 0) {
	                    continue;
	                }
	            }
	            routeplanToNavi(mCoordinateType);
	        }

	    }
	
	
	
	/**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
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
    @Override
    protected void onPause() {
    	mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
    	mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
      
        // 关闭定位图层
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_back:
			finish();
			break;
		case R.id.tv_gps:
			System.out.println("经纬度：："+city_y+","+city_x);
			routeplanToNavi(CoordinateType.BD09LL);
			break;
		default:
			break;
		}
	}
	
	
	
	/**
	 * 
	 * 路线规划
	 * @param arg0
	 */
	//骑行
	@Override
	public void onGetBikingRouteResult(BikingRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}
	//驾车
	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(LookMapActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;

            if (result.getRouteLines().size() > 1 ) {
                nowResultd = result;
                
                
                for (int i = 0; i < result.getRouteLines().size(); i++) {
					drivingRouteLines.add(result.getRouteLines().get(i));
				}
                route = result.getRouteLines().get(0);
                //多少米
                 int distance = route.getDistance();
                 if(distance<1000){
                	 ((TextView)findViewById(R.id.tv_distance)).setText(distance+"米");
                 }else{
                	 float l_distance =  distance/1000 ;
                	 ((TextView)findViewById(R.id.tv_distance)).setText(l_distance+"公里");
                 }
                 int time =route.getDuration();
                 if ( time / 3600 == 0 ) {
                	 ((TextView)findViewById(R.id.tv_time)).setText(time / 60 + "分钟");
                 } else {
                	 ((TextView)findViewById(R.id.tv_time)).setText( time / 3600 + "小时" + (time % 3600) / 60 + "分钟" );
                 }
                 
                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
                routeOverlay = overlay;
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();

            } else if ( result.getRouteLines().size() == 1 ) {
                route = result.getRouteLines().get(0);
              //多少米
                int distance = route.getDistance();
                if(distance<1000){
               	 ((TextView)findViewById(R.id.tv_distance)).setText(distance+"米");
                }else{
               	 float l_distance =  distance/1000 ;
               	 ((TextView)findViewById(R.id.tv_distance)).setText(l_distance+"公里");
                }
                int time =route.getDuration();
                if ( time / 3600 == 0 ) {
               	 ((TextView)findViewById(R.id.tv_time)).setText(time / 60 + "分钟");
                } else {
               	 ((TextView)findViewById(R.id.tv_time)).setText( time / 3600 + "小时" + (time % 3600) / 60 + "分钟" );
                }
                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
                routeOverlay = overlay;
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }

        }
	}
	//公交结果
	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}
	//步行
	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
		 if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
	            Toast.makeText(LookMapActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
	        }
	        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
	            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
	            // result.getSuggestAddrInfo()
	            return;
	        }
	        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
	            nodeIndex = -1;
	            if(result.getRouteLines().size()>1){
	            	nowReslutw = result;
	            	
	            }else if ( result.getRouteLines().size() == 1 ) {
	            	route = result.getRouteLines().get(0);
	            	//多少米
	                 int distance = route.getDistance();
	                 if(distance<1000){
	                	 ((TextView)findViewById(R.id.tv_distance)).setText(distance+"米");
	                 }else{
	                	 float l_distance =  distance/1000 ;
	                	 ((TextView)findViewById(R.id.tv_distance)).setText(l_distance+"公里");
	                 }
	                 int time =route.getDuration();
	                 if ( time / 3600 == 0 ) {
	                	 ((TextView)findViewById(R.id.tv_time)).setText(time / 60 + "分钟");
	                 } else {
	                	 ((TextView)findViewById(R.id.tv_time)).setText( time / 3600 + "小时" + (time % 3600) / 60 + "分钟" );
	                 }
		            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
		            mBaiduMap.setOnMarkerClickListener(overlay);
		            routeOverlay = overlay;
		            overlay.setData(result.getRouteLines().get(0));
		            overlay.addToMap();
		            overlay.zoomToSpan();
	            }
	            
	            
	        }		
	}


	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		int pos = data.getIntExtra("position", 0);
		switch (requestCode) {
		case 2:
//			Function.toastMsg(getApplicationContext(), "改变路线？");
			 route = drivingRouteLines.get(pos);
             DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
             mBaiduMap.setOnMarkerClickListener(overlay);
             routeOverlay = overlay;
             overlay.setData( drivingRouteLines.get(pos));
             overlay.addToMap();
             overlay.zoomToSpan();
			break;

		default:
			break;
		}
	}

	 private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

	        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
	            super(baiduMap);
	        }

	        @Override
	        public BitmapDescriptor getStartMarker() {
	                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
	        }

	        @Override
	        public BitmapDescriptor getTerminalMarker() {
	                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
	        }
	    }

	 // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
        }
    }


	@Override
	public void onMapClick(LatLng arg0) {
		 mBaiduMap.hideInfoWindow();
	}
	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		return false;
	}




	@Override
	public void onGetIndoorRouteResult(IndoorRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void onGetMassTransitRouteResult(MassTransitRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}
}
