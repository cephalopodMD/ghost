package edu.virginia.cs2110;

import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * the Game loop where all logic happens Async task takes a save file and loads
 * its data Async task starts a Handler loop Handler loop runs a modified
 * "input, process, output" game loop this modifies game variables along with
 * the save file when the loop is stopped, the async task returns the save file
 * (actual saving not used so far)
 * 
 * any code not sourced is derived from the android api guide or skills learned in class
 * 
 * @author Augustus Logsdon(acl3qb) and Charlotte Blais(ccb7wb) and Piotr
 *         Gregrowski(pzg5sj) and Carina Cai(yc5bd)
 * 
 */
public class GameLoop extends AsyncTask<GameSave, Integer, GameSave> {

	private GameActivity gameActivity; // parent activity
	private GameSave gameSave; // stores game state in case of death/level up
	private boolean running; // used to determine if game loop should continue
	private Button btnPause;
	private Button btnLocation;
	private LocationTracker gps;

	// entities in the game
	private ArrayList<Ghost> ghosts;
	private Player player;
	private BoneMap boneMap;

	// used to get distance results
	float[] results = new float[3];

	// notification stuff
	private NotificationManager notificationManager;
	private Builder gameNotification;

	// used to make frame render more efficient
	private boolean textChanged;
	private boolean mapChanged;

	private MediaPlayer mediaPlayer;

	/**
	 * a handler that is reused to run a single frame operation
	 */
	private final Handler handler = new Handler();
	/**
	 * a frame operation keeps running until the running field is set to false
	 * in the game loop
	 * 
	 * based on
	 * http://stackoverflow.com/questions/18188118/run-a-asynctask-every
	 * -10-seconds-until-cancelled
	 */
	private final Runnable frame = new Runnable() {
		@Override
		public void run() {

			// simple game loop
			getInput();
			processUpdates();
			publishProgress();

			// change ghost speed based on how frequently the game is updating
			if (isRunning()) {
				if (isCancelled()) {
					//update every 10 seconds in background
					Ghost.speed = 10 * getGameSave().getGhostSpeed();
					handler.postDelayed(frame, 10000);
				} else {
					//update every second in foreground
					Ghost.speed = getGameSave().getGhostSpeed();
					handler.postDelayed(frame, 1000);
				}
			} else {
				Log.d("notcancelled", "the game is not running");
				synchronized (frame) {
					frame.notify();
				}
			}
		}
	};

	/**
	 * constructor
	 * 
	 * @param activity
	 *            called upon create to get context when needed
	 */
	public GameLoop(GameActivity activity) {
		this.setMyActivity(activity);
	}

