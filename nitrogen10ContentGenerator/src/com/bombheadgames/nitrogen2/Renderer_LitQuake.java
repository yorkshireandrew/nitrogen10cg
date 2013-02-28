package com.bombheadgames.nitrogen2;

public class Renderer_LitQuake implements Renderer{

		private static Renderer_LitAffineTexture r = new Renderer_LitAffineTexture();
		private static final int SHIFT = 20;
		private static final int ZSHIFT = 20;
		private static final int NUM = 1 << SHIFT;
		private static final int TEXTURE_SHIFT = 20; // align with Nitrogen2Vertex

		private static final int QUAKE_STEP = 16;
		
		private static final int LIGHT_SHIFT = 10;
		private static final int LIGHT_NUM = 1 << LIGHT_SHIFT;
		private static final int ALPHA = 0xFF000000;
		
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
			final int lightVal = (int)(lightingValue * LIGHT_NUM);
			
			// **************** stuff for texture coordinate calc ****************************
			final float contextMag = context.magnification;
			final float invContextMag = 1f / contextMag;
			final int contextMidW = context.midw; 
			final int contextMidH = context.midh;
			final boolean contentGeneratorForcesNoPerspective = context.contentGeneratorForcesNoPerspective;

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
			
			
			// **************** Initialise Left start position and calculate delta ***********
			int		leftSX			= leftN2V.intSX;
			
			float	leftVSX			= leftN2V.vsX;
			float	leftVSY			= leftN2V.vsY;
			float	leftVSZ			= leftN2V.vsZ;
			
			int 	bigLeftSX 		= leftN2V.intSX << SHIFT;
			int 	leftSY			= leftN2V.intSY;
			int 	leftSZ			= leftN2V.intSZ;
			long 	bigLeftSZ		= ((long)leftSZ) << ZSHIFT;
			
			int		leftStartTX			= leftN2V.intTX;
			int		leftStartTY			= leftN2V.intTY;

			int 	leftDestSX 		= leftDestN2V.intSX;
			int 	leftDestSY 		= leftDestN2V.intSY;
			long 	bigLeftDestSZ	= ((long)leftDestN2V.intSZ) << ZSHIFT;
			
			
			float	leftDestVSX			= leftDestN2V.vsX;
			float	leftDestVSY			= leftDestN2V.vsY;
			float	leftDestVSZ			= leftDestN2V.vsZ;
			
			int		leftDestTX		= leftDestN2V.intTX;
			int		leftDestTY		= leftDestN2V.intTY;
			
			float	leftDelta_TX	= (float)(leftDestTX - leftStartTX);
			float	leftDelta_TY	= (float)(leftDestTY - leftStartTY);
			
			float 	leftDeltaVSX = leftDestVSX - leftVSX;
			float 	leftDeltaVSY = leftDestVSY - leftVSY;
			float 	leftDeltaVSZ = leftDestVSZ - leftVSZ;
			
			int 	leftDeltaSX; 
			int 	leftDeltaSY;
			long 	leftDeltaSZ;
			
			leftDeltaSY = leftDestSY - leftSY;
			
			if(leftDeltaSY > 0)
			{
					int rec 	= NUM / leftDeltaSY;
					leftDeltaSX = (leftDestSX - leftSX)	* rec;
					leftDeltaSY = (leftDestSY - leftSY);	// down counter
					leftDeltaSZ = (bigLeftDestSZ - bigLeftSZ)/leftDeltaSY;
					
			}
			else
			{
					leftDeltaSX = 0;
					leftDeltaSY = 0;
					leftDeltaSZ = 0;						
			}
			
			// **************** Initialise Right start position and calculate delta ***********
			int		rightSX			= rightN2V.intSX;
			int 	bigRightSX 		= rightN2V.intSX << SHIFT;
			int 	rightSY			= rightN2V.intSY;
			int 	rightSZ			= rightN2V.intSZ;
			long 	bigRightSZ		= ((long)rightSZ) << SHIFT;
			
			float	rightVSX			= rightN2V.vsX;
			float	rightVSY			= rightN2V.vsY;
			float	rightVSZ			= rightN2V.vsZ;
			
			int		rightStartTX			= rightN2V.intTX;
			int		rightStartTY			= rightN2V.intTY;

