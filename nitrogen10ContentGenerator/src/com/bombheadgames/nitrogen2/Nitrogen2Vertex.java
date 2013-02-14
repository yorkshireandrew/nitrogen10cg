package com.bombheadgames.nitrogen2;

final public class Nitrogen2Vertex {
	static final float VIEWSPACE_XY_MULTIPLIER = 1000;
	static final float VIEWSPACE_Z_MULTIPLIER = 200;
	static final float TEXTURE_MULTIPLIER = 1 << 20;
	
	/** use floating point for clipping */
	float vsX;
	float vsY;
	float vsZ;
	
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
    
    /** texture coordinates */
    transient int intTX;
    transient int intTY;
       
    /** next Nitrogen2PolygonRenderer in LLL moving clockwise */
    Nitrogen2Vertex clockwise;
    /** next Nitrogen2PolygonRenderer in LLL moving anticlockwise */
    Nitrogen2Vertex anticlockwise;
	
	final void initializeAllFromVertex(final Vertex vertex)
	{
		vsX = vertex.vs_x;
		vsY = vertex.vs_y;
		vsZ = vertex.vs_z;		
		
		intVSX = (int)(vertex.vs_x * VIEWSPACE_XY_MULTIPLIER);
		intVSY = (int)(vertex.vs_y * VIEWSPACE_XY_MULTIPLIER);
		intVSZ = (int)(vertex.vs_z * VIEWSPACE_Z_MULTIPLIER);
		
		intSX = vertex.sx;
		intSY = vertex.sy;
		intSZ = vertex.sz;
		
		intTX = (int)(vertex.aux1 * TEXTURE_MULTIPLIER);
		intTY = (int)(vertex.aux2 * TEXTURE_MULTIPLIER);
	}
	
	final void initializeScreenSpaceFromVertex(final Vertex vertex)
	{
		intSX = vertex.sx;
		intSY = vertex.sy;
		intSZ = vertex.sz;
	}
	
	

}
