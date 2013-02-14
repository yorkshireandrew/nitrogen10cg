package com.bombheadgames.nitrogen2;

/** responsible for clipping polygon using floating point math */
public class Nitrogen2PolygonClipper {
	
	final static int NUMBER_OF_WORKING_VERTEXES = 10;
	final static Vertex[] workingVertexes= new Vertex[NUMBER_OF_WORKING_VERTEXES];
	static int workingVertexIndex;

	
	final static void process(

			final NitrogenContext context,
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
			final boolean useHLP
			)
	{
		context.polygonsRendered++;
		workingVertexIndex = 0;
		int numberOfVertexes = vertexes.length;
		Vertex backlink = null;
		
		// create clockwise links
		for(int x = 0; x < numberOfVertexes; x++)
		{
			Vertex a = vertexes[x];
			a.clockwise = backlink;
			backlink = a;
		}
		
		// create anticlockwise links
		Vertex frontlink = null;
		for(int x = numberOfVertexes-1; x >= 0 ; x--)
		{
			Vertex b = vertexes[x];
			b.anticlockwise = frontlink;
			frontlink = b;
		}
		
		// complete the loop
		Vertex end 		= vertexes[numberOfVertexes-1];
		Vertex start	= vertexes[0];
		start.anticlockwise = end;
		end.clockwise = start;
		
		// clip the loop against various planes
		// start ends up being an on screen vertex
		if(touchedNear)		start = nearPlaneClip(start, -context.nearClip);		
		if(touchedRight)	start = rightPlaneClip(start, context.xClip);		
		if(touchedLeft)		start = leftPlaneClip(start, context.xClip);		
		if(touchedBottom)	start = bottomPlaneClip(start, context.yClip);		
		if(touchedTop)		start = topPlaneClip(start, context.yClip);		

		Nitrogen2PolygonRenderer.process
		(
			context,		
			start, 	
			renderer,
			polyData,
			textureMap,
			lightingValue,
			useHLP
		);
	}
	
	
	// *********************************************************
	// *********************************************************
	//                    NEAR PLANE CLIPPING
	// *********************************************************
	// *********************************************************
	
	final static Vertex nearPlaneClip(Vertex start, float nearClip)
	{
		Vertex a = findNearClippedVertex(start, start, nearClip);
		Vertex retval = start;
		while(a != null)
		{
			Vertex b,c,d,e;
			b = findAnticlockwiseMostNearClippedVertex(a,a,nearClip);
			if (b == null)return(null);
			c = findClockwiseMostNearClippedVertex(a,b,nearClip);
			if (c == null)return(null);
			d = calcNearPlaneIntersect(c.clockwise, c, nearClip);
			e = calcNearPlaneIntersect(b, b.anticlockwise, nearClip);
			
			//link c.clockwise - d - e - b.anticlockwise
			
			Vertex cClockwise = c.clockwise;
			Vertex bAnticlockwise = b.anticlockwise;
			
			cClockwise.anticlockwise = d;
			d.clockwise = cClockwise;
			
			d.anticlockwise = e;
			e.clockwise = d;
			
			e.anticlockwise = bAnticlockwise;
			bAnticlockwise = e;
			
			retval = d;	// ensure we return something in the onscreen loop
			a = findNearClippedVertex(bAnticlockwise, cClockwise, nearClip);	
		}
		return retval;
	}
	
	final static Vertex findNearClippedVertex(Vertex start, Vertex endPoint, float nearClip)
	{
		Vertex toTest = start;
		do{
			if(toTest.vs_z >= nearClip) return toTest;
			toTest = toTest.anticlockwise;
		}while(toTest != endPoint);	
		return start;
	}
	
	final static Vertex findAnticlockwiseMostNearClippedVertex(Vertex start, Vertex endPoint, float nearClip)
	{

		Vertex toTest = start.anticlockwise;
		Vertex anticlockwise = toTest.anticlockwise;
		do{
			if(anticlockwise.vs_z < nearClip) return toTest;
			toTest = anticlockwise;
			anticlockwise = toTest.anticlockwise;
		}while(toTest != endPoint);	
		return null;
	}
	
	final static Vertex findClockwiseMostNearClippedVertex(Vertex start, Vertex endPoint, float nearClip)
	{

		Vertex toTest = start.clockwise;
		Vertex clockwise = toTest.clockwise;
		do{
			if(clockwise.vs_z < nearClip) return toTest;
			toTest = clockwise;
			clockwise = toTest.anticlockwise;
		}while(toTest != endPoint);	
		return null;
	}
	
	final static Vertex calcNearPlaneIntersect(Vertex onscreen, Vertex offscreen, float nearClip)
	{	
		float n;
		float onscreenZ = onscreen.vs_z; 
		n = (nearClip - onscreenZ)/(onscreenZ - offscreen.vs_z);
		return(calcInbetweenVertex(onscreen,offscreen,n));	
	}
	
	
	// *********************************************************
	// *********************************************************
	//                    RIGHT PLANE CLIPPING
	// *********************************************************
	// *********************************************************
	
