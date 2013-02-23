package cg;

import com.bombheadgames.nitrogen2.RendererHelper;
import com.bombheadgames.nitrogen2.RendererTriplet;
import com.bombheadgames.nitrogen2.Renderer_AffineTexture;
import com.bombheadgames.nitrogen2.Renderer_LitQuake;
import com.bombheadgames.nitrogen2.Renderer_LitSimpleSingleColour;
import com.bombheadgames.nitrogen2.Renderer_Quake;
import com.bombheadgames.nitrogen2.Renderer_DirtyTexture;
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
 
        // RendererTriplet using just the SimpleSingleColour renderer 
        Renderer_LitSimpleSingleColour litSSCR = new Renderer_LitSimpleSingleColour();           
        RendererTriplet litSimpleSingleColourTriplet = new RendererTriplet(litSSCR);
        
        // RendererTriplet using just the outline renderer 
        Renderer_Outline ro = new Renderer_Outline();           
        RendererTriplet outlineTriplet = new RendererTriplet(ro);
 
        
        // RendererTriplet for affine texture
        Renderer_AffineTexture aff = new Renderer_AffineTexture();           
        RendererTriplet affineTextureTriplet = new RendererTriplet(aff);
        
        // quake renderer
        Renderer_Quake quakeTextureRenderer = new Renderer_Quake();           
        RendererTriplet quakeTextureRendererTriplet = new RendererTriplet(quakeTextureRenderer);

        // RendererTriplet using just the outline renderer 
        Renderer_LitQuake litQuakeTextureRenderer = new Renderer_LitQuake();           
        RendererTriplet litQuakeTextureRendererTriplet = new RendererTriplet(litQuakeTextureRenderer);
        
        try
        {
        	RendererHelper.addRendererTriplet("simpleSingleColour",simpleSingleColourTriplet);
        	RendererHelper.addRendererTriplet("litSimpleSingleColour",litSimpleSingleColourTriplet);
        	RendererHelper.addRendererTriplet("outline",outlineTriplet);
        	RendererHelper.addRendererTriplet("affineTexture",affineTextureTriplet);
        	RendererHelper.addRendererTriplet("quakeTexture",quakeTextureRendererTriplet);
        	RendererHelper.addRendererTriplet("litQuakeTexture",litQuakeTextureRendererTriplet);
        }
        catch(Exception e)
        {
        	System.out.println(e.getMessage());
        	e.printStackTrace();
        }
	}
}
