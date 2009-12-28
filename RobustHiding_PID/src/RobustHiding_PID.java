import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


public class RobustHiding_PID implements PlugInFilter {

	private ImagePlus imp;
	
	@Override
	public void run(ImageProcessor ip) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return PlugInFilter.DOES_8G;
	}

}
