package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import modified_nitrogen1.ImmutableVertex;

public class WorkingVertexModel {
	WorkingVertexController workingVertexController;
	int x;
	int y;
	int z;
	ImmutableVertex pickedVertex = null;
	
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
	
	void setReference()
	{
		ref_x = x;
		ref_y = y;
		ref_z = z;
	}
}
