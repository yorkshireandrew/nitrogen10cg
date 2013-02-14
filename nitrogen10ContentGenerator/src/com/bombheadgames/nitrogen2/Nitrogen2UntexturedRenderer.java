package com.bombheadgames.nitrogen2;

public class Nitrogen2UntexturedRenderer {
	
	static final Nitrogen2Vertex[] workingN2Vs;
	static final int WORKING_N2V_SIZE = 20;
	static int workingN2Vindex;
	
	static{
		// instantiate working vertexes
		workingN2Vs = new Nitrogen2Vertex[WORKING_N2V_SIZE];
		for(int x = 0; x < WORKING_N2V_SIZE; x++)
		{
			workingN2Vs[x] = new Nitrogen2Vertex();
		}
	}
	
	final static void process
	(
		final NitrogenContext context,		
		final Vertex start, 	
		final Renderer renderer,
		final int[] polyData,
		final TexMap textureMap,
		final float lightingValue
	){	
		context.polygonsRendered++;	
		Nitrogen2Vertex startN2V = produceN2Vs(context, start);
		
		

	}
	
	final static Nitrogen2Vertex produceN2Vs(NitrogenContext nc, Vertex start)
	{
		Nitrogen2Vertex retval;	
		int index = 0;
		int minSY;
		
		Nitrogen2Vertex[] workingN2VsL = workingN2Vs; // cache locally for speed

		// create N2V for passed in Vertex
		Nitrogen2Vertex startN2V = workingN2VsL[index++];
		start.calculateScreenSpaceCoordinate(nc);
		minSY = startN2V.initializeScreenSpaceFromVertex(start);
		retval = startN2V;
		
		// step round anticlockwise creating N2Vs and clockwise references
		// also remember the N2V with lowest screen Y coordinate
		Vertex next = start.anticlockwise;
		Vertex previousOne = start;
		while(next != start)
		{
			Nitrogen2Vertex nextN2V = workingN2VsL[index++];
			next.calculateScreenSpaceCoordinate(nc);
			int nextSY = nextN2V.initializeScreenSpaceFromVertex(next);
			if(nextSY < minSY)
			{
				retval = startN2V;
				minSY = nextSY;
			}
			next.clockwise = previousOne;
			previousOne = next;
			next = next.anticlockwise;
		}
		start.clockwise = previousOne; // complete the loop of clockwise references
	
		// step round clockwise creating the anticlockwise references
		Nitrogen2Vertex previousN2V = startN2V;
		Nitrogen2Vertex nextN2V = startN2V.clockwise;
		while(nextN2V != startN2V)
		{
			nextN2V.anticlockwise = previousN2V;
			previousN2V = nextN2V;
			nextN2V = nextN2V.clockwise;	
		}
		
		startN2V.anticlockwise = previousN2V;
		
		return retval;	
	}
	
	
}
