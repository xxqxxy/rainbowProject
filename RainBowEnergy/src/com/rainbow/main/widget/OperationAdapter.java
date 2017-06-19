package com.rainbow.main.widget;

import java.util.HashMap;
import java.util.List;

import com.comutils.main.Function;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rainbow.main.R;
import com.rainbow.main.RainBowApplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OperationAdapter extends BaseAdapter {

	private Context mContext;
	private List<HashMap<String, Object>> mList;
	private OnClickListener mItemOnClickListener;
	LayoutInflater inflater;
	
	ImageLoader imageLoader = ImageLoader.getInstance();

	
	RainBowApplication application;
	public OperationAdapter(RainBowApplication rainBowApplication, Context context, List<HashMap<String, Object>> list,OnClickListener itemOnClickListener) {
		this.mContext = context;
		this.mList = list;
		this.application = rainBowApplication;
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
			convertView = inflater.inflate(R.layout.operation_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);
			holder.tv_post = (TextView) convertView.findViewById(R.id.tv_post);
			holder.tv_site = (TextView) convertView.findViewById(R.id.tv_site);
			holder.iv_map = (ImageView) convertView.findViewById(R.id.iv_map);
			holder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);
			holder.iv_view = (ImageView) convertView.findViewById(R.id.iv_view);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_name.setText(mList.get(position).get("m_name")+"");
		if((mList.get(position).get("busy_state")+"").equals("0")){
			holder.tv_state.setText(mContext.getString(R.string.tv_busy_state_0));
			holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.cr_green_1));
		}else{
			holder.tv_state.setText(mContext.getString(R.string.tv_busy_state_1));
			holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.cr_orange_1));
		}
		
		holder.tv_post.setText(mList.get(position).get("m_cardv")+"");
		holder.tv_site.setText(mList.get(position).get("task_des")+"");
		
		if((mList.get(position).get("select")+"").equals("1")){
			holder.iv_select.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_select_normal));
		}else{
			holder.iv_select.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_select));
		}
		
		holder.iv_select.setTag(position);
		holder.iv_select.setOnClickListener(mItemOnClickListener);
		
		
		int wt =  (int)(100* mContext.getResources().getDisplayMetrics().density);
		Function.setCircleMap(application, "", holder.iv_map,mList.get(position).get("m_logo")+"" , wt);
		return convertView;
	}
	
	class ViewHolder{
		TextView tv_name,tv_state,tv_post,tv_site;
		ImageView iv_select,iv_view;
		ImageView iv_map;
	}
	

}