			int 	rightDestSX 	= rightDestN2V.intSX;
			int 	rightDestSY 	= rightDestN2V.intSY;
			long 	bigRightDestSZ	= ((long)rightDestN2V.intSZ) << ZSHIFT;

			float	rightDestVSX			= rightDestN2V.vsX;
			float	rightDestVSY			= rightDestN2V.vsY;
			float	rightDestVSZ			= rightDestN2V.vsZ;
			
			int		rightDestTX			= rightDestN2V.intTX;
			int		rightDestTY			= rightDestN2V.intTY;
			
			float	rightDelta_TX	= (float)(rightDestTX - rightStartTX);
			float	rightDelta_TY	= (float)(rightDestTY - rightStartTY);
			
			int 	rightDeltaSX; 
			int 	rightDeltaSY;
			long 	rightDeltaSZ;
			
			float 	rightDeltaVSX = rightDestVSX - rightVSX;
			float 	rightDeltaVSY = rightDestVSY - rightVSY;
			float 	rightDeltaVSZ = rightDestVSZ - rightVSZ;
			
			rightDeltaSY = rightDestSY - rightSY;
			
			if(rightDeltaSY > 0)
			{
					int rec 	= NUM / rightDeltaSY;
					rightDeltaSX = (rightDestSX - rightSX)	* rec;
					rightDeltaSY = (rightDestSY - rightSY);	// down counter
					rightDeltaSZ = (bigRightDestSZ - bigRightSZ) / rightDeltaSY;		
			}
			else
			{
					rightDeltaSX = 0;
					rightDeltaSY = 0;
					rightDeltaSZ = 0;					
			}
			
			// ***********************
			int leftTX, leftTY;
			int rightTX, rightTY;
			
			float lineStartVSX, lineStartVSY, lineStartVSZ;
			float lineFinishVSX, lineFinishVSY, lineFinishVSZ;
			
			
			boolean trucking = true;
			
