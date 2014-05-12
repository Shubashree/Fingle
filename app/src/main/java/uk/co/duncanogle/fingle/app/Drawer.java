package uk.co.duncanogle.fingle.app;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

/**
 * Created by Duncan on 11/05/2014.
 */
public class Drawer extends Thread {

    private SurfaceHolder mSurfaceHolder;
    private Paint paint;
    private Canvas c;

    public Drawer(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
        paint = new Paint();
        paint.setARGB(255, 255, 255, 255);
        paint.setTextSize(32);
    }

    public void run() {
        c = null;
        try {
            c = mSurfaceHolder.lockCanvas(null);
            synchronized (mSurfaceHolder) {
                doDraw(c);
                sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                mSurfaceHolder.unlockCanvasAndPost(c);
            }
        }
    }

    private void doDraw(Canvas canvas) {
        // clear the canvas
        // canvas.drawColor(Color.BLACK);

        Random random = new Random();
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        int x = random.nextInt(w - 50);
        int y = random.nextInt(h - 50);
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        int size = 20;
        canvas.drawCircle(x, y, size, paint);
        canvas.restore();
    }

}

