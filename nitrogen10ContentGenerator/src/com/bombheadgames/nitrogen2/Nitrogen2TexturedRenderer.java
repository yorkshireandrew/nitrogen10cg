package com.bombheadgames.nitrogen2;

/** Responsible for calculating perspective level, and rendering textures using affine texturing */
public class Nitrogen2TexturedRenderer {

	final static boolean isHLP(Vertex start, float qualityLevel)
	{
		float min = start.vs_z;
		float max = start.vs_z;	
		Vertex next = start.anticlockwise;	
		while(next != start)
		{
			float z = next.vs_z;
			if(z > max) max = z;
			if(z < min) min = z;
		}		
		float thresh = max * qualityLevel;
		return(min < thresh);
	}
	
	final static void process
	(
		final NitrogenContext context,		
		final Vertex start, 	
		final Renderer renderer,
		final int[] polyData,
		final TexMap textureMap,
		final float lightingValue
	)
	{
		// TO DO
	}

}
