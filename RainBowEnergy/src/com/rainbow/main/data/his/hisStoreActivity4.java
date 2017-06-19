package com.rainbow.main.data.his;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.comutils.main.DateDialog;
import com.comutils.main.DateDialog2;
import com.comutils.main.Function;
import com.comutils.main.SharePreferences;
import com.comutils.net.HttpUtil;
import com.comutils.rain_view.MyListView;
import com.comutils.util.UrlUtil;
import com.rainbow.main.R;
import com.rainbow.main.function.comFunction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class hisStoreActivity4 extends Activity implements OnClickListener {
	DataInfoTask iDataInfoTask;
	MyListView lv_his;
	HorizontalScrollView hs_view;
	SimpleAdapter isSimpleAdapter;
	LinearLayout ll_top;
	LinearLayout include_top_2;
	int ll_height = 0;
	int include_height = 0;
	int chart_height = 0;
	private LineChartView chart, chart2;

	private LineChartData data;
	private LineChartData data2;
	int right = 24;
	int right2 = 24;
	private boolean hasAxes = true;
	private boolean hasAxesNames = true;
	private ValueShape shape = ValueShape.CIRCLE;
	private boolean pointsHaveDifferentColor;

	float all_max, all_min;
	int all_max_one, all_min_one;
	float all_max_two, all_min_two;

	int length1, length2;

	TextView tv_year_month_select, tv_month_day_select;

	DateDialog mDateDialog = null;
	DateDialog2 mDateDialog2 = null;
	ArrayList<Float> datalist1 = new ArrayList<Float>();
	ArrayList<Float> datalist2 = new ArrayList<Float>();
	ArrayList<Integer> datalist3 = new ArrayList<Integer>();

	private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
	private List<AxisValue> mAxisYValues = new ArrayList<AxisValue>();
	private List<AxisValue> mAxisXValues2 = new ArrayList<AxisValue>();
	private List<AxisValue> mAxisYValues2 = new ArrayList<AxisValue>();

	ArrayList<Float> ydatalist = new ArrayList<Float>();
	ArrayList<Float> ydatalist2 = new ArrayList<Float>();

	
	ArrayList<String> xdatalist = new ArrayList<String>();
	ArrayList<String> xdatalist2 = new ArrayList<String>();

	
	SharePreferences isPreferences = null;
	
	UrlUtil mUrlUtil;
	private void addxdatalist() {
		xdatalist.add("");
		xdatalist.add("");
		xdatalist.add("");
		xdatalist.add("03:00");
		xdatalist.add("");
		xdatalist.add("");
		xdatalist.add("06:00");
		xdatalist.add("");
		xdatalist.add("");
		xdatalist.add("09:00");
		xdatalist.add("");
		xdatalist.add("");
		xdatalist.add("12:00");
		xdatalist.add("");
		xdatalist.add("");
		xdatalist.add("15:00");
		xdatalist.add("");
		xdatalist.add("");
		xdatalist.add("18:00");
		xdatalist.add("");
		xdatalist.add("");
		xdatalist.add("21:00");
		xdatalist.add("");
		xdatalist.add("");

	}

	private void addxdatalist_month() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int len = 0;
		if (year % 4 == 0) {// 闰年
			if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
				len = 31;
			} else if (month == 2) {
				len = 29;
			} else {
				len = 30;
			}
		} else {
			if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
				len = 31;
			} else if (month == 2) {
				len = 28;
			} else {
				len = 30;
			}
		}
		for (int i = 0; i <= len; i++) {
			if (((i+1) + "").length() == 1) {
				xdatalist.add("0" + i);
			} else {
				xdatalist.add("" + i);
			}
		}

	}

	private void addxdatalist2() {
		xdatalist2.add("");
		xdatalist2.add("");
		xdatalist2.add("");
		xdatalist2.add("03:00");
		xdatalist2.add("");
		xdatalist2.add("");
		xdatalist2.add("06:00");
		xdatalist2.add("");
		xdatalist2.add("");
		xdatalist2.add("09:00");
		xdatalist2.add("");
		xdatalist2.add("");
		xdatalist2.add("12:00");
		xdatalist2.add("");
		xdatalist2.add("");
		xdatalist2.add("15:00");
		xdatalist2.add("");
		xdatalist2.add("");
		xdatalist2.add("18:00");
		xdatalist2.add("");
		xdatalist2.add("");
		xdatalist2.add("21:00");
		xdatalist2.add("");
		xdatalist2.add("");
		xdatalist2.add("");
	}

	private void addxdatalist_month2() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int len = 0;
		if (year % 4 == 0) {// 闰年
			if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
				len = 31;
			} else if (month == 2) {
				len = 29;
			} else {
				len = 30;
			}
		} else {
			if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
				len = 31;
			} else if (month == 2) {
				len = 28;
			} else {
				len = 30;
			}
		}
		for (int i = 0; i <= len; i++) {
			if (((i+1) + "").length() == 1) {
				xdatalist2.add("0" + i);
			} else {
				xdatalist2.add("" + i);
			}
		}

	}

	float[][] randomNumbersTab = new float[length1][xdatalist.size()];
	float[][] randomNumbersTab2 = new float[length1][xdatalist2.size()];
	private float dw;
	private float dw2;

	int height = 0;
	int width = 0;
	int size = 0;
	String graf_from = "";
	String measpoint_id = "";
	String type = "1";
	String time = "";
	String unit = "";
	String st_name = "";
	String unit_one, unit_two;

	List<HashMap<String, Object>> list1 = null;
	List<List<HashMap<String, Object>>> list2 = null;
	List<HashMap<String, Object>> wlist = null;
	List<List<HashMap<String, Object>>> timelist = null;

	List<List<HashMap<String, Object>>> alltimelist = null;
	List<HashMap<String, Object>> alltimewlist = null;
	List<HashMap<String, Object>> timewlist = null;

	List<List<HashMap<String, Object>>> timelist2 = null;
	List<List<HashMap<String, Object>>> alltimelist2 = null;
	List<HashMap<String, Object>> alltimewlist2 = null;
	List<HashMap<String, Object>> timewlist2 = null;

	
	
	LinearLayout ll_bar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		isPreferences = new SharePreferences(this);
		mUrlUtil = UrlUtil.getInstance();
		list1 = new ArrayList<HashMap<String, Object>>();
		list2 = new ArrayList<List<HashMap<String, Object>>>();
		timelist = new ArrayList<List<HashMap<String, Object>>>();
		alltimelist = new ArrayList<List<HashMap<String, Object>>>();
		timelist2 = new ArrayList<List<HashMap<String, Object>>>();
		alltimelist2 = new ArrayList<List<HashMap<String, Object>>>();

		Intent i = getIntent();
		Bundle b = i.getExtras();
		graf_from = b.getString("graf_from");
		measpoint_id = "";
		measpoint_id = b.getString("measpoint_id");
		st_name = b.getString("st_name");
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		time = String.format("%d-%02d-%02d", year, month + 1, day);

		if (graf_from.equals("2")) {
			System.out.println("data list == " + graf_from);
			setContentView(R.layout.his3);
			initViewTop();

			chart = (LineChartView) findViewById(R.id.linechart);
			chart2 = (LineChartView) findViewById(R.id.linechart2);
			ll_top = (LinearLayout) findViewById(R.id.ll_top);
			hs_view = (HorizontalScrollView) findViewById(R.id.hs_view);
			include_top_2 = (LinearLayout) findViewById(R.id.include_top2);
			lv_his = (MyListView) findViewById(R.id.lv_his);
			lv_his.setDividerHeight(0);
			lv_his.setDivider(new ColorDrawable(getResources().getColor(R.color.cr_gray1)));
			lv_his.setDividerHeight((int) (getResources().getDisplayMetrics().density * 5));
			lv_his.setDivider(new ColorDrawable(R.color.cr_gray3));
			isSimpleAdapter = new SimpleAdapter(this, list1, R.layout.his_item,
					new String[] { "no", "devname", "measpoint_name", "value", "unit" },
					new int[] { R.id.tv_no, R.id.tv_devname, R.id.tv_measpoint_name, R.id.tv_value, R.id.tv_unit });
			lv_his.setAdapter(isSimpleAdapter);
			getDataInfo();
		}

	}

	private void initViewTop() {

		findViewById(R.id.tv_back).setOnClickListener(this);

		ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
		((TextView) findViewById(R.id.tv_title)).setText("历史曲线");
		tv_year_month_select = (TextView) findViewById(R.id.tv_year_month_select);
		tv_month_day_select = (TextView) findViewById(R.id.tv_month_day_select);
		tv_year_month_select.setOnClickListener(this);
		tv_month_day_select.setOnClickListener(this);

	}

	public void initCW2() {
		xdatalist2.clear();
		int len = 0;
		if(type.equals("1")){
			addxdatalist2();
			len = (xdatalist2.size() / 5) * 3;
		}else{
			addxdatalist_month2();
			len = xdatalist2.size();
		}
		float cv_w = 0, minweight = 0;
		cv_w += getTWidth("100");
		minweight = getTWidth("22.22");
		for (int i = 0; i < len; i++) {
			if (minweight > getTWidth(xdatalist2.get(i))) {
				cv_w += minweight;
			} else {
				cv_w += getTWidth(xdatalist2.get(i));
			}
		}
		if (cv_w < dw2)
			cv_w = dw2;
		chart2.setLayoutParams(new LinearLayout.LayoutParams((int) cv_w, LayoutParams.FILL_PARENT));
	}

	public void initCW() {
		xdatalist.clear();
		int len = 0;
		if (type.equals("1")) {
			addxdatalist();
			len = (xdatalist.size() / 5) * 3;
		} else {
			addxdatalist_month();
			len = xdatalist.size();
		}
		float cv_w = 0, minweight = 0;
		cv_w += getTWidth("100");
		minweight = getTWidth("22.22");
		for (int i = 0; i < len; i++) {
			if (minweight > getTWidth(xdatalist.get(i))) {
				cv_w += minweight;
			} else {
				cv_w += getTWidth(xdatalist.get(i));
			}
		}
		if (cv_w < dw)
			cv_w = dw;
		chart.setLayoutParams(new LinearLayout.LayoutParams((int) cv_w, LayoutParams.FILL_PARENT));
	}

	private void getDataInfo() {
		if (Function.isWiFi_3G(this)) {
			if (iDataInfoTask == null) {
				iDataInfoTask = new DataInfoTask();
				iDataInfoTask.execute();
			}
		} else {
			Function.toastMsg(this, getString(R.string.tv_not_netlink));
		}
	}


	private class DataInfoTask extends AsyncTask<String, Void, String> {
		@SuppressWarnings("deprecation")
		List<NameValuePair> paramList = null;
		JSONObject jsonj = null;
		JSONArray jarray = null;
		JSONArray tarray = null;
		String errorString = null;
		int errorCode = 0;

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ll_bar.setVisibility(View.VISIBLE);
			errorString = null;
			paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("measpoint_id", measpoint_id));
			paramList.add(new BasicNameValuePair("type", type));
			paramList.add(new BasicNameValuePair("dates", time));
			paramList.add(new BasicNameValuePair("app_mid", isPreferences.getSp().getString("m_id", "")));
			paramList.add(new BasicNameValuePair("mtoken", isPreferences.getSp().getString("m_token", "")));
			Log.i("", "tag 111 == " + measpoint_id + ".." + type + ".." + time);
		}

		@Override
		protected String doInBackground(String... params) {
			String result = HttpUtil.queryStringForPost(mUrlUtil.his_data, paramList);
			Log.i("", "tag 111 == " + result);
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
				list1.clear();
				list2.clear();
				jarray = jsonj.getJSONArray("data");
				length1 = jarray.length();
				Log.i("", "tag 1111 == " + jarray);
				for (int i = 0; i < jarray.length(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					String devname = jarray.getJSONObject(i).getString("devname").toString().replace("null", "")==""?st_name:jarray.getJSONObject(i).getString("devname").toString().replace("null", "");
					map.put("devname", devname);
					map.put("measpoint_name",
							jarray.getJSONObject(i).getString("measpoint_name").toString().replace("null", ""));
					map.put("value", jarray.getJSONObject(i).getString("value").toString().replace("null", ""));
					map.put("unit", jarray.getJSONObject(i).getString("unit").toString().replace("null", ""));
					map.put("graf_color",
							jarray.getJSONObject(i).getString("graf_color").toString().replace("null", ""));
					map.put("min", jarray.getJSONObject(i).getString("min").toString().replace("null", ""));
					map.put("max", jarray.getJSONObject(i).getString("max").toString().replace("null", ""));
					map.put("no", (i + 1) + "");

					tarray = new JSONArray(jarray.getJSONObject(i).getString("values"));
					length2 = tarray.length();

					map.put("length2", length2);
					list1.add(map);

					wlist = new ArrayList<HashMap<String, Object>>();
					for (int j = 0; j < tarray.length(); j++) {
						HashMap<String, Object> map2 = new HashMap<String, Object>();
						map2.put("value", tarray.getJSONObject(j).getString("value").toString().replace("null", ""));
						map2.put("datetime",
								tarray.getJSONObject(j).getString("datetime").toString().replace("null", ""));
						wlist.add(map2);
					}
					list2.add(wlist);

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
				isSimpleAdapter.notifyDataSetChanged();
			
				if(wlist.size()<=0){
					chart.setVisibility(View.GONE);
					chart2.setVisibility(View.GONE);
					Function.toastMsg(hisStoreActivity4.this, getString(R.string.tv_no_shuju));
				}
				
				if (length1 == 1) {
					hs_view.setVisibility(View.GONE);
					chart.setVisibility(View.VISIBLE);
					chart2.setVisibility(View.GONE);
					FristQuXian();

				} else if (length1 == 2) {
					// 第一个曲线图
					chart.setVisibility(View.VISIBLE);
					chart2.setVisibility(View.VISIBLE);
					FristQuXian();
					SecondQuXian();

				}

			} else {
				Function.toastMsg(hisStoreActivity4.this, errorString);
				chart2.setVisibility(View.GONE);
				comFunction.outtoLogin(hisStoreActivity4.this, errorString, errorCode,isPreferences);
				errorString = null;
			}

		}

	}

	private void FristQuXian() {
		float min_one = Float.parseFloat(list1.get(0).get("min") + "");
		float max_one = Float.parseFloat(list1.get(0).get("max") + "");
		float beishu_one = (float)((max_one - min_one) *0.1);
		unit_one = list1.get(0).get("unit") + "";

		System.out.println("unit == " + unit_one +" min_one = "+min_one +" , max_one = "+max_one);
		// 0.18 0.27
		if (min_one > 0 && max_one > 1) {
			System.out.println("unit2 ==  min_one > 0 && max_one > 1" );
			min_one = Function.float1number(min_one);
			max_one = Function.float1number(max_one);
			beishu_one = Function.float1number(beishu_one);
		}

			if (min_one > 0 && max_one > 1) {
				System.out.println("unit2 ==  all_min > 0 && all_max > 1" );
				all_min = Function.float1number(min_one);
				all_max = Function.float1number(max_one);
				
				Log.i("tag", "tag all_min = "+all_min +" , all_max = "+all_max);
				/*ydatalist.add(Function.float1number(min_one - beishu_one * 2));
				ydatalist.add(Function.float1number(min_one - beishu_one));*/
				ydatalist.add(Function.float1number(min_one));
				ydatalist.add(Function.float1number(min_one + beishu_one));
				ydatalist.add(Function.float1number(min_one + beishu_one * 2));
				ydatalist.add(Function.float1number(min_one + beishu_one * 3));
				ydatalist.add(Function.float1number(min_one + beishu_one * 4));
				ydatalist.add(Function.float1number(min_one + beishu_one * 5));
				ydatalist.add(Function.float1number(min_one + beishu_one * 6));
				ydatalist.add(Function.float1number(min_one + beishu_one * 7));
				ydatalist.add(Function.float1number(min_one + beishu_one * 8));
				ydatalist.add(Function.float1number(min_one + beishu_one * 9));
				ydatalist.add(Function.float1number(min_one + beishu_one * 10));
			
			} else {
				all_min = Function.float2number(min_one);
				all_max = Function.float2number(max_one);
				/*ydatalist.add(Function.float2number(min_one - beishu_one * 2));
				ydatalist.add(Function.float2number(min_one - beishu_one));*/
				ydatalist.add(Function.float2number(min_one));
				ydatalist.add(Function.float2number(min_one + beishu_one));
				ydatalist.add(Function.float2number(min_one + beishu_one * 2));
				ydatalist.add(Function.float2number(min_one + beishu_one * 3));
				ydatalist.add(Function.float2number(min_one + beishu_one * 4));
				ydatalist.add(Function.float2number(min_one + beishu_one * 5));
				ydatalist.add(Function.float2number(min_one + beishu_one * 6));
				ydatalist.add(Function.float2number(min_one + beishu_one * 7));
				ydatalist.add(Function.float2number(min_one + beishu_one * 8));
				ydatalist.add(Function.float2number(min_one + beishu_one * 9));
				ydatalist.add(Function.float2number(min_one + beishu_one * 10));
				
			}
			if (type.equals("1")) {
				addxdatalist();
			} else {// 月份
				addxdatalist_month();
			}
		if (type.equals("2")) {// 月份的选择
			for (int j = 0; j < 1; j++) {
				alltimewlist = new ArrayList<HashMap<String, Object>>();
				if (j == 0) {
					for (int i = 0; i < Integer.parseInt((list1.get(0).get("length2") + "")); i++) {
						datalist1.add(Float.parseFloat(list2.get(0).get(i).get("value") + ""));
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("date_time", list2.get(0).get(i).get("datetime") + "");
						alltimewlist.add(map);
					}
					alltimelist.add(alltimewlist);
				}
			}

			int length = 0;
			int length1 = Integer.parseInt((list1.get(0).get("length2") + ""));
			length = length1;

			ViewTreeObserver vto = ll_top.getViewTreeObserver();
			vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {
					ll_height = ll_top.getMeasuredHeight();
					return true;
				}
			});
			ViewTreeObserver vto2 = include_top_2.getViewTreeObserver();
			vto2.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {
					include_height = include_top_2.getMeasuredHeight();
					return true;
				}
			});

			ViewTreeObserver vto3 = chart.getViewTreeObserver();
			vto3.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {
					chart_height = chart.getMeasuredHeight();
					return true;
				}
			});

			randomNumbersTab = new float[1][length];
			if (list2.size() != 0) {
				timesub3();
			}

		} else if (type.equals("1")) {// 日期的选择
			for (int j = 0; j < 1; j++) {
				alltimewlist = new ArrayList<HashMap<String, Object>>();
				if (j == 0) {
					for (int i = 0; i < Integer.parseInt((list1.get(0).get("length2") + "")); i++) {
						
						
						String time = (list2.get(0).get(i).get("datetime") + "").substring(0,
								(list2.get(0).get(i).get("datetime") + "").length() - 3);
						
						if(i == 0){
							datalist1.add(Float.parseFloat(list2.get(0).get(i).get("value") + ""));
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("date_time", list2.get(0).get(i).get("datetime") + "");
							alltimewlist.add(map);
						}else{
							if(time.equals((list2.get(0).get(i - 1).get("datetime") + "").substring(0,
									(list2.get(0).get(i - 1).get("datetime") + "").length() - 3))){
							}else{
								datalist1.add(Float.parseFloat(list2.get(0).get(i).get("value") + ""));
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("date_time", list2.get(0).get(i).get("datetime") + "");
								alltimewlist.add(map);
							}
						}
						
						
						/*String time = (list2.get(0).get(i).get("datetime") + "").substring(14,
								(list2.get(0).get(i).get("datetime") + "").length() - 3);
						String time2 = "";
						if (i < (Integer.parseInt((list1.get(0).get("length2") + "")) - 1)) {
							time2 = (list2.get(0).get(i + 1).get("datetime") + "").substring(14,
									(list2.get(0).get(i + 1).get("datetime") + "").length() - 3);
						}

						String time3 = "";
						if (i < (Integer.parseInt((list1.get(0).get("length2") + "")) - 2)) {
							time3 = (list2.get(0).get(i + 2).get("datetime") + "").substring(14,
									(list2.get(0).get(i + 2).get("datetime") + "").length() - 3);
						}

						String time4 = "";
						if (i < (Integer.parseInt((list1.get(0).get("length2") + "")) - 3)) {
							time4 = (list2.get(0).get(i + 3).get("datetime") + "").substring(14,
									(list2.get(0).get(i + 3).get("datetime") + "").length() - 3);
						}

						
						String time5 = "";
						if (i < (Integer.parseInt((list1.get(0).get("length2") + "")) - 4)) {
							time5 = (list2.get(0).get(i + 4).get("datetime") + "").substring(14,
									(list2.get(0).get(i + 4).get("datetime") + "").length() - 3);
						}

						String time6 = "";
						if (i < (Integer.parseInt((list1.get(0).get("length2") + "")) - 5)) {
							time6 = (list2.get(0).get(i + 5).get("datetime") + "").substring(14,
									(list2.get(0).get(i + 5).get("datetime") + "").length() - 3);
						}
						
						if (Integer.parseInt(time) == 30 || Integer.parseInt(time) == 00) {
							if (time.equals(time2) && time.equals(time3) && time.equals(time4) && time.equals(time5) && time.equals(time6)) {
								datalist1.add(Float.parseFloat(list2.get(0).get(i).get("value") + ""));
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("date_time", list2.get(0).get(i).get("datetime") + "");
								alltimewlist.add(map);
								System.out.println("datalist1 === " + list2.get(0).get(i).get("value") + "");
								i += 5;
							}else if (time.equals(time2) && time.equals(time3) && time.equals(time4) && time.equals(time5) && !time.equals(time6)) {
								datalist1.add(Float.parseFloat(list2.get(0).get(i).get("value") + ""));
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("date_time", list2.get(0).get(i).get("datetime") + "");
								alltimewlist.add(map);
								System.out.println("datalist1 === " + list2.get(0).get(i).get("value") + "");
								i += 4;
							}else if (time.equals(time2) && time.equals(time3) && time.equals(time4) && !time.equals(time5) && !time.equals(time6)) {
								datalist1.add(Float.parseFloat(list2.get(0).get(i).get("value") + ""));
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("date_time", list2.get(0).get(i).get("datetime") + "");
								alltimewlist.add(map);
								System.out.println("datalist1 === " + list2.get(0).get(i).get("value") + "");
								i += 3;
							}else if (time.equals(time2) && time.equals(time3) && !time.equals(time4) && !time.equals(time5) && !time.equals(time6)) {
								datalist1.add(Float.parseFloat(list2.get(0).get(i).get("value") + ""));
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("date_time", list2.get(0).get(i).get("datetime") + "");
								alltimewlist.add(map);
								System.out.println("datalist1 === " + list2.get(0).get(i).get("value") + "");
								i += 2;
							}else if (time.equals(time2) && !time.equals(time3) && !time.equals(time4) && !time.equals(time5) && !time.equals(time6)) {
								datalist1.add(Float.parseFloat(list2.get(0).get(i).get("value") + ""));
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("date_time", list2.get(0).get(i).get("datetime") + "");
								alltimewlist.add(map);
								System.out.println("datalist1 === " + list2.get(0).get(i).get("value") + "");
								i += 1;
							}else{
								datalist1.add(Float.parseFloat(list2.get(0).get(i).get("value") + ""));
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("date_time", list2.get(0).get(i).get("datetime") + "");
								alltimewlist.add(map);
								System.out.println("datalist1 === " + list2.get(0).get(i).get("value") + "");
							}
						}*/
					}
					alltimelist.add(alltimewlist);
				}
			}
			int length = 0;
			int length1 = Integer.parseInt((list1.get(0).get("length2") + ""));
			length = length1;

			ViewTreeObserver vto = ll_top.getViewTreeObserver();
			vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {
					ll_height = ll_top.getMeasuredHeight();
					return true;
				}
			});
			ViewTreeObserver vto2 = include_top_2.getViewTreeObserver();
			vto2.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {
					include_height = include_top_2.getMeasuredHeight();
					return true;
				}
			});

			ViewTreeObserver vto3 = chart.getViewTreeObserver();
			vto3.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {
					chart_height = chart.getMeasuredHeight();
					return true;
				}
			});

			randomNumbersTab = new float[1][length];
			if (list2.size() != 0) {
				timesub();
			}
		}
	}

	private void SecondQuXian() {
		// 第二个曲线图
		float min_two = Float.parseFloat(list1.get(1).get("min") + "");
		float max_two = Float.parseFloat(list1.get(1).get("max") + "");
		float beishu_two = (float)((max_two - min_two) *0.1);

		unit_two = list1.get(1).get("unit") + "";

		System.out.println("unit2 ==" + unit_two +" , min_two = "+min_two +" , max_two = "+max_two);
		if (min_two > 0 && max_two > 1) {
			System.out.println("unit2 ==  min_two > 0 && max_two > 1" );
			
			min_two = Function.float1number(min_two);
			max_two = Function.float1number(max_two);
			beishu_two = Function.float1number(beishu_two);
		}
			if (min_two > 0 && max_two > 1) {
				System.out.println("unit2 ==  all_min_two > 0 && all_max_two > 1" );
				all_min_two = Function.float1number(min_two);
				all_max_two = Function.float1number(max_two);
			
				/*ydatalist2.add(Function.float1number(min_two - beishu_two * 2));
				ydatalist2.add(Function.float1number(min_two - beishu_two));*/
				ydatalist2.add(Function.float1number(min_two));
				ydatalist2.add(Function.float1number(min_two + beishu_two));
				ydatalist2.add(Function.float1number(min_two + beishu_two * 2));
				ydatalist2.add(Function.float1number(min_two + beishu_two * 3));
				ydatalist2.add(Function.float1number(min_two + beishu_two * 4));
				ydatalist2.add(Function.float1number(min_two + beishu_two * 5));
				ydatalist2.add(Function.float1number(min_two + beishu_two * 6));
				ydatalist2.add(Function.float1number(min_two + beishu_two * 7));
				ydatalist2.add(Function.float1number(min_two + beishu_two * 8));
				ydatalist2.add(Function.float1number(min_two + beishu_two * 9));
				ydatalist2.add(Function.float1number(min_two + beishu_two * 10));
				
			} else {
				all_min_two = Function.float2number(min_two);
				all_max_two = Function.float2number(max_two);
			/*	
				ydatalist2.add(Function.float2number(min_two - beishu_two * 2));
				ydatalist2.add(Function.float2number(min_two - beishu_two));*/
				ydatalist2.add(Function.float2number(min_two));
				ydatalist2.add(Function.float2number(min_two + beishu_two));
				ydatalist2.add(Function.float2number(min_two + beishu_two * 2));
				ydatalist2.add(Function.float2number(min_two + beishu_two * 3));
				ydatalist2.add(Function.float2number(min_two + beishu_two * 4));
				ydatalist2.add(Function.float2number(min_two + beishu_two * 5));
				ydatalist2.add(Function.float2number(min_two + beishu_two * 6));
				ydatalist2.add(Function.float2number(min_two + beishu_two * 7));
				ydatalist2.add(Function.float2number(min_two + beishu_two * 8));
				ydatalist2.add(Function.float2number(min_two + beishu_two * 9));
				ydatalist2.add(Function.float2number(min_two + beishu_two * 10));
			}
			if (type.equals("1")) {
				addxdatalist2();
			} else {
				addxdatalist_month2();
			}
	
		if(type.equals("2")){
			for (int j = 0; j < 1; j++) {
				alltimewlist2 = new ArrayList<HashMap<String, Object>>();
				if (j == 0) {
					for (int i = 0; i < Integer.parseInt((list1.get(1).get("length2") + "")); i++) {
						datalist2.add(Float.parseFloat(list2.get(1).get(i).get("value") + ""));
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("date_time", list2.get(1).get(i).get("datetime") + "");
						alltimewlist2.add(map);
					}
					alltimelist2.add(alltimewlist2);
				}
			}
			
			int length2 = Integer.parseInt((list1.get(1).get("length2") + ""));
			randomNumbersTab2 = new float[1][length2];

			if (list2.size() != 0) {
				timesub4();
			}
			
		}else if (type.equals("1")) {
			for (int j = 0; j < 1; j++) {
				alltimewlist2 = new ArrayList<HashMap<String, Object>>();
				if (j == 0) {
					for (int i = 0; i < Integer.parseInt((list1.get(1).get("length2") + "")); i++) {
						String time = (list2.get(1).get(i).get("datetime") + "").substring(0,
								(list2.get(1).get(i).get("datetime") + "").length() - 3);
						
						if(i == 0){
							datalist2.add(Float.parseFloat(list2.get(1).get(i).get("value") + ""));
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("date_time", list2.get(1).get(i).get("datetime") + "");
							alltimewlist2.add(map);
						}else{
							if(time.equals((list2.get(1).get(i - 1).get("datetime") + "").substring(0,
									(list2.get(1).get(i - 1).get("datetime") + "").length() - 3))){
							}else{
								datalist2.add(Float.parseFloat(list2.get(1).get(i).get("value") + ""));
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("date_time", list2.get(1).get(i).get("datetime") + "");
								alltimewlist2.add(map);
							}
						}
						
						/*String time = (list2.get(1).get(i).get("datetime") + "").substring(14,
								(list2.get(1).get(i).get("datetime") + "").length() - 3);
						String time2 = "";
						if (i < (Integer.parseInt((list1.get(1).get("length2") + "")) - 1)) {
							time2 = (list2.get(1).get(i + 1).get("datetime") + "").substring(14,
									(list2.get(1).get(i + 1).get("datetime") + "").length() - 3);
						}
						String time3 = "";
						if (i < (Integer.parseInt((list1.get(1).get("length2") + "")) - 2)) {
							time3 = (list2.get(1).get(i + 2).get("datetime") + "").substring(14,
									(list2.get(1).get(i + 2).get("datetime") + "").length() - 3);
						}
						String time4 = "";
						if (i < (Integer.parseInt((list1.get(1).get("length2") + "")) - 3)) {
							time4 = (list2.get(1).get(i + 3).get("datetime") + "").substring(14,
									(list2.get(1).get(i + 3).get("datetime") + "").length() - 3);
						}
						String time5 = "";
						if (i < (Integer.parseInt((list1.get(1).get("length2") + "")) - 4)) {
							time5 = (list2.get(1).get(i + 4).get("datetime") + "").substring(14,
									(list2.get(1).get(i + 4).get("datetime") + "").length() - 3);
						}
						String time6 = "";
						if (i < (Integer.parseInt((list1.get(1).get("length2") + "")) - 5)) {
							time6 = (list2.get(1).get(i + 5).get("datetime") + "").substring(14,
									(list2.get(1).get(i + 5).get("datetime") + "").length() - 3);
						}

						if (Integer.parseInt(time) == 30 || Integer.parseInt(time) == 00) {
							if (time.equals(time2) && time.equals(time3) && time.equals(time4) && time.equals(time5) && time.equals(time6)) {
								datalist2.add(Float.parseFloat(list2.get(1).get(i).get("value") + ""));
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("date_time", list2.get(1).get(i).get("datetime") + "");
								alltimewlist2.add(map);
								i += 5;
							}else if (time.equals(time2) && time.equals(time3) && time.equals(time4) && time.equals(time5) && !time.equals(time6)) {
								datalist2.add(Float.parseFloat(list2.get(1).get(i).get("value") + ""));
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("date_time", list2.get(1).get(i).get("datetime") + "");
								alltimewlist2.add(map);
								i += 4;
							}else if (time.equals(time2) && time.equals(time3) && time.equals(time4) && !time.equals(time5) && !time.equals(time6)) {
								datalist2.add(Float.parseFloat(list2.get(1).get(i).get("value") + ""));
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("date_time", list2.get(1).get(i).get("datetime") + "");
								alltimewlist2.add(map);
								i += 3;
							}else if (time.equals(time2) && time.equals(time3) && !time.equals(time4) && !time.equals(time5) && !time.equals(time6)) {
								datalist2.add(Float.parseFloat(list2.get(1).get(i).get("value") + ""));
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("date_time", list2.get(1).get(i).get("datetime") + "");
								alltimewlist2.add(map);
								i += 2;
							}else if (time.equals(time2) && !time.equals(time3) && !time.equals(time4) && !time.equals(time5) && !time.equals(time6)) {
								datalist2.add(Float.parseFloat(list2.get(1).get(i).get("value") + ""));
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("date_time", list2.get(1).get(i).get("datetime") + "");
								alltimewlist2.add(map);
								i += 1;
							}else{
								datalist2.add(Float.parseFloat(list2.get(1).get(i).get("value") + ""));
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("date_time", list2.get(1).get(i).get("datetime") + "");
								alltimewlist2.add(map);
							}
						}*/
					}
					alltimelist2.add(alltimewlist2);
				}
			}

			int length2 = Integer.parseInt((list1.get(1).get("length2") + ""));
			randomNumbersTab2 = new float[1][length2];

			if (list2.size() != 0) {
				timesub2();
			}
		}

	}

	private void generateValues2() {
		for (int i = 0; i < 1; ++i) {

			System.out.println("data list = " + datalist2.size());
			if (i == 0) {
				for (int j = 0; j < datalist2.size(); ++j) {
					randomNumbersTab2[i][j] = datalist2.get(j);
				}
			}
		}
	}

	private void generateData2() {

		List<Line> lines = new ArrayList<Line>();
		for (int i = 0; i < 1; ++i) {

			List<PointValue> values = new ArrayList<PointValue>();
			for (int j = 0; j < timelist2.get(i).size(); ++j) {
				values.add(new PointValue(Float.parseFloat(timelist2.get(i).get(j).get("show_time") + ""),
						randomNumbersTab2[i][j]));
			}

			Line line = new Line(values);
			// 设置颜色
			list1.get(i).get("graf_color");

			line.setColor(ChartUtils.COLORS[Integer.parseInt(list1.get(1).get("graf_color") + "")]);
			line.setShape(shape);
			line.setCubic(true);
			line.setFilled(false);
			line.setPointRadius(4);// 设置节点半径
			line.setHasLines(true);// 是否显示折线
			line.setHasLabels(true);// 是否显示节点数据
			line.setHasPoints(true);// 是否显示节点
			line.setShape(ValueShape.CIRCLE);// 节点图形样式
												// DIAMOND菱形、SQUARE方形、CIRCLE圆形
			line.setHasLabelsOnlyForSelected(true);// 隐藏数据，触摸可以显示
			if (pointsHaveDifferentColor) {
				line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
			}
			lines.add(line);
		}

		data2 = new LineChartData(lines);

		if (hasAxes) {
			Axis axisX = new Axis().setHasLines(true);
			Axis axisY = new Axis().setHasLines(true);

			for (int i = 0; i < xdatalist2.size(); i++) {
				mAxisXValues2.add(new AxisValue(i).setLabel(xdatalist2.get(i)));
				System.out.println("data list = xzhou = " + xdatalist2.get(i));
			}
			// y
			for (int j = 0; j < ydatalist2.size(); j++) {
				mAxisYValues2.add(new AxisValue((ydatalist2.get(j))).setLabel(ydatalist2.get(j) + unit_two));// 添加Y轴显示的刻度值
			}
			axisX.setValues(mAxisXValues2); // 填充X轴的坐标名称
			axisY.setValues(mAxisYValues2);
			axisX.setHasTiltedLabels(true);// 设置X轴文字向左旋转45度
			axisY.setMaxLabelChars(8);
			axisX.setTextColor(Color.BLACK);
			axisY.setTextColor(Color.BLACK);
			if (hasAxesNames) {
				axisX.setName("");
				axisY.setName("");
			}
			data2.setAxisXBottom(axisX);
			data2.setAxisYLeft(axisY);
		} else {
			data2.setAxisXBottom(null);
			data2.setAxisYLeft(null);
		}

		data2.setValueLabelBackgroundAuto(true);// 设置数据背景是否跟随节点颜色
		data2.setValueLabelBackgroundColor(Color.WHITE);// 设置数据背景颜色
		data2.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
		data2.setValueLabelsTextColor(getResources().getColor(R.color.white));// 设置数据文字颜色
		data2.setValueLabelTextSize(0);// 设置数据文字大小
		data2.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式

		data2.setBaseValue(Float.NEGATIVE_INFINITY);
		initCW2();
		chart2.setValueSelectionEnabled(false);
		chart2.setInteractive(true);
		chart2.setLineChartData(data2);
		chart2.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.popshow_anim);
		chart2.setAnimation(animation);

	}

	private void resetViewport2() {
		// Reset viewport height range to (0,100)
		final Viewport v = new Viewport(chart2.getMaximumViewport());
		v.bottom = all_min_two;
		v.top = all_max_two;
		v.left = 0;
		v.right = right2;
		chart2.setMaximumViewport(v);
		chart2.setCurrentViewport(v);
	}

	private void resetViewport() {
		final Viewport v = new Viewport(chart.getMaximumViewport());
		System.out.println("all_min_one" + all_min_one);
		v.bottom = all_min;
		v.top = all_max;
		v.left = 0;
		v.right = right;
		chart.setMaximumViewport(v);
		chart.setCurrentViewport(v);
	}

	private void generateValues() {
		for (int i = 0; i < 1; ++i) {

			System.out.println("data list = " + datalist1.size());
			if (i == 0) {
				for (int j = 0; j < datalist1.size(); ++j) {
					randomNumbersTab[i][j] = datalist1.get(j);
				}
			}

		}
	}

	private void generateData() {

		List<Line> lines = new ArrayList<Line>();
		for (int i = 0; i < 1; ++i) {

			List<PointValue> values = new ArrayList<PointValue>();
			for (int j = 0; j < timelist.get(i).size(); ++j) {
				values.add(new PointValue(Float.parseFloat(timelist.get(i).get(j).get("show_time") + ""),
						randomNumbersTab[i][j]));
			}

			Line line = new Line(values);
			// 设置颜色
			list1.get(i).get("graf_color");

			line.setColor(ChartUtils.COLORS[Integer.parseInt(list1.get(i).get("graf_color") + "")]);
			line.setShape(shape);
			line.setCubic(true);
			line.setFilled(false);
			// line.setHasLabels(hasLabels);
			// line.setPointColor(Color.WHITE);//设置节点颜色
			line.setPointRadius(4);// 设置节点半径
			line.setHasLines(true);// 是否显示折线
			line.setHasLabels(true);// 是否显示节点数据
			line.setHasPoints(true);// 是否显示节点
			line.setShape(ValueShape.CIRCLE);// 节点图形样式
												// DIAMOND菱形、SQUARE方形、CIRCLE圆形
			line.setHasLabelsOnlyForSelected(true);// 隐藏数据，触摸可以显示
			if (pointsHaveDifferentColor) {
				line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
			}
			lines.add(line);
		}

		data = new LineChartData(lines);

		if (hasAxes) {
			Axis axisX = new Axis().setHasLines(true);
			Axis axisY = new Axis().setHasLines(true);

			for (int i = 0; i < xdatalist.size(); i++) {
				mAxisXValues.add(new AxisValue((float) (i)).setLabel(xdatalist.get(i)));
			}
			// y
			for (int j = 0; j < ydatalist.size(); j++) {
				mAxisYValues.add(new AxisValue((ydatalist.get(j))).setLabel(ydatalist.get(j) + unit_one));// 添加Y轴显示的刻度值
			}

			axisX.setValues(mAxisXValues); // 填充X轴的坐标名称
			axisY.setValues(mAxisYValues);
			axisX.setHasTiltedLabels(true);// 设置X轴文字向左旋转45度
			axisY.setMaxLabelChars(8);
			axisX.setTextColor(Color.BLACK);
			axisY.setTextColor(Color.BLACK);
			if (hasAxesNames) {
				axisX.setName("");
				axisY.setName("");
			}
			data.setAxisXBottom(axisX);
			data.setAxisYLeft(axisY);
		} else {
			data.setAxisXBottom(null);
			data.setAxisYLeft(null);
		}

		data.setValueLabelBackgroundAuto(true);// 设置数据背景是否跟随节点颜色
		data.setValueLabelBackgroundColor(Color.WHITE);// 设置数据背景颜色
		data.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
		data.setValueLabelsTextColor(getResources().getColor(R.color.white));// 设置数据文字颜色
		data.setValueLabelTextSize(0);// 设置数据文字大小
		data.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式

		data.setBaseValue(Float.NEGATIVE_INFINITY);
		initCW();
		chart.setValueSelectionEnabled(false);
		chart.setInteractive(true);
		chart.setLineChartData(data);
		chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.popshow_anim);
		chart.setAnimation(animation);

	}

	private class ValueTouchListener implements LineChartOnValueSelectListener {

		@Override
		public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
			System.out.println("value = " + value);
			initPopWindow(lineIndex, pointIndex, value);
		}

		@Override
		public void onValueDeselected() {
			popupWindow.dismiss();
		}

	}

	private class ValueTouchListener2 implements LineChartOnValueSelectListener {

		@Override
		public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
			System.out.println("value = " + value);
			initPopWindow2(lineIndex, pointIndex, value);
		}

		@Override
		public void onValueDeselected() {
			popupWindow2.dismiss();
		}

	}

	/**
	 * 一个曲线图 月份选择
	 */
	private void timesub3() {

		// 判断是几条线
		// 第一条线的长度

		timelist.clear();
		for (int j = 0; j < 1; j++) {
			timewlist = new ArrayList<HashMap<String, Object>>();
			int length = 0;
			if (j == 0) {
				length = datalist1.size();
			}

			System.out.println("datalist size = " + datalist1.size());
			for (int i = 0; i < length; i++) {
				String time = alltimelist.get(j).get(i).get("date_time") + "";
				String time1 = time.substring(8, 10);

				float day_f = Float.parseFloat(time1);

				BigDecimal b = new BigDecimal(day_f);
				float datetime = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("show_time", datetime);

				timewlist.add(map);
			}
			System.out.println("tag 111 wlist show == " + timewlist);
			timelist.add(timewlist);
		}


		dw = getResources().getDisplayMetrics().widthPixels;

		chart.setOnValueTouchListener(new ValueTouchListener());

		generateValues();

		generateData();
		initCW();

		chart.setViewportCalculationEnabled(false);
		resetViewport();


	}

	private void timesub() {

		// 判断是几条线
		// 第一条线的长度

		timelist.clear();
		for (int j = 0; j < 1; j++) {
			timewlist = new ArrayList<HashMap<String, Object>>();
			int length = 0;
			if (j == 0) {
				length = datalist1.size();
			}
			for (int i = 0; i < length; i++) {

				String time = alltimelist.get(j).get(i).get("date_time") + "";
				String time1 = time.substring(10, 16);
				// 小时 分钟
				String hour = time1.substring(0, 3);
				String minute = time1.substring(4, time1.length());

				// 小时 Float
				float hour_f = Float.parseFloat(hour);

				float minute_f = Float.parseFloat(minute) / 60;
				BigDecimal b = new BigDecimal(minute_f);
				float datetime = hour_f + b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("show_time", datetime);

				timewlist.add(map);
			}
			timelist.add(timewlist);
		}

		dw = getResources().getDisplayMetrics().widthPixels;

		chart.setOnValueTouchListener(new ValueTouchListener());

		// Generate some random values.
		generateValues();

		generateData();
		initCW();

		chart.setViewportCalculationEnabled(false);
		resetViewport();

	}

	
	
	/**
	 * 两个 曲线图 
	 * 月份选择
	 */
	private void timesub4() {

		// 判断是几条线
		// 第一条线的长度

		timelist2.clear();
		for (int j = 0; j < 1; j++) {
			timewlist2 = new ArrayList<HashMap<String, Object>>();
			int length = 0;
			if (j == 0) {
				length = datalist2.size();
			}

			System.out.println("datalist2 size = " + datalist2.size());
			for (int i = 0; i < length; i++) {

				
				String time = alltimelist2.get(j).get(i).get("date_time") + "";
				String time1 = time.substring(8, 10);

				float day_f = Float.parseFloat(time1);

				BigDecimal b = new BigDecimal(day_f);
				float datetime = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("show_time", datetime);

				timewlist2.add(map);
			}
			timelist2.add(timewlist2);
			
		}

		dw2 = getResources().getDisplayMetrics().widthPixels;
		chart2.setOnValueTouchListener(new ValueTouchListener2());

		generateValues2();

		generateData2();
		initCW2();

		chart2.setViewportCalculationEnabled(false);
		resetViewport2();

	}
	
	private void timesub2() {

		// 判断是几条线
		// 第一条线的长度

		timelist2.clear();
		for (int j = 0; j < 1; j++) {
			timewlist2 = new ArrayList<HashMap<String, Object>>();
			int length = 0;
			if (j == 0) {
				length = datalist2.size();
			}

			System.out.println("datalist2 size = " + datalist2.size());
			for (int i = 0; i < length; i++) {

				String time = alltimelist2.get(j).get(i).get("date_time") + "";
				String time1 = time.substring(10, 16);
				// 小时 分钟
				String hour = time1.substring(0, 3);
				String minute = time1.substring(4, time1.length());

				// 小时 Float
				float hour_f = Float.parseFloat(hour);

				float minute_f = Float.parseFloat(minute) / 60;
				BigDecimal b = new BigDecimal(minute_f);
				float datetime = hour_f + b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("show_time", datetime);

				timewlist2.add(map);
			}
			timelist2.add(timewlist2);
		}

		dw2 = getResources().getDisplayMetrics().widthPixels;
		chart2.setOnValueTouchListener(new ValueTouchListener2());

		generateValues2();

		generateData2();
		initCW2();

		chart2.setViewportCalculationEnabled(false);
		resetViewport2();

	}

	PopupWindow popupWindow = null;
	PopupWindow popupWindow2 = null;

	public void initPopWindow2(int i, int j, PointValue value) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View v = inflater.inflate(R.layout.his_pop, null);
		((TextView) v.findViewById(R.id.tv_his))
				.setText(alltimelist2.get(i).get(j).get("date_time") + "\n\t\t\t\t" + value);
		popupWindow2 = new PopupWindow(v, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		popupWindow2.setFocusable(true);
		popupWindow2.setOutsideTouchable(false);
		popupWindow2.setBackgroundDrawable(new ColorDrawable(0x00000000));
		popupWindow2.showAtLocation(chart, Gravity.TOP, 0, (include_height + ll_height
				+ Function.getStatusHeight(hisStoreActivity4.this) + Function.getStatusHeight(hisStoreActivity4.this)));
	}

	public void initPopWindow(int i, int j, PointValue value) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View v = inflater.inflate(R.layout.his_pop, null);
		((TextView) v.findViewById(R.id.tv_his))
				.setText(alltimelist.get(i).get(j).get("date_time") + "\n\t\t\t\t" + value);
		popupWindow = new PopupWindow(v, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(false);
		popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
		popupWindow.showAtLocation(chart, Gravity.TOP, 0, (include_height + ll_height
				+ Function.getStatusHeight(hisStoreActivity4.this) + Function.getStatusHeight(hisStoreActivity4.this)));
	}

	private static Paint paint;

	public static float getTWidth(String text) {
		paint = new Paint();
		paint.setTextSize(30);
		paint.setStyle(Paint.Style.FILL); // 设置画柱非空心
		paint.setAntiAlias(true); // 消除锯齿 Color.WHITE
		paint.setColor(Color.WHITE);
		return paint.measureText(text) + 20;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_back:
			finish();
			break;
		case R.id.tv_year_month_select:// 年月
			dateDialogShow2();
			break;
		case R.id.tv_month_day_select:// 月日
			dateDialogShow();
			break;
		default:
			break;
		}
	}

	int y_show = 0;

	/** 日期选择对话框 **/
	private void dateDialogShow() {
		if (mDateDialog == null) {
			mDateDialog = new DateDialog(this,1, new DateDialog.OnBackClickListener() {
				@Override
				public void onClick(String date) {
					// date 获得日期 2016-02-23

					if (list2 == null) {
						y_show = 0;
					} else {
						y_show = 1;
					}
					type = "1";

					right = 24;
					right2 = 24;
					tv_month_day_select.setText(getString(R.string.tv_month_day_select) + date);
					if (!tv_year_month_select.getText().toString().trim().equals("")) {
						tv_year_month_select.setText("年月选择：");
					}
					time = date;
					mAxisXValues.clear();
					mAxisXValues2.clear();
					mAxisYValues.clear();
					mAxisYValues2.clear();
					chart.setLineChartData(null);

					chart2.setLineChartData(null);

					dw = getResources().getDisplayMetrics().widthPixels;
					dw2 = getResources().getDisplayMetrics().widthPixels;
					xdatalist.clear();
					ydatalist.clear();
					xdatalist2.clear();
					ydatalist2.clear();
					alltimelist.clear();
					alltimelist2.clear();
					datalist1.clear();
					datalist2.clear();
					if (list2.size() != 0) {
						list1.clear();
					}
					list2.clear();
					timelist.clear();
					timelist2.clear();

					getDataInfo();

				}
			});
		}
		mDateDialog.show();
	}

	/** 日期选择对话框 **/
	private void dateDialogShow2() {
		if (mDateDialog2 == null) {
			mDateDialog2 = new DateDialog2(this,1, new DateDialog2.OnBackClickListener() {
				@Override
				public void onClick(String date) {
					// date 获得日期 2016-02-23

					Calendar calendar = Calendar.getInstance();
					int year = calendar.get(Calendar.YEAR);
					int month = calendar.get(Calendar.MONTH) + 1;
					if (year % 4 == 0) {// 闰年
						if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
							right = 31;
							right2 = 31;
						} else if (month == 2) {
							right = 29;
							right2 = 29;
						} else {
							right = 30;
							right2 = 30;
						}
					} else {
						if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
							right = 31;
							right2 = 31;
						} else if (month == 2) {
							right = 28;
							right2 = 28;
						} else {
							right = 30;
							right2 = 30;
						}
					}
					
					
					if (list2 == null) {
						y_show = 0;
					} else {
						y_show = 1;
					}
					type = "2";
					time = date;
					
					tv_year_month_select.setText(getString(R.string.tv_year_month_select) + date);
					if (!tv_month_day_select.getText().toString().trim().equals("")) {
						tv_month_day_select.setText("月日选择：");
					}
					// 更新曲线数据

					mAxisXValues.clear();
					mAxisXValues2.clear();
					mAxisYValues.clear();
					mAxisYValues2.clear();
					chart.setLineChartData(null);

					chart2.setLineChartData(null);

					dw = getResources().getDisplayMetrics().widthPixels;
					dw2 = getResources().getDisplayMetrics().widthPixels;
					xdatalist.clear();
					ydatalist.clear();
					xdatalist2.clear();
					ydatalist2.clear();
					alltimelist.clear();
					alltimelist2.clear();
					datalist1.clear();
					datalist2.clear();
					if (list2.size() != 0) {
						list1.clear();
					}
					list2.clear();
					timelist.clear();
					timelist2.clear();

					getDataInfo();

				}
			});
		}
		mDateDialog2.show();
	}

}
