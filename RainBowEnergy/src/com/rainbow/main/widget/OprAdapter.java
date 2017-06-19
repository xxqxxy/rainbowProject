package com.rainbow.main.widget;

import java.util.HashMap;
import java.util.List;

import com.comutils.main.Function;
import com.rainbow.main.R;
import com.rainbow.main.RainBowApplication;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OprAdapter extends BaseAdapter {
	private Context mContext;
	private List<HashMap<String, Object>> mList =null;
	RainBowApplication rainbowApplication;
	LayoutInflater inflater;
	public OprAdapter(Context context,List<HashMap<String, Object>> list,RainBowApplication rainBowApplication) {
		this.mContext = context;
		this.mList = list;
		this.rainbowApplication  = rainBowApplication;
		inflater = LayoutInflater.from(mContext);
	}
	
	
	
	@Override
	public int getCount() {
		return mList==null?0:mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList==null?null:mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder= null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.oper_item, null);
			holder.iv_map = (ImageView) convertView.findViewById(R.id.iv_map);
			holder.tv_opr_name = (TextView) convertView.findViewById(R.id.tv_opr_name);
			convertView.setTag(holder);
			
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		if(( mList.get(position).get("map")+"").equals("123456")){
			holder.iv_map.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_df_head));
		}else if(( mList.get(position).get("map")+"").equals("")){
			holder.iv_map.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_df_head));
		}else{
			int wt =  (int)(100* mContext.getResources().getDisplayMetrics().density);
			Function.setCircleMap(rainbowApplication, "", holder.iv_map, mList.get(position).get("map")+"" , wt);
		}
		
		holder.tv_opr_name.setText(mList.get(position).get("name")+"");
		
		return convertView;
	}
	
	
	class ViewHolder{
		ImageView iv_map;
		TextView tv_opr_name;
	}

}
