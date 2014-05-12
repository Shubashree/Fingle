package uk.co.duncanogle.fingle.app;

import android.graphics.Color;
import android.graphics.Paint;
/**
 * Created by Duncan on 11/05/2014.
 */
public class Circle {
    int x;
    int y;
    int radius;
    Paint paint;

    public Circle(int x, int y, int radius, String paint) {
        Paint paint2 = new Paint();
        paint2.setStyle(Paint.Style.FILL);

        this.x = x;
        this.y = y;
        this.radius = radius;
        paint2.setColor(Color.parseColor(paint));
        this.paint = paint2;
        paint2.setAlpha(125);
    }

    public void subRadius(int val) {
        if (radius > 0) {
            this.radius -= val;
        }
    }
}

