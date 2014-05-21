package uk.co.duncanogle.fingle.app;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import java.util.Random;

public class MainActivity extends Activity {
    Context self = this;
    private Handler handler;
    private Circle circles2[] = new Circle[10];
    int counter = 0;
    int ccontr = 0;
    float fingerX = 0;
    float fingerY = 0;
    private GameView gameView;
    static volatile boolean paused;
    static AlertDialog alertDialog = null;
    Thread mainThread;
    SharedPreferences sharedPref;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        sharedPref = getApplicationContext().getSharedPreferences("MyPref", 0);
        paused = true;
        gameView = new GameView(self);
        setContentView(gameView);
        handler = new Handler();
        alertDialog = new AlertDialog.Builder(this).create();
        addCircle(true);
        dialog("Lets go!", "Keep your finger pressed on the circle for as long as possible!");
        mainThread = new Thread() {
            public void run() {
                int countr = 0;
                //noinspection InfiniteLoopStatement
                while (true) {
                    if (!paused) {
                        try {
                            Thread.sleep(10);
                            countr += 20;
                            ccontr += 20;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.post(new Runnable() {
                            public void run() {
                                shrinkCircles();
                            }
                        });
                        if (countr % 2000 == 0) {
                            handler.post(new Runnable() {
                                public void run() {
                                    addCircle(false);
                                }
                            });
                        }
                    } else synchronized (this) {
                        try {
                            paused = false;
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        mainThread.start();
    }

    private void addCircle(boolean center) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int HighX = size.x;
        int HighY = size.y;

        Random r = new Random();

        int RX = r.nextInt(HighX);
        int RY = r.nextInt(HighY);

        int Radius = r.nextInt(600 - 200) + 200;

        if(center) {
            RX = HighX/2;
            RY = HighY/2;
            Radius = 200;
        }

        String Col[] = {"#2A80B9", "#2C3E50", "#1ABC9C", "#E0293F", "#1ABC14", "#FFFC14"};

        String Clr = Col[r.nextInt(6)];

        circles2[counter % 10] = new Circle(RX, RY, Radius, Clr);
        counter++;

        gameView.invalidate();
    }


    private void shrinkCircles() {
        for (int i = 0; i < 10; i++) {
            if (circles2[i] != null) {
                circles2[i].subRadius(1);
            }
        }
        gameView.invalidate();
    }

    public boolean onTouchEvent(MotionEvent e) {
        fingerX = e.getX();
        fingerY = e.getY();
        if (e.getPointerCount() == 1) {
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    if (!alertDialog.isShowing()) {
                        go();
                        if (!checkBounds(e.getX(), e.getY())) {
                            pause();
                            saveHighScore();
                            dialog("Oops!", "You moved outside a circle! \n" +
                                    "You scored " + getScore() + ". \n" +
                                    "Your high score is " + getHighScore());
                            reset();
                        }
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                    pause();
                    saveHighScore();
                    dialog("Oops!", "You lifted your finger from the screen! \n" +
                            "You scored " + getScore() + ". \n" +
                            "Your highscore is " + getHighScore());
                    reset();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (!alertDialog.isShowing()) {
                        if (!checkBounds(e.getX(), e.getY())) {
                            pause();
                            saveHighScore();
                            dialog("Oops!", "You moved outside a circle! \n" +
                                    "You scored " + getScore() + ". \n" +
                                    "Your high score is " + getHighScore());
                            reset();
                        }
                    }

                    return true;
            }
        } else {
            if (!alertDialog.isShowing()) {
                pause();
                saveHighScore();
                dialog("Oops!", "You can only use 1 finger at a time! \n" +
                        "You scored " + getScore() + ". \n" +
                        "Your high score is " + getHighScore());
                reset();
            }

        }

        return checkBounds(e.getX(), e.getY());
    }

    private synchronized void pause() {
        paused = true;
    }

    private synchronized void go() {
        paused = false;
        //noinspection SynchronizeOnNonFinalField
        synchronized (mainThread) {
            mainThread.notify();
        }
    }

    private void reset() {
        counter = 0;
        ccontr = 0;
        circles2 = new Circle[10];
        addCircle(true);
    }

    private int getScore() {
        return counter * ccontr;
    }

    private void dialog(String s1, String s2) {
        if (!alertDialog.isShowing()) {
            alertDialog.setTitle(s1);
            alertDialog.setMessage(s2);
            alertDialog.show();
        }
    }

    private void saveHighScore() {
        if (counter * ccontr > getHighScore()) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("hs", counter * ccontr);
            editor.commit();
        }
    }

    private void resetHighScore() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("hs", 0);
        editor.commit();
    }

    private int getHighScore() {
        return sharedPref.getInt("hs", -2);
    }

    private boolean checkBounds(float x, float y) {
        boolean result = false;

        for (int i = 0; i < 10; i++) {
            if (circles2[i] != null) {
                if ((Math.pow(x - circles2[i].x, 2) + Math.pow(y - circles2[i].y, 2)) < Math.pow(circles2[i].radius, 2)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    // GAME VIEW CLASS
    class GameView extends View {
        Paint paint = new Paint();

        public GameView(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
            canvas.drawPaint(paint);


            for (int i = 0; i < 10; i++) {
                if (circles2[i] != null) {
                    canvas.drawCircle(circles2[i].x, circles2[i].y, circles2[i].radius, circles2[i].paint);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_get_score) {
            dialog("Your high score", String.valueOf(getHighScore()));
            return true;
        }
        if (id == R.id.action_reset_score) {
            dialog("Score reset", "Looks like you're back to 0!");
            resetHighScore();
            return true;
        }
        if (id == R.id.action_show_about) {
            dialog("Fingle - Thanks for playing!",
                    "Fingle is a game where circles of decreasing size are drawn on the " +
                            "screen and with one finger pressed on the screen you must stay on these " +
                            "circles. If you come off the circles, try using more than one finger, " +
                            "or take your finger off the screen, you lose." +
                            "\nThis app was made for part of my university degree. I hope you enjoy it!");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}