package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
// import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.event.ChangeListener;

public class TemplateDialog extends JDialog implements ChangeListener
{
	JTextField fileNameTextField;
	JSpinner leftRightSpinner;
	String leftRightString;
	JSpinner downUpSpinner;
	String downUpString;
	JSpinner scaleSpinner;
	JSlider intensitySlider;
	
	JLabel leftRightLabel 				= new JLabel("leftRightLabelNeedsSetting");
	JLabel downUpLabel 					= new JLabel("downUpLabelNeedsSetting");
	JLabel scaleLabel 					= new JLabel("Scale x1000   ");
	JLabel intensityLabel 				= new JLabel("Intensity     ");
	
	private TemplateModel tm;
	
	/** The state of the TemplateModel when this TemplateDialog was constructed */
	TemplateModel initialState;
	
	TemplateDialog(final ContentGenerator cg, final TemplateModel tm)
	{

		// create a (final) reference to this template dialog
		// so it can be passed to the ActionListeners for OK button etc.
		final TemplateDialog td = this;
		
		// save state of passed in TemplateModel in case user hits cancel 
		initialState = TemplateModel.copy(tm);
		
		// set tm field so handlers can use it
		// and they do not have to be in this constructor
		this.tm = tm;
		
		// create and initialise the fileNameText Field
		fileNameTextField = new JTextField(null,10);
		fileNameTextField.setEnabled(false);
		
		// *** FIX FOR JTextField (which works) ***
		// place JTextField in a JPanel with flow layout
		// to prevent it expanding
		// add fileNameTextFieldPanel into TemplateDialog instead
		//   JPanel fileNameTextFieldPanel = new JPanel();
		//   fileNameTextFieldPanel.add(fileNameTextField);
		
		// *** CLEANER FIX FOR JTextField (which also works) ***
		fileNameTextField.setMaximumSize(fileNameTextField.getPreferredSize());

		// initialise fileNameTextField from the TemplateModel
		if (tm.templateFile != null)
		{
			fileNameTextField.setText(tm.templateFile.getName());
		}
		else
		{
			fileNameTextField.setText("none");
		}
		

		
		// initialise the leftRightSpinner
		leftRightSpinner = new JSpinner();
		leftRightSpinner.setModel(new SpinnerNumberModel(tm.leftRightOffset,-500,500,1));
		leftRightSpinner.addChangeListener(this);
		leftRightSpinner.setMaximumSize(leftRightSpinner.getPreferredSize());

		// initialise the downUpSpinner
		downUpSpinner = new JSpinner();
		downUpSpinner.setModel(new SpinnerNumberModel(tm.downUpOffset,-500,500,1));
		downUpSpinner.addChangeListener(this);
		downUpSpinner.setMaximumSize(downUpSpinner.getPreferredSize());
		
		// initialise the scaleSpinner
		scaleSpinner = new JSpinner();
		scaleSpinner.setModel(new SpinnerNumberModel(tm.scale,0,10000,10));
		scaleSpinner.addChangeListener(this);
		scaleSpinner.setMaximumSize(scaleSpinner.getPreferredSize());
		
		// initialise the intensity slider
		intensitySlider = new JSlider();
		intensitySlider.setModel(new DefaultBoundedRangeModel(tm.intensity,1,0,100));
		intensitySlider.setMinorTickSpacing(10);
		intensitySlider.setPaintTicks(true);
		intensitySlider.addChangeListener(this);
		
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(
				new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
						/*
						// record the new templateFileName
						if(t.getText().equals("none"))
						{
							// do nothing
						}
						else
						{
							cg.templateFileName[cg.viewDirection] = t.getText();
						}
						*/
						td.setVisible(false);
						td.dispose();			
					}			
				});
		
		final JFileChooser fileChooser = new JFileChooser(tm.templateFile);
		final JButton fileChooserButton = new JButton("Select File");
		fileChooserButton.addActionListener(
				new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
					int retval = fileChooser.showOpenDialog(td);

			        if (retval == JFileChooser.APPROVE_OPTION) {
			            tm.templateFile = fileChooser.getSelectedFile();
			            fileNameTextField.setText(tm.templateFile.getName());
			            fileNameTextField.repaint();
			            System.out.println("Opening: " + tm.templateFile.getName());
			            // TO DO cause the model to update
			            
			        } else {
			        	System.out.println("Open command cancelled by user.");
			        }

					}	
				}
		);
		
		// create and fill fileBox
		Box fileBox = new Box(BoxLayout.X_AXIS);
		fileBox.add(fileNameTextField);
		fileBox.add(fileChooserButton);
		fileBox.add(Box.createHorizontalGlue());
		
		// create and fill leftRightBox
		Box leftRightBox = new Box(BoxLayout.X_AXIS);
		leftRightBox.add(leftRightLabel);		
		leftRightBox.add(leftRightSpinner);
		leftRightBox.add(Box.createHorizontalGlue());
		
		// create and fill downUpBox
		Box downUpBox = new Box(BoxLayout.X_AXIS);
		downUpBox.add(downUpLabel);		
		downUpBox.add(downUpSpinner);
		downUpBox.add(Box.createHorizontalGlue());
		
		// create and fill scaleBox
		Box scaleBox = new Box(BoxLayout.X_AXIS);
		scaleBox.add(scaleLabel);		
		scaleBox.add(scaleSpinner);
		scaleBox.add(Box.createHorizontalGlue());
		
		// create and fill intensityBox
		Box intensityBox = new Box(BoxLayout.X_AXIS);
		intensityBox.add(intensityLabel);		
		intensityBox.add(intensitySlider);
		intensityBox.add(Box.createHorizontalGlue());		

		
		// create a dialogBox and add everything to it
		Box dialog = new Box(BoxLayout.Y_AXIS);	
		dialog.add(fileBox);
		dialog.add(Box.createVerticalGlue());
		dialog.add(leftRightBox);
		dialog.add(Box.createVerticalGlue());		
		dialog.add(downUpBox);
		dialog.add(Box.createVerticalGlue());
		dialog.add(scaleBox);
		dialog.add(Box.createVerticalGlue());
		dialog.add(intensityBox);
		dialog.add(Box.createVerticalGlue());
		dialog.add(okButton);
		this.add(dialog);
		this.setSize(400,250);
		this.setModal(true);
		this.validate();
		this.setLocationRelativeTo(cg);
        
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
    public void stateChanged(javax.swing.event.ChangeEvent evt) {
    	
    	// handle leftRightSpinner
       if (evt.getSource() == leftRightSpinner) {
    	    tm.leftRightOffset = (Integer)leftRightSpinner.getModel().getValue();
    	    System.out.println("leftRightSpinner = " + tm.leftRightOffset);
       }
       
       // handle downUpSpinner
       if (evt.getSource() == downUpSpinner) {
   	    tm.downUpOffset = (Integer)downUpSpinner.getModel().getValue();
   	    System.out.println("downUpSpinner = " + tm.downUpOffset);
       }
       
      // handle scaleSpinner
       if (evt.getSource() == scaleSpinner) {
      	    tm.scale = (Integer)scaleSpinner.getModel().getValue();
      	    System.out.println("scaleSpinner = " + tm.scale);
       }
       
       // handle scaleSpinner
       if (evt.getSource() == scaleSpinner) {
      	    tm.scale = (Integer)scaleSpinner.getModel().getValue();
      	    System.out.println("scaleSpinner = " + tm.scale);
       }  
       
       // handle intensitySlider
       if (evt.getSource() == intensitySlider) {
    	    DefaultBoundedRangeModel dbrm = (DefaultBoundedRangeModel)intensitySlider.getModel();
      	    if(!dbrm.getValueIsAdjusting())
      	    {
      	    	tm.intensity = dbrm.getValue();
      	    	 System.out.println("intensitySlider = " + tm.intensity);
      	    }
       }    
    }
    
    public void leftRightSpinnerStateChanged(javax.swing.event.ChangeEvent evt)
    {
    	System.out.println("TO DO - do something the spinner changed");
    	System.out.println("value now " + leftRightSpinner.getValue());
        if (evt.getSource() == leftRightSpinner.getEditor()) {
            System.out.println("it was an editor event");
        }
    }
    
    
}
