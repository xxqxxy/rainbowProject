package com.rainbow.main.widget;


import java.util.HashMap;
import java.util.List;

import com.comutils.main.Function;
import com.comutils.rain_view.RoundImageViewByXfermode;
import com.rainbow.main.R;
import com.rainbow.main.RainBowApplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


public class OListItemAdapter2 extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<HashMap<String, Object>> mList;
	private int mResource;
	private int[] mTo;
    private String[] mFrom;
    private OnClickListener mOnClickListener;
    private RainBowApplication mBGApplication;
    private String TAG;
    private int mptposition;
    Context mContext;
	LayoutInflater inflater;
    public OListItemAdapter2(Context context,RainBowApplication mBGApplication,String tag,OnClickListener onClickListener,int ptposition, List<HashMap<String, Object>> items, 
			int resource) {
    	mOnClickListener =  onClickListener;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mList = items;
        this.mptposition = ptposition;
        this.mContext=context;
		mResource = resource;
		this.mBGApplication= mBGApplication;
		this.TAG =tag;
		inflater = LayoutInflater.from(context);
	}


	@Override
	public int getCount() {
		return mList.size();
	}


	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}


	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
ViewHolder holder = null;
		
		if(null == convertView){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.site_item_sub, null);
			holder.tv_site_location = (TextView) convertView.findViewById(R.id.tv_site_location);
			holder.iv_map = (RoundImageViewByXfermode) convertView.findViewById(R.id.iv_map);
			holder.ll_site  = (LinearLayout) convertView.findViewById(R.id.ll_site);
			int wt =  (int)(mContext.getResources().getDisplayMetrics().widthPixels);
			int ht = (int)((380*mContext.getResources().getDisplayMetrics().widthPixels)/640);
			Log.i("soso",  mList.get(position).get("facemap")+"");
			Function.setCKMap(mBGApplication, TAG,holder.iv_map, mList.get(position).get("facemap")+"",wt,ht);
			
			convertView.setTag(holder);
	
		}else{
			holder= (ViewHolder) convertView.getTag();
		}
		
		
		holder.tv_site_location.setText(mList.get(position).get("st_name")+"");
		 holder.ll_site.setTag(mptposition+"="+position);
		 holder.ll_site.setOnClickListener(mOnClickListener);
		return convertView;
	}
	
	class ViewHolder{
		TextView tv_site_location;
		RoundImageViewByXfermode iv_map;
		LinearLayout ll_site;
	}
}
