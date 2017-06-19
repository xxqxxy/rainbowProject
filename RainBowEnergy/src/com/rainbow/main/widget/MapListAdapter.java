package com.rainbow.main.widget;



import java.util.HashMap;
import java.util.List;

import com.comutils.main.CUApplication;
import com.comutils.main.Function;
import com.rainbow.main.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class MapListAdapter extends SimpleAdapter {
	private Context mContext; 
	private LayoutInflater mInflater;
	private List<HashMap<String, Object>> mList;
	private int mResource;
	private int[] mTo;
    private String[] mFrom;
    //private AsyncImageLoader iAsyncImageLoader;
    private CUApplication mBGApplication;
    private String TAG;
	//mDataList
	public MapListAdapter(Context context,CUApplication mBGApplication,String tag,List<HashMap<String, Object>> items, int resource, String[] from, int[] to) {
		super(context, items, resource, from, to); 
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mList = items;
		mFrom = from;
        mTo = to;
		mResource = resource;
		this.mBGApplication= mBGApplication;
		this.TAG =tag;
	}
	
	@Override  
    public View getView(int position, View convertView, ViewGroup parent) { 
		View v;
       // if (convertView == null) {
            v = mInflater.inflate(mResource, parent, false);

            final int[] to = mTo;
            final int count = to.length;
            final View[] holder = new View[count];

            for (int i = 0; i < count; i++) {
                holder[i] = v.findViewById(to[i]);
            }

            v.setTag(holder);
           
       // } else {
       //     v = convertView;
       // }
        bindView(position, v);
        return v;
		
        
    }
	private void bindView(int position, View view) {
		HashMap<String, Object> dataSet;
		try {
			dataSet = mList.get(position);
		} catch (Exception e) {
			dataSet = null;
		}
        if (dataSet == null) {
            return;
        }
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
            	if(!text.equals(""))
            	setViewImage((ImageView) v, text);
            }
        }
	}
	public void setViewImage(ImageView v, String value) { 
		////Log.i("", "tag sss dds1 w ="+v.getWidth()+"=h="+v.getHeight());
		//v.setBackgroundResource(R.drawable.icon_background);
		if(isNumeric(value)){
			v.setImageResource(Integer.valueOf(value));
		}else{
			Log.i("soso", "tag setViewImage==== "+value);
			v.setBackgroundResource(R.drawable.icon_map_add);
			initImgView(v,value);
		}
		
		//float td =  mContext.getResources().getDisplayMetrics().density; 
		//int wt = (int)((mContext.getResources().getDisplayMetrics().widthPixels - (td*60))/3);
		//int wt = (int)(60*mContext.getResources().getDisplayMetrics().density);
		//v.setLayoutParams(new LinearLayout.LayoutParams(wt,wt));
			
    }
	private void initImgView(ImageView iv_view,String mapurl){
		float td =  mContext.getResources().getDisplayMetrics().density; 
		int rd = (int)(td*70);
		Function.setZFMap(mBGApplication, TAG, iv_view, mapurl, rd);
	}
	public void setViewText(TextView v, String text) {
        v.setText(text);
    }
	
	
	  public static boolean isNumeric(String str){    
	    	return str.matches("[0-9]*");
	    }
}
