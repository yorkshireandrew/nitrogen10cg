package com.bombheadgames.nitrogen2;

public class Renderer_DirtyTexture implements Renderer{
	
	private static final int 	SHIFT = 20;
	private static final int 	ZSHIFT = 20;
	private static final int 	NUM = 1 << SHIFT;
	private static final int 	TEXTURE_SHIFT = 20; // align with Nitrogen2Vertex
	private static final int 	VIEWSPACE_SHIFT = 10;
	private static final float 	VIEWSPACE_NUM = (float)(1 << VIEWSPACE_SHIFT );
	private static final int 	VIEWSPACE = 3;
	
	Renderer_AffineTexture r = new Renderer_AffineTexture();
	
	@Override
	public void render(
			final NitrogenContext context,	
			
			Nitrogen2Vertex leftN2V,
			Nitrogen2Vertex leftDestN2V,
			
			Nitrogen2Vertex rightN2V,
			Nitrogen2Vertex rightDestN2V,			
			
			final int[] polyData,
			final TexMap textureMap,
			final float lightingValue	
			)
	{
		r.render
		(
				context,	
		
				leftN2V,
				leftDestN2V,
		
				rightN2V,
				rightDestN2V,			
		
				polyData,
				textureMap,
				lightingValue	
		);
	}
	