	final static Vertex rightPlaneClip(Vertex start, float xClip)
	{
		Vertex a = findRightClippedVertex(start, start, xClip);
		Vertex retval = start;
		while(a != null)
		{
			Vertex b,c,d,e;
			b = findAnticlockwiseMostRightClippedVertex(a,a,xClip);
			if (b == null)return(null);
			c = findClockwiseMostRightClippedVertex(a,b,xClip);
			if (c == null)return(null);
			d = calcRightPlaneIntersect(c.clockwise, c, xClip);
			e = calcRightPlaneIntersect(b, b.anticlockwise, xClip);
			
			//link c.clockwise - d - e - b.anticlockwise
			
			Vertex cClockwise = c.clockwise;
			Vertex bAnticlockwise = b.anticlockwise;
			
			cClockwise.anticlockwise = d;
			d.clockwise = cClockwise;
			
			d.anticlockwise = e;
			e.clockwise = d;
			
			e.anticlockwise = bAnticlockwise;
			bAnticlockwise = e;
			
			retval = d;	// ensure we return something in the onscreen loop
			a = findNearClippedVertex(bAnticlockwise, cClockwise, xClip);	
		}
		return retval;
	}
	
	final static Vertex findRightClippedVertex(Vertex start, Vertex endPoint, float xClip)
	{
		Vertex toTest = start;
		do{
			// test returning true if clipped
			if(toTest.vs_x >= (-toTest.vs_z * xClip)) return toTest;
			toTest = toTest.anticlockwise;
		}while(toTest != endPoint);	
		return start;
	}
	
	final static Vertex findAnticlockwiseMostRightClippedVertex(Vertex start, Vertex endPoint, float xClip)
	{

		Vertex toTest = start.anticlockwise;
		Vertex anticlockwise = toTest.anticlockwise;
		do{
			// test returning true if NOT clipped
			if(anticlockwise.vs_x < (-anticlockwise.vs_z * xClip)) return toTest;
			toTest = anticlockwise;
			anticlockwise = toTest.anticlockwise;
		}while(toTest != endPoint);	
		return null;
	}
	
	final static Vertex findClockwiseMostRightClippedVertex(Vertex start, Vertex endPoint, float xClip)
	{

		Vertex toTest = start.clockwise;
		Vertex clockwise = toTest.clockwise;
		do{
			// test returning true if NOT clipped
			if(clockwise.vs_x < (-clockwise.vs_z * xClip)) return toTest;
			toTest = clockwise;
			clockwise = toTest.anticlockwise;
		}while(toTest != endPoint);	
		return null;
	}
	
	final static Vertex calcRightPlaneIntersect(Vertex onscreen, Vertex offscreen, float xClip)
	{	
		float in_deapth = -onscreen.vs_z;
		float out_deapth = -offscreen.vs_z;
		float onscreenX = onscreen.vs_x;
		float n = (xClip * in_deapth - onscreenX) / ((offscreen.vs_x - onscreenX) - xClip * (out_deapth - in_deapth));	
		return(calcInbetweenVertex(onscreen,offscreen,n));	
	}
	
	
	// *********************************************************
	// *********************************************************
	//                    Left PLANE CLIPPING
	// *********************************************************
	// *********************************************************
	
	final static Vertex leftPlaneClip(Vertex start, float xClip)
	{
		Vertex a = findLeftClippedVertex(start, start, xClip);
		Vertex retval = start;
		while(a != null)
		{
			Vertex b,c,d,e;
			b = findAnticlockwiseMostLeftClippedVertex(a,a,xClip);
			if (b == null)return(null);
			c = findClockwiseMostLeftClippedVertex(a,b,xClip);
			if (c == null)return(null);
			d = calcLeftPlaneIntersect(c.clockwise, c, xClip);
			e = calcLeftPlaneIntersect(b, b.anticlockwise, xClip);
			
			//link c.clockwise - d - e - b.anticlockwise
			
			Vertex cClockwise = c.clockwise;
			Vertex bAnticlockwise = b.anticlockwise;
			
			cClockwise.anticlockwise = d;
			d.clockwise = cClockwise;
			
			d.anticlockwise = e;
			e.clockwise = d;
			
			e.anticlockwise = bAnticlockwise;
			bAnticlockwise = e;
			
			retval = d;	// ensure we return something in the onscreen loop
			a = findNearClippedVertex(bAnticlockwise, cClockwise, xClip);	
		}
		return retval;
	}
	
