package com.diversey.servicio.logistica;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

class MyLocationListener implements LocationListener {  

	private String position = "";    


	public void onLocationChanged(Location loc) {  
		position = loc.getLongitude() + "," + loc.getLatitude();  
	}  

	public void onProviderDisabled(String provider) {  
		// TODO Auto-generated method stub  
	}  

	public void onProviderEnabled(String provider) {  
		// TODO Auto-generated method stub  
	}  

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// 	TODO Auto-generated method stub
	}  

	public String getStringPosition(){
		return position;
	}
} 
