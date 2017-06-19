package com.rainbow.main;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.MediaPlayer.PlayM4.Player;
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
import com.comutils.util.UrlUtil;
import com.comutils.video.util.CrashUtil;
import com.comutils.video.util.PlaySurfaceView;
import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;
import com.rainbow.main.function.comFunction;
import com.videogo.openapi.EZConstants.EZRealPlayConstants;
import com.videogo.openapi.EZPlayer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressWarnings("unused")
public class alarmDesActivity extends Activity implements OnClickListener, Callback,Handler.Callback{
	DoTask iDotask;
	private static final String TAG = "alarmDesActivity";
	TextView tv_back, tv_title, tv_right,tv_select_map, tv_send_task, tv_site, tv_device, tv_fault, tv_happen_time;
	// 定位
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	float city_x;
	float city_y;
	String addr = "";
	String warn_id = "", warn_sta = "";
	String m_role, do_state = "0", warn_stlat, warn_stlong, warn_staid, warn_point, devname, warn_evnt,warn_level="",task_id="";
	WarnDesTask iWarnDesTask;
	SharePreferences isPreferences;
	List<HashMap<String, Object>> warntimes = null;

	private LinearLayout ll_video_bar;
	private TextView tv_progress,tv_fault_level;

	ImageView iv_play, iv_bg;
	private int m_iPort = -1; // play port
	private int m_iStartChan = 1; // start channel no
	private int m_iChanNum = 0; // channel number
	private int m_iLogID = -1; // return by NET_DVR_Login_v30
	private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;

	private boolean m_bNeedDecode = true;
	private boolean m_bMultiPlay = false;
	private int m_iPlayID = -1; // return by NET_DVR_RealPlay_V30
	private int m_iPlaybackID = -1; // return by NET_DVR_PlayBackByTime
	private static PlaySurfaceView[] playView = new PlaySurfaceView[4];
	private boolean m_bStopPlayback = false;
	private LinearLayout ll_main, ll_title_one;
	private int nPort;
	private String strIP;
	private String strUser;
	private String strPsd;
	private String video_ip = "";
	private String video_login = "";
	private String video_pass = "";
	private String video_channel = "";
	private String video_apport = "";
	
	private String video_ip2 = "";
	private String video_login2 = "";
	private String video_pass2 = "";
	private String video_channel2 = "";
	private String video_apport2 = "";
	int channel = 0;

	EditText et_name;
	
	LinearLayout ll_bar;
	
	private String video_appyschannel = ""; // 通道号 是  Int
	private String video_appyslicence = ""; // 序列号  String
	private String video_appysvcode = "";   // 验证码  String
	
	
	Handler mmHandler = null;
	private EZPlayer mEZPlayer;
	private SurfaceHolder mRealPlaySh;
	SurfaceView realplay_sv;
	
	ImageView iv_switch,iv_video_bg;
	
