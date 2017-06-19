package com.rainbow.main.widget;


import java.util.HashMap;
import java.util.List;

import com.comutils.main.Function;
import com.rainbow.main.R;
import com.rainbow.main.RainBowApplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class queListAdapter  extends SimpleAdapter {
	private Context mContext; 
	private LayoutInflater mInflater;
	private List<HashMap<String, Object>> mList;
	private int mResource;
	private int[] mTo;
    private String[] mFrom;
    private OnClickListener mOnClickListener;
    private RainBowApplication mGITMApplication;
    private String TAG;
	public queListAdapter(Context context,RainBowApplication mGITMApplication,String tag,OnClickListener onClickListener, List<HashMap<String, Object>> items, 
			int resource, String[] from, int[] to) {
		super(context, items, resource, from, to); 
		mOnClickListener =  onClickListener;
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mList = items;
		mFrom = from;
        mTo = to;
		mResource = resource;
		this.mGITMApplication= mGITMApplication;
		this.TAG =tag;
	}
	
	@Override  
    public View getView(int position, View convertView, ViewGroup parent) { 
		View v;
            v = mInflater.inflate(mResource, parent, false);

            final int[] to = mTo;
            final int count = to.length;
            final View[] holder = new View[count];

            for (int i = 0; i < count; i++) {
                holder[i] = v.findViewById(to[i]);
            }

            v.setTag(holder);
           
        bindView(position, v);
        return v;
		
        
    }
	private void bindView(int position, View view) {
		final HashMap<String, Object> dataSet = mList.get(position);
        if (dataSet == null) {
            return;
        }
        
        int rqw = (int)(64*mContext.getResources().getDisplayMetrics().density);
        int rqh = (int)(64*mContext.getResources().getDisplayMetrics().density);
        final View[] holder = (View[]) view.getTag();
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;
        for (int i = 0; i < count; i++) {
        	final View v = holder[i];
        	if (v == null)
        		continue;
        	final Object data = dataSet.get(from[i]);
            String text = data == null ? "" : data.toString();
            if (text == null)
                text = "";
            if (v instanceof TextView) {
            	setViewText((TextView) v,text);
            }else if (v instanceof ImageView) {
            	if(!text.equals("")){
            			float td =  mContext.getResources().getDisplayMetrics().density;  
        				final int rd2 = (int)(50*(td));
        				Function.setCircleMap(mGITMApplication,"personalDes", (ImageView)v, 
        						text, rd2);
            	}else{
            	}
            	
            }
        }
        try {
			
		
      
        } catch (Exception e) {}
        
	}

	public void setViewText(TextView v, String text) {
       if(v.getId() == R.id.tv_num){
    	   v.setText("已解答"+text);
       }else{
    	   v.setText(text);
    	   
       }
       
    }

}
