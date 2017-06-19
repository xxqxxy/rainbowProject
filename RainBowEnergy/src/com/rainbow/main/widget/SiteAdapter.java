package com.rainbow.main.widget;

import java.util.HashMap;
import java.util.List;

import com.comutils.rain_view.MyListView;
import com.rainbow.main.R;
import com.rainbow.main.RainBowApplication;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SiteAdapter extends BaseAdapter {

	private Context mContext;
	private List<HashMap<String, Object>> olist_1;
	private List<List<HashMap<String, Object>>> olist_2;
	private RainBowApplication rainBowApplication;
	private OnClickListener mOnClickListener;
	LayoutInflater inflater;
	public SiteAdapter(Context context, List<HashMap<String, Object>> molist_1,
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
			convertView = inflater.inflate(R.layout.site_item, null);
			holder.tv_section = (TextView) convertView.findViewById(R.id.tv_section);
			holder.lv_site = (MyListView) convertView.findViewById(R.id.mylistview);
			
			convertView.setTag(holder);
	
		}else{
			holder= (ViewHolder) convertView.getTag();
		}
		
		holder.tv_section.setText(olist_1.get(position).get("dpt_name")+"");
		
		holder.lv_site.setDividerHeight(0);
		holder.lv_site.setDivider(new ColorDrawable(mContext.getResources().getColor(R.color.cr_gray1)));
		holder.lv_site.setDividerHeight((int)(mContext.getResources().getDisplayMetrics().density*5));
		holder.lv_site.setDivider(new ColorDrawable(R.color.cr_gray3));
		
		
		OListItemAdapter2 iOLListAdapter = new OListItemAdapter2(mContext,rainBowApplication,"",mOnClickListener,
				position,olist_2.get(position),R.layout.site_item_sub);
		holder.lv_site.setAdapter(iOLListAdapter);
		
		
		return convertView;
	}

	class ViewHolder{
		TextView tv_section;
		MyListView lv_site;
		
	}
	
	
}
