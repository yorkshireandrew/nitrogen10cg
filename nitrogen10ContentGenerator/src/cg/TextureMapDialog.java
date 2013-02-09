package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.bombheadgames.nitrogen1.NitrogenCreationException;
import com.bombheadgames.nitrogen1.SharedImmutableSubItem;
import com.bombheadgames.nitrogen1.TexMap;


public class TextureMapDialog extends JDialog{

	JTextField	nameTextField;
	
	JLabel nameLabel = new JLabel("name ");
	
	final ContentGenerator cg;
	
	String fileString;
//	String fileFullString;
	boolean filePicked = false;
	
	JButton cancelButton;
	JButton okButton;
	
	TextureMapDialog(final ContentGenerator cg)
	{
		super(cg);
		setTitle("Add Texture Map");
		final TextureMapDialog tmd = this;
		
		this.cg = cg;
		cg.cgc.saveSISI();
				
		final JFileChooser pathChooser = new JFileChooser(cg.resourceURL);
		pathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		final JButton pathChooserButton = new JButton("Select root path");
		pathChooserButton.addActionListener(
				new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
						int retval = pathChooser.showOpenDialog(tmd);
	
				        if (retval == JFileChooser.APPROVE_OPTION) {
				            File textureMapPath = pathChooser.getSelectedFile();
				            if(!textureMapPath.getPath().equals(TextureMapDialog.this.cg.resourceURL))
				            {
				            	System.out.println("changing path");
				            	filePicked = false;
				            	TextureMapDialog.this.cg.resourceURL = textureMapPath.getPath();
				            }
				            else
				            {
				            	System.out.println("path did not need changing");
				            }
				        } else {
				        	System.out.println("Open command cancelled by user.");
				        }
					}// end of action performed	
				}// end of action listener inner class
		);// end of add ActionListener method call		
		
		final JFileChooser fileChooser = new JFileChooser(cg.resourceURL);
		final JButton fileChooserButton = new JButton("Select File");
		fileChooserButton.addActionListener(
				new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
					fileChooser.setCurrentDirectory(new File(TextureMapDialog.this.cg.resourceURL));
					int retval = fileChooser.showOpenDialog(tmd);

			        if (retval == JFileChooser.APPROVE_OPTION) {
			            File textureMapFile = fileChooser.getSelectedFile();
		//	            fileNameTextField.setText(tm.templateFile.getName());
			//            fileNameTextField.repaint();
			            System.out.println("Opening: " + textureMapFile.getPath());
			            System.out.println("Opening: " + textureMapFile.getName());
			            // TO DO cause the model to update
			            
			            String full = textureMapFile.getPath();
			            if(full.startsWith(TextureMapDialog.this.cg.resourceURL))
			            {
			            	fileString = full.substring(TextureMapDialog.this.cg.resourceURL.length());
//			            	fileFullString = full;
			            	filePicked = true;
			            }
			            else
			            {
			            	JOptionPane.showMessageDialog(TextureMapDialog.this, "The file is not under the set path", "Error",JOptionPane.ERROR_MESSAGE);
			            	filePicked = false;
			            }			            
			        } else {
			        	System.out.println("Open command cancelled by user.");
			        }

					}	
				}
		);
		
		cancelButton = new JButton("CANCEL");
		cancelButton.addActionListener(
				new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
						cg.contentGeneratorSISI = cg.undoStack.pop();
						TextureMapDialog.this.setVisible(false);
						TextureMapDialog.this.dispose();			
					}			
				});	
		
		okButton = new JButton("OK");
		
		okButton.addActionListener(
				new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) {
						TextureMapDialog.this.handleOK();	
					}			
				});	
		
		nameTextField = new JTextField(20);
		nameTextField.setMaximumSize(nameTextField.getPreferredSize());	
		Box nameBox = Box.createHorizontalBox();
		nameBox.add(nameLabel);
		nameBox.add(nameTextField);
		
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(okButton);
		buttonBox.add(cancelButton);
		
		Box dialog = Box.createVerticalBox();
		dialog.add(nameBox);
		dialog.add(pathChooserButton);
		dialog.add(fileChooserButton);
		dialog.add(buttonBox);
		this.getContentPane().removeAll();
		this.getContentPane().add(dialog);
		this.setSize(250,150);
		// this.setModal(true);
		this.validate();
		this.setLocationRelativeTo(cg);	
	}
	
	private void handleOK()
	{
		
		if(!filePicked)
		{
			JOptionPane.showMessageDialog(TextureMapDialog.this, "Please pick a file", "Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
				
		String name = nameTextField.getText().trim();
		
		if(name.equals(""))
		{
			JOptionPane.showMessageDialog(TextureMapDialog.this, "Please enter a name for this texture map", "Error",JOptionPane.ERROR_MESSAGE);
			return;			
		}
		if(nameIsOK(name))
		{
			try
			{
				// load the texture map from file
				// fileString is used to create a unix style resourceURL in the TexMap itself
				TexMap newTexMap = new TexMap(cg.resourceURL,fileString);

				ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;
				Map<String,ContentGeneratorTextureMap> textureMapMap = cgsisi.textureMapMap;
				ContentGeneratorTextureMap cgtm = new ContentGeneratorTextureMap(cg.resourceURL,fileString,newTexMap);
				textureMapMap.put(name,cgtm);
				TextureMapDialog.this.setVisible(false);
				TextureMapDialog.this.dispose();		
			}
			catch(NitrogenCreationException e)
			{
				JOptionPane.showMessageDialog(TextureMapDialog.this, "Unable to create texture map due to :" + e.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
				return;
			}			
		}
	}
	
	/** checks the name returning OK if it is not present or user wishes to overwrite*/
	private boolean nameIsOK(String name)
	{
		SharedImmutableSubItem sisi = cg.generatedSISI;
		ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;
		Map<String,ContentGeneratorTextureMap> textureMapMap = cgsisi.textureMapMap;
		
		if(textureMapMap.containsKey(name))
		{
			if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
											this, 
											"The name " + name + " is already in use. Overwrite existing data?", 
											"Name already exists"
											,JOptionPane.YES_NO_OPTION)
			)
			{
				return true;
			}
			else
			{
				return false;
			}
		}		
		return true;
	}
}

