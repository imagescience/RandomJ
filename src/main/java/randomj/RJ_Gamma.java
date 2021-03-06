package randomj;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

import imagescience.image.FloatImage;
import imagescience.image.Image;
import imagescience.random.Randomizer;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class RJ_Gamma implements PlugIn, WindowListener {
	
	private static String order = "1";
	
	private static final String[] inserts = { "Additive", "Multiplicative" };
	private static int insert = 0;
	
	private static Point position = new Point(-1,-1);
	
	public void run(String arg) {
		
		if (!RJ.check()) return;
		final ImagePlus input = RJ.imageplus();
		if (input == null) return;
		
		RJ.log(RJ.name()+" "+RJ.version()+": Gamma");
		
		RJ.options();
		
		GenericDialog gd = new GenericDialog(RJ.name()+": Gamma");
		gd.addStringField("Order:",order);
		gd.addPanel(new Panel(),GridBagConstraints.EAST,new Insets(0,0,0,0));
		gd.addChoice("Insertion:",inserts,inserts[insert]);
		
		if (position.x >= 0 && position.y >= 0) {
			gd.centerDialog(false);
			gd.setLocation(position);
		} else gd.centerDialog(true);
		gd.addWindowListener(this);
		gd.showDialog();
		
		if (gd.wasCanceled()) return;
		
		order = gd.getNextString();
		insert = gd.getNextChoiceIndex();
		
		(new RJGamma()).run(input,order,insert);
	}
	
	public void windowActivated(final WindowEvent e) { }
	
	public void windowClosed(final WindowEvent e) {
		
		position.x = e.getWindow().getX();
		position.y = e.getWindow().getY();
	}
	
	public void windowClosing(final WindowEvent e) { }
	
	public void windowDeactivated(final WindowEvent e) { }
	
	public void windowDeiconified(final WindowEvent e) { }
	
	public void windowIconified(final WindowEvent e) { }
	
	public void windowOpened(final WindowEvent e) { }
	
}

class RJGamma { // To avoid exceptions when ImageScience is not installed
	
	void run(final ImagePlus input, final String order, final int insert) {
		
		try {
			int orderValue;
			try { orderValue = Integer.parseInt(order); }
			catch (Exception e) { throw new IllegalArgumentException("Invalid order value"); }
			int insertValue = Randomizer.ADDITIVE;
			switch (insert) {
				case 0: insertValue = Randomizer.ADDITIVE; break;
				case 1: insertValue = Randomizer.MULTIPLICATIVE; break;
			}
			
			final Image image = Image.wrap(input);
			final Randomizer randomizer = new Randomizer();
			randomizer.messenger.log(RJ_Options.log);
			randomizer.progressor.display(RJ_Options.pgs);
			final Image output = RJ_Options.floatout ?
				randomizer.gamma(new FloatImage(image),orderValue,insertValue,false) :
				randomizer.gamma(image,orderValue,insertValue,true);
			RJ.show(output,input);
			
		} catch (OutOfMemoryError e) {
			RJ.error("Not enough memory for this operation");
			
		} catch (IllegalArgumentException e) {
			RJ.error(e.getMessage());
			
		} catch (Throwable e) {
			RJ.error("An unidentified error occurred while running the plugin");
			
		}
	}
	
}