	final static Vertex findLeftClippedVertex(Vertex start, Vertex endPoint, float xClip)
	{
		Vertex toTest = start;
		do{
			// test returning true if clipped
			if(-toTest.vs_x >= (-toTest.vs_z * xClip)) return toTest;
			toTest = toTest.anticlockwise;
		}while(toTest != endPoint);	
		return start;
	}
	
	final static Vertex findAnticlockwiseMostLeftClippedVertex(Vertex start, Vertex endPoint, float xClip)
	{

		Vertex toTest = start.anticlockwise;
		Vertex anticlockwise = toTest.anticlockwise;
		do{
			// test returning true if NOT clipped
			if(-anticlockwise.vs_x < (-anticlockwise.vs_z * xClip)) return toTest;
			toTest = anticlockwise;
			anticlockwise = toTest.anticlockwise;
		}while(toTest != endPoint);	
		return null;
	}
	
	final static Vertex findClockwiseMostLeftClippedVertex(Vertex start, Vertex endPoint, float xClip)
	{

		Vertex toTest = start.clockwise;
		Vertex clockwise = toTest.clockwise;
		do{
			// test returning true if NOT clipped
			if(-clockwise.vs_x < (-clockwise.vs_z * xClip)) return toTest;
			toTest = clockwise;
			clockwise = toTest.anticlockwise;
		}while(toTest != endPoint);	
		return null;
	}
	
	final static Vertex calcLeftPlaneIntersect(Vertex onscreen, Vertex offscreen, float xClip)
	{	
		float in_deapth = -onscreen.vs_z;
		float out_deapth = -offscreen.vs_z;
		float onscreenX = onscreen.vs_x;
		// same as calcRightPlaneIntersect but x coordinates are inverted
		float n = (xClip * in_deapth + onscreenX) / ((onscreenX - offscreen.vs_x) - xClip * (out_deapth - in_deapth));	
		return(calcInbetweenVertex(onscreen,offscreen,n));	
	}	
	
	
	// *********************************************************
	// *********************************************************
	//                    BOTTOM PLANE CLIPPING
	// *********************************************************
	// *********************************************************
	
	final static Vertex bottomPlaneClip(Vertex start, float yClip)
	{
		Vertex a = findBottomClippedVertex(start, start, yClip);
		Vertex retval = start;
		while(a != null)
		{
			Vertex b,c,d,e;
			b = findAnticlockwiseMostBottomClippedVertex(a,a,yClip);
			if (b == null)return(null);
			c = findClockwiseMostBottomClippedVertex(a,b,yClip);
			if (c == null)return(null);
			d = calcBottomPlaneIntersect(c.clockwise, c, yClip);
			e = calcBottomPlaneIntersect(b, b.anticlockwise, yClip);
			
			//link c.clockwise - d - e - b.anticlockwise
			
			Vertex cClockwise = c.clockwise;
			Vertex bAnticlockwise = b.anticlockwise;
			
			cClockwise.anticlockwise = d;
			d.clockwise = cClockwise;
			
			d.anticlockwise = e;
			e.clockwise = d;
			
			e.anticlockwise = bAnticlockwise;
			bAnticlockwise = e;
			
			retval = d;	// ensure we return something in the onscreen loop
			a = findNearClippedVertex(bAnticlockwise, cClockwise, yClip);	
		}
		return retval;
	}
	
	final static Vertex findBottomClippedVertex(Vertex start, Vertex endPoint, float yClip)
	{
		Vertex toTest = start;
		do{
			// test returning true if clipped
			if(toTest.vs_y >= (-toTest.vs_z * yClip)) return toTest;
			toTest = toTest.anticlockwise;
		}while(toTest != endPoint);	
		return start;
	}
	
	final static Vertex findAnticlockwiseMostBottomClippedVertex(Vertex start, Vertex endPoint, float yClip)
	{

		Vertex toTest = start.anticlockwise;
		Vertex anticlockwise = toTest.anticlockwise;
		do{
			// test returning true if NOT clipped
			if(anticlockwise.vs_y >= (-anticlockwise.vs_z * yClip)) return toTest;
			toTest = anticlockwise;
			anticlockwise = toTest.anticlockwise;
		}while(toTest != endPoint);	
		return null;
	}
	
	final static Vertex findClockwiseMostBottomClippedVertex(Vertex start, Vertex endPoint, float yClip)
	{

		Vertex toTest = start.clockwise;
		Vertex clockwise = toTest.clockwise;
		do{
			// test returning true if NOT clipped
			if (clockwise.vs_y >= (-clockwise.vs_z * yClip)) return toTest;
			toTest = clockwise;
			clockwise = toTest.anticlockwise;
		}while(toTest != endPoint);	
		return null;
	}
	