	public void renderHLP(
			final NitrogenContext context,	
			
			Nitrogen2Vertex leftN2V,
			Nitrogen2Vertex leftDestN2V,
			
			Nitrogen2Vertex rightN2V,
			Nitrogen2Vertex rightDestN2V,			
			
			final int[] polyData,
			final TexMap textureMap,
			final float lightingValue	
			)
	{
		// **************** initialise colour ********************************************
		System.out.println("dirty render called");
		
		// **************** initialise nitrogen context references ***********************
		final int[] contextPixels = context.pix;
		final int[] contextZBuffer = context.zbuff;
		final int contextWidth = context.w;
		final int[] tex;
		final int textureWidth;
		
		if(textureMap !=  null)
		{
			tex = textureMap.tex;
			textureWidth = textureMap.w;
		}
		else{return;}
		
		// **************** stuff for quick screen space calc ****************************
		final float contextMag = context.magnification;
		final float contextZK = context.zk;
		final int contextMidW = context.midw; 
		final int contextMidH = context.midh;
		final boolean contentGeneratorForcesNoPerspective = context.contentGeneratorForcesNoPerspective;
		
		// **************** Initialise Left start position and calculate delta ***********
		float		leftVSX			= leftN2V.vsX;
		float		leftVSY			= leftN2V.vsY;
		float		leftVSZ			= leftN2V.vsZ;
		
		int 		leftSX 			= leftN2V.intSX;
		int 		leftSY			= leftN2V.intSY;
		int 		leftSZ			= leftN2V.intSZ;
		
		long 		bigLeftSZ		= ((long)leftSZ) << ZSHIFT;
		
		int			leftTX			= leftN2V.intTX;
		int			leftTY			= leftN2V.intTY;

		// initialise left Dest		
		float		leftDestVSX			= leftDestN2V.vsX;
		float		leftDestVSY			= leftDestN2V.vsY;
		float		leftDestVSZ			= leftDestN2V.vsZ;
		
		int 	leftDestSX 		= leftDestN2V.intSX;
		int 	bigLeftSX 		= leftN2V.intSX << SHIFT;
		int 	leftDestSY 		= leftDestN2V.intSY;
		long 	bigLeftDestSZ	= ((long)leftDestN2V.intSZ) << ZSHIFT;

		int		leftDestTX		= leftDestN2V.intTX;
		int		leftDestTY		= leftDestN2V.intTY;
		
		float 	leftDeltaVSX;
		float 	leftDeltaVSY;
		float 	leftDeltaVSZ;
		
		int 	leftDeltaSX; 
		int 	leftDeltaSY;
		long 	leftDeltaSZ;
		
		int		leftDeltaTX;
		int 	leftDeltaTY;
		
		leftDeltaSY = leftDestSY - leftSY;
		
		if(leftDeltaSY > 0)
		{
				int rec 	= NUM / leftDeltaSY;
				leftDeltaSX = (leftDestSX - leftSX)	* rec;
			
				leftDeltaSY = (leftDestSY - leftSY);	// down counter
				leftDeltaSZ = (bigLeftDestSZ - bigLeftSZ)/leftDeltaSY;
				
				leftDeltaTX = (leftDestTX - leftTX) / leftDeltaSY;
				leftDeltaTY = (leftDestTY - leftTY) / leftDeltaSY;
				
				int leftViewspaceDeltaSY = leftDeltaSY * VIEWSPACE;
				
				leftDeltaVSX = (leftDestVSX - leftVSX) / leftViewspaceDeltaSY;
				leftDeltaVSY = (leftDestVSY - leftVSY) / leftViewspaceDeltaSY;
				leftDeltaVSZ = (leftDestVSZ - leftVSZ) / leftViewspaceDeltaSY;
							
		}
		else
		{
				leftDeltaSX = 0;
				leftDeltaSY = 0;
				leftDeltaSZ = 0;
				
				leftDeltaTX = 0;
				leftDeltaTY = 0;
				
				leftDeltaVSX = 0;
				leftDeltaVSY = 0;
				leftDeltaVSZ = 0;
		}
		
		// **************** Initialise Right start position and calculate delta ***********
		float		rightVSX		= rightN2V.vsX;
		float		rightVSY		= rightN2V.vsY;
		float		rightVSZ		= rightN2V.vsZ;
		
		int		rightSX			= rightN2V.intSX;
		int 	bigRightSX 		= rightN2V.intSX << SHIFT;
		int 	rightSY			= rightN2V.intSY;
		int 	rightSZ			= rightN2V.intSZ;
		long 	bigRightSZ		= ((long)rightSZ) << SHIFT;
		
		int		rightTX			= rightN2V.intTX;
		int		rightTY			= rightN2V.intTY;

		// initisalise right dest
		float		rightDestVSX		= rightDestN2V.vsX;
		float		rightDestVSY		= rightDestN2V.vsY;
		float		rightDestVSZ		= rightDestN2V.vsZ;
		
		int 	rightDestSX 	= rightDestN2V.intSX;
		int 	rightDestSY 	= rightDestN2V.intSY;
		long 	bigRightDestSZ	= ((long)rightDestN2V.intSZ) << ZSHIFT;

		int		rightDestTX			= rightDestN2V.intTX;
		int		rightDestTY			= rightDestN2V.intTY;
		
		float 	rightDeltaVSX; 
		float	rightDeltaVSY;
		float 	rightDeltaVSZ;
		
		int 	rightDeltaSX; 
		int 	rightDeltaSY;
		long 	rightDeltaSZ;
		
		int 	rightDeltaTX; 
		int 	rightDeltaTY;
		
		rightDeltaSY = rightDestSY - rightSY;
		
		if(rightDeltaSY > 0)
		{
				int rec 	= NUM / rightDeltaSY;
				rightDeltaSX = (rightDestSX - rightSX)	* rec;
				rightDeltaSY = (rightDestSY - rightSY);	// down counter
				rightDeltaSZ = (bigRightDestSZ - bigRightSZ) / rightDeltaSY;		

				rightDeltaTX = (rightDestTX - rightTX) / rightDeltaSY;
				rightDeltaTY = (rightDestTY - rightTY) / rightDeltaSY;
				
				int rightViewspaceDeltaSY = rightDeltaSY * VIEWSPACE;
				
				rightDeltaVSX = (rightDestVSX - rightVSX) / rightViewspaceDeltaSY;
				rightDeltaVSY = (rightDestVSY - rightVSY) / rightViewspaceDeltaSY;
				rightDeltaVSZ = (rightDestVSZ - rightVSZ) / rightViewspaceDeltaSY;

		}
		else
		{
				rightDeltaSX = 0;
				rightDeltaSY = 0;
				rightDeltaSZ = 0;
				
				rightDeltaTX = 0;
				rightDeltaTY = 0;
				
				rightDeltaVSX = 0;
				rightDeltaVSY = 0;
				rightDeltaVSZ = 0;
		}
		
		boolean trucking = true;
		
		int escape = 1000;
		while(trucking && (escape > 0))
		{
			escape--;
			// ************ Render a line *******
			
			renderLine(
					bigLeftSX,
					
					leftVSX,
					leftVSY,
					leftVSZ,
					bigLeftSZ, 
					
					bigRightSX,
					
					rightVSX,
					rightVSY,
					rightVSZ,
					bigRightSZ,
					
					leftSY * contextWidth,
					
					tex,
					textureWidth,
					
					leftTX,
					leftTY,
					
					rightTX,
					rightTY,
					
					contextPixels,
					contextZBuffer,
					contextWidth,

					
					contextMag,
					contextZK,
					contextMidW,

					contentGeneratorForcesNoPerspective
					);
			

			// *********** move left down one pixel ******
			int y2 = leftSY;
			
			while(y2 == leftSY)
			{
				leftVSX += leftDeltaVSX;
				leftVSY += leftDeltaVSY;
				leftVSZ += leftDeltaVSZ;
				leftTX += leftDeltaTX;
				leftTY += leftDeltaTY;
				
				y2 = 	calculateSY(
							leftVSY, 
							leftVSZ, 
							contextMag,
							contextMidH,
							contentGeneratorForcesNoPerspective
						);
				
			}
			leftSY = y2;
			
			// *********** move left by delta *******
			bigLeftSX += leftDeltaSX;
			bigLeftSZ += leftDeltaSZ;
			
			// ***********
			
			// *********** move right down one pixel ******
			int yy2 = rightSY;
			
			while(yy2 == rightSY)
			{
				rightVSX += rightDeltaVSX;
				rightVSY += rightDeltaVSY;
				rightVSZ += rightDeltaVSZ;
				rightTX += rightDeltaTX;
				rightTY += rightDeltaTY;
				
				yy2 = 	calculateSY(
							rightVSY, 
							rightVSZ, 
							contextMag,
							contextMidH,
							contentGeneratorForcesNoPerspective
						);
				
			}
			
			rightSY = yy2;
			
			// ************** increment by delta
					
			bigRightSX += rightDeltaSX;
			bigRightSZ += rightDeltaSZ;
			
			// *********** decrement steps to destination down counters ****
			leftDeltaSY--;
			rightDeltaSY--;
			
			// *********** handle if we reach left destination ******************
			if(leftDeltaSY <= 0)
			{
				System.out.println("handling leftDeltaSY <= 0");
				leftN2V 		= leftDestN2V;
				
				// update left to eliminate rounding errors
				leftVSX			= leftN2V.vsX;
				leftVSY			= leftN2V.vsY;
				leftVSZ			= leftN2V.vsZ;
				
				leftSX 			= leftN2V.intSX;
				leftSY			= leftN2V.intSY;
				leftSZ			= leftN2V.intSZ;
				
				bigLeftSZ		= ((long)leftSZ) << ZSHIFT;
				
				leftTX			= leftN2V.intTX;
				leftTY			= leftN2V.intTY;

						
				System.out.println("completed leftDestN2V is " + leftDestN2V);
				// find a new destination
				leftDestN2V = Nitrogen2UntexturedRenderer.findLeftDestN2V(leftDestN2V);
				
				if(leftDestN2V == null)
				{
					System.out.println("handling (leftDestN2V == null)");
					leftDeltaSX = 0;
					leftDeltaSY = 0;
					leftDeltaSZ = 0;
					
					leftDeltaTX = 0;
					leftDeltaTY = 0;				
					
					trucking = false;
				}
				else
				{
					System.out.println("new leftDest is" + leftDestN2V);
					leftDeltaSY = leftDestN2V.intSY - leftSY;
					System.out.println("new leftDeltaSY = " + leftDeltaSY );
					if(leftDeltaSY > 0)
					{
						int rec 	= NUM / leftDeltaSY;
						leftDeltaSX = (leftDestSX - leftSX)	* rec;
					
						leftDeltaSY = (leftDestSY - leftSY);	// down counter
						leftDeltaSZ = (bigLeftDestSZ - bigLeftSZ)/leftDeltaSY;
						
						leftDeltaTX = (leftDestTX - leftTX) / leftDeltaSY;
						leftDeltaTY = (leftDestTY - leftTY) / leftDeltaSY;
						
						int leftViewspaceDeltaSY = leftDeltaSY * VIEWSPACE;
						
						leftDeltaVSX = (leftDestVSX - leftVSX) / leftViewspaceDeltaSY;
						leftDeltaVSY = (leftDestVSY - leftVSY) / leftViewspaceDeltaSY;
						leftDeltaVSZ = (leftDestVSZ - leftVSZ) / leftViewspaceDeltaSY;
					}
					else
					{
							leftDeltaSX = 0;
							leftDeltaSY = 0;
							leftDeltaSZ = 0;
							
							leftDeltaTX = 0;
							leftDeltaTY = 0;
							
							trucking = false;
					}
				}		
			}
			
			// *********** handle if we reach right destination ******************
			if(rightDeltaSY <= 0)
			{
				System.out.println("handling rightDeltaSY <= 0");
				rightN2V 		= rightDestN2V;
				
				// now update right to eliminate rounding errors
				rightVSX		= rightN2V.vsX;
				rightVSY		= rightN2V.vsY;
				rightVSZ		= rightN2V.vsZ;
				
				rightSX			= rightN2V.intSX;
				bigRightSX 		= rightN2V.intSX << SHIFT;
				rightSY			= rightN2V.intSY;
				rightSZ			= rightN2V.intSZ;
				bigRightSZ		= ((long)rightSZ) << SHIFT;
				
				rightTX			= rightN2V.intTX;
				rightTY			= rightN2V.intTY;
				
				System.out.println("completed rightDestN2V is " + rightDestN2V);
				
				// find a new destination
				rightDestN2V = Nitrogen2UntexturedRenderer.findRightDestN2V(rightDestN2V);
				
				if(rightDestN2V == null)
				{
					System.out.println("handling (rightDestN2V == null)");
					rightDeltaSX = 0;
					rightDeltaSY = 0;
					rightDeltaSZ = 0;
					
					rightDeltaTX = 0;
					rightDeltaTY = 0;
					
					trucking = false;
				}
				else
				{
					System.out.println("new rightDest is" + rightDestN2V);
					rightDeltaSY = rightDestN2V.intSY - rightSY;
					System.out.println("new rightDeltaSY = " + rightDeltaSY );
					if(rightDeltaSY > 0)
					{
						int rec 	= NUM / rightDeltaSY;
						rightDeltaSX = (rightDestSX - rightSX)	* rec;
						rightDeltaSY = (rightDestSY - rightSY);	// down counter
						rightDeltaSZ = (bigRightDestSZ - bigRightSZ) / rightDeltaSY;		

						rightDeltaTX = (rightDestTX - rightTX) / rightDeltaSY;
						rightDeltaTY = (rightDestTY - rightTY) / rightDeltaSY;
						
						int rightViewspaceDeltaSY = rightDeltaSY * VIEWSPACE;
						
						rightDeltaVSX = (leftDestVSX - leftVSX) / rightViewspaceDeltaSY;
						rightDeltaVSY = (leftDestVSY - leftVSY) / rightViewspaceDeltaSY;
						rightDeltaVSZ = (leftDestVSZ - leftVSZ) / rightViewspaceDeltaSY;
					}
					else
					{
							rightDeltaSX = 0;
							rightDeltaSY = 0;
							rightDeltaSZ = 0;
							
							rightDeltaTX = 0;
							rightDeltaTY = 0;

							
							trucking = false;
					}
				}		
			}
		}//end of while loop
		
		// ************ Render final line *******
		System.out.println("render final line");
		renderLine(
				bigLeftSX,
				
				leftVSX,
				leftVSY,
				leftVSZ,
				bigLeftSZ, 
				
				bigRightSX,
				
				rightVSX,
				rightVSY,
				rightVSZ,
				bigRightSZ,
				
				leftSY * contextWidth,
				
				tex,
				textureWidth,
				
				leftTX,
				leftTY,
				
				rightTX,
				rightTY,
				
				contextPixels,
				contextZBuffer,
				contextWidth,

				
				contextMag,
				contextZK,
				contextMidW,

				contentGeneratorForcesNoPerspective
				);

	}
		
		
		
