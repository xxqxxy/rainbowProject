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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TaskAdapter extends BaseAdapter {

	private Context mContext;
	private List<HashMap<String, Object>> mLists;
	OnClickListener mOnClickListener;
	LayoutInflater inflater;
	public TaskAdapter(Context context, List<HashMap<String, Object>> lists,OnClickListener onClickListener) {
		this.mContext = context;
		this.mLists = lists;
		this.mOnClickListener = onClickListener;
		inflater = LayoutInflater.from(context);
		
	}
	
	@Override
	public int getCount() {
		return mLists== null?0:mLists.size();
	}

	@Override
	public Object getItem(int position) {
		return  mLists== null?null:mLists.get(position);
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
			convertView  = inflater.inflate(R.layout.task_item, null);
			
			holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.tv_task_state = (TextView) convertView.findViewById(R.id.tv_task_state);
			holder.tv_task_type = (TextView) convertView.findViewById(R.id.tv_task_type);
			holder.tv_finish = (TextView) convertView.findViewById(R.id.tv_finish);
			holder.ll_unfinish = (LinearLayout) convertView.findViewById(R.id.ll_unfinish);
			holder.rl_finish = (RelativeLayout) convertView.findViewById(R.id.rl_finish);
			holder.tv_task_finish = (TextView) convertView.findViewById(R.id.tv_task_finish);
			convertView.setTag(holder);
			
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		if((mLists.get(position).get("tk_type")+"").equals("2")){
			holder.tv_content.setText(mLists.get(position).get("tk_context")+"");
			
		}else if((mLists.get(position).get("tk_type")+"").equals("1")){
			holder.tv_content.setText(mLists.get(position).get("task_des")+"");
			
		}
		//不是 发布的
		if((mLists.get(position).get("is_owner")+"").equals("0")){
			if((mLists.get(position).get("tk_state")+"").equals("0")){
				holder.tv_task_state.setText(mContext.getString(R.string.tv_tk_state_0));//待接收
				holder.tv_task_type.setText(mContext.getString(R.string.tv_tk_type_0));//接收任务
				holder.tv_task_state.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.icon_receiver), null, null, null);//图片资源文件
			}else if((mLists.get(position).get("tk_state")+"").equals("1")){
				holder.tv_task_state.setText(mContext.getString(R.string.tv_tk_state_1));//进行中
				holder.tv_task_type.setText(mContext.getString(R.string.tv_tk_type_1));//完成任务
				holder.tv_task_state.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.icon_task_ing), null, null, null);
				
			}else if((mLists.get(position).get("tk_state")+"").equals("2")){
				holder.tv_task_state.setText(mContext.getString(R.string.tv_tk_state_2));//已完成
				holder.tv_task_type.setText(mContext.getString(R.string.tv_tk_type_2));//汇报任务
				holder.tv_task_state.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.icon_finish), null, null, null);
				
			} else if((mLists.get(position).get("tk_state")+"").equals("3")){
				holder.tv_task_state.setText(mContext.getString(R.string.tv_tk_type_3));//已汇报
				holder.tv_task_type.setText(mContext.getString(R.string.tv_look));//查看任务
				holder.tv_task_state.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.icon_finish), null, null, null);
			} 
		}else{
			if((mLists.get(position).get("tk_state")+"").equals("0")){
				holder.tv_task_state.setText(mContext.getString(R.string.tv_tk_state_0));
				holder.tv_task_type.setText(mContext.getString(R.string.tv_look));
				holder.tv_task_state.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.icon_receiver), null, null, null);
			}else if((mLists.get(position).get("tk_state")+"").equals("1")){
				holder.tv_task_state.setText(mContext.getString(R.string.tv_tk_state_1));
				holder.tv_task_type.setText(mContext.getString(R.string.tv_look));
				holder.tv_task_state.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.icon_task_ing), null, null, null);
				
			}else if((mLists.get(position).get("tk_state")+"").equals("2")){
				holder.tv_task_state.setText(mContext.getString(R.string.tv_tk_state_2));
				holder.tv_task_type.setText(mContext.getString(R.string.tv_look));
				holder.tv_task_state.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.icon_finish), null, null, null);
				
			} else if((mLists.get(position).get("tk_state")+"").equals("3")){
				holder.tv_task_state.setText(mContext.getString(R.string.tv_tk_type_3));
				holder.tv_task_type.setText(mContext.getString(R.string.tv_look));
				holder.tv_task_state.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.icon_finish), null, null, null);
			}
		}
		
		
		
		holder.tv_task_type.setTag(position);
		holder.tv_task_type.setOnClickListener(mOnClickListener);
		
		
		
		return convertView;
	}
	
	class ViewHolder{
		TextView tv_content,tv_task_state,tv_task_type,tv_finish,tv_task_finish;
		LinearLayout ll_unfinish;
		RelativeLayout rl_finish;
	}

}
