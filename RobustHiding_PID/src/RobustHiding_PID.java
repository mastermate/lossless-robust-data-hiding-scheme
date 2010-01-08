
import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import es.us.pid.grupo14.AlphasImage;
import es.us.pid.grupo14.ExtractionAlgorithm;
import es.us.pid.grupo14.HidingResult;
import es.us.pid.grupo14.impl.EmbeddingAlgorithmG8Impl;
import es.us.pid.grupo14.impl.EmbeddedValidationG8Impl;
import es.us.pid.grupo14.impl.ExtractionAlgorithmG8Impl;

import java.io.IOException;

public class RobustHiding_PID implements PlugInFilter {

	private ImagePlus imp;
	private double m=8, n=8;
	private double t=128, g=64;
	private double n0=0, n1=0;
	private double delta=0;
	private String choice;
	private String imageChoice;
	private boolean estadisticas;
	private boolean extraccion;
	public static final String INYECCION ="Inyecci�n de datos";
	public static final String EXTRACCION ="Extracci�n de datos";
	public static final String RGB = "Imagen RGB";
	public static final String G8 = "Imagen escala de grises 8 bits";
	public static final String INYECCIONYEXTRACCION = "Extraer los datos despu�s de inyectar";
	public static final String ESTADISTICAS = "Mostrar estad�sticas";
		