	UrlUtil mUrlUtil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_des);
		isPreferences = new SharePreferences(this);
		mUrlUtil = UrlUtil.getInstance();

		/*
		 * 视频监控 收集日志
		 */
		
		CrashUtil crashUtil = CrashUtil.getInstance();
		crashUtil.init(this);
		
		Intent i = getIntent();
		Bundle b = i.getExtras();
		warn_id = b.getString("warn_id");
		warn_sta = b.getString("warn_sta");
		warntimes = new ArrayList<HashMap<String, Object>>();
		initView();
		et_name = (EditText) findViewById(R.id.et_name);
		iv_play = (ImageView) findViewById(R.id.iv_play);
		iv_play.setOnClickListener(this);
		iv_bg = (ImageView) findViewById(R.id.iv_video_bg);

		ll_video_bar = (LinearLayout) findViewById(R.id.ll_video_bar);
		ll_video_bar.setOnClickListener(this);
		tv_progress = (TextView) findViewById(R.id.tv_progress);
		mmHandler = new Handler(this);
		realplay_sv = (SurfaceView) findViewById(R.id.realplay_sv);
		realplay_sv.setOnClickListener(this);

		
		tv_fault_level = (TextView) findViewById(R.id.tv_fault_level);
		
		iv_switch = (ImageView) findViewById(R.id.iv_switch);
		iv_video_bg = (ImageView) findViewById(R.id.iv_video_bg);
		if (!initeSdk()) {
			this.finish();
			return;
		}
		getWarnDes();

		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		initLocation();
		mLocClient.start();

	
		
		iv_switch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(m_iPlayID>=0){
					stop_play();
				}else{
					iv_bg.setVisibility(View.VISIBLE);
					iv_play.setVisibility(View.VISIBLE);
					realplay_sv.setVisibility(View.GONE);
				}
				
				if(isPreferences.getSp().getBoolean("switch", true)){
//					Function.toastMsg(alarmDesActivity.this, "内网连接中");
					iv_switch.setImageResource(R.drawable.icon_switch_on);
					isPreferences.updateSp("switch", false);
				}else{
//					Function.toastMsg(alarmDesActivity.this, "外网连接中");
					iv_switch.setImageResource(R.drawable.icon_switch_off);
					isPreferences.updateSp("switch", true);
				}
				
				if(!video_ip.equals("") || !video_apport.equals("")){
					//注册登录设备
       			 if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID)) {
                        Log.e(TAG, " NET_DVR_Logout is failed!");
                    }
                    m_iLogID = -1;
       			
       			login_video();
				}
				
			}
		});
		
	
		
	}

	/*
	 * 初始化 视频监控的sdk
	 */
	private boolean initeSdk() {
		// init net sdk
		// 初始化sdk 一切的前提
		/*
		 * if (!HCNetSDK.getInstance().NET_DVR_Init()) { Log.e(TAG,
		 * "HCNetSDK init is failed!"); return false; }
		 */
		if (!HCNetSDK.getInstance().NET_DVR_Init()) {
			Log.e(TAG, "HCNNetSDK init is failed!");
			return false;
		}

		// 启用写日志文件 3 是 输出 err bug info 信息 true：false==》超过文件个数是否删除
		HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, getString(R.string.app_cu_path), true);
		return true;
	}

	  // @Override
    public void surfaceCreated(SurfaceHolder holder) {
    	
    	if(!video_ip.equals("") || !video_apport.equals("")){
    		 realplay_sv.getHolder().setFormat(PixelFormat.TRANSLUCENT);
// 	        realplay_sv.setBitmap(Function.getCKBitmapByUrl(DataDesActivity.this, levlone_face, dw, dw2));
 	        Log.i(TAG, "surface is created" + m_iPort);
 	        if (-1 == m_iPort) {
 	            return;
 	        }
 	        Surface surface = holder.getSurface();
 	        if (true == surface.isValid()) {
 	            if (false == Player.getInstance()
 	                    .setVideoWindow(m_iPort, 0, holder)) {
 	                Log.e(TAG, "Player setVideoWindow failed!");
 	            }
 	        }
       }else{
     		if (mEZPlayer != null) {
     			mEZPlayer.setSurfaceHold(holder);
     		}
     		mRealPlaySh = holder;
       }
    	
    }

    // @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
    }

    // @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    	if(!video_ip.equals("") || !video_apport.equals("")){
    		 Log.i(TAG, "Player setVideoWindow release!" + m_iPort);
   	        if (-1 == m_iPort) {
   	            return;
   	        }
   	        if (true == holder.getSurface().isValid()) {
   	            if (false == Player.getInstance().setVideoWindow(m_iPort, 0, null)) {
   	                Log.e(TAG, "Player setVideoWindow failed!");
   	            }
   	        }
     	  }else{
     			if (mEZPlayer != null) {
     				mEZPlayer.setSurfaceHold(null);
     			}
     			mRealPlaySh = null;
     	  }
    }



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("m_iPort", m_iPort);
		super.onSaveInstanceState(outState);
		Log.i(TAG, "onSaveInstanceState");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		m_iPort = savedInstanceState.getInt("m_iPort");
		super.onRestoreInstanceState(savedInstanceState);
		Log.i(TAG, "onRestoreInstanceState");
	}

	@Override
	protected void onResume() {
		super.onResume();
		isPreferences.updateSp("tk_type", "0");
		iv_bg.setVisibility(View.VISIBLE);
		iv_play.setVisibility(View.VISIBLE);
		realplay_sv.setVisibility(View.GONE);
		ll_video_bar.setVisibility(View.GONE);
		
	}
	
	@Override
	protected void onStop() {
		mLocClient.stop();
		super.onStop();
	}
	
	
	public void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode) {
		if (!m_bNeedDecode) {
		} else {
			if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
				if (m_iPort >= 0) {
					return;
				}
				m_iPort = Player.getInstance().getPort();
				if (m_iPort == -1) {
					Log.e(TAG, "getPort is failed with: " + Player.getInstance().getLastError(m_iPort));
					return;
				}
				Log.i(TAG, "getPort succ with: " + m_iPort);
				if (iDataSize > 0) {
					if (!Player.getInstance().setStreamOpenMode(m_iPort, iStreamMode)) // set
																						// stream
																						// mode
					{
						Log.e(TAG, "setStreamOpenMode failed");

						return;
					}
					if (!Player.getInstance().openStream(m_iPort, pDataBuffer, iDataSize, 2 * 1024 * 1024)) // open
																											// stream
					{
						Log.e(TAG, "openStream failed");
						return;
					}
					if (!Player.getInstance().play(m_iPort, realplay_sv.getHolder())) {
						Log.e(TAG, "play failed");
						Log.e(TAG, "playSound failed with error code:" + Player.getInstance().getLastError(m_iPort));
						
						new Thread(){
							public void run() {
								try {
									sleep(1000);
									mHandler.sendEmptyMessage(2);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							};
						}.start();
						return;
					}
					if (!Player.getInstance().playSound(m_iPort)) {
						Log.e(TAG, "playSound failed with error code:" + Player.getInstance().getLastError(m_iPort));
						return;
					}
				}
			} else {
				if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize)) {
					for (int i = 0; i < 4000 && m_iPlaybackID >= 0 && !m_bStopPlayback; i++) {
						if (Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize)) {
							break;
						}

						if (i % 100 == 0) {
							Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort) + ", i:"
									+ i);
						}

						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();

						}
					}
				}

			}
		}

	}

	private RealPlayCallBack getRealPlayerCbf() {
		RealPlayCallBack cbf = new RealPlayCallBack() {
			public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
				// player channel 1
				alarmDesActivity.this.processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
			}
		};
		return cbf;
	}

	private void startSinglePreview() {
		if (m_iPlaybackID >= 0) {
			Log.i(TAG, "Please stop palyback first");
			return;
		}
		RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
		if (fRealDataCallBack == null) {
			Log.e(TAG, "fRealDataCallBack object is failed!");
			return;
		}
		Log.i(TAG, "m_iStartChan:" + channel);
		NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
		 if(isPreferences.getSp().getBoolean("switch", true)){
	        	previewInfo.lChannel = Integer.parseInt(video_channel)+32;	        	
	        }else{
	        	previewInfo.lChannel = Integer.parseInt(video_channel2)+32;
	        }
		previewInfo.dwStreamType = 0; // substream
		previewInfo.bBlocked = 1;
		// HCNetSDK start preview 开始浏览
		m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(m_iLogID, previewInfo, fRealDataCallBack);
		if (m_iPlayID < 0) {
			Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
			ll_video_bar.setVisibility(View.VISIBLE);
			tv_progress.setText(getString(R.string.err_video_all).replace("%1$",  HCNetSDK.getInstance().NET_DVR_GetLastError()+""));
			return;
		}else{
			ll_video_bar.setVisibility(View.GONE);
			realplay_sv.setVisibility(View.VISIBLE);
		}
		
			
		Log.i(TAG, "NetSdk Play sucess ***********************3***************************");
	}

	private void stopSinglePreview() {
		if (m_iPlayID < 0) {
			Log.e(TAG, "m_iPlayID < 0");
			return;
		}

		// net sdk stop preview
		if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPlayID)) {
			Log.e(TAG, "StopRealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
			return;
		}

		m_iPlayID = -1;
		stopSinglePlayer();
	}

	private void stopSinglePlayer() {
		Player.getInstance().stopSound();
		// player stop play
		if (!Player.getInstance().stop(m_iPort)) {
			Log.e(TAG, "stop is failed!");
			return;
		}

		if (!Player.getInstance().closeStream(m_iPort)) {
			Log.e(TAG, "closeStream is failed!");
			return;
		}
		if (!Player.getInstance().freePort(m_iPort)) {
			Log.e(TAG, "freePort is failed!" + m_iPort);
			return;
		}
		m_iPort = -1;
	}

	/**
	 * 
	 * 浏览视频监控
	 */
	private void priview_play() {
		try {
			/*((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
            .hideSoftInputFromWindow(alarmDesActivity.this
                    .getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);*/
			if (m_iLogID < 0) {
				Log.e(TAG, "please login on device first");

				new Thread() {
					public void run() {
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} finally {
							mHandler.sendEmptyMessage(1);
						}
					};
				}.start();
				return;
			}
			
			if (m_bNeedDecode) {
				
				if (m_iChanNum > 1)// preview more than a channel
                {
               	 if (m_iPlayID < 0) {
                         startSinglePreview();
                     } else {
                         stopSinglePreview();
                         iv_bg.setVisibility(View.VISIBLE);
                         iv_play.setVisibility(View.VISIBLE);
                         realplay_sv.setVisibility(View.GONE);
                     }
               	 
                } else // preivew a channel
                {
                    if (m_iPlayID < 0) {
                        startSinglePreview();
                    } else {
                        stopSinglePreview();
                        iv_bg.setVisibility(View.VISIBLE);
                        iv_play.setVisibility(View.VISIBLE);
                        realplay_sv.setVisibility(View.GONE);
                    }
                }
			} else {

			}
		} catch (Exception err) {
			Log.e(TAG, " err error: " + err.toString());
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				ll_video_bar.setVisibility(View.VISIBLE);
				tv_progress.setText("请先执行登录操作！");
				realplay_sv.setFocusable(true);
			}else if (msg.what == 2) {
				ll_video_bar.setVisibility(View.VISIBLE);
				tv_progress.setText("播放失败，点击重试！");
				realplay_sv.setFocusable(true);
			}
		};
	};

	/**
	 * 
	 * 注册设备
	 * 
	 * @return
	 */
	private int loginDevice() {
		Log.i(TAG, "is login devices");
		int iLogID = -1;
		iLogID = loginNormalDevice();

		return iLogID;
	}

	private int loginNormalDevice() {
		// get instance
		m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
		Log.i(TAG, "m_oNetDvrDeviceInfoV30  == " + m_oNetDvrDeviceInfoV30.byStartChan);
		if (null == m_oNetDvrDeviceInfoV30) {
			Log.e(TAG, "HKNetDvrDeviceInfoV30 new is failed!");
			return -1;
		}
		
		System.out.println("stip外网:"+video_ip+":"+video_apport+":"+video_login+":"+video_pass);

		System.out.println("stip内网:"+video_ip2+":"+video_apport2+":"+video_login2+":"+video_pass2);
		if(isPreferences.getSp().getBoolean("switch", true)){
//			Function.toastMsg(alarmDesActivity.this, "外网登录中");
			//外网
			strIP = video_ip;
			nPort = Integer.parseInt(video_apport);
			strUser = video_login;
			strPsd = video_pass;
		}else{
//			Function.toastMsg(alarmDesActivity.this, "内网登录中");
			//内网
			strIP = video_ip2;
			nPort = Integer.parseInt(video_apport2);
			strUser = video_login2;
			strPsd = video_pass2;
		}

		System.out.println("stip:"+strIP+":"+nPort+":"+strUser+":"+strPsd);
		// call NET_DVR_Login_v30 to login on, port 8000 as default
		// 用户注册这个设备
		int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(strIP, nPort, strUser, strPsd, m_oNetDvrDeviceInfoV30);
		Log.i("DemoActivity", "iLogID = " + iLogID);
		if (iLogID < 0) {
			Log.e(TAG, "NET_DVR_Login is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
			return -1;
		}
		if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
			m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
			m_iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
		} else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
			m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
			m_iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
		}
		Log.i(TAG, "NET_DVR_Login is Successful!");

		return iLogID;
	}

	private ExceptionCallBack getExceptiongCbf() {
		ExceptionCallBack oExceptionCbf = new ExceptionCallBack() {
			public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
				System.out.println("recv exception, type:" + iType);
			}
		};
		return oExceptionCbf;
	}

	private void login_video() {
		Log.i(TAG, "is login ing ");
		try {
			
			if (m_iLogID < 0) {
				// login on the device
				m_iLogID = loginDevice();
				if (m_iLogID < 0) {
					Log.e(TAG, "This device logins failed!");
					return;
				} else {
					System.out.println("m_iLogID=" + m_iLogID);
				}
				// get instance of exception callback and set
				ExceptionCallBack oexceptionCbf = getExceptiongCbf();
				if (oexceptionCbf == null) {
					Log.e(TAG, "ExceptionCallBack object is failed!");
					return;
				}

				if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(oexceptionCbf)) {
					Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
					return;
				}

				Log.i(TAG, "Login sucess ****************************1***************************");
			} else {
				// whether we have logout
				if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID)) {
					Log.e(TAG, " NET_DVR_Logout is failed!");
					return;
				}
				m_iLogID = -1;
			}
		} catch (Exception err) {
			Log.e(TAG, "error: " + err.toString());
		}
	}

	private void initView() {
		ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
		findViewById(R.id.tv_back).setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(warn_sta);
		/*tv_right  = (TextView) findViewById(R.id.tv_right);
		tv_right.setPadding(20,50, 20, 50);
		tv_right.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_publish_task), null, null, null);
		tv_right.setOnClickListener(this);*/
		
		findViewById(R.id.tv_select_map).setOnClickListener(this);

		tv_send_task  = (TextView) findViewById(R.id.tv_send_task);
		tv_send_task.setOnClickListener(this);
		
		tv_site = (TextView) findViewById(R.id.tv_site);// 站点
		tv_device = (TextView) findViewById(R.id.tv_device);// 设备
		tv_fault = (TextView) findViewById(R.id.tv_fault);// 故障
		tv_happen_time = (TextView) findViewById(R.id.tv_happen_time);// 告警发生时间

	}

	private void getWarnDes() {
		if (Function.isWiFi_3G(this)) {
			if (iWarnDesTask == null) {
				iWarnDesTask = new WarnDesTask();
				iWarnDesTask.execute();
			}
		} else {
			ll_bar.setVisibility(View.GONE);
			Function.toastMsg(this, getString(R.string.tv_not_netlink));
		}
	}

	private class WarnDesTask extends AsyncTask<String, Void, String> {
		List<NameValuePair> paramList = null;
		JSONObject jsonj = null;
		JSONArray jarray = null;
		String errorString = null;
		int errorCode = 0;
		String warn_sta;
		private String bg_img ="";

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(isPreferences.getSp().getBoolean("alarm_update", false)){
				ll_bar.setVisibility(View.GONE);
				isPreferences.updateSp("alarm_update", false);
			}else{
				ll_bar.setVisibility(View.VISIBLE);
			}
			errorString = null;
			paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("warn_id", warn_id));
			paramList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
			paramList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
		}

		@Override
		protected String doInBackground(String... params) {
			
			String result = HttpUtil.queryStringForPost( mUrlUtil.warning_des, paramList);
			Log.i("", "tag 1111 == " + result);
			if (result.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}

			try {
				jsonj = new JSONObject(result);
				if (jsonj.getInt("state") == 0) {
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
				Log.i("", "tag 1111 === " + jsonj);
				
				task_id  = jsonj.getString("task_id").toString().replace("null", "");
				warn_level = jsonj.getString("warn_level").toString().replace("null", "");
				
				m_role = jsonj.getString("m_role").toString().replace("null", "");
				warn_sta = jsonj.getString("warn_sta").toString().replace("null", "");
				warn_stlat = jsonj.getString("warn_stlat").toString().replace("null", "");
				warn_stlong = jsonj.getString("warn_stlong").toString().replace("null", "");
				do_state = jsonj.getString("do_state").toString().replace("null", "");
				
				devname = jsonj.getString("devname").toString().replace("null", "");
				warn_evnt = jsonj.getString("warn_evnt").toString().replace("null", "");
				warn_staid = jsonj.getString("warn_staid").toString().replace("null", "");
				warn_point = jsonj.getString("warn_point").toString().replace("null", "");
				
				video_ip = jsonj.getString("video_ip").toString().replace("null", "");
				video_login = jsonj.getString("video_login").toString().replace("null", "");
				video_pass = jsonj.getString("video_pass").toString().replace("null", "");
				video_apport = jsonj.getString("video_appport").toString().replace("null", "");
				video_channel = jsonj.getString("video_channel").toString().replace("null", "");
				
				video_ip2 = jsonj.getString("video_ip2").toString().replace("null", "");
				video_login2 = jsonj.getString("video_login2").toString().replace("null", "");
				video_pass2 = jsonj.getString("video_pass2").toString().replace("null", "");
				video_apport2 = jsonj.getString("video_appport2").toString().replace("null", "");
				video_channel2 = jsonj.getString("video_channel2").toString().replace("null", "");
				
				
				video_appyschannel = jsonj.getString("video_appyschannel").toString().replace("null", "");
				video_appyslicence = jsonj.getString("video_appyslicence").toString().replace("null", "");
				video_appysvcode = jsonj.getString("video_appysvcode").toString().replace("null", "");
				
				bg_img = jsonj.getString("bg_img").toString().replace("null", "");
				jarray = jsonj.getJSONArray("warn_times");
				for (int i = 0; i < jarray.length(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("datetime", jarray.getJSONObject(i).getString("datetime").toString().replace("null", ""));
					warntimes.add(map);
				}

				return null;

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			iWarnDesTask = null;
			if (errorString == null) {
				tv_site.setText(warn_sta);
				tv_device.setText(devname);
				tv_fault.setText(warn_evnt);
				if(warn_level.equals("1")){
					tv_fault_level.setText(getString(R.string.tv_title_fault));//轻度故障  背景黄色
					tv_fault_level.setBackgroundColor(getResources().getColor(R.color.cr_yellow_2));
				}else if(warn_level.equals("2")){
					tv_fault_level.setText(getString(R.string.tv_midlle_fault));//
					tv_fault_level.setBackgroundColor(getResources().getColor(R.color.cr_orange_3));
				}else if(warn_level.equals("3")){
					tv_fault_level.setText(getString(R.string.tv_big_fault));//轻度故障  背景黄色
					tv_fault_level.setBackgroundColor(getResources().getColor(R.color.cr_red_2));
				}
				
				if(bg_img.equals("")){
					iv_video_bg.setImageDrawable(getResources().getDrawable(R.drawable.icon_video));
				}else{
					int wt =  (int)(getResources().getDisplayMetrics().widthPixels);
					int ht = (int)((380*getResources().getDisplayMetrics().widthPixels)/640);
					Function.setCKMap(((RainBowApplication)alarmDesActivity.this.getApplication()), "alarmdes", iv_video_bg, bg_img,wt, ht);
				}
				StringBuffer buffer = new StringBuffer();

				for (int i = 0; i < warntimes.size(); i++) {
					buffer.append((warntimes.get(i).get("datetime") + "") + "\t\t");
				}

				tv_happen_time.setText(buffer);
				
				if (do_state.equals("0")) {//未处理
					tv_send_task.setText(getString(R.string.tv_receive_task));
					findViewById(R.id.rl_task).setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_cricle7));
					tv_send_task.setEnabled(true);
				} else if (do_state.equals("1")) {//待完成
					tv_send_task.setText(getString(R.string.tv_tk_type_1));
					findViewById(R.id.rl_task).setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_cricle7));
					tv_send_task.setEnabled(true);
				} else if (do_state.equals("2")) {//已完成   准备汇报
					tv_send_task.setText(getString(R.string.tv_tk_type_2));
					findViewById(R.id.rl_task).setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_cricle7));
					tv_send_task.setEnabled(true);
				}else if(do_state.equals("3")){// 汇报完成 查看
					tv_send_task.setText(getString(R.string.tv_look));
					findViewById(R.id.rl_task).setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_cricle7));
					tv_send_task.setEnabled(true);
				}else if(do_state.equals("4")){// 未发布任务
					tv_send_task.setText(getString(R.string.tv_publish_task));
					findViewById(R.id.rl_task).setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_cricle7));
					tv_send_task.setEnabled(true);
				} else if(do_state.equals("5")){// 无法发布任务
					tv_send_task.setText(getString(R.string.tv_publish_task));
					findViewById(R.id.rl_task).setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_cricle10));
					tv_send_task.setEnabled(true);
				} 
				
				
				mRealPlaySh =  realplay_sv.getHolder();
				mRealPlaySh.addCallback(alarmDesActivity.this);
				
				
				if(isPreferences.getSp().getBoolean("switch", true)){
					//内网
					iv_switch.setImageResource(R.drawable.icon_switch_off);
					if(!video_ip.equals("") || !video_apport.equals("")){
						login_video();
					}
				}else{
					//外网
					
					iv_switch.setImageResource(R.drawable.icon_switch_on);
					if(!video_ip.equals("") || !video_apport.equals("")){
						login_video();
					}
				}
				ll_bar.setVisibility(View.GONE);
			} else {
				ll_bar.setVisibility(View.GONE);
				Function.toastMsg(alarmDesActivity.this, errorString);
				comFunction.outtoLogin(alarmDesActivity.this, errorString, errorCode,isPreferences);
				errorString = null;
			}

		}

	}

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 1000;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocClient.setLocOption(option);
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@SuppressWarnings("unchecked")
		@Override
		public void onReceiveLocation(final BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null) {
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
			sb.append(location.getLatitude());// 纬度
			city_y = (float) location.getLatitude();
			System.out.println("city_y=" + city_y);
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());// 经度
			city_x = (float) location.getLongitude();
			System.out.println("city_x=" + city_x);
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
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tv_back:
			finish();
			break;

		case R.id.tv_select_map:
			startActivity(new Intent(alarmDesActivity.this, LookMapActivity.class).putExtra("city_x", city_x)
					.putExtra("city_y", city_y).putExtra("st_latitudee6", warn_stlat)
					.putExtra("st_longitudee6", warn_stlong));
			break;
		
		case R.id.tv_send_task:
			if(task_id.equals("")){
				if(do_state.equals("4")){
					startActivity(new Intent(alarmDesActivity.this, sendTaskActivity.class)
							.putExtra("measpoint_id", warn_point).putExtra("warn_sta", warn_sta)
							.putExtra("devname", devname).putExtra("warn_evnt", warn_evnt)
							.putExtra("warn_staid", warn_staid));
				}else if(do_state.equals("5")){
					 Function.toastMsg(alarmDesActivity.this, getString(R.string.tv_site_not_send));
				}
			}else if(!task_id.equals("")){
				 if(do_state.equals("0")){//接收任务
					 Receiverd_Task(task_id,do_state);
				}else if(do_state.equals("1")){//完成任务
					 Receiverd_Task(task_id,do_state);
				}else if(do_state.equals("2")){//汇报任务
					startActivity(new Intent(alarmDesActivity.this, reportActivity.class)
							.putExtra("task_id", task_id)
							);
				}else if(do_state.equals("3")){//查看任务
					startActivity(new Intent(alarmDesActivity.this, taskDesActivity.class)
							.putExtra("tk_type", "1")
							.putExtra("task_id", task_id));
				}
				
			}
			
			break;
		case R.id.ll_video_bar:
			ll_video_bar.setVisibility(View.GONE);
			play_video();
			break;
		case R.id.iv_play:
			play_video();
			break;
		case R.id.realplay_sv:
			
			if(!video_ip.equals("") || !video_apport.equals("")){
				play_video();
			}else{
				realplay_sv.setVisibility(View.GONE);
				iv_play.setVisibility(View.VISIBLE);
				iv_bg.setVisibility(View.VISIBLE);
				stopPlay();
				realplay_sv.setFocusable(false);
			}
			
			break;
		default:
			break;
		}

	}
	
	
	private void Receiverd_Task(final String task_id,final String do_state){
    	LayoutInflater mflater=LayoutInflater.from(alarmDesActivity.this);//program_add_success_dialog.
		View mView=mflater.inflate(R.layout.simple_dialog,null);
		TextView tv_v_d_title = (TextView)mView.findViewById(R.id.tv_v_d_title);
		TextView tv_v_d_content = (TextView)mView.findViewById(R.id.tv_v_d_content);
		
		tv_v_d_title.setText(getString(R.string.tv_ok));
		
		if(do_state.equals("0")){//接收任务
			tv_v_d_content.setText(getString(R.string.tv_receiver_task));
		}else if(do_state.equals("1")){//完成任务
			tv_v_d_content.setText(getString(R.string.tv_receiverd_task));
		} 
		TextView tv_cancel = (TextView)mView.findViewById(R.id.tv_cancel);
		
		 
		TextView tv_yes = (TextView)mView.findViewById(R.id.tv_yes);
		final Dialog mDialog= new Dialog(alarmDesActivity.this,R.style.iDialog2);
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
				getDoTask(task_id,do_state);
				mDialog.dismiss();
			}
		});
    }
	private void getDoTask(String task_id,String do_state){
		if(Function.isWiFi_3G(alarmDesActivity.this)){
			if(iDotask == null){
				iDotask = new DoTask();
				iDotask.execute(task_id,do_state);
			}
		}else{
			ll_bar.setVisibility(View.GONE);
			Function.toastMsg(alarmDesActivity.this, getString(R.string.tv_not_netlink));
		}
	}
	
	
	private class DoTask extends AsyncTask<String, Void, String>{
		List<NameValuePair> paramsList;
		JSONObject jobj = null;
		String errorString,do_state = "",maps="";int errcode=0;
		@SuppressWarnings("deprecation")
		protected void onPreExecute() {
			ll_bar.setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.tv_tishi)).setText(getString(R.string.tv_operating_waitting));
			errorString = null;
			paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("app_mid",isPreferences.getSp().getString("m_id", "")));
			paramsList.add(new BasicNameValuePair("mtoken",isPreferences.getSp().getString("m_token", "")));
			
			Log.i("", "tag ===== "+isPreferences.getSp().getString("m_id", "")+" \t\t tag === "+isPreferences.getSp().getString("m_token", ""));
			
		}
    	@SuppressWarnings("deprecation")
		@Override
		protected String doInBackground(String... params){
    		String taskid = params[0];
    		do_state = params[1];
    		paramsList.add(new BasicNameValuePair("task_id",taskid));
    		String requery = HttpUtil.queryStringForPost(mUrlUtil.do_task,paramsList);
    		Log.i("", "tag sss sucss=="+requery);
    		if(requery.equals("601")){
    			errorString = "601";
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
 					if(jobj.getInt("code") == 203)
 						errorString = getString(R.string.err_203);
 					if(jobj.getInt("code") == 202)
 						 errorString = getString(R.string.err_202);
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
				
					if(do_state.equals("0")){//接收
						Function.toastMsg(alarmDesActivity.this, getString(R.string.tv_hb_scuesss2));
					}else if(do_state.equals("1")){//完成
						Function.toastMsg(alarmDesActivity.this, getString(R.string.tv_hb_scuesss3));
					}
					isPreferences.updateSp("alarm_update", true);
					getWarnDes();
					
				}else{
					
					Function.toastMsg(alarmDesActivity.this,errorString);
					comFunction.outtoLogin(alarmDesActivity.this, errorString, errcode,isPreferences);
					errorString = null;
				}
			} catch (Exception e) {}
	    }
	}
	
	
	
	private void stop_play(){
		if(!video_ip.equals("") || !video_apport.equals("")){
			ll_video_bar.setVisibility(View.GONE);
			realplay_sv.setFocusable(false);
			play_video();
		}else{
			realplay_sv.setVisibility(View.GONE);
			iv_play.setVisibility(View.VISIBLE);
			iv_bg.setVisibility(View.VISIBLE);
			stopPlay();
			realplay_sv.setFocusable(false);
		}
	
	}
	
	private void play_video(){
		
		if(!video_ip.equals("") || !video_apport.equals("")){
				iv_bg.setVisibility(View.GONE);
				iv_play.setVisibility(View.GONE);
				ll_video_bar.setVisibility(View.GONE);
				realplay_sv.setVisibility(View.VISIBLE);
				if(HCNetSDK.getInstance().NET_DVR_GetLastError()>0){
					ll_video_bar.setVisibility(View.VISIBLE);
					tv_progress.setText(getString(R.string.err_video_all).replace("%1$",
							HCNetSDK.getInstance().NET_DVR_GetLastError() + "")+"\n\t\t"+getString(R.string.tv_chongshi));
					Function.toastMsg(alarmDesActivity.this, getString(R.string.err_video_all).replace("%1$",
							HCNetSDK.getInstance().NET_DVR_GetLastError() + ""));
					login_video();
				}else{
					realplay_sv.setFocusable(true);
					priview_play();
					
				}
			}else{
				if (iv_play.getVisibility() == View.VISIBLE) {
					if(video_appyschannel.equals("") || video_appyslicence.equals("") || video_appysvcode.equals("")){
						Function.toastMsg(alarmDesActivity.this, getString(R.string.tv_facility_not_play_arguments_full));
					}else{
						iv_bg.setVisibility(View.GONE);
						iv_play.setVisibility(View.GONE);
						realplay_sv.setFocusable(true);
						realplay_sv.setVisibility(View.VISIBLE);
						startPlay();
					}
					
					
				}
			}
	
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case EZRealPlayConstants.MSG_REALPLAY_PLAY_SUCCESS:
			handlePlaySuccess(msg);
			break;
		case EZRealPlayConstants.MSG_REALPLAY_PLAY_FAIL:
			handlePlayFail(msg.arg1, msg.arg2);
			break;
		}
		return false;
	}

	private void handlePlayFail(final int arg1, int arg2) {
		Log.e("DataDesActivity", " play failed" + arg1 + "," + arg2);
		tv_progress.setText("播放视频失败,错误码：（" + arg1 + "）");
		iv_bg.setVisibility(View.VISIBLE);
		iv_play.setVisibility(View.VISIBLE);
		realplay_sv.setVisibility(View.GONE);
		ll_video_bar.setVisibility(View.GONE);
		Function.toastMsg(this, "播放视频失败,错误码：（" + arg1 + "）");
	}

	private void handlePlaySuccess(Message msg) {
		Log.i("DataDesActivity", "play sucess " + msg.toString());
		ll_video_bar.setVisibility(View.GONE);
	}
	


	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocClient.stop();
		if (mEZPlayer != null) {
			mEZPlayer.release();

		}
		mmHandler = null;
	}

	
	private void startPlay() {

		ll_video_bar.setVisibility(View.VISIBLE);
		if (mEZPlayer == null) {
			Log.i("PlayActivity", "mEZPlayer = " + mEZPlayer);

			mEZPlayer = RainBowApplication.getOpenSDK().createPlayer(video_appyslicence, Integer.parseInt(video_appyschannel));
			Log.i("PlayActivity", "mEZPlayer = " + mEZPlayer);

		}

		if (mEZPlayer == null)
			return;

		mEZPlayer.setPlayVerifyCode(video_appysvcode);

		mEZPlayer.setHandler(mmHandler);
		mEZPlayer.setSurfaceHold(mRealPlaySh);
		mEZPlayer.startRealPlay();

		updateLoadingProgress(0);
	}
	
	private void updateLoadingProgress(final int progress) {
		tv_progress.setTag(Integer.valueOf(progress));
		tv_progress.setText(progress + "%");
		mmHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (tv_progress != null) {
					Integer tag = (Integer) tv_progress.getTag();
					if (tag != null && tag.intValue() == progress) {
						Random r = new Random();
						tv_progress.setText((progress + r.nextInt(20)) + "%");
					}
				}
			}

		}, 500);
	}
	private void stopPlay() {
		if (mEZPlayer != null) {
			mEZPlayer.stopRealPlay();
		}
	}

}
