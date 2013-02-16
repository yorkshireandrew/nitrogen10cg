package com.bombheadgames.nitrogen2;

/** responsible for clipping polygon using floating point math */
public class Nitrogen2PolygonClipper {
	
	final static int NUMBER_OF_WORKING_VERTEXES = 64;
	final static Vertex[] workingVertexes;
	static int workingVertexIndex;
	
	static{
		workingVertexes = new Vertex[NUMBER_OF_WORKING_VERTEXES];
		for(int x = 0; x < NUMBER_OF_WORKING_VERTEXES; x++)
		{
			workingVertexes[x] = new Vertex();
		}
	}

	/** clip polygon using floating point math, creating a circular LLL of Vertexes */
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
	//	System.out.println("Nitrogen2PolygonClipper.process called");
		context.polygonsRendered++;
		workingVertexIndex = 0;
		int numberOfVertexes = vertexes.length;
		Vertex backlink = null;
		
		// create clockwise links
		for(int x = 0; x < numberOfVertexes; x++)
		{
			Vertex a = vertexes[x];
		//	System.out.println("" + x + ":" + a.toString());
			a.clockwise = backlink;
			backlink = a;
		}
	//	System.out.println("created clockwise links");
		
		// create anticlockwise links
		Vertex frontlink = null;
		for(int x = numberOfVertexes-1; x >= 0 ; x--)
		{
			Vertex b = vertexes[x];
			b.anticlockwise = frontlink;
			frontlink = b;
		}
	//	System.out.println("created anticlockwise links");
		
		// complete the loop
		Vertex end 		= vertexes[numberOfVertexes-1];
		Vertex start	= vertexes[0];
		start.clockwise = end;
		end.anticlockwise = start;
	//	System.out.println("completed loop");
		
		check(start);
		
		// clip the loop against various planes
		// start ends up being an on screen vertex
		if(touchedNear)		
		{
			start = nearPlaneClip(start, -context.nearClip);
			if(start == null)return;
//			System.out.println("--- Post nearPlane ---");
			check(start);
		}
		
		if(touchedRight)
		{
			start = rightPlaneClip(start, context.xClip);
			if(start == null)return;
	//		System.out.println("--- Post rightPlane ---");
			check(start);
		}	
		
		if(touchedLeft)
		{
			start = leftPlaneClip(start, context.xClip);
			if(start == null)return;
	//		System.out.println("--- Post leftPlane ---");
			check(start);
		}	
		
		if(touchedTop)
		{
			start = topPlaneClip(start, context.yClip);
			if(start == null)return;
	//		System.out.println("--- Post topPlane ---");
			check(start);
		}
		
		if(touchedBottom)
		{
			start = bottomPlaneClip(start, context.yClip);
			if(start == null)return;
//			System.out.println("--- Post bottomPlane ---");
			check(start);
		}		
//		System.out.println("completed clipping");
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
			System.out.println("near clipping");
			System.out.println(a);
			System.out.println("-------------");
			check(a);
			Vertex b,c,d,e;
			b = findAnticlockwiseMostNearClippedVertex(a,a,nearClip);
			System.out.println("most anticlockwise nearclipped vertex");
			System.out.println(b);
			if (b == null)return(null);
			Vertex bAnticlockwise = b.anticlockwise;
			c = findClockwiseMostNearClippedVertex(a,a,nearClip);
			System.out.println("most clockwise nearclipped vertex");
			System.out.println(c);
			if (c == null)return(null);
			Vertex cClockwise = c.clockwise;
			d = calcNearPlaneIntersect(cClockwise, c, nearClip);
			System.out.println("c.clockwise - c intersect");
			System.out.println(d);
			e = calcNearPlaneIntersect(bAnticlockwise, b, nearClip);
			System.out.println("b - b.anticlockwise intersect");
			System.out.println(e);			
			//link c.clockwise - d - e - b.anticlockwise
			
			
			cClockwise.anticlockwise = d;
			d.clockwise = cClockwise;
			
			d.anticlockwise = e;
			e.clockwise = d;
			
			e.anticlockwise = bAnticlockwise;
			bAnticlockwise.clockwise = e;
			
			retval = d;	// ensure we return something in the onscreen loop
			a = findNearClippedVertex(bAnticlockwise, cClockwise, nearClip);	
			
