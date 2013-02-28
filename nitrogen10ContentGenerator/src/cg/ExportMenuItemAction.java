package cg;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

class ExportMenuItemAction extends AbstractAction
{
	private static final long serialVersionUID = 1L;

	ContentGenerator cg;
	File initialFile = null;
	ExportMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		final JFileChooser fileChooser = new JFileChooser(initialFile);
		fileChooser.setFileFilter(new GeneralFileFilter("nit"));

		int retval = fileChooser.showSaveDialog(cg);

        if (retval == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            
            // add file extension if it is not present
            if(new GeneralFileFilter("nit").accept(saveFile) == false)
            {
            	// it needs an extension adding
            	String old = saveFile.getAbsolutePath();
            	saveFile = new File(old + ".nit");
            }
            
        
            ObjectOutputStream output = null;
            try {
				output = new ObjectOutputStream(new FileOutputStream(saveFile));
				output.writeObject(cg.generatedSISI);
				
			} catch (FileNotFoundException e1) {e1.printStackTrace();} 
            catch (IOException e1) {e1.printStackTrace();} 
            finally
            {
            	try {output.close();}
				catch (IOException e1) {e1.printStackTrace();}
            }
        }
	}

	}



