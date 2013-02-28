
	package cg;

	import java.awt.event.ActionEvent;

	import javax.swing.AbstractAction;

	class NewMenuItemAction extends AbstractAction
	{
		private static final long serialVersionUID = 1L;

		ContentGenerator cg;
		
		 NewMenuItemAction(ContentGenerator cg)
		{
			this.cg = cg;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			cg.contentGeneratorSISI = new ContentGeneratorSISI();
			cg.cgc.clearPolygonVertexes();
			cg.polygonDialogModel = null;
			cg.cgc.updateGeneratedItemAndEditArea();
		}	
}
