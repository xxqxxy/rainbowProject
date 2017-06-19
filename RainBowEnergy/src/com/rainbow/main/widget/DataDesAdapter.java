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

public class DataDesAdapter extends BaseAdapter {

	private Context mContext;
	private List<HashMap<String, Object>> mList;
	LayoutInflater inflater;
	int mResouce;
	SharePreferences isPrefences;
	public DataDesAdapter(Context context,List<HashMap<String, Object>> list, int resouce) {
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
			holder.ll_states = (LinearLayout) convertView.findViewById(R.id.ll_states);
			holder.tv_five = (ImageView) convertView.findViewById(R.id.tv_five);
			holder.tv_one = (TextView) convertView.findViewById(R.id.tv_one);
			holder.tv_two = (TextView) convertView.findViewById(R.id.tv_two);
			holder.tv_three = (TextView) convertView.findViewById(R.id.tv_three);
			holder.tv_four = (TextView) convertView.findViewById(R.id.tv_four);
			
			convertView.setTag(holder);;
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
	
//		if(mResouce == R.layout.transformer_item){
			holder.tv_one.setText(mList.get(position).get("measpoint_section_name")+"");
			holder.tv_two.setText(mList.get(position).get("measpoint_name")+"");
			holder.tv_three.setText(mList.get(position).get("value")+"");
			holder.tv_four.setText(mList.get(position).get("unit")+"");
			Log.i("", "soso swhich_state == "+mList.get(position).get("swhich_state")+"");
			
			if(isPrefences.getSp().getString("dform_show_states", "").equals("0")){
				holder.ll_states.setVisibility(View.GONE);
			}else if(isPrefences.getSp().getString("dform_show_states", "").equals("1")){
				holder.ll_states.setVisibility(View.VISIBLE);
				if((mList.get(position).get("swhich_state")+"").equals("合闸")){
					holder.tv_five.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_red));
				}else if((mList.get(position).get("swhich_state")+"").equals("分闸")){
					holder.tv_five.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_green));
				}else if((mList.get(position).get("swhich_state")+"").equals("故障")){
					holder.tv_five.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_yellow));
				}else if((mList.get(position).get("swhich_state")+"").equals("") ||(mList.get(position).get("swhich_state")+"") == null){
					holder.tv_five.setImageDrawable(null);
				}
			}else{
				holder.ll_states.setVisibility(View.GONE);
			}
			
//		}
	
		return convertView;
	}
	class ViewHolder{
		TextView tv_one,tv_two,tv_three,tv_four;
		ImageView tv_five;
		LinearLayout ll_states;
	}

}