	/**
	 * sets up music, gps/map bindings, initializes entities, notifications, and
	 * the two game buttons
	 */
	@Override
	protected void onPreExecute() {
		this.mediaPlayer = MediaPlayer.create(this.getGameActivity()
				.getApplicationContext(), R.raw.neptune_the_mystic);
		this.mediaPlayer.setLooping(true);
		this.mediaPlayer.start();
		gps = new LocationTracker(this.getGameActivity()
				.getApplicationContext(), this.getMap());
		this.player = new Player();
		this.player.setLatLng(this.getGps().getLatLng());
		this.boneMap = new BoneMap();
		Intent intent = new Intent(this.getGameActivity()
				.getApplicationContext(), GameActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(
				this.getGameActivity(), 0, intent, 0);
		gameNotification = new NotificationCompat.Builder(
				this.getGameActivity())
				.setContentTitle("You are being haunted")
				.setContentText("there are no ghosts nearby!")
				.setSmallIcon(R.drawable.ghostpic)
				.setLargeIcon(
						BitmapFactory.decodeResource(this.getGameActivity()
								.getResources(), R.drawable.ghostpic))
				.setContentIntent(pIntent).setAutoCancel(true);
		// .addAction(R.drawable.icon, "Call", pIntent)
		// .addAction(R.drawable.icon, "More", pIntent)
		// .addAction(R.drawable.icon, "And more", pIntent)
		notificationManager = (NotificationManager) this.getGameActivity()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, gameNotification.build());

		// stop on click
		btnPause = (Button) this.getGameActivity().findViewById(R.id.button1);
		btnPause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setRunning(false);
			}
		});

		// show location button click event
		btnLocation = (Button) this.getGameActivity()
				.findViewById(R.id.button4);
		btnLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				gps.recenterCamera();
				Toast.makeText(
						getGameActivity().getApplicationContext(),
						"Your Location is - \nLat: " + gps.getLatitude()
								+ "\nLong: " + gps.getLongitude(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * loads some information from the game save then starts the runnable loop
	 * above
	 */
	@Override
	protected GameSave doInBackground(GameSave... saves) {
		this.setGameSave(saves[0]);
		this.reloadSave();

		this.setRunning(true);
		frame.run();
		Log.d("game", "game started");
		synchronized (frame) {
			try {
				frame.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.getGameSave();
	}

	/**
	 * load data from the save object for player stats, the number of hosts, and
	 * whether or not the player can see ghost bones
	 */
	private void reloadSave() {
		this.player.setHealth(100);
		this.player.setLives(this.getGameSave().getPlayer().getLives());
		this.player.setKills(this.getGameSave().getPlayer().getKills());
		this.player.setHasMap(true);
		this.ghosts = new ArrayList<Ghost>();
		for (int i = 0; i < this.getGameSave().getGhosts(); i++) {
			this.ghosts.add(new Ghost());
			this.ghosts.get(i).setLatLng(
					ghosts.get(i).randomLatLng(player.getLatLng()));
			this.ghosts.get(i).bones.setLatLng(ghosts.get(i).randomLatLng(
					player.getLatLng()));
		}
		if (this.getGameSave().getThereAreMaps()) {
			this.boneMap.setLatLng(ghosts.get(0).randomLatLng(
					player.getLatLng()));
			this.player.setHasMap(false);
		}
		this.mapChanged = true;
		this.textChanged = true;
		this.publishProgress();
	}

	/**
	 * unused part of game loop
	 */
	private void getInput() {
		// pretty much all handled by various listeners and handlers
	}

	/**
	 * meat of game logic:
	 * updates player location
	 * checks for collisions with the boneMap, all ghosts, and the ghost bones
	 * updates player health according to bones being held (number of ghosts scared) and ghosts touched
	 * updates notification
	 */
	private void processUpdates() {
		// location updates
		this.player.setLatLng(this.getGps().getLatLng());
		this.player.setGhostsNearby(0);
		// map collision
		if (!player.isHasMap()) {
			android.location.Location.distanceBetween(
					player.getLatLng().latitude, player.getLatLng().longitude,
					boneMap.getLatLng().latitude,
					boneMap.getLatLng().longitude, results);
			if (results[0] < 5) {
				player.setHasMap(true);
				this.boneMap.getGroundOverlay().remove();
				this.mapChanged=true;
			}
		}
		// ghost and bone collisions
		for (Ghost ghost : new ArrayList<Ghost>(ghosts)) {
			//ghost collisions
			android.location.Location.distanceBetween(
					player.getLatLng().latitude, player.getLatLng().longitude,
					ghost.getLatLng().latitude, ghost.getLatLng().longitude,
					results);
			ghost.chase(results[0], this.player.getLatLng());
			if (results[0] < 15) {
				this.player.setGhostsNearby(player.getGhostsNearby() + 1);
				if (results[0] < 5) {
					if (ghost.isScared()) {
						ghost.getGroundOverlay().remove();
						ghosts.remove(ghost);
						player.setKills(player.getKills() + 1);
						this.getGameSave().getPlayer()
								.setKills(this.player.getKills());
						textChanged = true;
						if (ghosts.isEmpty()) {
							this.getGameSave().setGhosts(
									this.getGameSave().getGhosts() + 1);
							this.reloadSave();
							Toast.makeText(getGameActivity(),
									"Level up! \nYour health is replenished.",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						this.player.setHealth(this.player.getHealth() - 5);
						textChanged = true;
					}
				}
			}
			if (ghost.isScared()) {
				this.player.setHealth(this.player.getHealth() - 1);
				this.textChanged = true;
			} else if (player.isHasMap()) {
				//bone collisions
				android.location.Location.distanceBetween(
						player.getLatLng().latitude,
						player.getLatLng().longitude,
						ghost.bones.getLatLng().latitude,
						ghost.bones.getLatLng().longitude, results);
				if (results[0] < 5 && ghost.bones.getGroundOverlay() != null) {
					ghost.bones.getGroundOverlay().remove();
					ghost.setScared(true);
					Toast.makeText(
							getGameActivity(),
							"You just picked up the bones of a ghost.\nUse them quickly! They will slowly kill you!",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
		if (this.player.getHealth() <= 0) {
			this.getGameSave().getPlayer().setLives(this.player.getLives() - 1);
			if (player.getLives() == 0) {
				Toast.makeText(getGameActivity(),
						"You are now forever one with the ghosts!",
						Toast.LENGTH_SHORT).show();
				this.notificationManager.notify(0,
						this.gameNotification.build());
				this.setRunning(false);
			} else {
				Toast.makeText(getGameActivity(), "You died!",
						Toast.LENGTH_SHORT).show();
				this.reloadSave();
			}
		}
		//notification updates
		if (this.isRunning()) {
			this.gameNotification
					.setContentText("Watch out! You are being closely pursued by "
							+ this.player.getGhostsNearby() + " ghosts!");
			this.notificationManager.notify(0, this.gameNotification.build());
		}
	}

	/**
	 * just calls render method
	 * no real reason to do this other than for coding style
	 */
	@Override
	protected void onProgressUpdate(Integer... arg0) {
		render();
	}

	/**
	 * adds entities that should be visible to map
	 * updates player statistics to view
	 */
	private void render() {
		if (mapChanged) {
			this.getMap().clear();
			player.setGroundOverlay(gps.addEntity(player));
			if (!this.player.isHasMap()) {
				this.boneMap.setGroundOverlay(gps.addEntity(boneMap));
			}
			for (Ghost ghost : ghosts) {
				if (this.player.isHasMap()) {
					ghost.bones.setGroundOverlay(gps.addEntity(ghost.bones));
				}
				ghost.setGroundOverlay(gps.addEntity(ghost));
			}
			this.mapChanged = false;
		}
		if (textChanged) {
			((TextView) this.getGameActivity().findViewById(R.id.player_health))
					.setText("Health:" + this.player.getHealth());
			((TextView) this.getGameActivity().findViewById(R.id.player_lives))
					.setText("Lives:" + this.player.getLives());
			((TextView) this.getGameActivity().findViewById(
					R.id.inventory_bones)).setText("Kills:"
					+ this.player.getKills());
			textChanged = false;
		}
	}

	/**
	 * stops music, gps, and notifications
	 * launches back into main activity
	 */
	@Override
	protected void onPostExecute(GameSave result) {
		this.mediaPlayer.stop();
		this.getGps().stopUsingGPS();
		notificationManager.cancel(0);
		Intent restart = new Intent(this.getGameActivity()
				.getApplicationContext(), MainActivity.class);
		this.getGameActivity().startActivity(restart);
	}

	/**
	 * user location is not updated when the activity is cancelled, ghosts just chase the user
	 */
	@Override
	protected void onCancelled() {
		this.getGps().stopUsingGPS();
	}

	/**
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @param running
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * @return
	 */
	public LocationTracker getGps() {
		return this.gps;
	}

	/**
	 * @param gps
	 */
	public void setGps(LocationTracker gps) {
		this.gps = gps;
	}

	/**
	 * @return
	 */
	public GoogleMap getMap() {
		return this.getGameActivity().getMapObject();
	}

	/**
	 * @return the location
	 */
	/**
	 * @return
	 */
	public LatLng getLatLng() {
		return this.getGps().getLatLng();
	}

	/**
	 * @param gameActivity
	 */
	public void setGameActivity(GameActivity gameActivity) {
		this.gameActivity = gameActivity;
	}

	/**
	 * @return
	 */
	public GameActivity getGameActivity() {
		return gameActivity;
	}

	/**
	 * @param myActivity
	 */
	public void setMyActivity(GameActivity myActivity) {
		this.gameActivity = myActivity;
	}

	/**
	 * @return the gameSave
	 */
	/**
	 * @return
	 */
	public GameSave getGameSave() {
		return gameSave;
	}

	/**
	 * @param gameSave
	 *            the gameSave to set
	 */
	/**
	 * @param gameSave
	 */
	public void setGameSave(GameSave gameSave) {
		this.gameSave = gameSave;
	}

}
