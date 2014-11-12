package edu.virginia.cs2110;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * a player entity
 * has health, a set number of lives, a set number of ghost kills, and a set number of ghosts within proximity
 * all of these fields are modified in the game loop
 * 
 * any code not sourced is derived from the android api guide or skills learned in class
 * @author Augustus Logsdon(acl3qb) and Charlotte Blais(ccb7wb) and Piotr Gregrowski(pzg5sj) and Carina Cai(yc5bd)
 *
 */
public class Player extends Entity {

	private int health;
	private int lives;
	private int kills;
	private int ghostsNearby;
	private boolean hasMap;
	private boolean holdingMap;
	
	/**
	 * initialize player stats and set overlay icon
	 */
	public Player() {
		this.setHealth(100);
		this.setLives(3);
		this.setPicture(BitmapDescriptorFactory.fromResource(R.drawable.player));
	}

	/**
	 * unused abstract method
	 */
	@Override
	public void update() {
		// updatePosition();
		// updateHealth();
	}
	
	/**
	 * a mutator for player position
	 * @param latlng
	 */
	public void updatePosition(LatLng latlng) {
		this.setLatLng(latlng);
	}
	
	/**
	 * @return
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * @param health
	 */
	public void setHealth(int health) {
		this.health = health;
	}
	

	/**
	 * @return the lives
	 */
	/**
	 * @return
	 */
	public int getLives() {
		return lives;
	}
	

	/**
	 * @param lives the lives to set
	 */
	/**
	 * @param lives
	 */
	public void setLives(int lives) {
		this.lives = lives;
	}

	/**
	 * @return the kills
	 */
	/**
	 * @return
	 */
	public int getKills() {
		return kills;
	}

	/**
	 * @param kills the kills to set
	 */
	/**
	 * @param kills
	 */
	public void setKills(int kills) {
		this.kills = kills;
	}

	/**
	 * @return the ghostsNearby
	 */
	/**
	 * @return
	 */
	public int getGhostsNearby() {
		return ghostsNearby;
	}

	/**
	 * @param ghostsNearby the ghostsNearby to set
	 */
	/**
	 * @param ghostsNearby
	 */
	public void setGhostsNearby(int ghostsNearby) {
		this.ghostsNearby = ghostsNearby;
	}

	/**
	 * @return
	 */
	public boolean isHasMap() {
		return hasMap;
	}

	/**
	 * @param hasMap
	 */
	public void setHasMap(boolean hasMap) {
		this.hasMap = hasMap;
	}

	/**
	 * @return
	 */
	public boolean isHoldingMap() {
		return holdingMap;
	}

	/**
	 * @param holdingMap
	 */
	public void setHoldingMap(boolean holdingMap) {
		this.holdingMap = holdingMap;
	}

}
