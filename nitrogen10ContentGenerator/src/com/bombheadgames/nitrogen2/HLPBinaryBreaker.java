package com.bombheadgames.nitrogen2;

public class HLPBinaryBreaker {

	/**
	 * This method is the entry point into HLPBreaker. It marks if the polygon has a high perspective for PolygonRenderer 
	 * @param context Nitrogen context to render into
	 * @param fustrumTouchCount Total number of view-fustrum planes the Item supplying the polygon may have touched.
	 * @param touchedNear Item supplying the polygon may have touched near view-fustrum plane.
	 * @param touchedRight Item supplying the polygon may have touched right view-fustrum plane.
	 * @param touchedLeft Item supplying the polygon may have touched left view-fustrum plane.
	 * @param touchedTop Item supplying the polygon may have touched top view-fustrum plane.
	 * @param touchedBottom Item supplying the polygon may have touched bottom view-fustrum plane.
	 * <br />
	 * @param vertex1 First  Vertex of the polygon. Vertexes parameters must be given in clockwise order.
	 * @param vertex2 Second Vertex of the polygon.
	 * @param vertex3 Third  Vertex of the polygon.
	 * @param vertex4 Fourth Vertex of the polygon.
	 * <br />
	 * @param renderer The renderer to use to render the polygon into the supplied context.
	 * @param polyData Polygon data to pass on to the renderer, such as its colour.
	 * @param textureMap TextureMap to pass on to the renderer
	 * @param lightingValue lighting value that may have been computed by the polygons backside to pass to the Renderer
	 */
	
	static void process(	
			NitrogenContext context,
			int fustrumTouchCount, 
			boolean touchedNear,
			boolean touchedRight,
			boolean touchedLeft,
			boolean touchedTop,
			boolean touchedBottom,
			
			Vertex vertex1, Vertex vertex2, Vertex vertex3, Vertex vertex4,
			
			Renderer renderer,
			int[] polyData,
			TexMap textureMap,
			float lightingValue,
			boolean useHLPBreak
			)
	{
		context.clippedPolygonsRendered++;
		
		// skip HLP rendering if Item being rendered
		// or the polygons renderer says we can
		if(!useHLPBreak)
		{
			PolygonRenderer.process(							
					context,
					vertex1, vertex2, vertex3, vertex4,					
					renderer,
					polyData,
					textureMap, lightingValue, false 
					);
					return;
		}
		
		if(needsHLPBreak(context, vertex1, vertex2, vertex3, vertex4))
		{
			PolygonRenderer.process(							
					context,
					vertex1, vertex2, vertex3, vertex4,					
					renderer,
					polyData,
					textureMap, lightingValue, true 
					);
					return;			
		}
		else
		{
			PolygonRenderer.process(							
					context,
					vertex1, vertex2, vertex3, vertex4,					
					renderer,
					polyData,
					textureMap, lightingValue, false
					);
					return;					
		}
		
	}
	
	/** Given four vertexes and a Nitrogen context containing a valid qualityofHLP determines if HLP breaking is required */ 
	static boolean needsHLPBreak(NitrogenContext context, Vertex vertex1, Vertex vertex2, Vertex vertex3, Vertex vertex4)
	{
		float maxaz, maxbz, minaz, minbz, finalminz;
		float finalmaxz; // needs initialisation to compile
		float localThresholdDist; // needs initialisation to compile
		
		float z1 = vertex1.vs_z;
		float z2 = vertex2.vs_z;
		float z3 = vertex3.vs_z;
		float z4 = vertex4.vs_z;
				
		// sort first two vertexes (max = farthest from viewpoint)
		if(z1 < z2)
		{
			maxaz = z1; minaz = z2;
		}
		else
		{
			maxaz = z2; minaz = z1;
		}
				
		// sort last two vertexes
		if(z3 < z4)
		{
			maxbz = z3; minbz = z4;
		}
		else
		{
			maxbz = z4; minbz = z3;
		}
		// sort the sorted values to find farthest distance
		if(maxaz < maxbz)
		{
			finalmaxz = maxaz;
		}
		else
		{
			finalmaxz = maxbz;
		}

		// sort the sorted values to find nearest distance
		if(minaz < minbz)
		{
			finalminz = minbz;
		}
		else
		{
			finalminz = minaz;
		}
		localThresholdDist = finalminz * context.qualityOfHLP;
		
		// return result
		if(localThresholdDist < finalmaxz)
		{
			System.out.println("minz=" + finalminz + "minzt="+ localThresholdDist + "maxz=" + finalmaxz);
			System.out.println("HLPBinaryBreaker returns FALSE");
			return(false);
		}
		else
		{
			System.out.println("minz=" + finalminz + "minzt="+ localThresholdDist + "maxz=" + finalmaxz);
			System.out.println("HLPBinaryBreaker returns TRUE");
			return(true);
		}
	}
}