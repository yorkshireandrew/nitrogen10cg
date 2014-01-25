package cg;

import com.bombheadgames.nitrogen2.RendererHelper;
import com.bombheadgames.nitrogen2.RendererTriplet;
import com.bombheadgames.nitrogen2.Renderer_AffineTexture;
import com.bombheadgames.nitrogen2.Renderer_AffineTextureHoley;
import com.bombheadgames.nitrogen2.Renderer_LitAffineTexture;
import com.bombheadgames.nitrogen2.Renderer_LitAffineTextureHoley;
import com.bombheadgames.nitrogen2.Renderer_LitQuake;
import com.bombheadgames.nitrogen2.Renderer_LitQuakeHoley;
import com.bombheadgames.nitrogen2.Renderer_LitSimpleSingleColour;
import com.bombheadgames.nitrogen2.Renderer_Outline3;
import com.bombheadgames.nitrogen2.Renderer_Quake;
import com.bombheadgames.nitrogen2.Renderer_Outline;
import com.bombheadgames.nitrogen2.Renderer_QuakeHoley;
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
        Renderer_Outline3 ro = new Renderer_Outline3();           
        RendererTriplet outlineTriplet = new RendererTriplet(ro);
 
        // RendererTriplet for affine texture
        Renderer_AffineTexture aff = new Renderer_AffineTexture();           
        RendererTriplet affineTextureTriplet = new RendererTriplet(aff);

        // RendererTriplet for litaffine texture
        Renderer_LitAffineTexture litAFF = new Renderer_LitAffineTexture();           
        RendererTriplet litAffineTextureTriplet = new RendererTriplet(litAFF);  
        
        // quake renderer
        Renderer_Quake quakeTextureRenderer = new Renderer_Quake();           
        RendererTriplet quakeTextureRendererTriplet = new RendererTriplet(quakeTextureRenderer);

        // RendererTriplet using just the outline renderer 
        Renderer_LitQuake litQuakeTextureRenderer = new Renderer_LitQuake();           
        RendererTriplet litQuakeTextureRendererTriplet = new RendererTriplet(litQuakeTextureRenderer);
        
        Renderer_LitAffineTextureHoley litAffineTextureHoleyRenderer = new Renderer_LitAffineTextureHoley();
        RendererTriplet litAffineTextureHoleyTriplet = new RendererTriplet(litAffineTextureHoleyRenderer);
 
        Renderer_LitQuakeHoley litQuakeHoleyRenderer = new Renderer_LitQuakeHoley();
        RendererTriplet litQuakeHoleyTriplet = new RendererTriplet(litQuakeHoleyRenderer);
 
        Renderer_QuakeHoley quakeHoleyRenderer = new Renderer_QuakeHoley();
        RendererTriplet quakeHoleyTriplet = new RendererTriplet(quakeHoleyRenderer);
 
        Renderer_AffineTextureHoley affineTextureHoleyRenderer = new Renderer_AffineTextureHoley();
        RendererTriplet affineTextureHoleyTriplet = new RendererTriplet(affineTextureHoleyRenderer);
  
        RendererTriplet quakePlusSingleTriplet = new RendererTriplet(quakeTextureRenderer, quakeTextureRenderer, sscr);
       
        try
        {
        	RendererHelper.addRendererTriplet("simpleSingleColour",simpleSingleColourTriplet);
        	RendererHelper.addRendererTriplet("litSimpleSingleColour",litSimpleSingleColourTriplet);
        	RendererHelper.addRendererTriplet("outline",outlineTriplet);
        	RendererHelper.addRendererTriplet("affineTexture",affineTextureTriplet);
        	RendererHelper.addRendererTriplet("litAffineTexture",litAffineTextureTriplet);
        	RendererHelper.addRendererTriplet("quakeTexture",quakeTextureRendererTriplet);
        	RendererHelper.addRendererTriplet("litQuakeTexture",litQuakeTextureRendererTriplet);
        	RendererHelper.addRendererTriplet("litAffineTextureHoley",litAffineTextureHoleyTriplet);
        	RendererHelper.addRendererTriplet("litQuakeHoley",litQuakeHoleyTriplet);
        	RendererHelper.addRendererTriplet("quakeHoley",quakeHoleyTriplet);
        	RendererHelper.addRendererTriplet("affineTextureHoley",affineTextureHoleyTriplet);
        	RendererHelper.addRendererTriplet("quakePlusSingle",quakePlusSingleTriplet);
        }
        catch(Exception e)
        {
        	System.out.println(e.getMessage());
        	e.printStackTrace();
        }
	}
}
