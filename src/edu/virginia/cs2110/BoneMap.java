package edu.virginia.cs2110;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * a map that allows the user to see bones
 * 
 * any code not sourced is derived from the android api guide or skills learned in class
 * @author Augustus Logsdon(acl3qb) and Charlotte Blais(ccb7wb) and Piotr Gregrowski(pzg5sj) and Carina Cai(yc5bd)
 *
 */
public class BoneMap extends Entity {

	/**
	 * constructor sets the map icon
	 */
	public BoneMap () {
		this.setPicture(BitmapDescriptorFactory
			.fromResource(R.drawable.map));
	}
	
	/**
	 * unused entity method
	 */
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
