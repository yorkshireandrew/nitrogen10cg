package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
// import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.event.ChangeListener;

public class TemplateDialog extends JDialog implements ChangeListener
{
	private static final long serialVersionUID = 1L;

	JTextField 	fileNameTextField;
	JSpinner 	templateXSpinner;
	String 		templateXString;
	JSpinner 	templateYSpinner;
	String 		templateYString;
	JSpinner 	templateScaleSpinner;
	JSlider 	intensitySlider;
	JCheckBox	overlayCheckBox;
	
	JLabel tamplateXLabel 				= new JLabel("template X  ");
	JLabel templateYLabel 				= new JLabel("template Y  ");
	JLabel templateScaleLabel 			= new JLabel("Scale x1000 ");
	JLabel intensityLabel 				= new JLabel("Intensity   ");
	
	private TemplateModel tm;
	
	ContentGenerator contentGenerator;
	
	/** The state of the TemplateModel when this TemplateDialog was constructed */
	TemplateModel initialState;
	
	TemplateDialog(final ContentGenerator cg, final TemplateModel tm)
	{

		// create a (final) reference to this template dialog
		// so it can be passed to the ActionListeners for OK button etc.
		final TemplateDialog td = this;
		
		// copy the content generator so we can call methods to
		// update it in response to events
		contentGenerator = cg;
		
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
		

		
		// initialise the templateXSpinner
		templateXSpinner = new JSpinner();
		templateXSpinner.setModel(new SpinnerNumberModel(tm.templateX,-500,500,1));
		templateXSpinner.addChangeListener(this);
		templateXSpinner.setMaximumSize(templateXSpinner.getPreferredSize());

		// initialise the templateYSpinner
		templateYSpinner = new JSpinner();
		templateYSpinner.setModel(new SpinnerNumberModel(tm.templateY,-500,500,1));
		templateYSpinner.addChangeListener(this);
		templateYSpinner.setMaximumSize(templateYSpinner.getPreferredSize());
		
		// initialise the templateScaleSpinner
		templateScaleSpinner = new JSpinner();
		templateScaleSpinner.setModel(new SpinnerNumberModel(tm.templateScale,0,10000,10));
		templateScaleSpinner.addChangeListener(this);
		templateScaleSpinner.setMaximumSize(templateScaleSpinner.getPreferredSize());
		
		// initialise the intensity slider
		intensitySlider = new JSlider();
		intensitySlider.setModel(new DefaultBoundedRangeModel(tm.intensity,1,0,100));
		intensitySlider.setMinorTickSpacing(10);
		intensitySlider.setPaintTicks(true);
		intensitySlider.addChangeListener(this);
		
		// initialise overlay template
		overlayCheckBox = new JCheckBox("Overlay ");
		overlayCheckBox.setSelected(tm.overlay);
		overlayCheckBox.addActionListener(
				new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
				    	tm.overlay = overlayCheckBox.getModel().isSelected();
				    	System.out.println("overlay checkbox = " + tm.overlay);
						updateContentGenerator();		
					}			
				});
		
		// OK button is just a close dialog
		JButton okButton = new JButton("OK");
		okButton.addActionListener(
				new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) {
						td.setVisible(false);
						td.dispose();			
					}			
				});
		
		
		JButton cancelButton = new JButton("CANCEL");
		cancelButton.addActionListener(
				new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
						tm.restore(initialState);
						updateContentGenerator();
						td.setVisible(false);
						td.dispose();			
					}			
				});
		
		final JFileChooser fileChooser = new JFileChooser(tm.templateFile);
	
		fileChooser.setFileFilter(new GeneralFileFilter("png","jpg"));

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
			            tm.loadFile();
			            tm.generatePixels();
			            contentGenerator.renderEditArea(); 		            
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
		
		// create and fill templateXBox
		Box templateXBox = new Box(BoxLayout.X_AXIS);
		templateXBox.add(tamplateXLabel);		
		templateXBox.add(templateXSpinner);
		templateXBox.add(Box.createHorizontalGlue());
		
		// create and fill templateYBox
		Box templateYBox = new Box(BoxLayout.X_AXIS);
		templateYBox.add(templateYLabel);		
		templateYBox.add(templateYSpinner);
		templateYBox.add(Box.createHorizontalGlue());
		
		// create and fill scaleBox
		Box scaleBox = new Box(BoxLayout.X_AXIS);
		scaleBox.add(templateScaleLabel);		
		scaleBox.add(templateScaleSpinner);
		scaleBox.add(Box.createHorizontalGlue());
		
		// create and fill intensityBox
		Box intensityBox = new Box(BoxLayout.X_AXIS);
		intensityBox.add(intensityLabel);		
		intensityBox.add(intensitySlider);
		intensityBox.add(Box.createHorizontalGlue());		
		
		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(okButton);
		buttonBox.add(cancelButton);
		
		// create a dialogBox and add everything to it
		Box dialog = new Box(BoxLayout.Y_AXIS);	
		dialog.add(fileBox);
		dialog.add(Box.createVerticalGlue());
		dialog.add(templateXBox);
		dialog.add(Box.createVerticalGlue());		
		dialog.add(templateYBox);
		dialog.add(Box.createVerticalGlue());
		dialog.add(scaleBox);
		dialog.add(Box.createVerticalGlue());
		dialog.add(intensityBox);
		dialog.add(Box.createVerticalGlue());
		dialog.add(overlayCheckBox);
		dialog.add(Box.createVerticalGlue());
		dialog.add(buttonBox);
		this.add(dialog);
		this.setSize(400,250);
		// this.setModal(true);
		this.validate();
		this.setLocationRelativeTo(cg);
        
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
    public void stateChanged(javax.swing.event.ChangeEvent evt) {
    	
    	// handle templateX Spinner
       if (evt.getSource() == templateXSpinner) {
    	    tm.templateX = (Integer)templateXSpinner.getModel().getValue();
    	    updateContentGenerator();
    	    System.out.println("leftRightSpinner = " + tm.templateX);
    	    
       }
       
       // handle templateY spinner
       if (evt.getSource() == templateYSpinner) {
   	    tm.templateY = (Integer)templateYSpinner.getModel().getValue();
   	    updateContentGenerator();
   	    System.out.println("downUpSpinner = " + tm.templateY);
       }
       
      // handle scaleSpinner
       if (evt.getSource() == templateScaleSpinner) {
      	    tm.templateScale = (Integer)templateScaleSpinner.getModel().getValue();
      	    updateContentGenerator();
      	    System.out.println("scaleSpinner = " + tm.templateScale);
       } 
       
       // handle intensitySlider
       if (evt.getSource() == intensitySlider) {
    	    DefaultBoundedRangeModel dbrm = (DefaultBoundedRangeModel)intensitySlider.getModel();
      	    if(!dbrm.getValueIsAdjusting())
      	    {
      	    	tm.intensity = dbrm.getValue();
      	    	updateContentGenerator();
      	    	 System.out.println("intensitySlider = " + tm.intensity);
      	    }
       } 
    }
    
    void updateContentGenerator()
    {
    	tm.generatePixels();
    	contentGenerator.renderEditArea();    	
    }    
}
