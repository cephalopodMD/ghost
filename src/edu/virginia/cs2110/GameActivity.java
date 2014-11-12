package edu.virginia.cs2110;

import com.google.android.gms.maps.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * the activity from which the game is launched. 
 * can be exited using a button defined in on preExecute() in the game loop 
 * 
 * any code not sourced is derived from the android api guide or skills learned in class
 * @author Augustus Logsdon(acl3qb) and Charlotte Blais(ccb7wb) and Piotr Gregrowski(pzg5sj) and Carina Cai(yc5bd)
 *
 */
public class GameActivity extends FragmentActivity {

	private int level;
	private static GoogleMap mapObject;
	private GameLoop loop;
	private GameSave newGame;

	/**
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	/**
	 * gets the difficulty level from the intent and sets up the map
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		this.setLevel(intent.getIntExtra("ghosts", 1)); // get level from intent
		setContentView(R.layout.activity_game);
		this.setUpMapIfNeeded();
	}

	/**
	 * Called when the Activity becomes visible.
	 * loads the gamesave difficulty and then begins the async task that executes the game logic
	 */
	@Override
	protected void onResume() {
		super.onResume();
		this.loop = new GameLoop(this);
		if (this.getLevel()==1) {
				newGame = new GameSave(1, false, new Player(), .5f);
		} else if (this.getLevel()==2) {
				newGame = new GameSave(2, true, new Player(), .5f);
		} else if (this.getLevel()==3) {
				newGame = new GameSave(3, true, new Player(), 1f);		
		}
		this.loop.execute(newGame);
	}

	/**
	 * the game loop isn't cancelled when the activity is paused so that it can update in the background
	 */
	@Override
	protected void onPause() {
		super.onPause();
		this.loop.cancel(false);
	}

	/**
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		super.onStop();
	}

	/**
	 * Handle results returned to the FragmentActivity by Google Play services
	 * Taken from official android maps v2.0 guide
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {
		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK:
				/*
				 * Try the request again
				 */
				break;
			}

		}
	}

	private void setUpMapIfNeeded() {
		// Do a check to confirm that we haven't already instantiated the map.
		if (mapObject == null) {
			this.setMapObject(((SupportMapFragment) getSupportFragmentManager().findFragmentById(
					R.id.map)).getMap());
			// Check if we were successful in obtaining the map.
			if (this.getMapObject() != null) {
				// The Map is verified. It is now safe to manipulate the map.
			}
		}
	}

	/**
	 * @return the Map
	 */
	public GoogleMap getMapObject() {
		return mapObject;
	}
	
	/**
	 * @param Map
	 *            the Map to set
	 */
	public void setMapObject(GoogleMap map) {
		this.mapObject = map;
	}

	
	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * sets the level number
	 * 
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}
}
