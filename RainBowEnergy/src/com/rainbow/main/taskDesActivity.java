package com.rainbow.main;

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
import com.comutils.util.UrlUtil;
import com.rainbow.main.function.comFunction;
import com.rainbow.main.widget.MapListAdapter;
import com.rainbow.main.widget.OprAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class taskDesActivity extends Activity implements OnClickListener {

	TextView tv_back, tv_title, tv_select_map;
	String task_id = "", tk_type = "", tk_state = "0";

	private LinearLayout ll_main;

	TextView tv_tk_addtime, tv_tk_context, tv_tk_dotime, tv_tk_state;
	TextView tv_tk_address, tv_task_des, tv_tk_tool, tv_tk_order, tv_tk_back_addtime, tv_tk_back_context;
	String tk_longitudee6, tk_latitudee6;
	SharePreferences isPreferences;
	TaskInfosTask iTaskInfosTask;
	private GridView gv_maplist, gv_peplelist, gv_tasklist;
	private List<HashMap<String, Object>> maplst;
	private MapListAdapter iMapListAdapter;

	private List<HashMap<String, Object>> maplst2;
	private MapListAdapter iMapListAdapter2;

	OprAdapter oprAdapter;
	LinearLayout ll_bar;
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	float city_x;
	float city_y;
	String addr = "";
	List<HashMap<String, Object>> olist = new ArrayList<HashMap<String, Object>>();
	LinearLayout ll_one, ll_two, ll_three, ll_four, ll_five, ll_six;

	ImageView iv_publish_people, iv_receive_people;
	TextView tv_publish_people, tv_receive_people, tv_report_content, tv_que;

	UrlUtil mUrlUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isPreferences = new SharePreferences(this);
		mUrlUtil = UrlUtil.getInstance();
		Intent i = getIntent();
		Bundle b = i.getExtras();
		task_id = b.getString("task_id");
		tk_type = b.getString("tk_type");
		Log.i("taskDesActivity", tk_type);
		if (isPreferences.getSp().getBoolean("tk_state_bool", false)) {
			System.out.println("tk_state ==tk_state ");
			tk_state = b.getString("tk_state");
		} else {
			tk_state = "0";
		}

		if (tk_type.equals("1")) {//告警
			setContentView(R.layout.task_des);
			initAll();
			if (tk_state.equals("3")) {
				ll_vis(View.VISIBLE);
			} else {
				ll_vis(View.GONE);
			}
			initTypeOne();
		} else {//图文
			setContentView(R.layout.task_des2);
			initAll();

			System.out.println("tk_state == " + tk_state);
			if (tk_state.equals("3")) {
				ll_vis(View.VISIBLE);
			} else {
				ll_vis(View.GONE);
			}

			initTypeTwo();
		}
		getTaskInfos();
	}

	private void ll_vis(int vis) {
		ll_one.setVisibility(vis);
		ll_two.setVisibility(vis);
		ll_three.setVisibility(vis);
		ll_four.setVisibility(vis);
		ll_six.setVisibility(vis);
		ll_five.setVisibility(vis);

	}

	private void initTypeOne() {
		tv_tk_addtime = (TextView) findViewById(R.id.tv_tk_addtime);
		tv_tk_dotime = (TextView) findViewById(R.id.tv_tk_dotime);
		tv_tk_address = (TextView) findViewById(R.id.tv_tk_address);
		tv_task_des = (TextView) findViewById(R.id.tv_task_des);
		tv_tk_tool = (TextView) findViewById(R.id.tv_tk_tool);
		tv_tk_order = (TextView) findViewById(R.id.tv_tk_order);
		tv_tk_back_addtime = (TextView) findViewById(R.id.tv_tk_back_addtime);
		tv_tk_back_context = (TextView) findViewById(R.id.tv_tk_back_context);
	}

	// 图文
	private void initTypeTwo() {
		tv_tk_addtime = (TextView) findViewById(R.id.tv_tk_addtime);
		tv_tk_context = (TextView) findViewById(R.id.tv_tk_context);
		tv_tk_dotime = (TextView) findViewById(R.id.tv_tk_dotime);
		tv_tk_state = (TextView) findViewById(R.id.tv_tk_state);

		gv_maplist = (GridView) findViewById(R.id.gv_maplist);
		gv_maplist.setSelector(new ColorDrawable(Color.TRANSPARENT));
		maplst = new ArrayList<HashMap<String, Object>>();
		iMapListAdapter = new MapListAdapter(this, ((RainBowApplication) this.getApplication()), "taskDes", maplst,
				R.layout.map_list_item, new String[] { "fd_map" }, new int[] { R.id.iv_map });
		gv_maplist.setAdapter(iMapListAdapter);

	}

	private void changeMaplistViewHeight() {
		if (maplst.size() > 3) {
			gv_maplist.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
					(int) (240 * getResources().getDisplayMetrics().density)));
		} else {
			gv_maplist.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
		}
	}

	private void changeMaplistViewHeight2() {
		if (maplst2.size() > 3) {
			gv_tasklist.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
					(int) (240 * getResources().getDisplayMetrics().density)));
		} else {
			gv_tasklist.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
		}
	}

	private void changeMaplistViewHeight3() {
		if (olist.size() > 4) {
			gv_peplelist.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
					(int) (240 * getResources().getDisplayMetrics().density)));
		} else {
			gv_peplelist.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
		}
	}

	private void initAll() {

		ll_main = (LinearLayout) findViewById(R.id.ll_main);
		ll_main.setFocusable(true);
		ll_main.setFocusableInTouchMode(true);
		ll_main.requestFocus();

		ll_one = (LinearLayout) findViewById(R.id.ll_one);
		ll_two = (LinearLayout) findViewById(R.id.ll_two);
		ll_three = (LinearLayout) findViewById(R.id.ll_three);
		ll_four = (LinearLayout) findViewById(R.id.ll_four);
		ll_five = (LinearLayout) findViewById(R.id.ll_five);
		ll_six = (LinearLayout) findViewById(R.id.ll_six);

		iv_publish_people = (ImageView) findViewById(R.id.iv_publish_people);
		iv_receive_people = (ImageView) findViewById(R.id.iv_receive_people);
		tv_publish_people = (TextView) findViewById(R.id.tv_publish_people);
		tv_receive_people = (TextView) findViewById(R.id.tv_receive_people);
		tv_report_content = (TextView) findViewById(R.id.tv_report_content);
		tv_que = (TextView) findViewById(R.id.tv_que);

		ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_back.setOnClickListener(this);
		tv_title.setText(getString(R.string.tv_task_des));

		tv_select_map = (TextView) findViewById(R.id.tv_select_map);
		tv_select_map.setOnClickListener(this);

		tv_select_map.setVisibility(View.GONE);
		// gv_peplelist,gv_tasklist
		gv_peplelist = (GridView) findViewById(R.id.gv_peplelist);
		gv_peplelist.setSelector(new ColorDrawable(Color.TRANSPARENT));
		oprAdapter = new OprAdapter(getApplicationContext(), olist,
				(RainBowApplication) taskDesActivity.this.getApplication());
		gv_peplelist.setAdapter(oprAdapter);

		// gv_peplelist,gv_tasklist
		gv_tasklist = (GridView) findViewById(R.id.gv_tasklist);
		gv_tasklist.setSelector(new ColorDrawable(Color.TRANSPARENT));
		maplst2 = new ArrayList<HashMap<String, Object>>();
		iMapListAdapter2 = new MapListAdapter(this, ((RainBowApplication) this.getApplication()), "taskDes", maplst2,
				R.layout.map_list_item, new String[] { "fd_map" }, new int[] { R.id.iv_map });
		gv_tasklist.setAdapter(iMapListAdapter2);

	}

	private void getTaskInfos() {
		if (Function.isWiFi_3G(this)) {
			if (iTaskInfosTask == null) {
				iTaskInfosTask = new TaskInfosTask();
				iTaskInfosTask.execute();
			}
		} else {
			ll_bar.setVisibility(View.GONE);
			Function.toastMsg(this, getString(R.string.tv_not_netlink));
		}
	}

	private class TaskInfosTask extends AsyncTask<String, Void, String> {
		List<NameValuePair> paramList = null;
		JSONObject jsonj = null;
		String errorString = null;
		int errorCode = 0;
		JSONArray jarray = null;
		String tk_state, tk_addtime, tk_type2, tk_dotime, tk_context, tk_map;
		String tk_address, task_des, tk_tool, tk_order, tk_back_addtime, tk_back_context;
		private String tk_ps_logo;
		private String tk_ps_name;
		private String tk_rec_name;
		private String tk_rec_logo;
		private String tk_back_restqt;
		private String tk_back_maps;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ll_bar.setVisibility(View.VISIBLE);
			errorString = null;
			paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("task_id", task_id));
			paramList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
			paramList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
			Log.i("", "tag ===== " + isPreferences.getSp().getString("m_id", "") + " \t\t tag === "
					+ isPreferences.getSp().getString("m_token", ""));
		}

		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.task_des, paramList);
			Log.i("", "tag 111 taskinfos== " + result);
			if (result.equals("601")) {
				errorString = getString(R.string.tv_api_abnormal);
				return null;
			}
			try {
				jsonj = new JSONObject(result);
				if (jsonj.getInt("state") == 0) {
					errorCode = jsonj.getInt("code");
					errorString = "err" + errorCode;
					if (errorCode == 102) {
						errorString = getString(R.string.tv_no_quanxian);
					}
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
				Log.i("", "tag 1111 des === " + jsonj);

				tk_state = jsonj.getString("tk_state").toString().replace("null", "");
				tk_addtime = jsonj.getString("tk_addtime").toString().replace("null", "");
				tk_dotime = jsonj.getString("tk_dotime").toString().replace("null", "");
				tk_context = jsonj.getString("tk_context").toString().replace("null", "");
				tk_map = jsonj.getString("tk_map").toString().replace("null", "");
				tk_type2 = jsonj.getString("tk_type").toString().replace("null", "");
				tk_address = jsonj.getString("tk_address").toString().replace("null", "");
				task_des = jsonj.getString("task_des").toString().replace("null", "");
				tk_tool = jsonj.getString("tk_tool").toString().replace("null", "");
				tk_order = jsonj.getString("tk_order").toString().replace("null", "");
				tk_back_addtime = jsonj.getString("tk_back_addtime").toString().replace("null", "");
				tk_back_context = jsonj.getString("tk_back_context").toString().replace("null", "");
				tk_longitudee6 = jsonj.getString("tk_longitudee6").toString().replace("null", "");
				tk_latitudee6 = jsonj.getString("tk_latitudee6").toString().replace("null", "");

				tk_ps_name = jsonj.getString("tk_ps_name").toString().replace("null", "");// 发布人
				tk_ps_logo = jsonj.getString("tk_ps_logo").toString().replace("null", "");// 发布人头像
				tk_rec_name = jsonj.getString("tk_rec_name").toString().replace("null", "");// 任务接收人
				tk_rec_logo = jsonj.getString("tk_rec_logo").toString().replace("null", "");// 任务接收头像
				tk_back_restqt = jsonj.getString("tk_back_restqt").toString().replace("null", "");// 遗留问题
				tk_back_maps = jsonj.getString("tk_back_maps").toString().replace("null", "");// 汇报图片

				jarray = jsonj.getJSONArray("exc_member");
				for (int i = 0; i < jarray.length(); i++) {
					HashMap<String, Object> hashMap = new HashMap<String, Object>();
					hashMap.put("name", jarray.getJSONObject(i).getString("m_name").toString());
					hashMap.put("map", jarray.getJSONObject(i).getString("m_logo").toString().replace("null", ""));
					olist.add(hashMap);
				}

				tk_tool = jsonj.getString("tk_tool").toString().replace("null", "");
				tk_order = jsonj.getString("tk_order").toString().replace("null", "");
				tk_back_addtime = jsonj.getString("tk_back_addtime").toString().replace("null", "");
				tk_back_context = jsonj.getString("tk_back_context").toString().replace("null", "");
				tk_longitudee6 = jsonj.getString("tk_longitudee6").toString().replace("null", "");
				tk_latitudee6 = jsonj.getString("tk_latitudee6").toString().replace("null", "");

				return null;
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			iTaskInfosTask = null;
			ll_bar.setVisibility(View.GONE);
			if (errorString == null) {

				int rd = (int) (70 * (getResources().getDisplayMetrics().density));
				if (!tk_ps_name.equals("")) {
					if (tk_ps_logo.equals(""))
						iv_publish_people.setImageDrawable((getResources().getDrawable(R.drawable.icon_df_head)));
					else
						Function.setCircleMap(((RainBowApplication) taskDesActivity.this.getApplication()), "personal",
								iv_publish_people, tk_ps_logo, rd);

				}
				tv_publish_people.setText(tk_ps_name);

				if (!tk_rec_name.equals("")) {
					if (tk_rec_logo.equals(""))
						iv_receive_people.setImageDrawable((getResources().getDrawable(R.drawable.icon_df_head)));
					else
						Function.setCircleMap(((RainBowApplication) taskDesActivity.this.getApplication()), "personal",
								iv_receive_people, tk_rec_logo, rd);
				}

				tv_receive_people.setText(tk_rec_name);
				tv_report_content.setText(tk_back_context);
				tv_que.setText(tk_back_restqt);

				// 汇报图片
				if (tk_back_maps.equals("")) {
					gv_tasklist.setVisibility(View.GONE);
				} else {
					gv_tasklist.setVisibility(View.VISIBLE);
					String map[] = tk_back_maps.split(",");
					System.out.println("taskdes == " + map.length);
					for (int i = 0; i < map.length; i++) {
						HashMap<String, Object> hashMap = new HashMap<String, Object>();

						hashMap.put("fd_map", map[i]);
						maplst2.add(hashMap);
					}
					iMapListAdapter2.notifyDataSetChanged();
					changeMaplistViewHeight2();
				}

				if (olist.size() == 0) {
					gv_peplelist.setVisibility(View.GONE);
				} else {
					gv_peplelist.setVisibility(View.VISIBLE);
					changeMaplistViewHeight3();
					oprAdapter.notifyDataSetChanged();
				}

				if (tk_type.equals("2")) {
					tv_tk_addtime.setText(tk_addtime);
					tv_tk_dotime.setText(tk_dotime);
					tv_tk_context.setText(tk_context);
					if (tk_state.equals("0")) {
						tv_tk_state.setText(getString(R.string.tv_tk_state_0));
					} else if (tk_state.equals("1")) {
						tv_tk_state.setText(getString(R.string.tv_tk_state_1));
					} else if (tk_state.equals("2")) {
						tv_tk_state.setText(getString(R.string.tv_tk_state_2));
					} else if (tk_state.equals("3")) {
						tv_tk_state.setText(getString(R.string.tv_tk_state_3));
					}

					if (tk_map.equals("")) {
						gv_maplist.setVisibility(View.GONE);
					} else {
						gv_maplist.setVisibility(View.VISIBLE);
						String map[] = tk_map.split(",");
						System.out.println("taskdes == " + map.length);
						for (int i = 0; i < map.length; i++) {
							HashMap<String, Object> hashMap = new HashMap<String, Object>();
							hashMap.put("fd_map", map[i]);
							maplst.add(hashMap);
						}
						changeMaplistViewHeight();
						iMapListAdapter.notifyDataSetChanged();
					}

				} else {// 警报

					tv_tk_addtime.setText(tk_addtime);
					tv_tk_dotime.setText(tk_dotime);
					tv_tk_address.setText(tk_address);
					tv_task_des.setText(task_des);
					tv_tk_tool.setText(tk_tool);
					tv_tk_order.setText(tk_order);
					tv_tk_back_addtime.setText(tk_back_addtime);
					tv_tk_back_context.setText(tk_back_context);

				}

			} else {
				Function.toastMsg(taskDesActivity.this, errorString);
				comFunction.outtoLogin(taskDesActivity.this, errorString, errorCode, isPreferences);
				errorString = null;
			}

		}

	}

	@SuppressWarnings("unused")
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
	protected void onResume() {
		super.onResume();
		isPreferences.updateSp("tk_state_bool", false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_back:
			finish();
			break;
		case R.id.tv_select_map:
			startActivity(new Intent(taskDesActivity.this, LookMapActivity.class)
					.putExtra("st_latitudee6", tk_latitudee6).putExtra("st_longitudee6", tk_longitudee6)
					.putExtra("city_x", city_x).putExtra("city_y", city_y));
			break;
		default:
			break;
		}

	}

}
