package edu.virginia.cs2110;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * a bones entity
 * each ghost has some bones
 * the overlay is removed when the player picks them up
 * 
 * any code not sourced is derived from the android api guide or skills learned in class
 * @author Augustus Logsdon(acl3qb) and Charlotte Blais(ccb7wb) and Piotr Gregrowski(pzg5sj) and Carina Cai(yc5bd)
 *
 */
public class Bones extends Entity {

	/**
	 * constructor sets the icon
	 */
	public Bones() {
		this.setPicture(BitmapDescriptorFactory.fromResource(R.drawable.bones));
	}

	/**
	 * unused entity method
	 */
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}
}