			while(trucking)
			{
				
				// ************ Calculate left texture point
				
				float alpha = calculateAlphaUsingY(
						leftVSX, leftVSY, leftVSZ,
						leftDestVSX, leftDestVSY, leftDestVSZ,
						leftSY,
						invContextMag,
						contextMidH,
						contentGeneratorForcesNoPerspective
						);
				
				leftTX = (int)(leftDelta_TX * alpha) + leftStartTX;
				leftTY = (int)(leftDelta_TY * alpha) + leftStartTY;
				
				// calculate view space of line start
				lineStartVSX = leftDeltaVSX * alpha + leftVSX;
				lineStartVSY = leftDeltaVSY * alpha + leftVSY;
				lineStartVSZ = leftDeltaVSZ * alpha + leftVSZ;

				// ************* Calculate right texture point
				float alpha2 = calculateAlphaUsingY(
						rightVSX, rightVSY, rightVSZ,
						rightDestVSX, rightDestVSY, rightDestVSZ,
						rightSY,
						invContextMag,
						contextMidH,
						contentGeneratorForcesNoPerspective
						);
				
				rightTX = (int)(rightDelta_TX * alpha2) + rightStartTX;
				rightTY = (int)(rightDelta_TY * alpha2) + rightStartTY;
				
				// calculate view space of line finish
				lineFinishVSX = rightDeltaVSX * alpha2 + rightVSX;
				lineFinishVSY = rightDeltaVSY * alpha2 + rightVSY;
				lineFinishVSZ = rightDeltaVSZ * alpha2 + rightVSZ;
								
				renderLine(
						bigLeftSX, 
						bigLeftSZ, 
						leftSY * contextWidth, 
						
						bigRightSX,
						bigRightSZ,
						
						tex,
						textureWidth,
						
						leftTX,
						leftTY,
						
						rightTX,
						rightTY,
						
						// stuff for texture mapping
						lineStartVSX,
						lineStartVSY,
						lineStartVSZ,
						
						lineFinishVSX,
						lineFinishVSY,
						lineFinishVSZ,
						
						invContextMag,
						contextMidW,
						contentGeneratorForcesNoPerspective,					
						// -------------------------
						
						contextPixels,
						contextZBuffer,
						contextWidth,
						lightVal
				);
				
				// *********** move by delta *******
				bigLeftSX += leftDeltaSX;
				leftSY++;
				bigLeftSZ += leftDeltaSZ;
						
				bigRightSX += rightDeltaSX;		 
				rightSY++;
				bigRightSZ += rightDeltaSZ;
				
				// *********** decrement steps to destination down counters ****
				leftDeltaSY--;
				rightDeltaSY--;
				
				// *********** handle if we reach left destination ******************
				if(leftDeltaSY <= 0)
				{
					leftN2V 		= leftDestN2V;
					
					// now update left to eliminate rounding errors
					leftSX			= leftN2V.intSX;
					bigLeftSX 		= leftN2V.intSX << SHIFT;
					leftSY			= leftN2V.intSY;
					leftSZ			= leftN2V.intSZ;
					bigLeftSZ		= ((long)leftSZ) << SHIFT;
					
					leftVSX			= leftN2V.vsX;
					leftVSY			= leftN2V.vsY;
					leftVSZ			= leftN2V.vsZ;
					
					leftStartTX			= leftN2V.intTX;
					leftStartTY			= leftN2V.intTY;
							
					// find a new destination
					leftDestN2V = Nitrogen2UntexturedRenderer.findLeftDestN2V(leftDestN2V);
					
					if(leftDestN2V == null)
					{
						leftDeltaSX = 0;
						leftDeltaSY = 0;
						leftDeltaSZ = 0;			
						
						trucking = false;
					}
					else
					{
						leftDeltaSY = leftDestN2V.intSY - leftSY;
						if(leftDeltaSY > 0)
						{
								int rec 	= NUM / leftDeltaSY;
								leftDeltaSX = (leftDestN2V.intSX - leftSX)	* rec;
								leftDeltaSZ = ((long)leftDestN2V.intSZ) << ZSHIFT;
								leftDeltaSZ -= bigLeftSZ;
								leftDeltaSZ = leftDeltaSZ / leftDeltaSY;
								
								leftDestTX = leftDestN2V.intTX;
								leftDestTY = leftDestN2V.intTY;
								
								leftDestVSX			= leftDestN2V.vsX;
								leftDestVSY			= leftDestN2V.vsY;
								leftDestVSZ			= leftDestN2V.vsZ;
								
								leftDelta_TX = (leftDestTX - leftStartTX);
								leftDelta_TY = (leftDestTY - leftStartTY);
								
								leftDeltaVSX = leftDestVSX - leftVSX;
								leftDeltaVSY = leftDestVSY - leftVSY;
								leftDeltaVSZ = leftDestVSZ - leftVSZ;
						}
						else
						{
								leftDeltaSX = 0;
								leftDeltaSY = 0;
								leftDeltaSZ = 0;
								
								leftDelta_TX = 0;
								leftDelta_TY = 0;
								
								leftDeltaVSX = 0;
								leftDeltaVSY = 0;
								leftDeltaVSZ = 0;
								
								trucking = false;
						}
					}		
				}
				
				// *********** handle if we reach right destination ******************
				if(rightDeltaSY <= 0)
				{
					rightN2V 		= rightDestN2V;
					
					// now update right to eliminate rounding errors
					rightSX			= rightN2V.intSX;
					bigRightSX 		= rightN2V.intSX << SHIFT;
					rightSY			= rightN2V.intSY;
					rightSZ			= rightN2V.intSZ;
					bigRightSZ		= ((long)rightSZ) << SHIFT;
					
					rightVSX			= rightN2V.vsX;
					rightVSY			= rightN2V.vsY;
					rightVSZ			= rightN2V.vsZ;
					
					rightStartTX			= rightN2V.intTX;
					rightStartTY			= rightN2V.intTY;
										
					// find a new destination
					rightDestN2V = Nitrogen2UntexturedRenderer.findRightDestN2V(rightDestN2V);
					
					if(rightDestN2V == null)
					{
						rightDeltaSX = 0;
						rightDeltaSY = 0;
						rightDeltaSZ = 0;
						
						rightDelta_TX = 0;
						rightDelta_TY = 0;
						
						trucking = false;
					}
					else
					{
						rightDeltaSY = rightDestN2V.intSY - rightSY;
						if(rightDeltaSY > 0)
						{
								int rec 	= NUM / rightDeltaSY;
								rightDeltaSX = (rightDestN2V.intSX - rightSX)	* rec;
								rightDeltaSZ = (rightDestN2V.intSZ - rightSZ)	* rec;
								
								rightDeltaSZ = ((long)rightDestN2V.intSZ) << ZSHIFT;
								rightDeltaSZ -= bigRightSZ;
								rightDeltaSZ = rightDeltaSZ / rightDeltaSY;
								
								rightDestVSX			= rightDestN2V.vsX;
								rightDestVSY			= rightDestN2V.vsY;
								rightDestVSZ			= rightDestN2V.vsZ;
								
								rightDestTX = rightDestN2V.intTX;
								rightDestTY = rightDestN2V.intTY;
								
								
								rightDelta_TX = (rightDestTX - rightStartTX);
								rightDelta_TY = (rightDestTY - rightStartTY);
								
								rightDeltaVSX = rightDestVSX - rightVSX;
								rightDeltaVSY = rightDestVSY - rightVSY;
								rightDeltaVSZ = rightDestVSZ - rightVSZ;
								
						}
						else
						{
								rightDeltaSX = 0;
								rightDeltaSY = 0;
								rightDeltaSZ = 0;
								
								rightDelta_TX = 0;
								rightDelta_TY = 0;
								
								rightDeltaVSX = 0;
								rightDeltaVSY = 0;
								rightDeltaVSZ = 0;
								
								trucking = false;
						}
					}		
				}
			}//end of while loop
			
