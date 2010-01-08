package es.us.pid.grupo14.impl;

import ij.ImagePlus;
import ij.io.OpenDialog;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

import java.io.IOException;
import java.io.RandomAccessFile;

import es.us.pid.grupo14.AlphasImage;
import es.us.pid.grupo14.EmbeddedValidation;
import es.us.pid.grupo14.HidingResult;
import es.us.pid.grupo14.RobustHidingUtils;

public class EmbeddedValidationC24Impl implements EmbeddedValidation {
	
	private int selectedChannel;
	
	private int channelMax;
	
	public EmbeddedValidationC24Impl() {
		super();
		//por defecto el canal Azul ya que es menos sensible al ojo
		selectedChannel = 2;
		channelMax = 0;
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
		int[] pixel = new int[3];
		ImageProcessor ip = img.getProcessor();
		int min = Integer.MAX_VALUE , max = Integer.MIN_VALUE,aux;

		
		for (int i = 0; i < img.getHeight(); i++) {
			for (int j = 0; j < img.getWidth(); j++) {
				pixel = ip.getPixel(j, i, pixel);
				aux = pixel[selectedChannel];
				
				if (aux < min) {
					min = aux;
				}
				if (aux > max) {
					max = aux;
				}
			}
		}
		
		int bitDepth = img.getBitDepth()/3;
		int maxLevel = (int)Math.pow(2, bitDepth) - 1;
		int gap1 = min, gap2 = maxLevel - max;
		if (beta1 <= gap1){
			//tipos A y C
			if (beta1 <= gap2){
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
			if (beta1 <= gap2){
				//tipo B
				res = EmbeddedValidation.HISTOGRAM_TYPE_B;
			}
			else{
				//tipo D
				res = EmbeddedValidation.HISTOGRAM_TYPE_D;
			}
		}
		channelMax = max;
		return res;
	}

	@Override
	public int[][] getMatrixM(int m, int n) {
//		int[][] res = new int[m][n];
//		for (int i = 0; i < m; i++){
//			for (int j = 0; j < n; j++){
//				if ( (i % 2) == (j % 2) ){
//					res[i][j] = 1;
//				}
//				else{
//					res[i][j] = -1;
//				}
//				
//			}
//		}
//		return res;
		return RobustHidingUtils.getMatrixM(m, n);
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
		if (type == EmbeddedValidation.HISTOGRAM_TYPE_D) {
			// ImageStatistics is = img.getStatistics();
			int bitDepth = 8;
			int maxLevel = (int) Math.pow(2, bitDepth) - 1;
			int newMax;
			if (tGreaterAlphaMax){
				//en este caso, nunca se sumara beta1
				newMax = maxLevel - beta2;
			}
			else{
				newMax = maxLevel - beta1;
			}
			ImageProcessor ip = img.getProcessor();
			int w = img.getWidth(), h = img.getHeight();
			for (int i = 0; i < h; i++){
				for (int j = 0; j < w; j++){
					int[] rgb = ip.getPixel(j, i, null);
					if (rgb[selectedChannel] > newMax){
						rgb[selectedChannel] = newMax;			
						ip.putPixel(j, i, rgb);
					}
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
		return RobustHidingUtils.getBitErrorRate(originalData, recoveredData, size);
	}

	@Override
	public double getPSNR(ImagePlus original, ImagePlus stego) {
		// TODO testear este metodo
		if ((original.getWidth() == stego.getWidth()) && 
				(original.getHeight() == stego.getHeight())){
			double sum = 0;
			int h = original.getHeight(), w = original.getWidth();
			ImageProcessor originalIp = original.getProcessor();
			ImageProcessor stegoIp = stego.getProcessor();
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					int[] originalRGB = originalIp.getPixel(j, i, null);  
					int[] stegoRGB = stegoIp.getPixel(j, i, null);
					for (int k = 0; k < 3; k++){
						double aux = originalRGB[k] - stegoRGB[k];
						double aux2 = Math.pow(aux, 2);
						sum = sum + aux2;
					}		
				}
			}
			//al ser RGB
			sum = sum/3;
			int bitDepth = original.getBitDepth();
			double maxValue = Math.pow(2, bitDepth) - 1;
			double preRes = (maxValue * maxValue * w * h) / sum;
			double res = 10 * Math.log10(preRes);
			return res;
		}
		else{
			return 0;
		}
	}

	@Override
	public AlphasImage getAlphasImage(ImagePlus img, int m, int n, int delta) {
		// TODO hay que testearla
		int w = img.getWidth(), h = img.getHeight();
		int[][] matrixM = getMatrixM(m, n);
		
		
		int fi = 0, col = 0;
		int d1 = h/m, d2 = w/n;
		int[][] alphas = new int[d1][d2];
		int alphaMax = 0;
		ImageProcessor ip = img.getProcessor();
		
		for (int i = 0; i <= (h - m); i = i + m) {
			for (int j = 0; j <= (w - n); j = j + n) {
				int alpha = getAlpha(matrixM, ip, i, j, delta);
				alphas[fi][col] = alpha;
				if (Math.abs(alpha) > alphaMax){
					alphaMax = alpha;
				}
				col++;
			}
			col = 0;
			fi++;
		}
		AlphasImage res = new AlphasImage();
		res.setAlphaMax(alphaMax);
		res.setAlphas(alphas);
		return res;
	}

	public int getAlpha(int[][] matrixM, ImageProcessor image, int i, int j, int delta) {
		// funcion validada
		int alpha = 0;
		int aLimit = i + matrixM.length, bLimit = j + matrixM[0].length;
		int c = 0, d = 0;
		int[] value = new int[3];
		for (int a = i; a < aLimit; a++) {
			d = 0;
			for (int b = j; b < bLimit; b++) {
				value = image.getPixel(b, a, value);
				alpha = alpha + (delta * matrixM[c][d] * value[selectedChannel]);
				d++;
			}
			c++;
		}
		return alpha;
	}
	
}
