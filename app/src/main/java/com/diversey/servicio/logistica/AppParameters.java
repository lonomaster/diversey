package com.diversey.servicio.logistica;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public final class AppParameters {


	public static String appName = "DSSL";
	public static String appId = "1";
	public static String appToken = "12345678";

//	public static String host ="clientes.octano.cl"; // laboratorio
	
//	public static String host ="54.94.8.223"; // servidor produccion
	
	public static String host = "45.55.79.26"; // servidor nuevo 15/07/2015
	
//	public static String host = "45.55.79.26/desarrollo"; // servidor desarrollo nuevo 18/06/2015
		
//	public static String host ="200.91.41.176"; // servidor de prueba Elun
	public static String ftpUser = "octano_cl";// laboratorio
	public static String ftpPass = "mZMJH9iZ";// laboratorio
	public static String ftpHost = "clientes.octano.cl"; // laboratorio
	public static String ftpRootPath = "/octano/clientes/";// laboratorio

	//public static String rootPath = "/flux/produccion"; // laboratorio
    public static String rootPath = "/flux/desarrollo"; // desarrollo
	
	public static String ftpPath = ftpRootPath + rootPath + "/images/uploaded/";
	public static String ftpPathQr = ftpPath + "qrcodes/";

	public static String site_state="http://" + host + rootPath + "/webserve/state/"+appToken+"/";

	public static String url="http://" + host + rootPath + "/webserve/data/"+appToken+"/";

	public static String url_pendientes= "/2";
	public static String url_ejecucion ="/3";
	public static String url_realizadas = "/4";

	public static String url_ot="http://"+host+ rootPath + "/webserve/getot/"+appToken+"/";

	public static String url_post_ot="http://"+host+ rootPath + "/webserve/post_data";

	public static String url_users="http://"+host+ rootPath + "/webserve/users";

	public static String pin="http://"+host+ rootPath + "/images/icons/pin_1.png";
	public static String logourl="http://"+host+ rootPath + "/images/logo_empresas/";
	public static String qrurl="http://"+host+ rootPath + "/images/uploaded/qrcodes/qrcode";

	public static String otCasoPlagaMapa="http://"+host+ rootPath + "/images/uploaded/map_plagas/";
	
	public static String applicationPath = "/data/com.diversey.servicio.logistica";
	public static String qrPictureNamePrefix = "qrcode";
	public static String qrPicturePath = Environment.getExternalStorageDirectory() + applicationPath + "/" + qrPictureNamePrefix;
	
	public static String otCasoCafeFile = Environment.getExternalStorageDirectory() + applicationPath + "/" + "otCafe";
	public static String otCasoFileId = "ot";
	public static String otCasoFileProblemas = "problemas";
	public static String otCasoFileRepuestos = "repuestos";
	public static String otCasoFileComentarios = "comentarios";

	public static String otPictureNamePrefix = "photoOT";
	public static String otPicturePath = Environment.getExternalStorageDirectory() + applicationPath + "/" + otPictureNamePrefix;

	public static String cachePicturePath = Environment.getExternalStorageDirectory() + "/data/com.diversey.servicio.logistica/";
	public static String dataPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/"+Main.class.getName()+".data.bin";

	public static  int MAX_CAPTURE = 15;

	public final static  int OT_PENDIENTE = 2;
	public final static  int OT_EJECUCION = 3;
	public final static  int OT_REALIZADA = 4;

	public static boolean createDirIfNotExists(String path) {
		boolean ret = true;

		File file = new File(Environment.getExternalStorageDirectory(), path);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				Log.e(AppParameters.class.getName(), "Problem creating Image folder");
				ret = false;
			}
		}
		return ret;
	}

	public static boolean loadFromUrlBitmap(String url,String name) {

		URL newurl = null;
		InputStream in = null;
		Bitmap bitmap = null;
		try {
			newurl = new URL(url+name);
			URLConnection urlcon = newurl.openConnection();
			urlcon.setConnectTimeout(3*1000);
			urlcon.setReadTimeout(15*1000);
			in = urlcon.getInputStream();
			if(newurl != null){
				bitmap = BitmapFactory.decodeStream(in);
				FileOutputStream out = new FileOutputStream(cachePicturePath+name);
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
				return true;
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Bitmap getUrlImage(String url) {

		URL newurl = null;
		Bitmap bitmap = null;
		try {
			newurl = new URL(url);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		if(newurl != null)
			try {
				bitmap = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		return bitmap;
	}

	public static Bitmap getCacheBitmap(String name){
		File f = new File(cachePicturePath+name);
		if(f.exists())
			return BitmapFactory.decodeFile(cachePicturePath+name);
		else{
			if(loadFromUrlBitmap(logourl,name))
				return BitmapFactory.decodeFile(cachePicturePath+name);
		}
		return null;
	}

	public static boolean postData(JSONObject data,String tipo) {
		Log.i("AppParameters",data.toString());
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url_post_ot);
		Log.i("AppParameters",url_post_ot+"\n"+data.toString());
		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair(tipo, data.toString()));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);
			Log.i("AppParameters",httppost.toString());

			StatusLine statusLine = response.getStatusLine();
			
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				/*Checking response */
				if(response!=null){
					InputStream in = response.getEntity().getContent();
					String result = in.toString();
					Log.i("Http Response:",tipo + ": " + result);
					return true;
				}
			}
		}catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		return false;
	}

	public static boolean uploadFile(String filename, String ftpPath){
		boolean ret = false;
		try {
			SimpleFTP ftp = new SimpleFTP();
			Log.i("FTP:",filename + " copy to:" + ftpPath);
			ftp.connect(AppParameters.ftpHost, 21, AppParameters.ftpUser, AppParameters.ftpPass);
			ftp.bin();
			ftp.cwd(ftpPath);
			File f = new File(filename);
			if(f!=null){
				ftp.stor(f);
				ret = true;
			}
			ftp.disconnect();
		}catch (IOException e) {
			ret = false;
		}
		return ret;
	}

	public static boolean StoreByteImage(byte[] imageData, int quality, String expName, String outputImage) {

		FileOutputStream fileOutputStream = null;
		try {

			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inSampleSize = 5;

			Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0, imageData.length,options);

			fileOutputStream = new FileOutputStream(outputImage);


			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

			myImage.compress(CompressFormat.JPEG, quality, bos);

			bos.flush();
			bos.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

}
