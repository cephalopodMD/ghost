package edu.virginia.cs2110;

import java.util.Random;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * Ghost entity
 * the ghost has a randomized location generator that is used to initialize the location 
 * a ghost has a set of bones that are initialized using the same randomized location generator
 * if the player picks up the bones, the ghost is set to "scared" meaning that the bones are not displayed and the ghost starts running away
 * 
 * any code not sourced is derived from the android api guide or skills learned in class
 * @author Augustus Logsdon(acl3qb) and Charlotte Blais(ccb7wb) and Piotr Gregrowski(pzg5sj) and Carina Cai(yc5bd)
 *
 */
public class Ghost extends Entity {

	Bones bones;
	boolean scared;
	static float speed = .5f;

	/**
	 * sets overlay icon and initializes bones/state of fear
	 */
	public Ghost() {
		this.setPicture(BitmapDescriptorFactory
				.fromResource(R.drawable.ghostpic));
		this.setTransparency(0.5f);
		this.bones = new Bones();
		this.setScared(false);
	}

	/**
	 * generates a random location around a set of coordinates
	 * used for bones and the bone map as well
	 * 
	 * @param latLng
	 * @return randomized LatLng
	 */
	public LatLng randomLatLng(LatLng latLng) {
		Random r = new Random();
		int radius = r.nextInt(15) + 30;
		int angle = r.nextInt(360);
		double yDiff = radius * Math.cos(angle) / 110996;
		double xDiff = radius * Math.sin(angle) / 110996;
		return (new LatLng(xDiff + latLng.latitude, yDiff + latLng.longitude));
	}

	/**
	 * unused abstract method
	 */
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	/**
	 * has the ghost either chase the target at Ghost.speed or run away at the same speed
	 *  
	 * @param distance distance calculated from ghost to target
	 * @param target position to chase after
	 */
	public void chase(float distance, LatLng target) {
		if (this.isScared()) {
			this.setLatLng(new LatLng(
					this.getLatLng().latitude
					- Ghost.speed * (target.latitude - this.getLatLng().latitude) / distance,
					this.getLatLng().longitude
					- Ghost.speed * (target.longitude - this.getLatLng().longitude) / distance));			
		} else {
			this.setLatLng(new LatLng(
					this.getLatLng().latitude
					+ Ghost.speed * (target.latitude - this.getLatLng().latitude) / distance,
					this.getLatLng().longitude
					+ Ghost.speed * (target.longitude - this.getLatLng().longitude) / distance));
		}
	}

	/**
	 * @return
	 */
	public boolean isScared() {
		return scared;
	}

	/**
	 * @param scared
	 */
	public void setScared(boolean scared) {
		this.scared = scared;
		if (scared) {
			this.getGroundOverlay().setImage(BitmapDescriptorFactory
					.fromResource(R.drawable.scaredghostpic));
		}
	}
}