	//*****************************************************************************
	//*****************************************************************************
	// ****************************** Render Line *********************************
	//*****************************************************************************
	//*****************************************************************************

	private final void renderLine(
			final int 	bigLeftSX,
			
			final float leftVSX,
			final float leftVSY,
			final float leftVSZ,
			long 	bigLeftSZ, 
			
			final int 	bigRightSX,
			
			final float rightVSX,
			final float rightVSY,
			final float rightVSZ,
			final long 	bigRightSZ,
			
			final int indexOffset,
			
			final int[] tex,
			final int textureWidth,
			
			int leftTX,
			int leftTY,
			
			final int rightTX,
			final int rightTY,
			
			final int[] contextPixels,
			final int[] contextZBuffer,
			final int contextWidth,

			
			final float contextMag,
			final float contextZK,
			final int contextMidW,
			final boolean contentGeneratorForcesNoPerspective	
			)
	{
		
		int leftBigVSX = (int)(leftVSX * VIEWSPACE_NUM);
		int leftBigVSY = (int)(leftVSY * VIEWSPACE_NUM);
		int leftBigVSZ = (int)(leftVSZ * VIEWSPACE_NUM);
		
		int rightBigVSX = (int)(rightVSX * VIEWSPACE_NUM);
		int rightBigVSY = (int)(rightVSY * VIEWSPACE_NUM);
		int rightBigVSZ = (int)(rightVSZ * VIEWSPACE_NUM);
		
		int rightSX 	= bigRightSX >> SHIFT;
		int leftSX 		= bigLeftSX  >> SHIFT;
							
		int lineLength = rightSX - leftSX;
		if(lineLength == 0)return;
		
		long zDelta = (bigRightSZ - bigLeftSZ) / lineLength;
		
		
		int viewspaceLineLength = lineLength * VIEWSPACE;
		
		int leftBigVSXDelta = (rightBigVSX - leftBigVSX) / viewspaceLineLength;
		int leftBigVSYDelta = (rightBigVSY - leftBigVSY) / viewspaceLineLength;
		int leftBigVSZDelta = (rightBigVSZ - leftBigVSZ) / viewspaceLineLength;
		
		int leftTXDelta = (rightTX - leftTX)/ viewspaceLineLength;
		int leftTYDelta = (rightTY - leftTY)/ viewspaceLineLength;
		System.out.println("----------------------------------");
		System.out.println("lTX = " + (leftTX >> TEXTURE_SHIFT));
		System.out.println("lTY = " + (leftTY >> TEXTURE_SHIFT));
		System.out.println("rTX = " + (rightTX >> TEXTURE_SHIFT));
		System.out.println("rTY = " + (rightTY >> TEXTURE_SHIFT));
		System.out.println("----------------------------------");
		int x = calculateSX(
				leftBigVSX, 
				leftBigVSZ, 
				contextMag,
				contextMidW,
				contentGeneratorForcesNoPerspective		
				);
		
		while(x <= rightSX)
		{
			
			// ***************** RENDER PIXEL ****************
			int pixelZ = (int)(bigLeftSZ >> ZSHIFT);
			int index = indexOffset + x;
			
			if(pixelZ > contextZBuffer[index])
			{
				contextZBuffer[index] = pixelZ;
				System.out.println("TEX Y=" + (leftTY >> TEXTURE_SHIFT));
				System.out.println("TEX X=" + (leftTX >> TEXTURE_SHIFT));
				contextPixels[index] = tex[(leftTY >> TEXTURE_SHIFT) * textureWidth + (leftTX >> TEXTURE_SHIFT)];
			}
			
			// ***********************************************
			// *********** move right hopefully by one pixel
			int x2 = x;
			while(x2 == x)
			{
				leftBigVSX += leftBigVSXDelta;
				leftBigVSY += leftBigVSYDelta;
				leftBigVSZ += leftBigVSZDelta;
				
				leftTX += leftTXDelta;
				leftTY += leftTYDelta;
				
				
				x2 = calculateSX(
						leftBigVSX, 
						leftBigVSZ, 
						contextMag,
						contextMidW,
						contentGeneratorForcesNoPerspective		
				);
			}
			x = x2;
			//*************************************************
			bigLeftSZ += zDelta;	
		}
	}
		

