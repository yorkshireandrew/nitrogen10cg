package com.bombheadgames.nitrogen2;

public class Nitrogen2PolygonRenderer {
	
	final static int NUMBER_OF_WORKING_N2VS = 10;
	final static Nitrogen2Vertex[] workingN2Vs= new Nitrogen2Vertex[NUMBER_OF_WORKING_N2VS];
	static int workingN2VIndex = 0;
	static{
		for(int x = 0; x < NUMBER_OF_WORKING_N2VS; x++)
		{
			workingN2Vs[x] = new Nitrogen2Vertex();
		}
	}
	
	
	/** Responsible for rendering the polygon
	 * 
	 * @param context 	The NitrogenContext the polygons will finally be rendered into
	 * <br/><br/>
	 * @param fustrumTouchCount	Count of the number of clip planes the polygon touches
	 * @param touchedNear The polygon may touch the near plane
	 * @param touchedRight The polygon may touch the right plane
	 * @param touchedLeft The polygon may touch the left plane
	 * @param touchedTop The polygon may touch the top plane
	 * @param touchedBottom The polygon may touch the bottom plane
	 * <br/><br/>
	 * @param vertex1 1st polygon vertex. The four Vertexes must be in clockwise order
	 * @param vertex2 2nd polygon vertex.
	 * @param vertex3 3rd polygon vertex.
	 * @param vertex4 4th polygon vertex.
	 * <br/><br/>
	 * @param renderer Renderer to use to render the polygon.
	 * @param polyData Polygon data to pass to the Renderer, such as its colour.
	 * @param texMap TextureMap top pass to the Renderer.
	 * @param lightingValue lighting value that may have been computed by the polygons backside to pass to the Renderer
	 * <br/><br/>
	 * @param v11-v34 The orientation matrix computed by the scene graph (12 floating point values)
	 */	
	final static void process(

			final NitrogenContext context,
			final int fustrumTouchCount, 
			final boolean touchedNear,
			final boolean touchedRight,
			final boolean touchedLeft,
			final boolean touchedTop,
			final boolean touchedBottom,
			
			final Vertex[] vertexes, 
		
			final Renderer renderer,
			final int[] polyData,
			final TexMap textureMap,
			final float lightingValue,
			final boolean useHLPBreak
			)
	{
		context.polygonsRendered++;
		
		Nitrogen2Vertex[] workingN2VsL = workingN2Vs; // cache locally for speed
		
		boolean isTextured = renderer.isTextured();
		
		int numberOfVertexes = vertexes.length;
		
		// create circular LLL
		int workingN2VIndexL = 0;
		Nitrogen2Vertex backlink = null;
		for(int x = 0; x < numberOfVertexes; x++)
		{
			if(isTextured)
			{
				Nitrogen2Vertex a = workingN2VsL[workingN2VIndexL++];
				a.initializeAllFromVertex(vertexes[x]);
				a.clockwise = backlink;
				backlink = a;
				
			}
			else
			{
				Nitrogen2Vertex b = workingN2VsL[workingN2VIndexL++];
				b.initializeScreenSpaceFromVertex(vertexes[x]);
				b.clockwise = backlink;
				backlink = b;
			}
		}
		
		// create anticlockwise links
		Nitrogen2Vertex frontlink = null;
		for(int x = numberOfVertexes-1; x >= 0 ; x--)
		{
			Nitrogen2Vertex c = workingN2VsL[x];
			c.anticlockwise = frontlink;
			frontlink = c;
		}
		
		// create circle LLL
		Nitrogen2Vertex end 	= workingN2VsL[numberOfVertexes-1];
		Nitrogen2Vertex start 	= workingN2VsL[0];
		start.anticlockwise = end;
		end.clockwise = start;
	}
}