			leftTX =  leftStartTX;
			leftTY =  leftStartTY;
			
			rightTX = rightStartTX;
			rightTY = rightStartTY;
			
			// ************ Render final line *******
			renderLine(
					bigLeftSX, 
					bigLeftSZ, 
					leftSY * contextWidth, 
					
					bigRightSX,
					bigRightSZ,
					
					tex,
					textureWidth,
					
					leftTX,
					leftTY,
					
					rightTX,
					rightTY,
					
					// stuff for texture mapping
					leftVSX,
					leftVSY,
					leftVSZ,
					
					rightVSX,
					rightVSY,
					rightVSZ,
					
					invContextMag,
					contextMidW,
					contentGeneratorForcesNoPerspective,					
					// -------------------------
					
					contextPixels,
					contextZBuffer,
					contextWidth,
					lightVal
			);
		}

		//*****************************************************************************
		//*****************************************************************************
		// ****************************** Render Line *********************************
		//*****************************************************************************
		//*****************************************************************************

		private final void renderLine(
				final int 	bigLeftSX, 
				long 		bigLeftSZ, 
				final int 	indexOffset,
				
				final int 	bigRightSX,
				final long 	bigRightSZ,

				final int[] tex,
				final int 	textureWidth,
				
				final int 	leftTX,
				final int 	leftTY,
				
				final int 	rightTX,
				final int 	rightTY,
				
				// stuff for texture mapping
				final float lineStartVSX,
				final float lineStartVSY,
				final float lineStartVSZ,
				
				final float lineFinishVSX,
				final float lineFinishVSY,
				final float lineFinishVSZ,
				
				final float invContextMag,
				final int contextMidW,
				final boolean contentGeneratorForcesNoPerspective,
				
				final int[] contextPixels,
				final int[] contextZBuffer,
				final int contextWidth,
				final int lightVal
				)
		{
			int lineStart 	= bigLeftSX >> SHIFT;
			int lineFinish 	= bigRightSX >> SHIFT;
					
			float deltaTX = rightTX - leftTX;
			float deltaTY = rightTY - leftTY;		
					
			int lineLength = lineFinish - lineStart;
			
			// calculate zDelta
			long zDelta;
			if(lineLength > 0)
			{
				zDelta = (bigRightSZ - bigLeftSZ) / lineLength;
			}
			else
			{
				zDelta = 0;
			}
	
		    while(lineLength >= 0)
			{
				int quakeStep;
				if(lineLength >= QUAKE_STEP)
				{
					quakeStep = QUAKE_STEP;
				}
				else
				{
					quakeStep = lineLength;
				}
				
				// calculate start of quake step
				float alpha = calculateAlphaUsingX(
								lineStartVSX,
								lineStartVSY, 
								lineStartVSZ,
								
								lineFinishVSX, 
								lineFinishVSY, 
								lineFinishVSZ,
								
								lineStart,
								
								invContextMag,
								contextMidW,
								contentGeneratorForcesNoPerspective
								);
				
				int quakeStartTX = (int)(alpha * deltaTX) + leftTX;
				int quakeStartTY = (int)(alpha * deltaTY) + leftTY;
				
				// calculate finish of quake step
				float alpha2 = calculateAlphaUsingX(
								lineStartVSX,
								lineStartVSY, 
								lineStartVSZ,
								
								lineFinishVSX, 
								lineFinishVSY, 
								lineFinishVSZ,
								
								(lineStart + quakeStep),
								
								invContextMag,
								contextMidW,
								contentGeneratorForcesNoPerspective
								);
				
				int quakeFinishTX = (int)(alpha2 * deltaTX) + leftTX;
				int quakeFinishTY = (int)(alpha2 * deltaTY) + leftTY;
				int quakeDeltaTX;
				int quakeDeltaTY;
				
				if(quakeStep > 0)
				{
					quakeDeltaTX = (quakeFinishTX - quakeStartTX) / quakeStep;
					quakeDeltaTY = (quakeFinishTY - quakeStartTY) / quakeStep;
				}
				else
				{
					quakeDeltaTX = 0;
					quakeDeltaTY = 0;
				}
				
				int quakeX = lineStart;
				int quakeDownCount = quakeStep;
				long quakeLeftSZ = bigLeftSZ;
				while(quakeDownCount >= 0)
				{
					
					// ***************** RENDER PIXEL ****************
					int pixelZ = (int)(quakeLeftSZ >> ZSHIFT);
					int index = indexOffset + quakeX;
					
					if(pixelZ > contextZBuffer[index])
					{
						contextZBuffer[index] = pixelZ;
						int texPix = tex[(quakeStartTY >> TEXTURE_SHIFT) * textureWidth + (quakeStartTX >> TEXTURE_SHIFT)];
						contextPixels[index] = litPixelValue(lightVal, texPix);
					}
					
					// ***********************************************
					quakeLeftSZ += zDelta;
					quakeX++;
					quakeDownCount--;
					quakeStartTX += quakeDeltaTX;
					quakeStartTY += quakeDeltaTY;		
				}
					
				lineStart += quakeStep;
				bigLeftSZ += (zDelta * quakeStep);
				
				if(quakeStep > 0)
				{
					lineLength -= quakeStep;
				}
				else
				{
					lineLength--;
				}
			}
		}
		
		
		// ***********************************************************
		// ***********************************************************
		//                      calculateAlphaUsingY
		// ***********************************************************
		// ***********************************************************
		
		final private float calculateAlphaUsingY(
				final float startVSX,
				final float startVSY, 
				final float startVSZ,
				
				final float destVSX, 
				final float destVSY, 
				final float destVSZ,
				
				int SY,
				
				final float invContextMag,
				final int contextMidH,
				final boolean contentGeneratorForcesNoPerspective
				)
		{
			if(contentGeneratorForcesNoPerspective)
			{
				return (calculateAlphaUsingYNoPerspective(
						startVSX,
						startVSY, 
						startVSZ,
						
						destVSX, 
						destVSY, 
						destVSZ,
						
						SY,
						
						invContextMag,
						contextMidH
						) );
			}
			
			
			final float 	startVSZ2 = -startVSZ;
			final float 	destVSZ2 = -destVSZ;
			
			final float deltaY = destVSY - startVSY;
			final float deltaZ = destVSZ2 - startVSZ2;
			
			final float yDivZ = invContextMag *((float)(contextMidH - SY));
			
			final float denom = deltaY - yDivZ * deltaZ;
			
			if(denom == 0) return (0);
			
			final float numerator = yDivZ * startVSZ2 - startVSY;
			
			float retval = numerator / denom;
			
			if(retval > 1)return(1f);
			if(retval < 0)return(0f);
			return retval;	
		}
		
		// ***********************************************************
		// ***********************************************************
		//                      calculateAlphaUsingX
		// ***********************************************************
		// ***********************************************************
		
		final private float calculateAlphaUsingX(
				final float startVSX,
				final float startVSY, 
				final float startVSZ,
				
				final float destVSX, 
				final float destVSY, 
				final float destVSZ,
				
				int SX,
				
				final float invContextMag,
				final int contextMidW,
				final boolean contentGeneratorForcesNoPerspective
				)
		{
			if(contentGeneratorForcesNoPerspective)
			{
				return (calculateAlphaUsingXNoPerspective(
						startVSX,
						startVSY, 
						startVSZ,
						
						destVSX, 
						destVSY, 
						destVSZ,
						
						SX,
						
						invContextMag,
						contextMidW
						) );
			}
			final float 	startVSZ2 = -startVSZ;
			final float 	destVSZ2 = -destVSZ;
			
			final float 	deltaX = destVSX - startVSX;
			final float 	deltaZ = destVSZ2 - startVSZ2;
			
			final float xDivZ = invContextMag *((float)(SX - contextMidW));
			
			final float denom = deltaX - xDivZ * deltaZ;
			
			if(denom == 0) return (0);
			
			final float numerator = xDivZ * startVSZ2 - startVSX;
			
			float retval = numerator / denom;
			
			if(retval > 1)return(1f);
			if(retval < 0)return(0f);
			return retval;	
		}

		//*****************************************************************************
		//*****************************************************************************
		//*****************************************************************************
		//*****************************************************************************
		final private float calculateAlphaUsingXNoPerspective(
				final float startVSX,
				final float startVSY, 
				final float startVSZ,
				
				final float destVSX, 
				final float destVSY, 
				final float destVSZ,
				
				int SX,
				
				final float invContextMag,
				final int contextMidW
				){
			
			float deltaVSX = destVSX - startVSX;
			
			if(deltaVSX <= 0)return(0);
			
			float vsx = SX - contextMidW;
			
			float retval = (vsx - startVSX)/deltaVSX;
			
			if(retval < 0) retval = 0;
			if(retval > 1) retval = 1;
			return retval;		
		}
		
		final private float calculateAlphaUsingYNoPerspective(
				final float startVSX,
				final float startVSY, 
				final float startVSZ,
				
				final float destVSX, 
				final float destVSY, 
				final float destVSZ,
				
				int SY,
				
				final float invContextMag,
				final int contextMidH
				){
			
			float deltaVSY = destVSY - startVSY;
			
			if(deltaVSY == 0)return(0);
			
			float vsy = contextMidH - SY;
			
			float retval = (vsy - startVSY)/deltaVSY;
			
			if(retval < 0) retval = 0;
			if(retval > 1) retval = 1;
			return retval;		
		}
		
