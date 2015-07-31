package com.diversey.servicio.logistica;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ElunVal {

	static SharedPreferences sp;
	static Editor edit;


	public static boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	public static boolean isPhoneValid(String phone) {
		boolean isValid = false;

		String expression = "^(09[5-9][0-9]{7})$";
		CharSequence inputStr = phone;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public Bitmap getRoundedCornerBitmap(Bitmap scaleBitmapImage) {
		Bitmap dstBmp;
		if (scaleBitmapImage.getWidth() >= scaleBitmapImage.getHeight()){

			dstBmp = Bitmap.createBitmap(
                    scaleBitmapImage,
                    scaleBitmapImage.getWidth() / 2 - scaleBitmapImage.getHeight() / 2,
                    0,
                    scaleBitmapImage.getHeight(),
                    scaleBitmapImage.getHeight()
            );

		}else{

			dstBmp = Bitmap.createBitmap(
                    scaleBitmapImage,
                    0,
                    scaleBitmapImage.getHeight() / 2 - scaleBitmapImage.getWidth() / 2,
                    scaleBitmapImage.getWidth(),
                    scaleBitmapImage.getWidth()
            );
		}
		//          create circle
		Bitmap output = Bitmap.createBitmap(dstBmp.getWidth(),
                dstBmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();

		final Rect rect1 = new Rect(0, 0, dstBmp.getWidth(), dstBmp.getHeight());
		final RectF rectF1 = new RectF(rect1);


		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawOval(rectF1, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(dstBmp, rect1, rect1, paint);

		return output;
	}	

	public Bitmap StringToBitMap(String encodedString){
		try{
			byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
			Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
			return bitmap;
		}catch(Exception e){
			e.getMessage();
			return null;
		}
	}    

	public String BitMapToString(Bitmap bitmap){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG,80, baos);
		byte [] b=baos.toByteArray();
		String temp= Base64.encodeToString(b, Base64.DEFAULT);
		return temp;
	}

	public Bitmap comprimirBitmap(Bitmap bitmap, int width, int height)
	{
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;

		int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
		int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

		if (heightRatio > 1 || widthRatio > 1)
		{
			if (heightRatio > widthRatio)
			{
				bmpFactoryOptions.inSampleSize = heightRatio;
			} 
			else
			{
				bmpFactoryOptions.inSampleSize = widthRatio; 
			}
		}

		bmpFactoryOptions.inJustDecodeBounds = false;
		return bitmap;
	}

	public static byte[] getBytesFromFile(File file) throws IOException {
		// Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
			throw new IOException("File is too large!");
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int)length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;

		InputStream is = new FileInputStream(file);
		try {
			while (offset < bytes.length
					&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
				offset += numRead;
			}
		} finally {
			is.close();
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "+file.getName());
		}
		return bytes;
	}

	public static boolean isAlpha(String name) {
		return name.matches("[a-zA-Záéíóú�?É�?ÓÚüÜñÑ\\s]+");
	}

	public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
	{ // BEST QUALITY MATCH

		//First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize, Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		int inSampleSize = 2;
		//
		//	    if (height > reqHeight)
		//	    {
		//	        inSampleSize = Math.round((float)height / (float)reqHeight);
		//	    }
		//	    int expectedWidth = width / inSampleSize;
		//
		//	    if (expectedWidth > reqWidth)
		//	    {
		//	        //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
		//	        inSampleSize = Math.round((float)width / (float)reqWidth);
		//	    }
		//
		options.inSampleSize = inSampleSize;
		//
		//	    // Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(path, options);
	}

	public static boolean isNetworkAvailable(Context activity) {
		ConnectivityManager connectivityManager
		= (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static boolean validateRut(String rut) {

		boolean validacion = false;
		try {
			rut =  rut.toUpperCase();
			//    		rut = rut.replace(".", "");
			//    		rut = rut.replace("-", "");
			int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

			char dv = rut.charAt(rut.length() - 1);

			int m = 0, s = 1;
			for (; rutAux != 0; rutAux /= 10) {
				s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
			}
			if (dv == (char) (s != 0 ? s + 47 : 75)) {
				validacion = true;
			}

		} catch (java.lang.NumberFormatException e) {
		} catch (Exception e) {
		}
		return validacion;
	}


	public static boolean isWhitespace(String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	Animation animRotate;

	//    public Dialog loader(Context ctx){
	//
	//    	Dialog dialog = null;
	//    	dialog = new Dialog(ctx);
	//    	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	//    	dialog.setCancelable(false);
	//    	dialog.setContentView(R.layout.dialog_loader);
	//    	dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	//
	//    	ImageView elipse = (ImageView)dialog.findViewById(R.id.loader_elipse);
	//    	animRotate = AnimationUtils.loadAnimation(ctx, R.anim.rotate_toright);
	//    	animRotate.setRepeatMode(Animation.INFINITE);
	//    	elipse.startAnimation(animRotate);
	//
	//    	return dialog;
	//    }



	public static Bitmap obtenerBitmap(String pathImage) {

		Uri photoUri = Uri.fromFile(new File(pathImage));

		File file = new File(photoUri.getPath());
		OutputStream fOut = null;
		Bitmap srcBmp = ElunVal.decodeSampledBitmapFromFile(file.getAbsolutePath(), 500, 500);
		Bitmap image_bm = srcBmp;

		return image_bm;
	}

}
