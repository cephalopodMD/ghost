package edu.virginia.cs2110;

import android.app.Activity;

/**
 * Simple game save object
 * stores how many ghosts there are, their speed, and if bone maps are used
 * note: does not actually save between distinct runs 
 * 
 * any code not sourced is derived from the android api guide or skills learned in class
 * @author Augustus Logsdon(acl3qb) and Charlotte Blais(ccb7wb) and Piotr Gregrowski(pzg5sj) and Carina Cai(yc5bd)
 *
 */
public class GameSave extends Activity {

	private int ghosts;
	private boolean thereAreMaps;
	private Player player;
	private float ghostSpeed;

	/**
	 * initialize save data
	 * @param ghosts
	 * @param thereAreMaps
	 * @param player
	 * @param ghostSpeed
	 */
	public GameSave(int ghosts, boolean thereAreMaps, Player player, float ghostSpeed) {
		this.ghosts = ghosts;
		this.setThereAreMaps(thereAreMaps);
		this.setPlayer(player);
		this.ghostSpeed = ghostSpeed;
	}

	/**
	 * @return the thereAreMaps
	 */
	public boolean getThereAreMaps() {
		return thereAreMaps;
	}

	/**
	 * @param thereAreMaps
	 *            the thereAreMaps to set
	 */
	public void setThereAreMaps(boolean thereAreMaps) {
		this.thereAreMaps = thereAreMaps;
	}

	/**
	 * @return the ghosts
	 */
	public int getGhosts() {
		return ghosts;
	}

	/**
	 * @param ghosts
	 *            the ghosts to set
	 */
	public void setGhosts(int ghosts) {
		this.ghosts = ghosts;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}


	/**
	 * @return
	 */
	public float getGhostSpeed() {
		return ghostSpeed;
	}

	/**
	 * @param ghostSpeed
	 */
	public void setGhostSpeed(int ghostSpeed) {
		this.ghostSpeed = ghostSpeed;
	}
	
	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

}
