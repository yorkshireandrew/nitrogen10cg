package cg;

import java.io.Serializable;
import com.bombheadgames.nitrogen2.ImmutableVertex;

public class WorkingVertexModel implements Serializable{
	private static final long serialVersionUID = 3688807584246986775L;

	transient WorkingVertexController workingVertexController;
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