//*****************************************************************************
//*****************************************************************************
//*****************************************************************************
//*****************************************************************************

		/** calculate the lit colour integer */
		final int litPixelValue(int lightVal, int colour)
		{
			// convert to RGB
			int red = (colour 	& 0xFF0000) >> 16;
			int green = (colour & 0x00FF00) >> 8;
			int blue = (colour & 0x0000FF);
			
			// calculate lit RGB
			red = (red * lightVal) >> LIGHT_SHIFT;
			green = (green * lightVal) >> LIGHT_SHIFT;
			blue = (blue * lightVal) >> LIGHT_SHIFT;
			
			// clamp
			if(red > 255)		red = 255;
			if(green > green)	green = 255;
			if(blue > 255)		blue = 255;
			
			return (ALPHA | (red << 16) | (green << 8) | blue);
		}
		
//*****************************************************************************
//*****************************************************************************
//*****************************************************************************
//*****************************************************************************
		
		public void render(
				final NitrogenContext context,	
				
				final Nitrogen2Vertex leftN2V,
				final Nitrogen2Vertex leftDestN2V,
				
				final Nitrogen2Vertex rightN2V,
				final Nitrogen2Vertex rightDestN2V,			
				
				final int[] polyData,
				final TexMap textureMap,
				final float lightingValue	
				){
					r.render(
					
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

		public boolean isTextured(){return true;}

		public boolean allowsHLP(){return true;}


}	