			System.out.println("completed near clipping");
			check(d);
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
		return null;
	}
	
	final static Vertex findAnticlockwiseMostNearClippedVertex(Vertex start, Vertex endPoint, float nearClip)
	{

		Vertex toTest = start;
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

		Vertex toTest = start;
		Vertex clockwise = toTest.clockwise;
		do{
			if(clockwise.vs_z < nearClip) return toTest;
			toTest = clockwise;
			clockwise = toTest.clockwise;
		}while(toTest != endPoint);	
		return null;
	}
	
	final static Vertex calcNearPlaneIntersect(Vertex onscreen, Vertex offscreen, float nearClip)
	{	
		float n;
		float onscreenZ = onscreen.vs_z; 
		n = (onscreenZ - nearClip)/(onscreenZ - offscreen.vs_z);
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
			System.out.println("right clipping");
			System.out.println(a);
			System.out.println("-------------");
			check(a);
			
			Vertex b,c,d,e;
			b = findAnticlockwiseMostRightClippedVertex(a,a,xClip);
			if (b == null)return(null);
			Vertex bAnticlockwise = b.anticlockwise;
			c = findClockwiseMostRightClippedVertex(a,a,xClip);
			if (c == null)return(null);
			Vertex cClockwise = c.clockwise;
			d = calcRightPlaneIntersect(cClockwise, c, xClip);
			System.out.println("c.clockwise - c intersect");
			System.out.println(d);
			e = calcRightPlaneIntersect(bAnticlockwise, b, xClip);
			System.out.println("b - b.anticlockwise intersect");
			System.out.println(e);	
			
			//link c.clockwise - d - e - b.anticlockwise
			
			cClockwise.anticlockwise = d;
			d.clockwise = cClockwise;
			
			d.anticlockwise = e;
			e.clockwise = d;
			
			e.anticlockwise = bAnticlockwise;
			bAnticlockwise.clockwise = e;
			
			retval = d;	// ensure we return something in the onscreen loop
			a = findRightClippedVertex(bAnticlockwise, cClockwise, xClip);	
		
			System.out.println("completed right clipping");
			check(d);
		}
		
		System.out.println("Right Plane Clip returns=" + retval);
		return retval;
	}
	
	final static Vertex findRightClippedVertex(Vertex start, Vertex endPoint, float xClip)
	{
		System.out.println("findRightClippedVertex start= " + start);
		Vertex toTest = start;
		do{
			// test returning true if clipped
			if(toTest.vs_x >= (-toTest.vs_z * xClip)) 
				{
				System.out.println("returned = " + toTest);
				return toTest;
				}
			toTest = toTest.anticlockwise;
		}while(toTest != endPoint);	
		System.out.println("didn't find one so returned null");
		return null;
	}
	
	final static Vertex findAnticlockwiseMostRightClippedVertex(Vertex start, Vertex endPoint, float xClip)
	{

		System.out.println("findAnticlockwiseMostRightClippedVertex start= " + start);
		Vertex toTest = start;
		Vertex anticlockwise = toTest.anticlockwise;
		do{
			// test returning true if NOT clipped
			if(anticlockwise.vs_x < (-anticlockwise.vs_z * xClip)) 
				{
				System.out.println("returned " + toTest);	
				return toTest;
				}
			toTest = anticlockwise;
			anticlockwise = toTest.anticlockwise;
		}while(toTest != endPoint);
		System.out.println("returned null");
		return null;
	}
	
	final static Vertex findClockwiseMostRightClippedVertex(Vertex start, Vertex endPoint, float xClip)
	{
		System.out.println("findClockwiseMostRightClippedVertex start= " + start);

		Vertex toTest = start;
		Vertex clockwise = toTest.clockwise;
		do{
			// test returning true if NOT clipped
			if(clockwise.vs_x < (-clockwise.vs_z * xClip)) 
				{
				System.out.println("returned " + toTest);	
				return toTest;
				}
			toTest = clockwise;
			clockwise = toTest.clockwise;
		}while(toTest != endPoint);	
		System.out.println("returned null");
		return null;
	}
	
	final static Vertex calcRightPlaneIntersect(Vertex onscreen, Vertex offscreen, float xClip)
	{	
		float in_deapth = -onscreen.vs_z;
		float out_deapth = -offscreen.vs_z;
		float onscreenX = onscreen.vs_x;
		System.out.println("calcRightPlaneIntersect Called");	
		System.out.println("onscreen  =" + onscreen);		
		System.out.println("offscreen =" + offscreen);
		System.out.println("offscreen vs_x " + offscreen.vs_x + ", onscreen vs_x=" + onscreenX);
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
			System.out.println("left clipping");
			Vertex b,c,d,e;
			b = findAnticlockwiseMostLeftClippedVertex(a,a,xClip);
			if (b == null)return(null);
			Vertex bAnticlockwise = b.anticlockwise;
			c = findClockwiseMostLeftClippedVertex(a,a,xClip);
			if (c == null)return(null);
			Vertex cClockwise = c.clockwise;
			d = calcLeftPlaneIntersect(cClockwise, c, xClip);
			e = calcLeftPlaneIntersect(bAnticlockwise, b, xClip);
			
			//link c.clockwise - d - e - b.anticlockwise
			
			cClockwise.anticlockwise = d;
			d.clockwise = cClockwise;
			
			d.anticlockwise = e;
			e.clockwise = d;
			
			e.anticlockwise = bAnticlockwise;
			bAnticlockwise.clockwise = e;
			
			retval = d;	// ensure we return something in the onscreen loop
			a = findLeftClippedVertex(bAnticlockwise, cClockwise, xClip);	
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
		return null;
	}
	
	final static Vertex findAnticlockwiseMostLeftClippedVertex(Vertex start, Vertex endPoint, float xClip)
	{

		Vertex toTest = start;
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

		Vertex toTest = start;
		Vertex clockwise = toTest.clockwise;
		do{
			// test returning true if NOT clipped
			if(-clockwise.vs_x < (-clockwise.vs_z * xClip)) return toTest;
			toTest = clockwise;
			clockwise = toTest.clockwise;
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
	//                    TOP PLANE CLIPPING
	// *********************************************************
	// *********************************************************
	
	final static Vertex topPlaneClip(Vertex start, float yClip)
	{
		Vertex a = findTopClippedVertex(start, start, yClip);
		Vertex retval = start;
		while(a != null)
		{
			System.out.println("top clipping");
			System.out.println(a);
			System.out.println("-------------");
			check(a);
			
			Vertex b,c,d,e;
			b = findAnticlockwiseMostTopClippedVertex(a,a,yClip);
			if (b == null)return(null);
			Vertex bAnticlockwise = b.anticlockwise;
			c = findClockwiseMostTopClippedVertex(a,a,yClip);
			if (c == null)return(null);
			Vertex cClockwise = c.clockwise;
			d = calcTopPlaneIntersect(cClockwise, c, yClip);
			System.out.println("c.clockwise - c intersect");
			System.out.println(d);
			e = calcTopPlaneIntersect(bAnticlockwise, b, yClip);
			System.out.println("b - b.anticlockwise intersect");
			System.out.println(e);	
			
			//link c.clockwise - d - e - b.anticlockwise
			
			cClockwise.anticlockwise = d;
			d.clockwise = cClockwise;
			
			d.anticlockwise = e;
			e.clockwise = d;
			
			e.anticlockwise = bAnticlockwise;
			bAnticlockwise.clockwise = e;
			
			retval = d;	// ensure we return something in the onscreen loop
			a = findTopClippedVertex(bAnticlockwise, cClockwise, yClip);	
		
			System.out.println("completed top clipping");
			check(d);
		}
		
		System.out.println("top Plane Clip returns=" + retval);
		return retval;
	}
	
	final static Vertex findTopClippedVertex(Vertex start, Vertex endPoint, float yClip)
	{
		System.out.println("findTopClippedVertex start= " + start);
		Vertex toTest = start;
		do{
			// test returning true if clipped
			if(toTest.vs_y >= (-toTest.vs_z * yClip)) 
				{
				System.out.println("returned = " + toTest);
				return toTest;
				}
			toTest = toTest.anticlockwise;
		}while(toTest != endPoint);	
		System.out.println("didn't find one so returned null");
		return null;
	}
	
	final static Vertex findAnticlockwiseMostTopClippedVertex(Vertex start, Vertex endPoint, float yClip)
	{

		System.out.println("findAnticlockwiseMostTopClippedVertex start= " + start);
		Vertex toTest = start;
		Vertex anticlockwise = toTest.anticlockwise;
		do{
			// test returning true if NOT clipped
			System.out.println("testing " + anticlockwise);
			if(anticlockwise.vs_y < (-anticlockwise.vs_z * yClip)) 
				{
				System.out.println("returned " + toTest);	
				return toTest;
				}
			toTest = anticlockwise;
			anticlockwise = toTest.anticlockwise;
		}while(toTest != endPoint);
		System.out.println("returned null");
		return null;
	}
	
	final static Vertex findClockwiseMostTopClippedVertex(Vertex start, Vertex endPoint, float yClip)
	{
		System.out.println("findClockwiseMostTopClippedVertex start= " + start);

		Vertex toTest = start;
		Vertex clockwise = toTest.clockwise;
		do{
			// test returning true if NOT clipped
			System.out.println("testing " + clockwise);
			if(clockwise.vs_y < (-clockwise.vs_z * yClip)) 
				{
				System.out.println("returned " + toTest);	
				return toTest;
				}
			toTest = clockwise;
			clockwise = toTest.clockwise;
		}while(toTest != endPoint);	
		System.out.println("returned null");
		return null;
	}
	
	final static Vertex calcTopPlaneIntersect(Vertex onscreen, Vertex offscreen, float yClip)
	{	
		float in_deapth = -onscreen.vs_z;
		float out_deapth = -offscreen.vs_z;
		float onscreenY = onscreen.vs_y;
		System.out.println("calcRightPlaneIntersect Called");	
		System.out.println("onscreen  =" + onscreen);		
		System.out.println("offscreen =" + offscreen);
		System.out.println("offscreen vs_y " + offscreen.vs_y + ", onscreen vs_x=" + onscreenY);
		float n = (yClip * in_deapth - onscreenY) / ((offscreen.vs_y - onscreenY) - yClip * (out_deapth - in_deapth));	
		return(calcInbetweenVertex(onscreen,offscreen,n));	
	}
	
	
	// *********************************************************
	// *********************************************************
	//                    Bottom PLANE CLIPPING
	// *********************************************************
	// *********************************************************
	
	final static Vertex bottomPlaneClip(Vertex start, float yClip)
	{
		Vertex a = findBottomClippedVertex(start, start, yClip);
		Vertex retval = start;
		while(a != null)
		{
			System.out.println("bottom clipping");
			Vertex b,c,d,e;
			b = findAnticlockwiseMostBottomClippedVertex(a,a,yClip);
			if (b == null)return(null);
			Vertex bAnticlockwise = b.anticlockwise;
			c = findClockwiseMostBottomClippedVertex(a,a,yClip);
			if (c == null)return(null);
			Vertex cClockwise = c.clockwise;
			d = calcBottomPlaneIntersect(cClockwise, c, yClip);
			e = calcBottomPlaneIntersect(bAnticlockwise, b, yClip);
			
			//link c.clockwise - d - e - b.anticlockwise
			
			cClockwise.anticlockwise = d;
			d.clockwise = cClockwise;
			
			d.anticlockwise = e;
			e.clockwise = d;
			
			e.anticlockwise = bAnticlockwise;
			bAnticlockwise.clockwise = e;
			
			retval = d;	// ensure we return something in the onscreen loop
			a = findBottomClippedVertex(bAnticlockwise, cClockwise, yClip);	
		}
		return retval;
	}
	
	final static Vertex findBottomClippedVertex(Vertex start, Vertex endPoint, float yClip)
	{
		Vertex toTest = start;
		do{
			// test returning true if clipped
			if(-toTest.vs_y >= (-toTest.vs_z * yClip)) return toTest;
			toTest = toTest.anticlockwise;
		}while(toTest != endPoint);	
		return null;
	}
	
	final static Vertex findAnticlockwiseMostBottomClippedVertex(Vertex start, Vertex endPoint, float yClip)
	{

		Vertex toTest = start;
		Vertex anticlockwise = toTest.anticlockwise;
		do{
			// test returning true if NOT clipped
			if(-anticlockwise.vs_y < (-anticlockwise.vs_z * yClip)) return toTest;
			toTest = anticlockwise;
			anticlockwise = toTest.anticlockwise;
		}while(toTest != endPoint);	
		return null;
	}
	
	final static Vertex findClockwiseMostBottomClippedVertex(Vertex start, Vertex endPoint, float yClip)
	{

		Vertex toTest = start;
		Vertex clockwise = toTest.clockwise;
		do{
			// test returning true if NOT clipped
			if(-clockwise.vs_y < (-clockwise.vs_z * yClip)) return toTest;
			toTest = clockwise;
			clockwise = toTest.clockwise;
		}while(toTest != endPoint);	
		return null;
	}
	
	final static Vertex calcBottomPlaneIntersect(Vertex onscreen, Vertex offscreen, float yClip)
	{	
		float in_deapth = -onscreen.vs_z;
		float out_deapth = -offscreen.vs_z;
		float onscreenY = onscreen.vs_y;
		// same as calcRightPlaneIntersect but x coordinates are inverted
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
		System.out.println("calcInbetweenVertex called workingVertexIndex=" + workingVertexIndex);
		System.out.println("ratio =" + ratio);
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
	
	final static void check(Vertex start)
	{
		/*
		System.out.println("********** CHECK *************");
//		System.out.println("Start clockwise =" + start.clockwise);
		System.out.println("Start           =" + start);
//		System.out.println("Start anticlock =" + start.anticlockwise);
		
		Vertex test = start.anticlockwise;	
		while(test != start)
		{
			System.out.println("------------------------------------");
//			System.out.println("clockwise  =" + test.clockwise);
			System.out.println("current    =" + test);
//			System.out.println("anticlock  =" + test.anticlockwise);
			System.out.println("------------------------------------");
			test = test.anticlockwise;
		}
		*/
	}
	
	
	
}
