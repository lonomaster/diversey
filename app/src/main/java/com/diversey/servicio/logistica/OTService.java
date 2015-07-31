package com.diversey.servicio.logistica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

public class OTService extends Service {
	public SharedPreferences sp;
	private Timer timer = new Timer();
	private static boolean isRunning = false;

	private String urlOtState = "";

	public JSONArray nombres_imagenes = new JSONArray();
	ArrayList<Messenger> mClients = new ArrayList<Messenger>(); // Keeps track of all current registered clients.
	int mValue = 0; // Holds last value set by a client.
	static final int MSG_REGISTER_CLIENT = 1;
	static final int MSG_UNREGISTER_CLIENT = 2;
	static final int MSG_SET_INT_VALUE = 3;
	static final int MSG_SET_STRING_VALUE = 4;
	static final int MSG_SET_HEADER_STRING = 5;
	static final int MSG_SET_CONTENT_STRING = 6;
	static final int REFRESH_OTS = 7;
	static final int MSG_SET_OT_LIST = 8;
	static final int MSG_SET_OT_UPDATE = 9;
	static final int COUNT_UPLOADS = 10;
	static final int DO_REFRESH_OTS = 11;

	private boolean refresh_ot = true;
	private ArrayList<String> lastOTid;
	private String userId = "";
	
	private JSONObject otUpdate;
	private int otPendientes = 0;

	final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.


	@Override
	public IBinder onBind(Intent intent) {
		urlOtState = AppParameters.site_state;
		return mMessenger.getBinder();
	}
	class IncomingHandler extends Handler { // Handler of incoming messages from clients.
		@Override
		public void handleMessage(Message msg) {
			Log.i("Service rcv:",msg.toString());
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				break;
			case MSG_UNREGISTER_CLIENT:
				mClients.remove(msg.replyTo);
				break;
			case MSG_SET_INT_VALUE:
				Log.i("Service rcv:",msg.toString());
				break;
			case MSG_SET_STRING_VALUE:
				Log.i("Service rcv:",msg.toString());
				break;
			case MSG_SET_HEADER_STRING:
				Log.i("Service rcv:",msg.toString());
				userId = msg.getData().getString("str");
				urlOtState = AppParameters.site_state + userId;
				break;
			case MSG_SET_CONTENT_STRING:
				Log.i("Service rcv:",msg.toString());
				break;
			case MSG_SET_OT_LIST:
				lastOTid = msg.getData().getStringArrayList("arr");
				Log.i("Service rcv:",msg.toString());
				break;
			case MSG_SET_OT_UPDATE:
				try {
					otUpdate = new JSONObject(msg.getData().getString("ot"));
					uploadDataThr(otUpdate);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				Log.i("Service rcv:",msg.toString());
				break;
			case DO_REFRESH_OTS:
				int status = msg.getData().getInt("status");
				if(status == 1)
					refresh_ot = true;
				if(status == 0)
					refresh_ot = false;

				Log.i("Service rcv:",msg.toString());
				break;
			default:
				super.handleMessage(msg);
			}

		}
	}
	private void sendMessageToUI(int intvaluetosend, int type) {
		for (int i=mClients.size()-1; i>=0; i--) {
			try {
				// Send data as an Integer
				mClients.get(i).send(Message.obtain(null, type, intvaluetosend, 0));

				//Send data as a String
				//				Bundle b = new Bundle();
				//				b.putString("str1", "ab" + intvaluetosend + "cd");
				//				Message msg = Message.obtain(null, MSG_SET_STRING_VALUE);
				//				msg.setData(b);
				//				mClients.get(i).send(msg);

			} catch (RemoteException e) {
				mClients.remove(i);
			}
		}
	}

	public synchronized void uploadDataThr(final JSONObject ot) {

		Thread mBackground1 = new Thread(new Runnable()
		{
			public void run()
			{
				while (!isNetworkAvailable()) {
                    try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}                     
                }
				
				/*String string_base64_bitmap = sp.getString("string_base64_bitmap", "null"); //así se asigna
				
				try {
					ot.put("string_base64_bitmap", string_base64_bitmap);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					nombres_imagenes = ot.getJSONArray("nombres_imagenes");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int numero_imgs = nombres_imagenes.length();
				
				for (int i =0; i<numero_imgs;i++){
					try {
						String nombre_img = nombres_imagenes.getString(i);
						String string_base64_bitmap = sp.getString(nombre_img, "null"); //así se asigna
						ot.put(nombre_img, string_base64_bitmap);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				*/
				
				try {
					JSONArray json_imagenes = new JSONArray(sp.getString("json_imagenes", "null"));
					JSONArray json_imagenes_qr = new JSONArray(sp.getString("json_imagenes_qr", "null"));
					
					ot.put("json_imagenes", json_imagenes);
					ot.put("json_imagenes_qr", json_imagenes_qr);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
 				if(AppParameters.postData(ot, "actualizar")){
					otPendientes--;
				}
			}
		});
		
		otPendientes++;
		mBackground1.start();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sp = PreferenceManager.getDefaultSharedPreferences(this); //instancia en onCreate
		Log.i("OTService", "Service Started.");

		timer.scheduleAtFixedRate(new TimerTask(){ public void run() {onTimerTick();}}, 60000, 10000L);
		isRunning = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("MyService", "Received start id " + startId + ": " + intent);
		return START_STICKY; // run until explicitly stopped.
	}

	public static boolean isRunning()
	{
		return isRunning;
	}

	private void onTimerTick() {
		sendMessageToUI(otPendientes,COUNT_UPLOADS);
		
		if(!isNetworkAvailable()) return;
		if(userId=="") return;
		if(otPendientes>0)return;
		if(urlOtState=="") return;
		
		Log.i("Location:",urlOtState);
		String lt = ProcessData.readStaticFeed(urlOtState);
		//if(lt=="") return;
		String[] ltArray = lt.split(",");
		ArrayList<String> lastOTid_server = new ArrayList<String>();
		for(String s: ltArray){
			if(s=="") continue;
			lastOTid_server.add(s);
		}

		Collections.sort(lastOTid_server);
		Collections.sort(lastOTid);
		
		Log.i("Local array:",lastOTid.toString());
		Log.i("Remote array:",lastOTid_server.toString());
		
		
		if(!lastOTid.equals(lastOTid_server) && refresh_ot)
			sendMessageToUI(0, REFRESH_OTS);
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (timer != null) {timer.cancel();}
		//nm.cancel(R.string.remote_service_started); // Cancel the persistent notification.
		Log.i("MyService", "Service Stopped.");
		isRunning = false;
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}


}