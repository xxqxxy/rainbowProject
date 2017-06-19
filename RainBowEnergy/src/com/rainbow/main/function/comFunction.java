package com.rainbow.main.function;

import com.comutils.main.SharePreferences;
import com.rainbow.main.user.loginActivity;

import android.content.Context;
import android.content.Intent;

public class comFunction {


	public static void outtoLogin(Context context, String message,int errCode, SharePreferences isPreferences) {
		
		switch (errCode) {
		
		case 203:
			isPreferences.updateSp("m_id", "");
			isPreferences.updateSp("m_token", "");
			isPreferences.updateSp("m_pw", "");
			context.startActivity(new Intent(context, loginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) ;
			break;
		case 204:
			isPreferences.updateSp("m_id", "");
			isPreferences.updateSp("m_token", "");
			isPreferences.updateSp("m_pw", "");
			context.startActivity(new Intent(context, loginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) ;
			break;
			
		case 202:
			context.startActivity(new Intent(context, loginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) ;
			break;
		case 201:
			context.startActivity(new Intent(context, loginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) ;
			break;
		case 101:
			context.startActivity(new Intent(context, loginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) ;
			break;
	
		}
	}

}
