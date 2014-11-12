package edu.virginia.cs2110;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;

/**
 * Abstract entity class
 * and entity has a position and a GroundOverlay for the map that can be changed over time once it is added
 * 
 * any code not sourced is derived from the android api guide or skills learned in class
 * @author Augustus Logsdon(acl3qb) and Charlotte Blais(ccb7wb) and Piotr Gregrowski(pzg5sj) and Carina Cai(yc5bd)
 *
 */
/**
 * @author gus
 *
 */
public abstract class Entity {

	/**
	 * global position of entity
	 */
	private LatLng position;
	
	/**
	 * ground overlay of the entity
	 */
	private GroundOverlay groundOverlay;
	
	/**
	 * variables used to change the ground overlay
	 */
	private int size;
	private BitmapDescriptor picture;
	private float transparency;

	/**
	 * constructor sets default overlay size and transparency
	 */
	public Entity() {
		setSize(1);
		setTransparency(0);
	}

	/**
	 * 
	 * @return global position
	 */
	public LatLng getLatLng() {
		// TODO Auto-generated method stub
		return this.position;
	}

	/**
	 * set global position
	 * @param position LatLng type coordinates of entity
	 */
	public void setLatLng(LatLng position) {
		this.position = position;
		if (groundOverlay != null) {
			groundOverlay.setPosition(this.getLatLng());
		}
	}

	/**
	 * @return the groundOverlay
	 */
	public GroundOverlay getGroundOverlay() {
		return groundOverlay;
	}

	/**
	 * @param groundOverlay the groundOverlay to set
	 */
	public void setGroundOverlay(GroundOverlay groundOverlay) {
		this.groundOverlay = groundOverlay;
	}

	/**
	 * overlay size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * set overlay size (unused)
	 * @param size overlay size
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	/**
	 * 
	 * @return overlay transparency
	 */
	public float getTransparency() {
		return transparency;
	}

	/**
	 * set overlay transparency
	 * @param transparency
	 */
	public void setTransparency(float transparency) {
		this.transparency = transparency;
	}

	/**
	 * unused abstract method
	 */
	public abstract void update();

	/**
	 * @return the picture
	 */
	public BitmapDescriptor getPicture() {
		return picture;
	}

	/**
	 * @param picture
	 *            the picture to set
	 */
	public void setPicture(BitmapDescriptor picture) {
		this.picture = picture;
	}
}