	@Override
	public void run(ImageProcessor ip) {
		 // Muestro el panel para seleccionar el proceso a realizar
		 
		this.getInyExtPanel();
		this.configurationPanel();
		if( this.choice == RobustHiding_PID.INYECCION){
			this.inyeccion();
			if(this.extraccion){
				
			}
			if(this.estadisticas){
				this.showEstadisticas();
			}
		}else{	
			this.extraccion();
		}
		 
	}
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return PlugInFilter.DOES_8G;
	}
	
	/**
	 * Genera el panel para que el usuario indique los valores de las variables m, n, t y g.
	 */
	public void  getVariablePanel(){
		
		GenericDialog d = new GenericDialog("Indique los valores de las siguientes variables", IJ.getInstance());
		d.addNumericField("N�mero de filas de la matriz M:", this.m, 0, 3,"m");
		d.addNumericField("N�mero de columnas de la matriz M:",this.n, 0, 3, "n");
		d.addNumericField("Constante T:", this.t, 0, 3, "T");
		d.addNumericField("Constante G:", this.g, 0, 3, "G");
		d.showDialog();
		if(d.wasCanceled()) 
			this.imp.close();
		this.m = d.getNextNumber();
		if(d.invalidNumber()) {
			IJ.showMessage("Error", "Valor incorrecto de la variable n. Se debe introducir un entero.");
			this.imp.close();
		}
		this.n = d.getNextNumber();
		if(d.invalidNumber()) {
			IJ.showMessage("Error", "Valor incorrecto de la variable m. Se debe introducir un entero.");
			this.imp.close();
		}
		this.t = d.getNextNumber();
		if(d.invalidNumber()) {
			IJ.showMessage("Error", "Valor incorrecto de la variable T. Se debe introducir un entero.");
			this.imp.close();
		}
		this.g = d.getNextNumber();
		if(d.invalidNumber()) {
			IJ.showMessage("Error", "Valor incorrecto de la variable G. Se debe introducir un entero.");
			this.imp.close();
		}
	}

	
	/**
	 * Genera el panel de selecci�n de algoritmo, inyecci�n o extracci�n de datos.
	 */
	public void getInyExtPanel(){
		GenericDialog d = new GenericDialog("Seleccione qu� proceso quiere realizar", IJ.getInstance());
		String [] labels = {"Inyecci�n de datos", "Extracci�n de datos"};
		d.addChoice("Procesos:", labels, labels[0]);
		d.showDialog();
		if(d.wasCanceled()) 
			this.imp.close();
		this.choice = d.getNextChoice();
	}
	
	
	/**
	 * Genera el panel para introducir los valores necesarios para la extracci�n de datos.
	 */
	public void getVariablePanelExtraccion(){
		
		GenericDialog d = new GenericDialog("Indique los valores de las siguientes variables", IJ.getInstance());
		d.addNumericField("N�mero de filas de la matriz M:", this.m, 0, 3,"m");
		d.addNumericField("N�mero de columnas de la matriz M:",this.n, 0, 3, "n");
		d.addNumericField("Constante T:", this.t, 0, 3, "T");
		d.addNumericField("Constante G:", this.g, 0, 3, "G");
		d.addNumericField("Constante delta:", this.g, 0, 3, "G");
		d.addNumericField("Constante N0:", this.g, 0, 3, "n�mero de ceros");
		d.addNumericField("Constante N1:", this.g, 0, 3, "n�mero de unos");
		d.showDialog();
		if(d.wasCanceled()) 
			this.imp.close();
		this.m = d.getNextNumber();
		if(d.invalidNumber()) {
			IJ.showMessage("Error", "Valor incorrecto de la variable n. Se debe introducir un entero.");
			this.imp.close();
		}
		this.n = d.getNextNumber();
		if(d.invalidNumber()) {
			IJ.showMessage("Error", "Valor incorrecto de la variable m. Se debe introducir un entero.");
			this.imp.close();
		}
		this.t = d.getNextNumber();
		if(d.invalidNumber()) {
			IJ.showMessage("Error", "Valor incorrecto de la variable T. Se debe introducir un entero.");
			this.imp.close();
		}
		this.g = d.getNextNumber();
		if(d.invalidNumber()) {
			IJ.showMessage("Error", "Valor incorrecto de la variable G. Se debe introducir un entero.");
			this.imp.close();
		}
		this.delta = d.getNextNumber();
		if(d.invalidNumber()) {
			IJ.showMessage("Error", "Valor incorrecto de la variable delta. Se debe introducir un entero.");
			this.imp.close();
		}
		this.n0 = d.getNextNumber();
		if(d.invalidNumber()) {
			IJ.showMessage("Error", "Valor incorrecto de la variable N0. Se debe introducir un entero.");
			this.imp.close();
		}
		this.n1 = d.getNextNumber();
		if(d.invalidNumber()) {
			IJ.showMessage("Error", "Valor incorrecto de la variable N1. Se debe introducir un entero.");
			this.imp.close();
		}
	}
	
	public void inyeccion(){
		
		//Instancias para validar e inyectar informaci�n en la imagen
		EmbeddingAlgorithmG8Impl emb = new EmbeddingAlgorithmG8Impl();
		EmbeddedValidationG8Impl val = new EmbeddedValidationG8Impl();
		byte [] data;
		
		
		// Panel para cargar un archivo que inyectar a la imagen en pantalla.
		 try {
			 data = val.readFile();
		 }catch (IOException e) {
			 IJ.write("Lo siento, el fichero que quieres inyectar no se puede abrir." +
			 		"Int�ntalo con otro archivo.");
			 data = null;
			 return;
		 }
		 
		// Panel para obtener los valores de m, n, g y t.
		 String texto = new String(data);
		IJ.showMessage("La informaci�n que se va a inyectar es:\n "+ texto);
		 
		IJ.showMessage("Ya tenemos los datos que vamos a inyectar. Ahora se le pedir� que introduzca los valores \n" +
				" de unas varibles que el altgoritmo necesita.");
		 getVariablePanel();
		//calculo los betas para generar el histograma
		int beta1 = val.getBeta1((int) g, (int) t, (int) m, (int) n);
		int beta2 = val.getBeta2((int) g, (int) t, (int) m, (int) n);
		
		int histogramType = val.getHistogramType(this.imp, beta1, beta2);
		AlphasImage aimg = val.getAlphasImage(imp, (int)m, (int)n, (int)delta);
		boolean tGreaterAlphaMax = (t > aimg.getAlphaMax());
		this.imp = val.reescaleHistogram(imp, histogramType, beta1, beta2, tGreaterAlphaMax);
		int delta = val.getDelta(histogramType);
		
		IJ.showMessage("Ya tenemos calculado todos los par�metros necesarios para realizar la inyecci�n. \n" +
				"A continuaci�n se mostrar� en pantalla la stego imagen y las distribuciones de alpha, antes y \n despu�s del proceso" +
				"de inyecci�n.");
		ImagePlus res = emb.embedBits(this.imp, data, (int) t, (int) g, (int) m, (int) n, beta1, beta2, delta);
		res.show();
		
		IJ.showMessage("Los datos se han terminado de inyectar.\n" +
				"La inyecci�n se ha realizado con un valor de delta igual a " + delta +", \n" +
				 ", con un n�mero de ceros igual a "+ emb.getN0() + "\n y un n�mero de unos igual a" + emb.getN1()
				+ "Este valor se le pedir� cuando quiera extraer la informaci�n de la imagen \n " +
				   "resultante en el proceso de inyecci�n");
	}
	
	/**
	 * Realiza la inyecci�n de datos en una imagen.
	 */
	public void extraccion(){
		ExtractionAlgorithm ext = new ExtractionAlgorithmG8Impl();
		getVariablePanelExtraccion();
		HidingResult hres = new HidingResult();
		hres = ext.extractBits(this.imp, (int) m, (int) n, (int) t, (int) g, (int) delta, (int) n0, (int) n1);
		String texto = new String(hres.getData());
		IJ.showMessage("El texto que se ha obtenido de la extracci�n es: \n" + texto);
		ImagePlus res = hres.getImg();
		res.show();
		
	}
	
	/**
	 * Panel para configurar las acciones del plugin.
	 */
	public void configurationPanel(){
		
		GenericDialog d = new GenericDialog("Configuraci�n del proceso de inyecci�n y extracci�n.", IJ.getInstance());
		String [] labels = {RobustHiding_PID.G8, RobustHiding_PID.RGB};
		d.addChoice("Tipo de imagen", labels, labels[0]);
		if(this.choice == RobustHiding_PID.INYECCION){
			d.addCheckbox(RobustHiding_PID.INYECCIONYEXTRACCION, false);
			d.addCheckbox(RobustHiding_PID.ESTADISTICAS, false);
		}
		d.showDialog();
		if(d.wasCanceled()) 
			this.imp.close();
		this.imageChoice = d.getNextChoice();
		if(this.choice == RobustHiding_PID.INYECCION){
			this.extraccion = d.getNextBoolean();
			this.estadisticas = d.getNextBoolean();
		}
	}
	
	/**
	 * Genera la documentaci�n estad�stica a partir de la inyecci�n y la extracci�n.
	 */
	public void showEstadisticas(){
		IJ.write("Se muestran las estad�sticas");
	}

}
