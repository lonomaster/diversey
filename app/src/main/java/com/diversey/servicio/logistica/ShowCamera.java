package com.diversey.servicio.logistica;



import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {

   private SurfaceHolder holdMe;
   Camera theCamera;

   public ShowCamera(Context context) {
      super(context);
      theCamera = null;
		try {
			theCamera = Camera.open();
			setWillNotDraw(false);//forzar el refresco
		} catch (Exception e) {
		}
      holdMe = getHolder();
      holdMe.addCallback(this);
   }

   public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	   Camera.Parameters parameters = theCamera.getParameters();
       parameters.setRotation(0);
       parameters.setPreviewSize(300, 300);
       parameters.setPictureSize(600, 600);
       parameters.setFocusMode("auto");
       
       theCamera.setParameters(parameters);
  
       theCamera.setDisplayOrientation(90);
       theCamera.startPreview();
   }

   public void surfaceCreated(SurfaceHolder holder) {
      try   {
         theCamera.setPreviewDisplay(holder);
         //theCamera.startPreview(); 
              
      } catch (IOException e) {
      }
   }

   public void surfaceDestroyed(SurfaceHolder arg0) {
	   //theCamera.stopPreview();
	  // theCamera.release();
   }
   
   protected void onDestroy(){
		  theCamera.stopPreview();
		  theCamera.release();
		  
		   }
   
   public void draw(Canvas canvas){
	   
	   super.draw(canvas);//
   }
   
	
}
