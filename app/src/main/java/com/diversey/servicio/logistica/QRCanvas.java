package com.diversey.servicio.logistica;

import jp.sourceforge.qrcode.util.DebugCanvasAdapter;
import android.util.Log;

class QRCanvas extends DebugCanvasAdapter {
	public void println(String s) {
		Log.i("QR decode:",s);
	}
}
