package es.us.pid.grupo14.impl;

import ij.ImagePlus;
import ij.io.OpenDialog;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

import java.io.IOException;
import java.io.RandomAccessFile;

import es.us.pid.grupo14.EmbeddedValidation;

public class EmbeddedValidationC24Impl implements EmbeddedValidation {
	
	private int selectedChannel;
	
	public EmbeddedValidationC24Impl() {
		super();
		//por defecto el canal Azul ya que es menos sensible al ojo
		selectedChannel = 2;
	}

	public int getSelectedChannel() {
		return selectedChannel;
	}

	public void setSelectedChannel(int selectedChannel) {
		if (selectedChannel >= 0 && selectedChannel < 3) {
			this.selectedChannel = selectedChannel;
		}
	}

	@Override
	public int getBeta1(int g, int t, int m, int n) {
		double r1 = ((2 * g) + t) * 2 / (m * n);
		double r2 = Math.ceil(r1);
		int res = (int)r2;
		return res;
	}

	@Override
	public int getBeta2(int g, int t, int m, int n) {
		double r1 = (t + g) * 2 / (m * n);
		double r2 = Math.ceil(r1);
		int res = (int)r2;
		return res;
	}

	@Override
	public int getDelta(int histogramType) {
		//solo sera -1 para el tipo C
		int res;
		if (histogramType == EmbeddedValidation.HISTOGRAM_TYPE_C){
			res = -1;
		}
		else{
			res = 1;
		}
		return res;
	}

	@Override
	public int getHistogramType(ImagePlus img, int beta1, int beta2) {
		//segun la formula, beta1 siempre sera mayor que beta2
		int res;
		int[] histogram = img.getProcessor().getHistogram();
		int min = Integer.MAX_VALUE , max = Integer.MIN_VALUE,aux;
		
		for (int i = 0; i < histogram.length; i++) {
			aux = channelValue(histogram[i],selectedChannel);
			if (aux < min) {
				min = aux;
			}
			if (aux > max) {
				max = aux;
			}
		}
		
		int bitDepth = img.getBitDepth()/4;
		int maxLevel = (int)Math.pow(2, bitDepth) - 1;
		int gap1 = min, gap2 = maxLevel - max;
		if (beta1 >= gap1){
			//tipos A y C
			if (beta1 >= gap2){
				//tipo A
				res = EmbeddedValidation.HISTOGRAM_TYPE_A;
			}
			else{
				//tipo C
				res = EmbeddedValidation.HISTOGRAM_TYPE_C;
			}
		}
		else{
			//tipos B y D
			if (beta1 >= gap2){
				//tipo B
				res = EmbeddedValidation.HISTOGRAM_TYPE_B;
			}
			else{
				//tipo D
				res = EmbeddedValidation.HISTOGRAM_TYPE_D;
			}
		}
		return res;
	}

	@Override
	public int[][] getMatrixM(int m, int n) {
		int[][] res = new int[m][n];
		for (int i = 0; i < m; i++){
			for (int j = 0; j < n; j++){
				if ( (i % 2) == (j % 2) ){
					res[i][j] = 1;
				}
				else{
					res[i][j] = -1;
				}
				
			}
		}
		return res;
	}

	@Override
	public int getNumberOfBlocks(ImagePlus img, int m, int n) {
		double h = img.getHeight(), w = img.getWidth(), m1 = (double)m, n1 = (double)n;	
		double r1 = Math.floor(h/m1);
		double r2 = Math.floor(w/n1);
		double r3 = r1*r2;
		int res = (int)r3;
		return res;
	}

	@Override
	public boolean isValidSize(ImagePlus img, byte[] data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte[] readFile() throws IOException {
		//Esta funcion necesita lanzar la excepcion IOException por si no se puede acceder al archivo.
	    String arg ="";
	  	String filename=arg;
	  	String directory ="";
	  	
	    RandomAccessFile f;
	  	byte [] res;
	  	byte [] embedfile;
	  	
	  	// Muestro el dialogo para capturar la ruta del archivo que vamos a inyectar
	    OpenDialog od = new OpenDialog("Selecciona el archivo a inyectar", arg);
	    filename = od.getFileName();
	    if(filename == null){
	      return null;
	    }
	    directory = od.getDirectory();
	    
	    // Accedo al archivo en modo lectura
	    f = new RandomAccessFile(directory+filename,"r");
	    embedfile = new byte[(int)f.length()];
	    
	    // Cargo el archivo en un array de bytes
	    f.readFully(embedfile);
	    
	    res = embedfile;
	    return res;
	}

	@Override
	public ImagePlus reescaleHistogram(ImagePlus img, int type, int beta1,
			int beta2, boolean tGreaterAlphaMax) {
		// TODO no está probada
		ImagePlus res = null;
		if (type == EmbeddedValidation.HISTOGRAM_TYPE_D){
//			ImageStatistics is = img.getStatistics();
			int bitDepth = img.getBitDepth();
			int maxLevel = (int)Math.pow(2, bitDepth) - 1;
			int newMax;
			if (tGreaterAlphaMax){
				//en este caso, nunca se sumara beta1
				newMax = maxLevel - beta2;
			}
			else{
				newMax = maxLevel - beta1;
			}
			ImageProcessor ip = img.getProcessor();
			//imagenes en escala de grises de 8 bits
			byte[] pixels = (byte[])ip.getPixels();
			for (int i = 0; i < pixels.length; i++){
				//truncamos al mayor valor posible
				if (pixels[i] > newMax){
					pixels[i] = (byte)newMax;
				}
			}
		}
		res = img;
		return res;
	}
	
	public int channelValue(int value, int channel){
		int res=0;
		
		switch (channel) {
		case 0:
			//red
			res = (value & 0xff0000) >> 16;
			break;
			
		case 1:
			//green
			res = (value & 0x00ff00) >> 8;
			break;
			
		case 2:
			//blue
			res = (value & 0x0000ff);
			break;
			
		default:
			break;
		}
		return res;
	}

	@Override
	public double getBitErrorRate(byte[] originalData, byte[] recoveredData,
			int size) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPSNR(ImagePlus original, ImagePlus stego) {
		// TODO Auto-generated method stub
		return 0;
	}
}
