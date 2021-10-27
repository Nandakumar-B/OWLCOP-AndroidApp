package com.smart;

import android.content.*;
import android.net.*;
import android.widget.*;
import android.view.*;

public class NetworkReceiver extends BroadcastReceiver
{
	private static Status st;
	
	@Override
	public void onReceive(Context context,Intent intent){
		ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info=cm.getActiveNetworkInfo();
		if(info!=null){
	     st.Received("yes");
		//  Toast.makeText(context,"yes",Toast.LENGTH_SHORT).show();
		}
		else{
	    st.Received("No");
	//	Toast.makeText(context,"no",Toast.LENGTH_SHORT).show();
		}
	}
	public static void Received(Status status){
		st=status;
	}
}

