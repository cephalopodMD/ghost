package edu.virginia.cs2110;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

/**
 * A splash screen activity that pops up over the main activity for 3 seconds and then disappears 
 * 
 * any code not sourced is derived from the android api guide or skills learned in class
 * @author Augustus Logsdon(acl3qb) and Charlotte Blais(ccb7wb) and Piotr Gregrowski(pzg5sj) and Carina Cai(yc5bd)
 *
 */
public class SplashScreen extends Activity{
	// splash screen timer
	private static int SPLASH_TIME_OUT = 3000;
	private MediaPlayer mediaPlayer;
	
	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		this.mediaPlayer = MediaPlayer.create(this.getApplicationContext(), R.raw.neptune_the_mystic);
		this.mediaPlayer.setLooping(true);
		this.mediaPlayer.start();
		
		new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
 
}