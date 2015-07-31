package com.diversey.servicio.logistica;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class VerificarInternet {

	public static class Internet{
		
		public boolean conexion(Context c){
			ConnectivityManager connectivity;
			NetworkInfo info;
			
			  ConnectivityManager cm = (ConnectivityManager) c.getApplicationContext().getSystemService( Context.CONNECTIVITY_SERVICE ); 
				    NetworkInfo netInfo = cm.getActiveNetworkInfo (); 
				    if ( netInfo !=  null  && netInfo.isConnectedOrConnecting())  { 
				    	Log.i("Conectado", netInfo.toString());
				        return  true ; 
				    } 
				    Log.i("Conectado", "no se pudo establecer una conexion");
				    return  false ; 
			
			
			/*
			connectivity = (ConnectivityManager) c.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			if(connectivity != null){
				info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				Log.i("conectadar", "mobile");
				if(info != null){
					Log.i("conectado", "mobile");
					if(info.isConnected()){
						return true;
					}else{
						return false;
					}
				}else{
					return false;
				}
				
				}else{
				
					info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					if (info != null) {
						if (info.isConnected()) {
						return true;
						}else{
							return false;
						}
		
				}
			
		}
			return false;
		*/
		}
		
	}
}
