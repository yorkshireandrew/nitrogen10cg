package cg;

import com.bombheadgames.nitrogen2.RendererHelper;
import com.bombheadgames.nitrogen2.RendererTriplet;
import com.bombheadgames.nitrogen2.Renderer_AffineTexture;
import com.bombheadgames.nitrogen2.Renderer_Outline;
import com.bombheadgames.nitrogen2.Renderer_SimpleSingleColour;

public class LoadRendererClasses {

	
	static void createRenderers()
	{
//        // RendererTriplet using just the SimpleTexture renderer
//		Renderer_SimpleTexture str = new Renderer_SimpleTexture();
//        RendererTriplet simpleTextureTriplet = new RendererTriplet(str);
        
        // RendererTriplet using just the SimpleSingleColour renderer 
        Renderer_SimpleSingleColour sscr = new Renderer_SimpleSingleColour();           
        RendererTriplet simpleSingleColourTriplet = new RendererTriplet(sscr);
        
        // RendererTriplet using just the outline renderer 
        Renderer_Outline ro = new Renderer_Outline();           
        RendererTriplet outlineTriplet = new RendererTriplet(ro);
 
        
        // RendererTriplet for affine texture
        Renderer_AffineTexture aff = new Renderer_AffineTexture();           
        RendererTriplet affineTextureTriplet = new RendererTriplet(aff);
        
 //       // RendererTriplet using just the outline renderer 
 //       Renderer_DirtyTextureRenderer dirtyTextureRenderer = new Renderer_DirtyTextureRenderer();           
 //       RendererTriplet dirtyTextureRendererTriplet = new RendererTriplet(dirtyTextureRenderer);

        try
        {
 //       	RendererHelper.addRendererTriplet("simpleTexture",simpleTextureTriplet);
        	RendererHelper.addRendererTriplet("singleSingleColour",simpleSingleColourTriplet);
        	RendererHelper.addRendererTriplet("outline",outlineTriplet);
        	RendererHelper.addRendererTriplet("affineTexture",affineTextureTriplet);
//        	RendererHelper.addRendererTriplet("dirtyTexture",dirtyTextureRendererTriplet);
        }
        catch(Exception e)
        {
        	System.out.println(e.getMessage());
        	e.printStackTrace();
        }
	}
}
