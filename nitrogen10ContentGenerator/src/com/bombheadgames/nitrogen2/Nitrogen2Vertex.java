package com.bombheadgames.nitrogen2;

final public class Nitrogen2Vertex {
	static final float VIEWSPACE_XY_MULTIPLIER = 1000;
	static final float VIEWSPACE_Z_MULTIPLIER = 200;
	
    /** view-space x coordinate. Positive is right-ward */
    transient int intVSX;
    /** views-pace y coordinate. Positive is up-ward. */
    transient int intVSY;
    /** view-space z coordinate. Increasing negativity is moving away from the view-point. */
    transient int intVSZ;
    
	/** screen x coordinate */
    transient int intSX;
    /** screen y coordinate */
    transient int intSY;
    /** screen z coordinate (which may differ from the vertexes view-space z due to perspective projection)*/
    transient int intSZ;
    
    /** next Nitrogen2PolygonRenderer in LLL moving clockwise */
    Nitrogen2Vertex clockwise;
    /** next Nitrogen2PolygonRenderer in LLL moving anticlockwise */
    Nitrogen2Vertex anticlockwise;
	
	final void initializeAllFromVertex(final Vertex vertex)
	{
		intVSX = (int)(vertex.vs_x * VIEWSPACE_XY_MULTIPLIER);
		intVSY = (int)(vertex.vs_y * VIEWSPACE_XY_MULTIPLIER);
		intVSZ = (int)(vertex.vs_z * VIEWSPACE_Z_MULTIPLIER);
		
		intSX = vertex.sx;
		intSX = vertex.sy;
		intSX = vertex.sz;
	}
	
	final void initializeScreenSpaceFromVertex(final Vertex vertex)
	{
		intSX = vertex.sx;
		intSX = vertex.sy;
		intSX = vertex.sz;
	}
	
	

}
