package com.rainbow.main.widget;

import java.util.HashMap;
import java.util.List;

import com.comutils.main.Function;
import com.comutils.rain_view.MyGridView2;
import com.comutils.rain_view.RoundImageViewByXfermode;
import com.rainbow.main.R;
import com.rainbow.main.RainBowApplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SiteOneSecAdapter extends BaseAdapter {

	private Context mContext;
	private List<HashMap<String, Object>> olist_1;
	private List<List<HashMap<String, Object>>> olist_2;
	private RainBowApplication rainBowApplication;
	private OnClickListener mOnClickListener;
	LayoutInflater inflater;
	public SiteOneSecAdapter(Context context, List<HashMap<String, Object>> molist_1,
			List<List<HashMap<String, Object>>> molist_2,RainBowApplication rainBowApplication,
			OnClickListener onClickListener) {
		this.mContext = context;
		this.olist_1 = molist_1;
		this.olist_2 = molist_2;
		this.rainBowApplication = rainBowApplication;
		this.mOnClickListener = onClickListener;
		inflater = LayoutInflater.from(context);
	}
	
	
	
	@Override
	public int getCount() {
		return olist_1== null?0:olist_1.size();
	}

	@Override
	public Object getItem(int position) {
		return olist_1==null?null:olist_1.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(null == convertView){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.site_des_item_group, null);
			holder.tv_volt_name = (TextView) convertView.findViewById(R.id.tv_volt_name);
			holder.mygridview = (MyGridView2) convertView.findViewById(R.id.mygridview);
			holder.iv_map = (RoundImageViewByXfermode) convertView.findViewById(R.id.iv_map);
			holder.tv_states = (ImageView) convertView.findViewById(R.id.tv_bg);
			holder.ll_site_one = (LinearLayout) convertView.findViewById(R.id.ll_site_one);
			int wt =  (int)(mContext.getResources().getDisplayMetrics().widthPixels);
			int ht = (int)((380*mContext.getResources().getDisplayMetrics().widthPixels)/640);
			Function.setCKMap(rainBowApplication, "", holder.iv_map, olist_1.get(position).get("levlone_face")+"", wt,ht);
			
			convertView.setTag(holder);
		}else{
			holder= (ViewHolder) convertView.getTag();
		}
		holder.tv_volt_name.setText(olist_1.get(position).get("levlone_measptname") + "");
		
		if((olist_1.get(position).get("st_type")+"").equals("1")){
			holder.tv_states.setVisibility(View.VISIBLE);
			if((olist_1.get(position).get("level_one_state")).equals("0")){
				holder.tv_states.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_green));
			}else{
				holder.tv_states.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_red));
			}
		}else{
			holder.tv_states.setVisibility(View.GONE);
		}
		
		holder.ll_site_one.setTag(position);
		holder.ll_site_one.setOnClickListener(mOnClickListener);
		
		
		if(olist_2==null || olist_2.size()<=0){
		}else{
			SiteSecAdapter iOLListAdapter = new SiteSecAdapter(mContext,rainBowApplication,"",mOnClickListener,position,olist_2.get(position),R.layout.site_des_item_chird);
			holder.mygridview.setAdapter(iOLListAdapter);
		}

		return convertView;
	}
	
	class ViewHolder{
		 RoundImageViewByXfermode iv_map;
		 TextView tv_volt_name;
		 MyGridView2 mygridview;
		 ImageView tv_states;
		 LinearLayout ll_site_one;
	}

}
