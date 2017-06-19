package com.rainbow.main;
import com.baidu.mapapi.SDKInitializer;
import com.comutils.main.CUApplication;
import com.comutils.main.Function;
import com.videogo.openapi.EZOpenSDK;

import android.app.ActivityManager;
import android.content.Context;

public class RainBowApplication extends CUApplication {
   
	
	/*public static final String APP_KEY = "6c7eccd234bf4f43b7e3e5ee4d33fc1d";
	public static final String APP_SECRET = "2c7ca31b051ffb7b3d2a12dcc1762445";*/
	
	public static final String APP_KEY = "80d1309f5df948368bdaf4db3c7d2488";
	public static final String APP_SECRET = "4a1bbc110da50ec305df0c56b504de89";
	
	
	@Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
        
        EZOpenSDK.initLib(this, APP_KEY, "");
        
        Function.setDefaultFont(this, "DEFAULT", "fonts/FZLTHJW.TTF");
        Function.setDefaultFont(this, "MONOSPACE", "fonts/FZLTHJW.TTF");
        Function.setDefaultFont(this, "SERIF", "fonts/FZLTHJW.TTF");
        Function.setDefaultFont(this, "SANS_SERIF", "fonts/FZLTHJW.TTF");
       
    }
    
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    
    public static EZOpenSDK getOpenSDK(){
    	return EZOpenSDK.getInstance();
    }
    
    
    
}
