package edu.virginia.cs2110;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

/**
 * a location and map object
 * location object is updated by android constantly
 * map object from gameactivity is modified either by recentering the camera or adding overlays
 * 
 * any code not sourced is derived from the android api guide or skills learned in class
 * @author Augustus Logsdon(acl3qb) and Charlotte Blais(ccb7wb) and Piotr Gregrowski(pzg5sj) and Carina Cai(yc5bd)
 *
 */
public class LocationTracker extends Service implements LocationListener {

	private final Context myContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 3 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 0; // 5 seconds

	// Declaring a Location Manager
	protected LocationManager locationManager;

	private GoogleMap map;

	private String provider;

	/**
	 * initializes context and the google map object
	 * goes on to set up the location listener and move the camera to the user
	 * @param context
	 * @param map
	 */
	public LocationTracker(Context context, GoogleMap map) {
		this.myContext = context;
		this.map = map;
		this.initLocation();
		this.recenterCamera();
	}

	/**
	 * initializes the location listener based on criteria for best available provider
	 * mostly junk code, but we don't want to get rid of it
	 * @return
	 */
	public Location initLocation() {
		/*
		 * junk that is ultimately overwritten starts here
		 */
		try {
			locationManager = (LocationManager) myContext
					.getSystemService(LOCATION_SERVICE);
			
			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				// First get location from Network Provider

				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				} else if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		 * actually used code starts here
		 */
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(provider);
		locationManager.requestLocationUpdates(
				provider,
				MIN_TIME_BW_UPDATES,
				MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		return location;
	}

	public LatLng getLatLng() {
		return new LatLng((float) this.getLatitude(),
				(float) this.getLongitude());
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(LocationTracker.this);
		}
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}
		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}

		// return longitude
		return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	public LocationManager getLocationManager() {
		return locationManager;
	}

	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * launch Settings Options
	 * */
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(myContext);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog
				.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						myContext.startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	/**
	 * when android updates the location, this method is called to update the Location Listener
	 */
	@Override
	public void onLocationChanged(Location location) {
		this.location = location;
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude(); 
	}

	/**
	 * public method to clear the map
	 * purely for coding style since we wanted all map operations to be handled by this class 
	 */
	public void clearMap() {
		this.map.clear();
	}
	
	/**
	 * adds an entity to the map and returns the overlay object for future editing
	 * @param entity
	 * @return
	 */
	public GroundOverlay addEntity(Entity entity) {
		return this.map.addGroundOverlay(new GroundOverlayOptions()
				.image(entity.getPicture()).position(entity.getLatLng(), 5)
				.transparency(entity.getTransparency()));
	}

	/**
	 * centers the camera on the user at a zoom level of 20
	 */
	public void recenterCamera() {
		if (this.canGetLocation()) {
			this.map.animateCamera(
					CameraUpdateFactory.newLatLngZoom(this.getLatLng(), 20),
					1000, null);		
		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			this.showSettingsAlert();
		}
	}
	
	/**
	 * recenter the camera on a specific location
	 * @param latlng
	 */
	public void recenterCamera(LatLng latlng) {
		if (this.canGetLocation()) {
			this.map.animateCamera(
					CameraUpdateFactory.newLatLngZoom(latlng, 20),
					1000, null);		
		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			this.showSettingsAlert();
		}
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String provider) {
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String provider) {
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}


}