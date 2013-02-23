package cg;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

class SaveMenuItemAction extends AbstractAction
{
	ContentGenerator cg;
	File initialFile = null;
	SaveMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		final JFileChooser fileChooser = new JFileChooser(initialFile);
		fileChooser.setFileFilter(new GeneralFileFilter("ncg"));
		int retval = fileChooser.showSaveDialog(cg);

        if (retval == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            
            // add file extension if it is not present
            if(new GeneralFileFilter("ncg").accept(saveFile) == false)
            {
            	// it needs an extension adding
            	String old = saveFile.getAbsolutePath();
            	saveFile = new File(old + ".ncg");
            }
            
            ObjectOutputStream output = null;
            try {
				output = new ObjectOutputStream(new FileOutputStream(saveFile));
				cg.writeToFile(output);
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
            finally
            {
            	try {
					output.close();
				} catch (IOException e1) {e1.printStackTrace();}
            	
            	// fire a load to shake down cg
            	
                ObjectInputStream in = null;
                try {
    				in = new ObjectInputStream(new FileInputStream(saveFile));
    				cg.readFromFile(in);
    				
    			} catch (FileNotFoundException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			} catch (IOException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			} 
                
            	try {
					in.close();
				} catch (IOException e1) {e1.printStackTrace();}

            	
            }        
        }	
	}
}