	public boolean isTextured(){return true;}

	public boolean allowsHLP(){return true;}
	
	
	private int calculateSX(
			final int bigVSX, 
			final int bigVSZ, 
			float contextMag2,
			final int contextMidW,
			final boolean contentGeneratorForcesNoPerspective
			)
	{
		if(contentGeneratorForcesNoPerspective)
		{
    		return (contextMidW + (bigVSX >> VIEWSPACE_SHIFT));
		}
		return(contextMidW - (int)(contextMag2 * bigVSX/bigVSZ));
	}
	
	private int calculateSY(
			final float VSY, 
			final float VSZ, 
			float contextMag,
			final int contextMidH,
			final boolean contentGeneratorForcesNoPerspective
			)
	{
		if(contentGeneratorForcesNoPerspective)
		{
    		return (contextMidH - (int)VSY);
		}
		return(contextMidH - (int)(contextMag * VSY/VSZ));
	}
	

	/*
	private int getMeasure(Nitrogen2Vertex start)
	{
		int right = start.intSX;
		int left = start.intSX;
		int top = start.intSY;
		int bottom = start.intSY;
		
		Nitrogen2Vertex toTest = start.anticlockwise;
		while(toTest != start)
		{
			int intSX = toTest.intSX;
			int intSY = toTest.intSY;
			
			if(intSX > right)	right = intSX;
			if(intSX < left)	left  = intSX;
			if(intSY > bottom)	bottom = intSY;
			if(intSY < top)	top  = intSY;		
		}
		
		int dx = right - left;
		int dy = bottom - top;
		
		// return the approximate diagonal
		if(dx > dy)
		{
			return (dx + (dy >> 1));
		}
		else
		{
			return(dy + (dx >> 1));
		}
		
	}
	*/
	
}
