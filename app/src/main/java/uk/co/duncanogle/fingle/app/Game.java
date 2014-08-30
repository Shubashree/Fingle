package uk.co.duncanogle.fingle.app;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

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
    int ccontr = 0;
    private GameView gameView;
    static volatile boolean paused;
    static AlertDialog alertDialog = null; //new AlertDialog.Builder(this).create();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        paused = false;
        gameView = new GameView(self);
        setContentView(gameView);
        handler = new Handler();
        addCenterCircle();
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

    private void addCenterCircle() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int HighX = size.x;
        int HighY = size.y;

        Random r = new Random();

        String Col[] = {
                "#2A80B9",
                "#2C3E50",
                "#1ABC9C",
                "#E0293F",
                "#1ABC14",
                "#FFFC14"};

        String Clr = Col[r.nextInt(6)];

        circles2[counter % 10] = new Circle(HighX / 2, HighY / 2, 400, Clr);
        counter++;

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
//        Log.d("d", String.valueOf(gameView.checkColour(e.getX(), e.getY())));
        if (e.getPointerCount() == 1) {

            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    paused = true;

                    return true;

//                new Thread(runnable).start();

                case MotionEvent.ACTION_UP:
                    paused = false;
//                toast(String.valueOf(counter * ccontr));
                    saveHighScore();
                    dialog("You lifted your finger from the screen! \n" +
                            "You scored " + getScore() + ". \n" +
                            "Your highscore is " + getHighScore());
//                toast(String.valueOf(getHighScore()));
                    reset();
                    return true;
//                new Thread(runnable).start();

                case MotionEvent.ACTION_MOVE:
//                    toast(String.valueOf(checkBounds(e.getX(), e.getY())));
//                    TextView view = (TextView) findViewById(R.id.bounds);
//                    if (checkBounds(e.getX(), e.getY())) {
//                        view.setText("true");
//                    }
//                    else {
//                        view.setText("false");
//                    }
                    if (!checkBounds(e.getX(), e.getY())) {
                        paused = false;
                        saveHighScore();
                        dialog("You moved outside a circle! \n" +
                                "You scored " + getScore() + ". \n" +
                                "Your highscore is " + getHighScore());
                        reset();
                    }
                    return true;

            }
        } else {
            saveHighScore();
            dialog("You can only use 1 finger at a time! \n" +
                    "You scored " + getScore() + ". \n" +
                    "Your highscore is " + getHighScore());
            reset();
            paused = false;
//            return false;
        }

        return true;
    }

    private void reset() {
        counter = 0;
        ccontr = 0;
        circles2 = new Circle[10];
        addCenterCircle();
    }

    private int getScore() {
        return counter * ccontr;
    }

    private void dialog(String s) {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(this).create();
        }
        if (alertDialog.isShowing() == false) {
            alertDialog.setTitle("Oops!");
            alertDialog.setMessage(s);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
// here you can add functions
                }
            });
            alertDialog.show();
        }
//        toast(String.valueOf(alertDialog.isShowing()));
    }

    private void toast(String s) {
        Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void saveHighScore() {
        if (counter * ccontr > getHighScore()) {
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("MyPref", 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("hs", counter * ccontr);
            editor.commit();
        }
    }

    private void resetHighScore() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("hs", 0);
        editor.commit();
    }

    private int getHighScore() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("MyPref", 0);
        int x = sharedPref.getInt("hs", -2);
        return x;
    }

    private boolean checkBounds(float x, float y) {
        boolean result = false;
//        int xx = Integer.parseInt(String.valueOf(x));
//        int yy = Integer.parseInt(String.valueOf(y));

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

    /**
     * Game View Class
     */
    class GameView extends View {

        Paint paint = new Paint();

        public GameView(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_get_score) {
            dialog(String.valueOf(getHighScore()));
            return true;
        }
        if (id == R.id.action_reset_score) {
            dialog("Score Reset");
            resetHighScore();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
