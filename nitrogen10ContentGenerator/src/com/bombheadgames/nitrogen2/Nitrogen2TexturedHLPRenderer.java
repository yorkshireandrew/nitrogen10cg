package com.bombheadgames.nitrogen2;

public class Nitrogen2TexturedHLPRenderer {

	
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
		System.out.println("Nitrogen2UntexturedRenderer.process called");
		context.polygonsRendered++;	
		Nitrogen2Vertex startN2V = produceN2Vs(context, start);
		System.out.println("produced N2Vs");
		System.out.println("topmost N2V is " + startN2V);
		
		Nitrogen2Vertex leftN2V = findLeftN2V(startN2V);
		System.out.println("found left N2V " + leftN2V.toString());
		Nitrogen2Vertex leftDestN2V = findLeftDestN2V(leftN2V);
		if(leftDestN2V == null)return;
		System.out.println("found left DestN2V " + leftDestN2V.toString());
		System.out.println("found left N2V " + leftN2V.intSX);
		
		Nitrogen2Vertex rightN2V = findRightN2V(startN2V);
		System.out.println("found right N2V " + rightN2V.toString());
		Nitrogen2Vertex rightDestN2V = findRightDestN2V(rightN2V);
		if(rightDestN2V == null)return;	
		System.out.println("found right DestN2V " + rightDestN2V.toString());
		System.out.println("found right N2V");
		
		renderer.renderHLP(
				context,
				leftN2V,leftDestN2V,
				rightN2V,rightDestN2V,
				polyData,
				textureMap,
				lightingValue
				);
	}
	
	/** generates Nitrogen2Vertexes from Vertex LLL returning one with lowest screen Y.
	 * calculating the Vertexes screen coordinates in the process */
	final static Nitrogen2Vertex produceN2Vs(NitrogenContext nc, Vertex start)
	{
		Nitrogen2Vertex retval;	
		int index = 0;
		int minSY;
		
		Nitrogen2Vertex[] workingN2VsL = workingN2Vs; // cache locally for speed

		// create N2V for passed in Vertex
		Nitrogen2Vertex startN2V = workingN2VsL[index++];
		start.calculateScreenSpaceCoordinate(nc);
		minSY = startN2V.initializeAllFromVertex(start);
		retval = startN2V;
		
		// step round anticlockwise creating N2Vs and clockwise references
		// also remember the N2V with lowest screen Y coordinate
		Vertex next = start.anticlockwise;
		Nitrogen2Vertex previousOne = startN2V;
		while(next != start)
		{
			Nitrogen2Vertex nextN2V = workingN2VsL[index++];
			next.calculateScreenSpaceCoordinate(nc);
			int nextSY = nextN2V.initializeAllFromVertex(next);
			if(nextSY < minSY)
			{
				retval = nextN2V;
				minSY = nextSY;
			}
			nextN2V.clockwise = previousOne;
			previousOne = nextN2V;
			next = next.anticlockwise;
		}
		startN2V.clockwise = previousOne; // complete the loop of clockwise references
	
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
	
	/** finds leftmost Nitrogen2Vertex with the same screen Y as parameter */
	final static Nitrogen2Vertex findLeftN2V(Nitrogen2Vertex in)
	{
		int inSY = in.intSY;
		Nitrogen2Vertex retval = in;
		Nitrogen2Vertex testN2V = in.anticlockwise;
		System.out.println("findLeftN2V testing " + testN2V);
		int debug = 0;
		while((testN2V.intSY == inSY)&&(testN2V != in)&&(debug < 10))
		{
			debug++;
			if(testN2V.intSX < retval.intSX)retval = testN2V;
			testN2V = in.anticlockwise;
			System.out.println("findLeftN2V testing " + testN2V);
		}
		return retval;	
	}
	
	/** finds rightmost Nitrogen2Vertex with the same screen Y as parameter */
	final static Nitrogen2Vertex findRightN2V(Nitrogen2Vertex in)
	{
		int inSY = in.intSY;
		Nitrogen2Vertex retval = in;
		Nitrogen2Vertex testN2V = in.clockwise;
		System.out.println("findRightN2V testing " + testN2V);
		int debug = 0;
		while((testN2V.intSY == inSY)&&(testN2V != in)&&(debug < 10))
		{
			debug++;
			if(testN2V.intSX < retval.intSX)retval = testN2V;
			testN2V = in.clockwise;
			System.out.println("findRightN2V testing " + testN2V);
		}
		return retval;	
	}
	
	/** find destination Nitrogen2Vertex moving anticlockwise. returns null if this is a lower SY than parameter */
	final static Nitrogen2Vertex findLeftDestN2V(Nitrogen2Vertex in)
	{
		Nitrogen2Vertex testN2V = in.anticlockwise;
		if(testN2V == null)System.out.println("findLeftDestN2V hit a null");
		if(testN2V.intSY < in.intSY)
		{
			System.out.println("findLeftDestN2V returning null");
			return null;
		}
		else
		{
			return testN2V;
		}
	}
	
	/** find destination Nitrogen2Vertex moving clockwise. returns null if this is a lower SY than parameter */
	final static Nitrogen2Vertex findRightDestN2V(Nitrogen2Vertex in)
	{
		Nitrogen2Vertex testN2V = in.clockwise;
		if(testN2V == null)System.out.println("findRightDestN2V hit a null");
		if(testN2V.intSY < in.intSY)
		{
			System.out.println("findRightDestN2V returning null");
			return null;
		}
		else
		{
			return testN2V;
		}
	}
}
