package uk.co.duncanogle.fingle.app;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import java.util.Random;

/**
 * Created by Duncan on 11/05/2014.
 */
public class Game extends Activity {
    Context self = this;
    private Handler handler;
    // private ArrayList<Circle> circles = new ArrayList<Circle>();
    private Circle circles2[] = new Circle[10];
    int counter = 0;
    private GameView gameView;
    static volatile boolean paused;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        paused = false;
        gameView = new GameView(self);
        setContentView(gameView);
        handler = new Handler();
        addCircle();
        Runnable runnable = new Runnable() {
            public void run() {
                int countr = 0;
                while (true) {
                    if (paused) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                addCircle();
//                            }
//                        });
//                        addCircle();
                        try {
                            Thread.sleep(20);
                            countr += 20;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.post(new Runnable() {
                            public void run() {
                                shrinkCircles();
                            }
                        });
                        if (countr % 1000 == 0) {
                            handler.post(new Runnable() {
                                public void run() {
                                    addCircle();
                                }
                            });
                        }
                    }

                }
            }
        };
        new Thread(runnable).start();

    }

    private void addCircle() {
        // Get display size in pixels
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Store display size
        int HighX = size.x;
        int HighY = size.y;

        // New random object
        Random r = new Random();

        // Randomly generate where the circle will appear next
        int RX = r.nextInt(HighX);
        int RY = r.nextInt(HighY);

        // Randomly generate radius of circle
        int Radius = r.nextInt(600 - 200) + 200;

        // Array of colours
        String Col[] = {
                "#2A80B9",
                "#2C3E50",
                "#1ABC9C",
                "#E0293F",
                "#1ABC14",
                "#FFFC14"};

        // Then randomly select a colour for the new circle
        String Clr = Col[r.nextInt(6)];

        // Add the circle to the canvas
        // circles.add(new Circle(RX, RY, Radius, Clr));

        circles2[counter % 10] = new Circle(RX, RY, Radius, Clr);
        counter++;

        // Invalidate the canvas and allow it to be redrawn
        gameView.invalidate();
    }

    private void shrinkCircles() {

        for (int i = 0; i < 10; i++) {
            if (circles2[i] != null) {
                circles2[i].subRadius(1);
            }
        }
        // for (Circle c : circles2) {
        // c.subRadius(1);
        // }

        gameView.invalidate();
    }

    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                paused = true;
                return true;
//                new Thread(runnable).start();

            case MotionEvent.ACTION_UP:
                paused = false;
                return true;
//                new Thread(runnable).start();
        }

        return true;
    }


    /**
     * Game View Class
     */
    class GameView extends View {

        Paint paint = new Paint();

        public GameView(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            // paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawPaint(paint);

            for (int i = 0; i < 10; i++) {
                if (circles2[i] != null) {
                    canvas.drawCircle(circles2[i].x, circles2[i].y,
                            circles2[i].radius, circles2[i].paint);
                }
            }

        }

    }
}
