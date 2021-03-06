/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bombheadgames.nitrogen2;

import java.io.Serializable;

/**
 *
 * @author andrew
 */
public final class ImmutableCollisionVertex implements Serializable{
	private static final long serialVersionUID = 6545349284668634779L;
	
	// Item-space coordinates
	/** Item space x coordinate. The containing Items orientation transform gets applied to the (usually fixed) Item space coordinates of the vertex in order to generate the vertex's view-space coordinates. */
	public float is_x;
	/** Item space y coordinate. */
	public float is_y;
	/** Item space z coordinate. */
	public float is_z;
	/** Radius of collision */
	public float radius;
    
    public ImmutableCollisionVertex(
    		float is_x,
    		float is_y,
    		float is_z,
    		float radius
    		)
    {
    	this.is_x = is_x;
    	this.is_y = is_y;
    	this.is_z = is_z;
    	this.radius = radius;
    }
    
    public ImmutableCollisionVertex(ImmutableCollisionVertex source)
    {
    	this.is_x = source.is_x;
    	this.is_y = source.is_y;
    	this.is_z = source.is_z;
    	this.radius = source.radius;   	
    }


}
