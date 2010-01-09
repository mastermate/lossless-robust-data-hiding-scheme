package pid;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.io.SaveDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import es.us.pid.grupo14.AlphasImage;
import es.us.pid.grupo14.EmbeddedValidation;
import es.us.pid.grupo14.EmbeddingAlgorithm;
import es.us.pid.grupo14.ExtractionAlgorithm;
import es.us.pid.grupo14.HidingResult;
import es.us.pid.grupo14.impl.EmbeddingAlgorithmG8Impl;
import es.us.pid.grupo14.impl.EmbeddedValidationG8Impl;
import es.us.pid.grupo14.impl.ExtractionAlgorithmG8Impl;
import es.us.pid.grupo14.impl.EmbeddedValidationC24Impl;
import es.us.pid.grupo14.impl.EmbeddingAlgorithmC24Impl;
import es.us.pid.grupo14.impl.ExtractionAlgorithmC24Impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Robust_Hiding implements PlugInFilter {

	private ImagePlus imp;
	private ImagePlus originalCopy;
	private ImagePlus res_inyeccion;
	private ImagePlus res_extraccion;
	private double m=8, n=8;
	private double t=128, g=64;
	private double n0=0, n1=0;
	private int selectedChanel = 2;
	private double delta=1;
	private String choice;
	private String imageChoice;
	private String texto;
	private boolean estadisticas;
	private boolean extraccion;
	public static final String INYECCION ="Inyecci�n de datos";
	public static final String EXTRACCION ="Extracci�n de datos";
	public static final String RGB = "Imagen RGB";
	public static final String G8 = "Imagen escala de grises 8 bits";
	public static final String INYECCIONYEXTRACCION = "Extraer los datos despu�s de inyectar";
	public static final String ESTADISTICAS = "Mostrar estad�sticas";
	public static final String C1 = "1";
	public static final String C2= "2";
	public static final String C3= "3";
		
	@Override
	public void run(ImageProcessor ip) {
		 
		//copia inalterada de la imagen original
		int w = imp.getWidth(), h = imp.getHeight();
		ImageProcessor procAux = imp.getProcessor().createProcessor(w, h);
		originalCopy = new ImagePlus("stego-image", procAux);
		originalCopy.getProcessor().insert(imp.getProcessor(), 0, 0);
		
		// Muestro el panel para seleccionar el proceso a realizar
		this.getInyExtPanel();
		this.configurationPanel();
		if(this.RGB == this.imageChoice){
		 	this.seleccionCanal();
		}
		if( this.choice == Robust_Hiding.INYECCION){
			this.res_inyeccion = this.inyeccion();
			if(this.extraccion){
				try {
					this.extraccion();
				} catch (IOException e) {
					IJ.showMessage("No se ha podido guardar los datos extra�dos.");
				}
			}
		}else{	
			try {
				this.extraccion();
			} catch (IOException e) {
				IJ.showMessage("No se ha podido guardar los datos extra�dos.");
			}
		}
		 
	}
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return PlugInFilter.DOES_8G + PlugInFilter.DOES_RGB;
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
		d.addNumericField("Constante delta:", this.delta, 0, 3, "delta");
		d.addNumericField("Constante N0:", this.n0, 0, 3, "n�mero de ceros");
		d.addNumericField("Constante N1:", this.n1, 0, 3, "n�mero de unos");
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
	
	
	public ImagePlus inyeccion(){
		
		EmbeddingAlgorithm emb;
		EmbeddedValidation val;
		 
		//Instancias para validar e inyectar informaci�n en la imagen
		if(this.imageChoice == this.G8){
			 emb = new EmbeddingAlgorithmG8Impl();
			 val = new EmbeddedValidationG8Impl();
		}else{
			 emb = new EmbeddingAlgorithmC24Impl();
			 val = new EmbeddedValidationC24Impl();
			 // Selecciono el canal en emb.
			 EmbeddingAlgorithmC24Impl embeded = (EmbeddingAlgorithmC24Impl) emb;
			 embeded.setSelectedChannel(this.selectedChanel);
		}
		
		
		byte [] data;
		

		// Panel para cargar un archivo que inyectar a la imagen en pantalla.
		 try {
			 data = val.readFile();
		 }catch (IOException e) {
			 IJ.write("Lo siento, el fichero que quieres inyectar no se puede abrir. " +
			 		"Int�ntalo con otro archivo.");
			 data = null;
			 return null;
		 }
		 
		// Panel para obtener los valores de m, n, g y t.
		texto = new String(data);
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
		
		IJ.showMessage("Ya tenemos calculados todos los par�metros necesarios para realizar la inyecci�n. \n" +
				"A continuaci�n se mostrar� en pantalla la stego imagen y las distribuciones de alpha, antes y \n" +
				"despu�s del proceso de inyecci�n.");
		ImagePlus res = emb.embedBits(this.imp, data, (int) t, (int) g, (int) m, (int) n, beta1, beta2, delta);
		res.show();
		
		// Almaceno el delta, el n�mero de ceros (n0) y el n�mero de unos (n1)
		this.delta = delta;
		this.n0 = emb.getN0();
		this.n1 = emb.getN1();
		// Muesro en pantalla los resultados obtenidos.
		IJ.showMessage("Los datos se han terminado de inyectar.\n" +
				"La inyecci�n se ha realizado con un valor de delta igual a " + delta +", \n" +
				 "con un n�mero de ceros igual a "+ emb.getN0() + " y un n�mero de unos igual a" + emb.getN1()+".\n" +
				 		"Este valor se le pedir� cuando quiera extraer la informaci�n de la imagen \n " +
				   "resultante en el proceso de inyecci�n.");
		return res;
	}
	
	/**
	 * Realiza la inyecci�n de datos en una imagen.
	 * @throws IOException 
	 */
	public void extraccion() throws IOException{
		ExtractionAlgorithm ext;
		EmbeddedValidation val;
		if(this.imageChoice == this.G8){
			 ext = new ExtractionAlgorithmG8Impl();
			 val = new EmbeddedValidationG8Impl();
		}else{
			 ext = new ExtractionAlgorithmC24Impl();
			 val = new EmbeddedValidationC24Impl();
		}
		getVariablePanelExtraccion();
		HidingResult hres = new HidingResult();
		if(this.extraccion){
			hres = ext.extractBits(this.res_inyeccion, (int) m, (int) n, (int) t, (int) g, (int) delta, (int) n0, (int) n1);
		}else{
			hres = ext.extractBits(this.imp, (int) m, (int) n, (int) t, (int) g, (int) delta, (int) n0, (int) n1);
		}
		
		byte [] text_data = hres.getData();
		String texto = new String(text_data);
		IJ.showMessage(texto);
		getPanelSalvarArchivo(texto);
		IJ.showMessage("El texto se ha obtenido correctamente y ha sido guardado.");
		ImagePlus res = hres.getImg();
		res.show();
		
		// C�lculo de valores estad�sticos.
		
		if(this.extraccion){
			double psnr1 = 0, psnr2 = 0;
			psnr1 = val.getPSNR(originalCopy, res_inyeccion);
			psnr2 = val.getPSNR(originalCopy, res);
			double bitErrorRate = val.getBitErrorRate(this.texto.getBytes(), text_data, text_data.length);
			if(this.estadisticas){
				this.showEstadisticas(psnr1, psnr2, bitErrorRate);
			}
		}
		
		
	}
	
	/**
	 * Panel para configurar las acciones del plugin.
	 */
	public void configurationPanel(){
		
		GenericDialog d = new GenericDialog("Configuraci�n del proceso de inyecci�n y extracci�n.", IJ.getInstance());
		String [] labels = {Robust_Hiding.G8, Robust_Hiding.RGB};
		d.addChoice("Tipo de imagen", labels, labels[0]);
		if(this.choice == Robust_Hiding.INYECCION){
			d.addCheckbox(Robust_Hiding.INYECCIONYEXTRACCION, false);
			d.addCheckbox(Robust_Hiding.ESTADISTICAS, false);
		}
		d.showDialog();
		if(d.wasCanceled()) 
			this.imp.close();
		this.imageChoice = d.getNextChoice();
		if(this.choice == Robust_Hiding.INYECCION){
			this.extraccion = d.getNextBoolean();
			this.estadisticas = d.getNextBoolean();
		}
	}
	
	/**
	 * Panel para seleccionar el canal.
	 */
	public void seleccionCanal(){
		GenericDialog d = new GenericDialog("Selecciona un canal.", IJ.getInstance());
		String [] labels = {Robust_Hiding.C1,Robust_Hiding.C2,Robust_Hiding.C3};
		String canal;
		d.addChoice("Canal", labels, labels[1]);
		d.showDialog();
		if(d.wasCanceled()) 
			this.imp.close();
		canal = d.getNextString();
		if(canal == Robust_Hiding.C1) {
			this.selectedChanel = 0;
		}else if(canal == Robust_Hiding.C2){
			this.selectedChanel = 1;
		}else{
			this.selectedChanel = 2;
		}	
	}
	
	public void getPanelSalvarArchivo(String texto) throws IOException{
		SaveDialog sd = new SaveDialog("Seleccione donde guardar el texto extra�do.", "texto extraido", ".txt");
		String directory = sd.getDirectory();
		String filename = sd.getFileName();
		if(filename == null){return;}
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(directory+filename));
			bw.write(texto);
			bw.close();
		}catch (IOException e) {
			IJ.showMessage("No se ha podido guardar los datos extraidos");
		}
	}
	
	/**
     * Genera la documentaci�n estad�stica a partir de la inyecci�n y la extracci�n.
     */
    public void showEstadisticas(double psnr1, double psnr2, double bitErrorRate){
            IJ.write("PSNR Original - Stego: " + psnr1);
            IJ.write("PSNR Original - Recovered: " + psnr2);
            IJ.write("Tase de error de bits: " + bitErrorRate);
    }



}
