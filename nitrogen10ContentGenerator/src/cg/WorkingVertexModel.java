package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class WorkingVertexModel {
	WorkingVertexController workingVertexController;
	int x;
	int y;
	int z;
	int index;
	boolean picked = false;
	
	int ref_x;
	int ref_y;
	int ref_z;
	
	// distances
	int dx;
	int dy;
	int dz;	
	int dist;

	void computeDistances()
	{
		dx = x - ref_x;
		dy = y - ref_y;
		dz = z - ref_z;
		
		double d = Math.sqrt(dx*dx+dy*dy+dz*dz);
		dist = (int)d;	
	}
}
