package com.rainbow.main.data.des;

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

import com.comutils.main.Function;
import com.comutils.main.SharePreferences;
import com.comutils.net.HttpUtil;
import com.comutils.rain_view.MyListView;
import com.comutils.util.UrlUtil;
import com.comutils.video.util.CrashUtil;
import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;
import com.rainbow.main.R;
import com.rainbow.main.RainBowApplication;
import com.rainbow.main.data.his.hisStoreActivity2;
import com.rainbow.main.data.his.hisStoreActivity4;
import com.rainbow.main.function.comFunction;
import com.rainbow.main.widget.DataDesAdapter;
import com.rainbow.main.widget.DataDesAdapter2;
import com.videogo.openapi.EZConstants.EZRealPlayConstants;
import com.videogo.openapi.EZPlayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DataDesActivity extends Activity implements OnClickListener, Callback, Handler.Callback {

	SharePreferences isPreferences;
	private static final String TextView = null;
	private static final String TAG = "DataDesActivity";
	int channel = 0;

	LinearLayout ll_video, ll_data, ll_bar, ll_video_bar;

	RelativeLayout rl_video;
	SimpleAdapter iSimpleAdapter2 = null;
	DataDesAdapter iDataDesAdapter = null;
	DataDesAdapter2 iDataDesAdapter2 = null;
	ProgressBar pb_bar;
	TextView tv_progress;
	List<HashMap<String, Object>> list = null;
	List<HashMap<String, Object>> sw_list = null;

	String detail_name = "", levlone_face = "", site_name = "", levlone_show_states = "", dform_show_states = "";
	int index = 0;
	ImageView iv_jiankong;
	String st_name, dofrom_id, st_type, levlone_id;// 名，详情类型
	MyListView lv_site, lv_swtich;
	DataInfoTask iDataInfoTask;

	DataInfoTask2 iDataInfoTask2;
	ImageView iv_play, iv_bg;
	Button m_oPreviewBtn;
	private int m_iPort = -1; // play port
	private int m_iStartChan = 1; // start channel no
	private int m_iChanNum = 0; // channel number
	private int m_iLogID = -1; // return by NET_DVR_Login_v30
	private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;

	private boolean m_bNeedDecode = true;
	private boolean m_bMultiPlay = false;
	private int m_iPlayID = -1; // return by NET_DVR_RealPlay_V30
	private int m_iPlaybackID = -1; // return by NET_DVR_PlayBackByTime
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
	private String video_appyschannel = ""; // 通道号 是 Int
	private String video_appyslicence = ""; // 序列号 String
	private String video_appysvcode = ""; // 验证码 String
	private String video_ip2 = "";
	private String video_login2 = "";
	private String video_pass2 = "";
	private String video_channel2 = "";
	private String video_apport2 = "";
	Handler mmHandler = null;
	private EZPlayer mEZPlayer;
	private SurfaceHolder mRealPlaySh;
	SurfaceView realplay_sv;
	ImageView iv_switch;
	int pos = 0;
	private String levlone_bg_img = "";
	private String dform_bg_img;

	UrlUtil mUrlUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * 视频监控 收集日志
		 */
		
		CrashUtil crashUtil = CrashUtil.getInstance();
		crashUtil.init(this);
		isPreferences = new SharePreferences(this);
		mUrlUtil = UrlUtil.getInstance();
		Intent i = getIntent();
		Bundle b = i.getExtras();
		st_type = isPreferences.getSp().getString("st_type", "");
		st_name = b.getString("st_name");
		site_name = isPreferences.getSp().getString("site_name", "");
		levlone_face = isPreferences.getSp().getString("levlone_face", "");

		video_ip = isPreferences.getSp().getString("video_ip", "");
		video_login = isPreferences.getSp().getString("video_login", "");
		video_pass = isPreferences.getSp().getString("video_pass", "");
		video_channel = isPreferences.getSp().getString("video_channel", "");
		video_apport = isPreferences.getSp().getString("video_appport", "");
		video_ip2 = isPreferences.getSp().getString("video_ip2", "");
		video_login2 = isPreferences.getSp().getString("video_login2", "");
		video_pass2 = isPreferences.getSp().getString("video_pass2", "");
		video_channel2 = isPreferences.getSp().getString("video_channel2", "");
		video_apport2 = isPreferences.getSp().getString("video_appport2", "");
		// 萤石云
		video_appyschannel = isPreferences.getSp().getString("video_appyschannel", "");
		video_appyslicence = isPreferences.getSp().getString("video_appyslicence", "");
		video_appysvcode = isPreferences.getSp().getString("video_appysvcode", "");

		list = new ArrayList<HashMap<String, Object>>();
		sw_list = new ArrayList<HashMap<String, Object>>();
		if (st_type.equals("1")) {// 空压站 纯水站
			levlone_id = b.getString("levlone_id");
			levlone_bg_img = isPreferences.getSp().getString("levlone_bg_img", "");
			levlone_show_states = isPreferences.getSp().getString("levlone_show_states", "");
			setContentView(R.layout.frist_des);
			initTopView();
			initViewFrist();
		} else {
			dofrom_id = b.getString("dform_id");
			dform_bg_img = isPreferences.getSp().getString("dform_bg_img", "");
			dform_show_states = isPreferences.getSp().getString("dform_show_states", "");
			setContentView(R.layout.transformer_des);
			initTopView();// 头部视图和数据
			initViewTwo();

		}
	}

	private void getDesInfo2() {
		if (Function.isWiFi_3G(this)) {
			if (iDataInfoTask2 == null) {
				iDataInfoTask2 = new DataInfoTask2();
				iDataInfoTask2.execute();
			}
		} else {
			Function.toastMsg(this, getString(R.string.tv_not_netlink));
		}
	}

	private void initViewFrist() {// 列表

		((TextView) findViewById(R.id.tv_setion_name)).setText(st_name);
		// 判断是否显示状态
		if (levlone_show_states.equals("0")) {
			((TextView) findViewById(R.id.tv_states)).setVisibility(View.GONE);
		} else if (levlone_show_states.equals("1")) {
			((TextView) findViewById(R.id.tv_states)).setVisibility(View.VISIBLE);
		}

		lv_swtich = (MyListView) findViewById(R.id.lv_swtich);
		lv_swtich.setDividerHeight(0);
		lv_swtich.setDivider(new ColorDrawable(getResources().getColor(R.color.cr_gray1)));
		lv_swtich.setDividerHeight((int) (getResources().getDisplayMetrics().density * 5));
		lv_swtich.setDivider(new ColorDrawable(R.color.cr_gray3));

		lv_site = (MyListView) findViewById(R.id.lv_site);
		lv_site.setDividerHeight(0);
		lv_site.setDivider(new ColorDrawable(getResources().getColor(R.color.cr_gray1)));
		lv_site.setDividerHeight((int) (getResources().getDisplayMetrics().density * 5));
		lv_site.setDivider(new ColorDrawable(R.color.cr_gray3));

		iDataDesAdapter2 = new DataDesAdapter2(DataDesActivity.this, list, R.layout.frist_item);
		lv_site.setAdapter(iDataDesAdapter2);

		getDesInfo2();

		iSimpleAdapter2 = new SimpleAdapter(DataDesActivity.this, sw_list, R.layout.switch_item,
				new String[] { "devname", "swhich_state" }, new int[] { R.id.tv_one, R.id.tv_two });
		lv_swtich.setAdapter(iSimpleAdapter2);

		lv_site.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				if ((list.get(arg2).get("graf_isquxian") + "").equals("1")) {// 有曲线
					// graf_from == 1 （1） 曲线图 // 2 （2） 曲线图
					if ((list.get(arg2).get("graf_form") + "").equals("1")) {
						startActivity(new Intent(DataDesActivity.this, hisStoreActivity2.class)
								.putExtra("graf_from", list.get(arg2).get("graf_form") + "")
								.putExtra("measpoint_id", list.get(arg2).get("measpoint_id") + "")
								.putExtra("st_name", st_name));// 测点id
					} else {
						startActivity(new Intent(DataDesActivity.this, hisStoreActivity4.class)
								.putExtra("graf_from", list.get(arg2).get("graf_form") + "")
								.putExtra("measpoint_id", list.get(arg2).get("measpoint_id") + "")
								.putExtra("st_name", st_name));// 测点id
					}

				} else {// 没有曲线
					Function.toastMsg(DataDesActivity.this, "该设备没有曲线图！");
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

		// 通过端口号 、 ip 判断 是通过 什么渠道 播放
		if (!video_ip.equals("") || !video_apport.equals("")) {
			realplay_sv.getHolder().setFormat(PixelFormat.TRANSLUCENT);
			Log.i(TAG, "surface is created" + m_iPort);
			if (-1 == m_iPort) {
				return;
			}
			Surface surface = holder.getSurface();
			if (true == surface.isValid()) {
				if (false == Player.getInstance().setVideoWindow(m_iPort, 0, holder)) {
					Log.e(TAG, "Player setVideoWindow failed!");
				}
			}
		} else {
			if (mEZPlayer != null) {
				mEZPlayer.setSurfaceHold(holder);
			}
			mRealPlaySh = holder;
		}

	}

	// @Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	// @Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (!video_ip.equals("") || !video_apport.equals("")) {
			Log.i(TAG, "Player setVideoWindow release!" + m_iPort);
			if (-1 == m_iPort) {
				return;
			}
			if (true == holder.getSurface().isValid()) {
				if (false == Player.getInstance().setVideoWindow(m_iPort, 0, null)) {
					Log.e(TAG, "Player setVideoWindow failed!");
				}
			}
		} else {
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

	// 头部视图和数据 点击事件都是相同的、
	private void initTopView() {
		if (!initeSdk()) {
			this.finish();
			return;
		}
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mmHandler = new Handler(DataDesActivity.this);
		ll_video = (LinearLayout) findViewById(R.id.ll_video);
		rl_video = (RelativeLayout) findViewById(R.id.rl_video);

		ll_data = (LinearLayout) findViewById(R.id.ll_data);
		ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		ll_video_bar = (LinearLayout) findViewById(R.id.ll_video_bar);
		ll_video_bar.setOnClickListener(this);
		tv_progress = (android.widget.TextView) findViewById(R.id.tv_progress);
		tv_progress.setOnClickListener(this);
		ll_main = (LinearLayout) findViewById(R.id.ll_main);
		ll_main.setFocusable(true);
		ll_main.setFocusableInTouchMode(true);
		ll_main.requestFocus();
		ll_title_one = (LinearLayout) findViewById(R.id.ll_title_one);

		m_oPreviewBtn = (Button) findViewById(R.id.m_oPreviewBtn);
		m_oPreviewBtn.setOnClickListener(this);

		iv_play = (ImageView) findViewById(R.id.iv_play);
		iv_play.setOnClickListener(this);
		iv_bg = (ImageView) findViewById(R.id.iv_video_bg);
		int wt = (int) (getResources().getDisplayMetrics().widthPixels);
		int ht = (int) ((380 * getResources().getDisplayMetrics().widthPixels) / 640);
		if (st_type.equals("1")) {
			if (levlone_bg_img.equals("")) {
				iv_bg.setImageDrawable(getResources().getDrawable(R.drawable.icon_video));
			} else {
				Function.setCKMap(((RainBowApplication) DataDesActivity.this.getApplication()), "alarmdes", iv_bg,
						levlone_bg_img, wt, ht);
			}
		} else {
			if (dform_bg_img.equals("")) {
				iv_bg.setImageDrawable(getResources().getDrawable(R.drawable.icon_video));
			} else {
				Function.setCKMap(((RainBowApplication) DataDesActivity.this.getApplication()), "alarmdes", iv_bg,
						dform_bg_img, wt, ht);
			}
		}

		realplay_sv = (SurfaceView) findViewById(R.id.realplay_sv);
		mRealPlaySh = realplay_sv.getHolder();
		mRealPlaySh.addCallback(this);
		realplay_sv.setOnClickListener(this);

		findViewById(R.id.tv_back).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_title)).setText(st_name);
		findViewById(R.id.tv_right).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_right)).setText("刷新");

		iv_switch = (ImageView) findViewById(R.id.iv_switch);

		iv_switch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isPreferences.getSp().getBoolean("switch", true)) {
					iv_switch.setImageResource(R.drawable.icon_switch_on);
					isPreferences.updateSp("switch", false);
				} else {
					iv_switch.setImageResource(R.drawable.icon_switch_off);
					isPreferences.updateSp("switch", true);
				}

				if (m_iPlayID >= 0) {
					Log.i("Video", "video m_iPlayID  > = 0");
					stop_play();
				} else {
					iv_bg.setVisibility(View.VISIBLE);
					iv_play.setVisibility(View.VISIBLE);
					realplay_sv.setVisibility(View.GONE);
				}
				
				if (!video_ip.equals("") || !video_apport.equals("")) {
					if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID)) {
						Log.e(TAG, " NET_DVR_Logout is failed!");
					}
					m_iLogID = -1;
					// 注册登录设备
					login_video();
				}

			}
		});
		;

		// Switch 初始化状态 登录设备
		if (isPreferences.getSp().getBoolean("switch", true)) {
			// 外网
			iv_switch.setImageResource(R.drawable.icon_switch_off);

			if (!video_ip.equals("") || !video_apport.equals("")) {
				login_video();
			}

		} else {
			// 内网
			iv_switch.setImageResource(R.drawable.icon_switch_on);
			if (!video_ip.equals("") || !video_apport.equals("")) {
				login_video();
			}

		}

	}

	/**
	 * 
	 * 注册设备
	 * 
	 * @return
	 */
	private int loginDevice() {
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

		if (isPreferences.getSp().getBoolean("switch", true)) {
			// 外网
			strIP = video_ip;
			nPort = Integer.parseInt(video_apport);
			strUser = video_login;
			strPsd = video_pass;
		} else {
			// 内网
			strIP = video_ip2;
			nPort = Integer.parseInt(video_apport2);
			strUser = video_login2;
			strPsd = video_pass2;

		}

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
		try {
			if (m_iLogID < 0) {
				// login on the device
				m_iLogID = loginDevice();
				System.out.println("m_iLogID=" + m_iLogID);
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
						ll_video_bar.setVisibility(View.VISIBLE);
						new Thread() {
							public void run() {
								try {
									sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								} finally {
									mHandler.sendEmptyMessage(2);
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
				DataDesActivity.this.processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
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
		if (isPreferences.getSp().getBoolean("switch", true)) {
			previewInfo.lChannel = Integer.parseInt(video_channel) + 32;
		} else {
			previewInfo.lChannel = Integer.parseInt(video_channel2) + 32;
		}
		previewInfo.dwStreamType = 0; // substream
		previewInfo.bBlocked = 1;
		// HCNetSDK start preview 开始浏览
		m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(m_iLogID, previewInfo, fRealDataCallBack);
		if (m_iPlayID < 0) {
			Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
			ll_video_bar.setVisibility(View.VISIBLE);
			tv_progress.setText(getString(R.string.err_video_all).replace("%1$",
					HCNetSDK.getInstance().NET_DVR_GetLastError() + ""));
			return;
		} else {
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
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
					DataDesActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
			Log.e(TAG, "error: " + err.toString());
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			;
			if (msg.what == 1) {
				ll_video_bar.setVisibility(View.VISIBLE);
				tv_progress.setText("请先执行登录操作！");
				realplay_sv.setFocusable(true);
			} else if (msg.what == 2) {
				ll_video_bar.setVisibility(View.VISIBLE);
				tv_progress.setText("播放失败，点击重试！");
				realplay_sv.setFocusable(true);
			}
		};
	};

	private void initViewTwo() {
		((TextView) findViewById(R.id.tv_setion_name)).setText(st_name);
		if (dform_show_states.equals("0")) {
			((TextView) findViewById(R.id.tv_states)).setVisibility(View.GONE);
		} else if (dform_show_states.equals("1")) {
			((TextView) findViewById(R.id.tv_states)).setVisibility(View.VISIBLE);
		}

		lv_swtich = (MyListView) findViewById(R.id.lv_swtich);
		lv_swtich.setDividerHeight(0);
		lv_swtich.setDivider(new ColorDrawable(getResources().getColor(R.color.cr_gray1)));
		lv_swtich.setDividerHeight((int) (getResources().getDisplayMetrics().density * 5));
		lv_swtich.setDivider(new ColorDrawable(R.color.cr_gray3));

		// 列表
		lv_site = (MyListView) findViewById(R.id.lv_site);
		lv_site.setDividerHeight(0);
		lv_site.setDivider(new ColorDrawable(getResources().getColor(R.color.cr_gray1)));
		lv_site.setDividerHeight((int) (getResources().getDisplayMetrics().density * 5));
		lv_site.setDivider(new ColorDrawable(R.color.cr_gray3));
		getDesInfo();

		iDataDesAdapter = new DataDesAdapter(DataDesActivity.this, list, R.layout.transformer_item);
		lv_site.setAdapter(iDataDesAdapter);

		iSimpleAdapter2 = new SimpleAdapter(DataDesActivity.this, sw_list, R.layout.switch_item,
				new String[] { "devname", "swhich_state" }, new int[] { R.id.tv_one, R.id.tv_two });
		lv_swtich.setAdapter(iSimpleAdapter2);

		lv_site.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				if ((list.get(arg2).get("graf_isquxian") + "").equals("1")) {// 有曲线
					if ((list.get(arg2).get("graf_form") + "").equals("1")) {
						startActivity(new Intent(DataDesActivity.this, hisStoreActivity2.class)
								.putExtra("graf_from", list.get(arg2).get("graf_form") + "")
								.putExtra("measpoint_id", list.get(arg2).get("measpoint_id") + "")
								.putExtra("st_name", st_name));
						// 测点id
					} else {
						startActivity(new Intent(DataDesActivity.this, hisStoreActivity4.class)
								.putExtra("graf_from", list.get(arg2).get("graf_form") + "")
								.putExtra("measpoint_id", list.get(arg2).get("measpoint_id") + "")
								.putExtra("st_name", st_name));// 测点id
					}
				} else {// 没有曲线
					Function.toastMsg(DataDesActivity.this, "该设备没有曲线图");
				}

			}
		});

	}

	/**
	 * 主变压器
	 */
	private void getDesInfo() {
		if (Function.isWiFi_3G(this)) {
			if (iDataInfoTask == null) {
				iDataInfoTask = new DataInfoTask();
				iDataInfoTask.execute();
			}
		} else {
			Function.toastMsg(this, getString(R.string.tv_not_netlink));
		}
	}

	/**
	 * 
	 * 主变压器 10KV线路
	 * 
	 * @author Administrator
	 *
	 */
	private class DataInfoTask extends AsyncTask<String, Void, String> {
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
			paramList.add(new BasicNameValuePair("dform_id", dofrom_id));
			paramList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
			paramList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
		}

		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.site_data_des, paramList);
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
					if (errorCode == 201)
						errorString = getString(R.string.err_201);
					if (errorCode == 202)
						errorString = getString(R.string.err_202);
					if (errorCode == 203)
						errorString = getString(R.string.err_203);
					if (errorCode == 204)
						errorString = getString(R.string.err_204);
					return null;
				}

				jsonj = new JSONObject(jsonj.getString("data"));
				Log.i("", "tag 3333 == " + jsonj);
				sw_list.clear();

				jarray = jsonj.getJSONArray("switch_info");
				for (int i = 0; i < jarray.length(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("measpoint_name",
							jarray.getJSONObject(i).getString("measpoint_name").toString().replace("null", ""));
					map.put("swhich_state",
							jarray.getJSONObject(i).getString("swhich_state").toString().replace("null", "") == ""
									? "运行"
									: jarray.getJSONObject(i).getString("swhich_state").toString().replace("null", ""));

					map.put("devname", jarray.getJSONObject(i).getString("devname").toString().replace("null", ""));
					map.put("measpoint_id",
							jarray.getJSONObject(i).getString("measpoint_id").toString().replace("null", ""));
					sw_list.add(map);
				}
				list.clear();
				jarray = jsonj.getJSONArray("data_info");
				Log.i("", "tag 3333 jarray == " + jarray);
				for (int i = 0; i < jarray.length(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();

					map.put("graf_form", jarray.getJSONObject(i).getString("graf_form").toString().replace("null", ""));// 展示状态
					map.put("graf_isquxian",
							jarray.getJSONObject(i).getString("graf_isquxian").toString().replace("null", ""));// 是否存在曲线
																												// 1有
																												// 2没有
					map.put("measpoint_id",
							jarray.getJSONObject(i).getString("measpoint_id").toString().replace("null", ""));
					map.put("measpoint_section_name",
							jarray.getJSONObject(i).getString("devname").toString().replace("null", ""));
					map.put("measpoint_name",
							jarray.getJSONObject(i).getString("measpoint_name").toString().replace("null", ""));
					map.put("unit", jarray.getJSONObject(i).getString("unit").toString().replace("null", ""));
					float vlaue = Function.float2number(Float
							.parseFloat(jarray.getJSONObject(i).getString("value").toString().replace("null", "")));
					map.put("value", vlaue + "");
					map.put("swhich_state", jarray.getJSONObject(i).getString("info").toString().replace("null", ""));
					list.add(map);
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
			iDataInfoTask = null;
			ll_bar.setVisibility(View.GONE);
			if (errorString == null) {
				ll_data.setVisibility(View.VISIBLE);
				lv_site.setVisibility(View.VISIBLE);

				iDataDesAdapter.notifyDataSetChanged();
				iSimpleAdapter2.notifyDataSetChanged();

			} else {
				Function.toastMsg(DataDesActivity.this, errorString);
				comFunction.outtoLogin(DataDesActivity.this, errorString, errorCode, isPreferences);
				errorString = null;
			}

		}

	}

	/**
	 * 
	 * 空压站 纯水站
	 * 
	 * @author Administrator
	 *
	 */
	private class DataInfoTask2 extends AsyncTask<String, Void, String> {
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
			paramList.add(new BasicNameValuePair("levlone_id", levlone_id));
			paramList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
			paramList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
		}

		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.site_data_des2, paramList);
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
					if (errorCode == 201)
						errorString = getString(R.string.err_201);
					if (errorCode == 202)
						errorString = getString(R.string.err_202);
					if (errorCode == 203)
						errorString = getString(R.string.err_203);
					if (errorCode == 204)
						errorString = getString(R.string.err_204);
					return null;
				}

				jsonj = new JSONObject(jsonj.getString("data"));
				Log.i("", "tag 3333 == " + jsonj);
				sw_list.clear();

				jarray = jsonj.getJSONArray("switch_info");
				for (int i = 0; i < jarray.length(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("measpoint_name",
							jarray.getJSONObject(i).getString("measpoint_name").toString().replace("null", ""));
					map.put("swhich_state",
							jarray.getJSONObject(i).getString("swhich_state").toString().replace("null", "") == ""
									? "运行"
									: jarray.getJSONObject(i).getString("swhich_state").toString().replace("null", ""));
					map.put("devname", jarray.getJSONObject(i).getString("devname").toString().replace("null", ""));
					map.put("measpoint_id",
							jarray.getJSONObject(i).getString("measpoint_id").toString().replace("null", ""));
					sw_list.add(map);
				}
				list.clear();
				jarray = jsonj.getJSONArray("data_info");
				for (int i = 0; i < jarray.length(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();

					map.put("graf_form", jarray.getJSONObject(i).getString("graf_form").toString().replace("null", ""));// 展示状态
					map.put("graf_isquxian",
							jarray.getJSONObject(i).getString("graf_isquxian").toString().replace("null", ""));// 是否存在曲线
																												// 1有
					map.put("measpoint_id",
							jarray.getJSONObject(i).getString("measpoint_id").toString().replace("null", ""));
					map.put("measpoint_section_name",
							jarray.getJSONObject(i).getString("devname").toString().replace("null", ""));
					map.put("measpoint_name",
							jarray.getJSONObject(i).getString("measpoint_name").toString().replace("null", ""));
					map.put("unit", jarray.getJSONObject(i).getString("unit").toString().replace("null", ""));
					map.put("value", jarray.getJSONObject(i).getString("value").toString().replace("null", ""));
					map.put("swhich_state",
							jarray.getJSONObject(i).getString("info").toString().replace("null", "").equals(""));
					list.add(map);
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
			iDataInfoTask2 = null;
			if (errorString == null) {
				ll_data.setVisibility(View.VISIBLE);
				ll_bar.setVisibility(View.GONE);
				lv_site.setVisibility(View.VISIBLE);
				iDataDesAdapter2.notifyDataSetChanged();

			} else {
				Function.toastMsg(DataDesActivity.this, errorString);
				comFunction.outtoLogin(DataDesActivity.this, errorString, errorCode, isPreferences);
				errorString = null;
			}

		}

	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_back:
			finish();
			break;
		case R.id.tv_right:
			ll_bar.setVisibility(View.VISIBLE);
			lv_site.setVisibility(View.GONE);
			if (st_type.equals("1")) {
				getDesInfo2();
			} else {
				getDesInfo();
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

			/*if(!video_appyschannel.equals("") || !video_appyslicence.equals("") || !video_appysvcode.equals("") ){
				realplay_sv.setVisibility(View.GONE);
				iv_play.setVisibility(View.VISIBLE);
				iv_bg.setVisibility(View.VISIBLE);
				stopPlay();
				realplay_sv.setFocusable(false);
			}else{
				play_video();
			}
			*/
			
			if (!video_ip.equals("") || !video_apport.equals("")) {
				play_video();
			} else {
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

	private void stop_play() {
		/*if(!video_appyschannel.equals("") || !video_appyslicence.equals("") || !video_appysvcode.equals("") ){
			realplay_sv.setVisibility(View.GONE);
			iv_play.setVisibility(View.VISIBLE);
			iv_bg.setVisibility(View.VISIBLE);
			stopPlay();
			realplay_sv.setFocusable(false);
		}else{
			ll_video_bar.setVisibility(View.GONE);
			realplay_sv.setFocusable(false);
			play_video();
		}*/
		
		if (!video_ip.equals("") || !video_apport.equals("")) {
			ll_video_bar.setVisibility(View.GONE);
			realplay_sv.setFocusable(false);
			play_video();
		} else {
			realplay_sv.setVisibility(View.GONE);
			iv_play.setVisibility(View.VISIBLE);
			iv_bg.setVisibility(View.VISIBLE);
			stopPlay();
			realplay_sv.setFocusable(false);
		}

	}

	private void play_video() {
		
		/*if(!video_appyschannel.equals("") || !video_appyslicence.equals("") || !video_appysvcode.equals("") ){
			if (iv_play.getVisibility() == View.VISIBLE || ll_video_bar.getVisibility() == View.VISIBLE) {
				if (video_appyschannel.equals("") || video_appyslicence.equals("") || video_appysvcode.equals("")) {
					Function.toastMsg(DataDesActivity.this, getString(R.string.tv_facility_not_play_arguments_full));
				} else {
					iv_bg.setVisibility(View.GONE);
					iv_play.setVisibility(View.GONE);
					realplay_sv.setFocusable(true);
					realplay_sv.setVisibility(View.VISIBLE);
					startPlay();
				}
			}
		}else{
			iv_bg.setVisibility(View.GONE);
			iv_play.setVisibility(View.GONE);
			ll_video_bar.setVisibility(View.GONE);
			realplay_sv.setVisibility(View.VISIBLE);
			if (HCNetSDK.getInstance().NET_DVR_GetLastError() > 0) {
				ll_video_bar.setVisibility(View.VISIBLE);
				tv_progress.setText(getString(R.string.err_video_all).replace("%1$",
						HCNetSDK.getInstance().NET_DVR_GetLastError() + "") + "\n\t\t"
						+ getString(R.string.tv_chongshi));
				Function.toastMsg(DataDesActivity.this, getString(R.string.err_video_all).replace("%1$",
						HCNetSDK.getInstance().NET_DVR_GetLastError() + ""));
				login_video();
			} else {
				realplay_sv.setFocusable(true);
				priview_play();
			}
		}*/
		
		
		if (!video_ip.equals("") || !video_apport.equals("")) {
			iv_bg.setVisibility(View.GONE);
			iv_play.setVisibility(View.GONE);
			ll_video_bar.setVisibility(View.GONE);
			realplay_sv.setVisibility(View.VISIBLE);
			if (HCNetSDK.getInstance().NET_DVR_GetLastError() > 0) {
				ll_video_bar.setVisibility(View.VISIBLE);
				tv_progress.setText(getString(R.string.err_video_all).replace("%1$",
						HCNetSDK.getInstance().NET_DVR_GetLastError() + "") + "\n\t\t"
						+ getString(R.string.tv_chongshi));
				Function.toastMsg(DataDesActivity.this, getString(R.string.err_video_all).replace("%1$",
						HCNetSDK.getInstance().NET_DVR_GetLastError() + ""));
				login_video();
			} else {
				realplay_sv.setFocusable(true);
				priview_play();
			}
		} else {
			if (iv_play.getVisibility() == View.VISIBLE || ll_video_bar.getVisibility() == View.VISIBLE) {
				if (video_appyschannel.equals("") || video_appyslicence.equals("") || video_appysvcode.equals("")) {
					Function.toastMsg(DataDesActivity.this, getString(R.string.tv_facility_not_play_arguments_full));
				} else {
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
//		tv_progress.setText("播放视频失败,错误码：（" + arg1 + "）");
		iv_bg.setVisibility(View.VISIBLE);
		iv_play.setVisibility(View.VISIBLE);
		realplay_sv.setVisibility(View.GONE);
		ll_video_bar.setVisibility(View.GONE);
		Function.toastMsg(this, "播放视频失败,错误码：（" + arg1 + "）");
	}

	private void handlePlaySuccess(Message msg) {
		ll_video_bar.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		iv_bg.setVisibility(View.VISIBLE);
		iv_play.setVisibility(View.VISIBLE);
		realplay_sv.setVisibility(View.GONE);
		ll_video_bar.setVisibility(View.GONE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mEZPlayer != null) {
			mEZPlayer.release();

		}
		mmHandler = null;
	}

	@Override
	protected void onStop() {
		super.onStop();
/*
		if(!video_appyschannel.equals("") || !video_appyslicence.equals("") || !video_appysvcode.equals("") ){
			stopPlay();
		}else{
			stopSinglePreview();
		}
		*/
		if (!video_ip.equals("") || !video_apport.equals("")) {
			stopSinglePreview();
		} else {
			stopPlay();
		}

	}

	private void startPlay() {

		ll_video_bar.setVisibility(View.VISIBLE);
		if (mEZPlayer == null) {
			Log.i("PlayActivity", "mEZPlayer = " + mEZPlayer);

			mEZPlayer = RainBowApplication.getOpenSDK().createPlayer(video_appyslicence,
					Integer.parseInt(video_appyschannel));
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
						tv_progress.setText((progress + r.nextInt(50)) + "%");
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