	final static Vertex calcBottomPlaneIntersect(Vertex onscreen, Vertex offscreen, float yClip)
	{	
		float in_deapth = -onscreen.vs_z;
		float out_deapth = -offscreen.vs_z;
		float onscreenY = onscreen.vs_y;
		float n = (yClip * in_deapth - onscreenY) / ((offscreen.vs_y - onscreenY) - yClip * (out_deapth - in_deapth));
		return(calcInbetweenVertex(onscreen,offscreen,n));	
	}
	
	// *********************************************************
	// *********************************************************
	//                    TOP PLANE CLIPPING
	// *********************************************************
	// *********************************************************
	
	final static Vertex topPlaneClip(Vertex start, float yClip)
	{
		Vertex a = findTopClippedVertex(start, start, yClip);
		Vertex retval = start;
		while(a != null)
		{
			Vertex b,c,d,e;
			b = findAnticlockwiseMostTopClippedVertex(a,a,yClip);
			if (b == null)return(null);
			c = findClockwiseMostTopClippedVertex(a,b,yClip);
			if (c == null)return(null);
			d = calcTopPlaneIntersect(c.clockwise, c, yClip);
			e = calcTopPlaneIntersect(b, b.anticlockwise, yClip);
			
			//link c.clockwise - d - e - b.anticlockwise
			
			Vertex cClockwise = c.clockwise;
			Vertex bAnticlockwise = b.anticlockwise;
			
			cClockwise.anticlockwise = d;
			d.clockwise = cClockwise;
			
			d.anticlockwise = e;
			e.clockwise = d;
			
			e.anticlockwise = bAnticlockwise;
			bAnticlockwise = e;
			
			retval = d;	// ensure we return something in the onscreen loop
			a = findNearClippedVertex(bAnticlockwise, cClockwise, yClip);	
		}
		return retval;
	}
	
	final static Vertex findTopClippedVertex(Vertex start, Vertex endPoint, float yClip)
	{
		Vertex toTest = start;
		do{
			// test returning true if clipped
			if(-toTest.vs_y >= (-toTest.vs_z * yClip)) return toTest;
			toTest = toTest.anticlockwise;
		}while(toTest != endPoint);	
		return start;
	}
	
	final static Vertex findAnticlockwiseMostTopClippedVertex(Vertex start, Vertex endPoint, float yClip)
	{

		Vertex toTest = start.anticlockwise;
		Vertex anticlockwise = toTest.anticlockwise;
		do{
			// test returning true if NOT clipped
			if(-toTest.vs_y < (-toTest.vs_z * yClip)) return toTest;
			toTest = anticlockwise;
			anticlockwise = toTest.anticlockwise;
		}while(toTest != endPoint);	
		return null;
	}
	
	final static Vertex findClockwiseMostTopClippedVertex(Vertex start, Vertex endPoint, float yClip)
	{

		Vertex toTest = start.clockwise;
		Vertex clockwise = toTest.clockwise;
		do{
			// test returning true if NOT clipped
			if (-toTest.vs_y < (-toTest.vs_z * yClip)) return toTest;
			toTest = clockwise;
			clockwise = toTest.anticlockwise;
		}while(toTest != endPoint);	
		return null;
	}
	
	final static Vertex calcTopPlaneIntersect(Vertex onscreen, Vertex offscreen, float yClip)
	{	
		float in_deapth = -onscreen.vs_z;
		float out_deapth = -offscreen.vs_z;
		float onscreenY = onscreen.vs_y;
		float n = (yClip * in_deapth + onscreenY) / ((onscreenY - offscreen.vs_y) - yClip * (out_deapth - in_deapth));
		return(calcInbetweenVertex(onscreen,offscreen,n));	
	}
	
	// *********************************************************
	// *********************************************************
	//                    CALC INBETWEEN VERTEX
	// *********************************************************
	// *********************************************************

	final static Vertex calcInbetweenVertex(Vertex onscreen, Vertex offscreen, float ratio)
	{
		Vertex retval = workingVertexes[workingVertexIndex++];
		
		/** The generated vertex view-space coordinates */
		float vvsx, vvsy,vvsz;
		/** The generated vertex aux values */
		float va1, va2, va3;
		
		vvsx = (offscreen.vs_x - onscreen.vs_x) * ratio + onscreen.vs_x;
		vvsy = (offscreen.vs_y - onscreen.vs_y) * ratio + onscreen.vs_y;
		vvsz = (offscreen.vs_z - onscreen.vs_z) * ratio + onscreen.vs_z;
		va1  = (offscreen.aux1 - onscreen.aux1) * ratio + onscreen.aux1;
		va2  = (offscreen.aux2 - onscreen.aux2) * ratio + onscreen.aux2;
		va3  = (offscreen.aux3 - onscreen.aux3) * ratio + onscreen.aux3;
		retval.setViewSpaceAndAux(vvsx, vvsy, vvsz, va1, va2, va3);
		return retval;	
	}
	
	
	
	
}