package com.rainbow.main.widget;

import java.util.HashMap;
import java.util.List;

import com.rainbow.main.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlarmAdapter extends BaseAdapter {

	private Context mContext;
	private List<HashMap<String, Object>> mList;
	private OnClickListener mItemOnClickListener;
	LayoutInflater inflater;
	public AlarmAdapter(Context context, List<HashMap<String, Object>> list,OnClickListener itemOnClickListener) {
		this.mContext = context;
		this.mList = list;
		this.mItemOnClickListener = itemOnClickListener;
		inflater = LayoutInflater.from(context);
		
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
		return mList==null?0:position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(null==convertView){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.alarm_item, null);
			holder.tv_alarm_time = (TextView) convertView.findViewById(R.id.tv_alarm_time);
			holder.tv_alarm_content = (TextView) convertView.findViewById(R.id.tv_alarm_content);
			holder.iv_more = (ImageView) convertView.findViewById(R.id.iv_more);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_alarm_content.setText(mList.get(position).get("warn_evnt")+"");
		holder.tv_alarm_time.setText(mList.get(position).get("warn_time")+"");
		holder.iv_more.setTag(position);
		holder.iv_more.setOnClickListener(mItemOnClickListener);
		
		return convertView;
	}
	
	class ViewHolder{
		TextView tv_alarm_time,tv_alarm_content;
		ImageView iv_more;
		
	}
	

}
