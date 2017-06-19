package com.rainbow.main.widget;

import java.util.HashMap;
import java.util.List;

import com.comutils.main.SharePreferences;
import com.rainbow.main.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DataDesAdapter2 extends BaseAdapter {

	private Context mContext;
	private List<HashMap<String, Object>> mList;
	LayoutInflater inflater;
	int mResouce;
	SharePreferences isPrefences = null;
	public DataDesAdapter2(Context context,List<HashMap<String, Object>> list, int resouce) {
		this.mContext = context;
		this.mList = list;
		this.mResouce = resouce;
		inflater = LayoutInflater.from(mContext);
		isPrefences = new SharePreferences(mContext);
	}
	
	@Override
	public int getCount() {
		return mList== null?0:mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList== null?null:mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(null==convertView){
			holder = new ViewHolder();
			convertView = inflater.inflate(mResouce, null);
			holder.tv_one = (TextView) convertView.findViewById(R.id.tv_one);
			holder.tv_two = (TextView) convertView.findViewById(R.id.tv_two);
			holder.tv_three = (TextView) convertView.findViewById(R.id.tv_three);
			holder.tv_four = (ImageView) convertView.findViewById(R.id.tv_four);
			holder.ll_states = (LinearLayout) convertView.findViewById(R.id.ll_states);
			convertView.setTag(holder);;
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
	
	
			holder.tv_one.setText(mList.get(position).get("measpoint_name")+"");
			holder.tv_two.setText(mList.get(position).get("value")+"");
			holder.tv_three.setText(mList.get(position).get("unit")+"");
			
			if(isPrefences.getSp().getString("levlone_show_states", "").equals("0")){
				holder.ll_states.setVisibility(View.GONE);
			}else if(isPrefences.getSp().getString("levlone_show_states", "").equals("1")){
				holder.ll_states.setVisibility(View.VISIBLE);
				if((mList.get(position).get("swhich_state")+"").equals("合闸")){
					holder.tv_four.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_red));
				}else if((mList.get(position).get("swhich_state")+"").equals("分闸")){
					holder.tv_four.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_green));
				}else if((mList.get(position).get("swhich_state")+"").equals("故障")){
					holder.tv_four.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_yellow));
				}else if((mList.get(position).get("swhich_state")+"").equals("") ||(mList.get(position).get("swhich_state")+"") == null){
					holder.tv_four.setImageDrawable(null);
				}
			}else{
				holder.ll_states.setVisibility(View.GONE);
			}
	
		
		return convertView;
	}
	class ViewHolder{
		TextView tv_one,tv_two,tv_three;
		ImageView tv_four;
		LinearLayout ll_states;
		
	}

}
