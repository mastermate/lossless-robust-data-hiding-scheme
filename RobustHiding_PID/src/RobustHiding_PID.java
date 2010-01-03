
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import es.us.pid.grupo14.impl.EmbeddingAlgorithmG8Impl;
import es.us.pid.grupo14.impl.EmbeddedValidationG8Impl;
import java.io.IOException;

public class RobustHiding_PID implements PlugInFilter {

	private ImagePlus imp;
	
	@Override
	public void run(ImageProcessor ip) {
		//Instancias para validar e inyectar información en la imagen
		EmbeddingAlgorithmG8Impl emb = new EmbeddingAlgorithmG8Impl();
		EmbeddedValidationG8Impl val = new EmbeddedValidationG8Impl();
		byte [] data;
		
		// Panel para cargar un archivo que inyectar a la imagen en pantalla.
		 try {
			 data = val.readFile();
		 }catch (IOException e) {
			 IJ.write("Lo siento, el fichero que quieres inyectar no se puede abrir." +
			 		"Inténtalo con otro.");
			 data = null;
			 return;
		 }
		 
		// Panel para obtener los valores de m, n, g y t.
		int m = 8, n=8;
		int t=128, g=64;
		
		//calculo los betas para generar el histograma
		int beta1 = val.getBeta1(g, t, m, n);
		int beta2 = val.getBeta2(g, t, m, n);

		//IJ.write("Beta 1 = "+beta1);
		//IJ.write("Beta 2 = "+beta2);
		
		int histogramType = val.getHistogramType(this.imp, beta1, beta2);
		int delta = val.getDelta(histogramType);
		IJ.showMessage(new String(data));
		ImagePlus res = emb.embedBits(this.imp, data, t, g, m, n, beta1, beta2, delta);
		res.show();
		
		
		
	}
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return PlugInFilter.DOES_8G;
	}

}
