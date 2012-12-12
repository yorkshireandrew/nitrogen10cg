/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nitrogen1;

// 

/**
 *
 * @author andrew
 */

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;  // needed for Dimension class
import java.awt.event.*;  // needed for ActionListener
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

final public class MyApplet extends JApplet{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1799576836511527595L;
	static final int APP_WIDTH = 501;
    static final int APP_HEIGHT =501;
    static final Renderer simpleTextureRenderer = new Renderer_SimpleTexture();

    Transform t4_class;
    Transform t3_class;
    NitrogenContext cnc;
    float rot = 0.0f;	// linked to t4
    float climb = 0.0f;	// linked to t3
    
    public void init()
	{       
            // Create nitrogen context and transforms
            final NitrogenContext cnc = new NitrogenContext(510,510,1,1,1,1000);
            final Transform t1, t2, t3, t4;
            final Transform t2b; // new transform for pick testing
            
            t1	= new 	Transform(
            			null,
            			1f, 0f, 0f, 0f,
            			0f, 1f, 0f, 0f,
            			0f, 0f, 1f, 0f);
            t1.setOuterName("t2");
            Transform looker= new Transform(t1,
            		1,0,0,1,
            		0,1,0,0,
            		0,0,1,0);
            
            Transform target = new Transform(t1,
            		1,0,0,10,
            		0,1,0,20,
            		0,0,1,30);
            looker.setTurn(900);
            System.out.println("x scalar = " + looker.xAxisScalarProduct(target));
            System.out.println("y scalar = " + looker.yAxisScalarProduct(target));
            System.out.println("z scalar = " + looker.zAxisScalarProduct(target));
            System.out.println("dist scalar = " + looker.dist(target));

            t2	= new 	Transform(
            			t1,
            			1f, 0f, 0f, 0f,
            			0f, 1f, 0f, 0f,
            			0f, 0f, 1f, -20f);
            t2.setOuterName("t2");
            t2b	= new 	Transform(
        			t1,
        			1f, 0f, 0f, 2f,
        			0f, 1f, 0f, 2f,
        			0f, 0f, 1f, -20f);
            t2b.setOuterName("t2b");
            Transform t2c = new 	Transform(
        			t2b,
        			1f, 0f, 0f, 0f,
        			0f, 1f, 0f, 0f,
        			0f, 0f, 1f, 0f);
            t2c.setOuterName("t2c");

            t3	= new 	Transform(
            			t2,
            			1f, 0f, 0f, 0f,
            			0f, 1f, 0f, 0f,
            			0f, 0f, 1f, 0f);            

            t4	= new 	Transform(
            			t3,
            			1f, 0f, 0f, 0f,
            			0f, 1f, 0f, 0f,
            			0f, 0f, 1f, 0f);
            
    
            // add renderers to RendererHelper
            Renderer_SimpleTexture str = new Renderer_SimpleTexture();
            RendererTriplet rt = new RendererTriplet(str);
            try
            {
            	RendererHelper.addRendererTriplet("str",rt);
            }
            catch(Exception e){System.out.println(e.getMessage());}

            Renderer_SimpleSingleColour sscr = new Renderer_SimpleSingleColour();           
            RendererTriplet sscrt = new RendererTriplet(sscr);
            try
            {
            	RendererHelper.addRendererTriplet("sscr",sscrt);
            }
            catch(Exception e){System.out.println(e.getMessage());}
            
            // create test item         
            SharedImmutableSubItem testItemSISI = null;        
            try{
            	
            	testItemSISI = new SharedImmutableSubItem("test1.txt");
            }
            catch(NitrogenCreationException e)
            {
            	e.printStackTrace();           	
            }
            
            // output sisi
            try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("test"));
				out.writeObject(testItemSISI);
				out.close();
            } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            // input sisi
            // output sisi
            SharedImmutableSubItem newsisi;
            try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream("test"));
				newsisi = (SharedImmutableSubItem) in.readObject();
				in.close();
            } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}          
            
            
            Item.setItemFactory(new ItemFactory_Caching());
            
            /*
            Item.getItemFactory().setMaxFreeItems(100000);
            Item.getItemFactory().setMaxFreeBacksides(600000);
            Item.getItemFactory().setMaxFreeVertexes(600000);
                                  
            // factory performance test
            int loops = 100000;
            Item[] testOfFactory = new Item[loops];
            long time1 = System.currentTimeMillis();
            // creating
            for(int x =0; x < loops ; x++)
            {
            	testOfFactory[x] = Item.createItem(newsisi,t4);
            }
            // recycling
            long time2 = System.currentTimeMillis();
            for(int x =0; x < loops ; x++)
            {
            	testOfFactory[x].recycle();
            }
            long time3 = System.currentTimeMillis();
            // creating again
            for(int x =0; x < loops ; x++)
            {
            	testOfFactory[x] = Item.createItem(newsisi,t4);
            }           
            long time4 = System.currentTimeMillis();
            System.out.println("initial creation " + (time2-time1));
            System.out.println("initial recycling " + (time3-time2));
            System.out.println("re- creation " + (time4-time3));
            
            */
            
 //           Item test = new Item(newsisi,t4); // shouldn't do this, we can because we are in package
            
            Item i = Item.createItem(newsisi,t4);
            i.setVisibility(true); 
            Item i2 = Item.createItem(newsisi,t2c);
            i2.setVisibility(true); 
            i2.setName("blogs boxes");
            
            t2c.setTurn(0.1f);
         
            // output sisi
            try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("test2"));
				out.writeObject(t2b);
				out.close();
            } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            t1.remove(t2b);
            // switch to another factory! test that !
            Item.setItemFactory(new ItemFactory_Default());
            
            //line below was a test needing public textures in TexMap
            //TexMap.textures = new HashMap<String,TexMap>();
            
            System.out.println("*********************");
            System.out.println("** LOADING BLOGS BOXES *****");
            System.out.println("*********************");

            // output sisi
            Transform readTransform;
            try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream("test2"));
				readTransform = (Transform) in.readObject();
				in.close();
            } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}  
            Item blogs = readTransform.findItemNamed("blogs boxes");
            if(blogs != null)System.out.println("FOUND BLOGS BOXES");
            blogs.setName("loaded blogs boxes");
            readTransform.setParent(t1);
      
            
            
 //           System.out.println("allocated Items " + ((ItemFactory_Development)Item.getItemFactory()).getAllocatedItemCount());
 //           System.out.println("allocated Backsides " + ((ItemFactory_Development)Item.getItemFactory()).getAllocatedBacksideCount());
 //           System.out.println("allocated Backsides " + ((ItemFactory_Development)Item.getItemFactory()).getAllocatedVertexCount());
            
            // create user interface
            getContentPane().setLayout(new BorderLayout());
            Box box_image = Box.createHorizontalBox();
            Box rotator_box = Box.createVerticalBox();            
            Box translator_box = Box.createVerticalBox();            
            
            addClimbControls(cnc,t3, t1, rotator_box,0.05f);
            addTurnControls(cnc,t4,t1, rotator_box,0.05f);
            
            addTranslationControls(cnc,t2, t1,translator_box, 0.3f);
            
            JButton debug = new JButton("debug");

            rotator_box.add(debug);
            
            box_image.add(cnc);
            box_image.add(rotator_box);
            box_image.add(translator_box);
            
            getContentPane().add(box_image);
            getContentPane().validate();
            getContentPane().setVisible(true);

            cnc.cls(0xFF0000FF);        
            t1.render(cnc);
            cnc.repaint();
                  
            debug.addActionListener(
            		new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent arg0) {
							cnc.debug = true;
						}}
            		);  
            
            // ********* OK Add stuff for picking *****
            i.setName("box1");
            RendererTriplet.pickingRenderer = new Renderer_Picking();
            class MyMouseListener extends MouseInputAdapter
            {
            	@Override
            	public void mouseClicked(MouseEvent e)
            	{
            		System.out.println("CLICK AT "+e.getX()+"," + e.getY());
            		cnc.isPicking = true;
            		cnc.pickDetected = false;
            		cnc.pickX = e.getX();
            		cnc.pickY = e.getY();
                    cnc.clearZBuffer();        
                    t1.render(cnc);
                    if(cnc.pickDetected)
                    {
                    	System.out.println("picked " + cnc.pickedItem.getName() );
                    }
                    else
                    {
                    	System.out.println("nothing picked" );
                    }
            		cnc.isPicking = false;
            		
            	}
            	      	
            }
            cnc.addMouseListener( new MyMouseListener());

	}
    
    //***************************************************************
    //***************************************************************    
    //***************************************************************
    //****************** END OF INIT ********************************
    //***************************************************************
    //***************************************************************


    @Override
        public Dimension getMinimumSize()
    {
        System.out.printf("getMinimumSize");
        return new Dimension(APP_WIDTH,APP_HEIGHT);
    }

    @Override
    public Dimension getPreferredSize()
    {
        System.out.printf("getPreferredSize");
        return new Dimension(APP_WIDTH,APP_HEIGHT);
    }

    @Override
    public Dimension getMaximumSize()
    {
        System.out.printf("getMaximumSize");
        return new Dimension(APP_WIDTH,APP_HEIGHT);
    }
    
    public void outputPerformanceData(NitrogenContext nc)
    {
    	System.out.println("items rendered ............" + nc.itemsRendered);
    	System.out.println("polygonsRendered rendered ." + nc.polygonsRendered);
    	System.out.println("clippedPolygonsRendered ..." + nc.clippedPolygonsRendered);
    	System.out.println("polygonRendererCalls ......" + nc.polygonRendererCalls);
    	System.out.println("linesRendered ............." + nc.linesRendered);
    }
    
    void addTranslationControls(final NitrogenContext nc, final Transform t, final Transform root, Box controlBox, final float step)
    {
        JButton x_plus  = new JButton("x+");
        JButton x_minus = new JButton("x-");
        JButton y_plus  = new JButton("y+");
        JButton y_minus = new JButton("y-");
        JButton z_plus  = new JButton("z+");
        JButton z_minus = new JButton("z-");

        controlBox.add(x_plus);
        controlBox.add(x_minus);
        controlBox.add(y_plus);
        controlBox.add(y_minus);
        controlBox.add(z_plus);
        controlBox.add(z_minus);
        
        x_plus.addActionListener(
        		new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						t.a14 += step;
						t.setNeedsTranslationUpdating();
						nc.cls(0xFF0000FF);
						root.render(nc);
						nc.repaint();
						outputPerformanceData(nc);
					}}
        );         
        x_minus.addActionListener(
        		new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						t.a14 -= step;
						t.setNeedsTranslationUpdating();
						nc.cls(0xFF0000FF);
						root.render(nc);
						nc.repaint();
						outputPerformanceData(nc);
					}}
        );  
        
        y_plus.addActionListener(
        		new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						t.a24 += step;
						t.setNeedsTranslationUpdating();
						nc.cls(0xFF0000FF);
						root.render(nc);
						nc.repaint();
						outputPerformanceData(nc);
					}}
        );         
        y_minus.addActionListener(
        		new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						t.a24 -= step;
						t.setNeedsTranslationUpdating();
						nc.cls(0xFF0000FF);
						root.render(nc);
						nc.repaint();
						outputPerformanceData(nc);
					}}
        );          
        z_plus.addActionListener(
        		new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						t.a34 += step;
						System.out.println("**************");
						System.out.println("Z = " + t.a34);
						System.out.println("**************");						
						t.setNeedsTranslationUpdating();
						nc.cls(0xFF0000FF);
						root.render(nc);
						nc.repaint();
						outputPerformanceData(nc);
					}}
        );         
        z_minus.addActionListener(
        		new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						t.a34 -= step;
						System.out.println("**************");
						System.out.println("Z = " + t.a34);
						System.out.println("**************");				
						t.setNeedsTranslationUpdating();
						nc.cls(0xFF0000FF);
						root.render(nc);
						nc.repaint();
						outputPerformanceData(nc);
					}}
        );
    }
  	
    
    
    void addTurnControls(final NitrogenContext nc, final Transform t, final Transform root, Box controlBox, final float step)
    {
        JButton turn_plus = new JButton("turn+");
        JButton turn_minus = new JButton("turn-");

        controlBox.add(turn_plus);
        controlBox.add(turn_minus);

        turn_plus.addActionListener(
        		new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) {
						rot += step;
						t.setTurn(rot);
						System.out.println("rot:"+rot);
						nc.cls(0xFF0000FF);
						root.render(nc);
						nc.repaint();
						outputPerformanceData(nc);
					}}
        		);
        
        turn_minus.addActionListener(
        		new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) {
						rot -= step;
						t.setTurn(rot);
						System.out.println("rot:"+rot);
						nc.cls(0xFF0000FF);
						root.render(nc);
						nc.repaint();
						outputPerformanceData(nc);
					}}
        		);
    } 
       
    

    
    void addClimbControls(final NitrogenContext nc, final Transform t, final Transform root, Box controlBox, final float step)
    {
        JButton climb_plus = new JButton("climb+");
        JButton climb_minus = new JButton("climb-");

        controlBox.add(climb_plus);
        controlBox.add(climb_minus);

        climb_plus.addActionListener(
        		new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) {
						climb += step;
						t.setClimb(climb);
						System.out.println("climb:"+climb);
						nc.cls(0xFF0000FF);
						root.render(nc);
						nc.repaint();
						outputPerformanceData(nc);
					}}
        		);
        
       climb_minus.addActionListener(
        		new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) {
						climb -= step;
						t.setClimb(climb);
						System.out.println("climb:"+climb);
						nc.cls(0xFF0000FF);
						root.render(nc);
						nc.repaint();
						outputPerformanceData(nc);
					}}
        		);
    }     
    
}// end of MyApplet