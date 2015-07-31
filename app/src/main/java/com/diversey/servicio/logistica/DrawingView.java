package com.diversey.servicio.logistica;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

	private Bitmap  mBitmap;
	private Canvas  mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;
	private Paint mPaint;
	Context context;
	private Paint circlePaint;
	private Path circlePath;

	private float mX, mY;
	private static final int COLOR_NOT_SELECTED = 0xff5c5c5c;

	public int markerId = 0;

	public DrawingView(Context c, AttributeSet attrs) {
		super(c,attrs);
		context=c;
		init();
	}

	private void init() {
		mPath = new Path();
		circlePath = new Path();

		circlePaint = new Paint();
		circlePaint.setAntiAlias(true);
		circlePaint.setColor(Color.GREEN);
		circlePaint.setStyle(Paint.Style.STROKE);
		circlePaint.setStrokeJoin(Paint.Join.MITER);
		circlePaint.setStrokeWidth(4f);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(COLOR_NOT_SELECTED);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(4);


		mBitmapPaint = new Paint(Paint.DITHER_FLAG);

	}


	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mBitmap.eraseColor(Color.WHITE);
		mCanvas = new Canvas(mBitmap);

	}

	public void clearScreen() {
		markerId = 0;
		mBitmap.eraseColor(Color.WHITE);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
		canvas.drawPath( mPath,  mPaint);
		canvas.drawPath( circlePath,  circlePaint);

	}

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= 4 || dy >= 4) {
             mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }
   
    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        mCanvas.drawPath(mPath,  mPaint);
        mPath.reset();
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		getParent().requestDisallowInterceptTouchEvent(true);
//		getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
		
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			break;
		}
		invalidate();
		return true;
	}

	public boolean saveCanvasToFile(String path){
		FileOutputStream fos = null;
		File f = new File(path);
		if(f.exists())f.delete();
		try{
			fos = new FileOutputStream(path);
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
			fos = null;
		}
		catch (IOException e)	{
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
