package com.bombheadgames.nitrogen2;

final public class Nitrogen2Vertex {
	static final float VIEWSPACE_XY_MULTIPLIER = 1000;
	static final float VIEWSPACE_Z_MULTIPLIER = 200;
	static final float TEXTURE_MULTIPLIER = 1 << 20;
	
	/** use floating point for clipping */
	float vsX;
	float vsY;
	float vsZ;
	  
	/** screen x coordinate */
    transient int intSX;
    /** screen y coordinate */
    transient int intSY;
    /** screen z coordinate (which may differ from the vertexes view-space z due to perspective projection)*/
    transient int intSZ;
    
    /** texture coordinates */
    transient int intTX;
    transient int intTY;
       
    /** next Nitrogen2PolygonRenderer in LLL moving clockwise */
    Nitrogen2Vertex clockwise;
    /** next Nitrogen2PolygonRenderer in LLL moving anticlockwise */
    Nitrogen2Vertex anticlockwise;
	
	final int initializeAllFromVertex(final Vertex vertex)
	{
		vsX = vertex.vs_x;
		vsY = vertex.vs_y;
		vsZ = vertex.vs_z;		
		
		// used to find topmost N2V and by findXXXXDestN2V methods
		intSX 		= vertex.sx;
		int intSYL 	= vertex.sy;
		intSY 		= intSYL;
		intSZ 		= vertex.sz;
		
		intTX = (int)(vertex.aux1 * TEXTURE_MULTIPLIER);
		intTY = (int)(vertex.aux2 * TEXTURE_MULTIPLIER);
		return intSYL;
	}
	
	/** initializes screen space coordinates, returning sy */
	final int initializeScreenSpaceFromVertex(final Vertex vertex)
	{
		intSX 		= vertex.sx;
		int intSYL 	= vertex.sy;
		intSY 		= intSYL;
		intSZ 		= vertex.sz;
		return intSYL;
	}
	
	public String toString()
	{
		String s = super.toString();
		s = s + "[" + intSX + "," + intSY +"]";
		return s;
	}
	
	

}
