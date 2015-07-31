package com.diversey.servicio.logistica;

import android.graphics.Bitmap;
import jp.sourceforge.qrcode.data.QRCodeImage;

public class QRBitmap implements QRCodeImage {

	Bitmap image;
	
	public QRBitmap(Bitmap source) {
		// TODO Auto-generated constructor stub
		this.image = source;
	}

	public int getHeight() {
		// TODO Auto-generated method stub
		return image.getHeight();
	}

	public int getPixel(int x, int y) {
		// TODO Auto-generated method stub
		return image.getPixel(x, y);
	}

	public int getWidth() {
		// TODO Auto-generated method stub
		return image.getWidth();
	}

}
