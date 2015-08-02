package com.diversey.servicio.logistica;


import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProcessData{

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	String latitud;
	String longitud;
	private List<UserRecord> data;
	private String readDb;
	private boolean netState = false;
	private static int HTTP_TIMEOUT = 30000;
	public boolean dataStatus = false;
	private UserRecord u;

	// constructor

	public class CustomComparator implements Comparator<UserRecord> {
		public int compare(UserRecord o1, UserRecord o2) {
			return o2.fecha_ejecucion.compareTo(o1.fecha_ejecucion);
		}
	}

	public ProcessData(String _url) {
		readDb = readDbFeed(_url);
		dataStatus = getJson(readDb);
	}

	public ProcessData() {
		data = new ArrayList<UserRecord>();
	}


	public boolean getJson(String r){
		data =  new ArrayList<UserRecord>();
		try { 
			Log.i("Data json que llegan:",r.toString());
			JSONArray jsonArray = new JSONArray(r);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Log.i("Data json que llegan:",jsonObject.toString());
				boolean resLatitud;
				boolean resLongitud;
				resLatitud = comprobarLatitud(jsonObject);
				
					if(resLatitud){
						latitud = jsonObject.getString("latitud");
					}else{
						latitud = "0";
					}
					
					resLongitud = comprobarLongitud(jsonObject);
					
					if(resLongitud){
						longitud = jsonObject.getString("longitud");
					}else{
						longitud = "0";
					}


				UserRecord userRecord =	new UserRecord(

						/*String id, String rut, String nombre, String razon_social, String direccion, String logo,
						String tipo_orden_id, String fecha_creacion, String fecha_ejecucion,
						String latitud, String longitud, String estado, String nombre_contacto,
						String correo_contacto, String descripcion, String modelo_maquina,
						String nro_serie, String cantidad, String codigo, String comentarios,

						String maquinas,

						 String obs_finales, String horas_hombre,
						String obs_general,String diagnostico,String tipo_mantencion,
						String tipo_servicio,String firma_nombre,String firma_rut,String firma_mail*/


						jsonObject.getString("id"),
						jsonObject.getString("rut"),
						jsonObject.getString("nombre_fantasia"),
						jsonObject.getString("razon_social"),
						jsonObject.getString("direccion"),
						jsonObject.getString("logo"),
						jsonObject.getString("tipo_orden_id"),

						jsonObject.getString("fecha_creacion"),
						jsonObject.getString("fecha_ejecucion"),
						//jsonObject.getString("latitud"),
						latitud,
						//jsonObject.getString("longitud"),
						longitud,
						jsonObject.getString("tipo_orden_id"), // ex-estado
						jsonObject.getString("nombre_contacto"),
						jsonObject.getString("correo_contacto"),
						jsonObject.getString("descripcion"),
						"modelo_maquina",
						"nro_serie",
						"cantidad",
						"codigo",
						jsonObject.getString("comentario"),
						jsonObject.getString("json_maquinas"),

						jsonObject.getString("observaciones_finales"),
						jsonObject.getString("horas_hombre"),
						jsonObject.getString("observaciones_generales"),
						jsonObject.getString("diagnostico"),
						jsonObject.getString("tipo_mantencion"),

						jsonObject.getString("tipo_servicio"),
						jsonObject.getString("nombre_receptor"),
						jsonObject.getString("rut_receptor"),
						jsonObject.getString("email_receptor"),
						jsonObject.getString("partes_usadas")


				);
				data.add(userRecord);
				List<UserRecord> OTsLocal  = UserRecord.find(UserRecord.class, "status = ?", "true");
				Boolean s = true;
				for (int j =0; j< OTsLocal.size(); j++) {
					if(jsonObject.getString("id").equals(OTsLocal.get(j).idot)){
						s = false;
						break;
					}
				}
				if(s)userRecord.save();

				Log.i("creacion ot records", "id: " + jsonObject.getString("id") + "/" + jsonObject.getString("tipo_mantencion")+ ":");
			}
			Collections.sort((List<UserRecord>) data, new CustomComparator());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}


	}

	private boolean comprobarLongitud(JSONObject jsonObject) {
		if(jsonObject.has("longitud")){
			
			return true;
		}else{
			return false;
		}
	}

	private boolean comprobarLatitud(JSONObject jsonObject) {
		
		if(jsonObject.has("latitud")){
			return true;
		}else{

			return false;
		}

	}

	public String readDbFeed(String url) {
		Log.i("ProcessData",url);
		StringBuilder builder = new StringBuilder();

		HttpGet httpGet = new HttpGet(url);
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, HTTP_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, HTTP_TIMEOUT);

		HttpClient client = new DefaultHttpClient(httpParameters);

		try {

			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				netState = true;
			} else {
				Log.e(ProcessData.class.toString(), "Failed to download file");
				netState = false;
			}
			return builder.toString();
		} catch (ClientProtocolException e) {
			Log.i("ProcessData","Error de red");
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			Log.i("ProcessData","Error de i/o");
			e.printStackTrace();
			return "";
		}
	}

	public List<UserRecord> getData(){
		return data;
	}

	public boolean setData(String id,String estado){
		int pos = 0;

		for(UserRecord uRec: data){
			if(Integer.parseInt(uRec.idot)==Integer.parseInt(id)){
				uRec.tipo_orden_id = estado;
				uRec.estado = estado;
				data.set(pos, uRec);
				Log.i("Actualizando id"+id,"Pasando a estado"+estado + "Log:" + data.get(pos).toString());
			}
			pos++;
		}
		return true;
	}

	public boolean setOtData(){
		return true;
	}

	public boolean getNetState(){
		return netState;
	}

	public static String readStaticFeed(String url){
		StringBuilder builder = new StringBuilder();
		HttpGet httpGet = new HttpGet(url);
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, HTTP_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, HTTP_TIMEOUT);

		HttpClient client = new DefaultHttpClient(httpParameters);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(ProcessData.class.toString(), "Failed to download file");
			}
			return builder.toString();
		} catch (ClientProtocolException e) {
			Log.i("ProcessData","Error de red");
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			Log.i("ProcessData","Error de i/o");
			e.printStackTrace();
			return "";
		}

	}

	public static String[] Json2StringArray(String r){
		JSONArray jsonArray = null;
		String[] strArray = null;
		try {
			jsonArray = new JSONArray(r);

			strArray = new String[jsonArray.length()];
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				strArray[i] = jsonObject.getString(Integer.toString(i+1));
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		return strArray;
	}

	public static boolean validUser(String idUser){
		String query;
		try {
			query = URLEncoder.encode(idUser, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			query = "";
		}
		Log.i("procdata url:",AppParameters.url_users+"/0/"+query);
		String usr = readStaticFeed(AppParameters.url_users+"/0/"+query);
		if(usr.equals("1"))
			return true;
		return false;
	}

	public static String getUserId(String email){
		String query;
		try {
			query = URLEncoder.encode(email, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			query = "";
		}
		Log.i("procdata url:",AppParameters.url_users+"/"+query);
		String usr = readStaticFeed(AppParameters.url_users+"/"+query);
		Log.i("idUser:",usr);
		return usr;
	}

	public static String getOtState(String ot){
		String query;
		try {
			query = URLEncoder.encode(ot, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			query = "";
		}
		Log.i("procdata url:",AppParameters.url_users+"/1/"+query);
		String state = readStaticFeed(AppParameters.url_users+"/1/"+query);
		return state;
	}

}