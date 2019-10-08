package com.example.showtime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class launch_screen extends Activity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 0;
    private boolean isTablet = false;
    private ImageView image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //isTablet = getResources().getBoolean(R.bool.isTablet);
        setContentView(R.layout.launch_screen);
        image = (ImageView) findViewById(R.id.launchimv);
        SPLASH_TIME_OUT = getResources().getInteger(R.integer.splash_screen_timeout);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start up the login page
                Intent i = new Intent(launch_screen.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                image.setImageBitmap(null);
                startActivity(i);

            }
        }, SPLASH_TIME_OUT);

    }

    //Shutdown the app
    @Override
    public void onBackPressed() {
        finish();
        System.exit(1);
        return;
    }
}