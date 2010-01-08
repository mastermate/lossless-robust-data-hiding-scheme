package es.us.pid.grupo14.impl;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import es.us.pid.grupo14.AlphasImage;
import es.us.pid.grupo14.EmbeddedValidation;
import ij.io.OpenDialog;

import java.io.IOException;
import java.io.RandomAccessFile;

public class EmbeddedValidationG8Impl implements EmbeddedValidation {

	// TODO probar todos estos metodos con imagenes y asegurarse de que
	// funcionan correctamente

	@Override
	public int getBeta1(int g, int t, int m, int n) {
		double r1 = ((2 * g) + t) * 2 / (m * n);
		double r2 = Math.ceil(r1);
		int res = (int) r2;
		return res;
	}

	@Override
	public int getBeta2(int g, int t, int m, int n) {
		double r1 = (t + g) * 2 / (m * n);
		double r2 = Math.ceil(r1);
		int res = (int) r2;
		return res;
	}

	@Override
	public int getDelta(int histogramType) {
		// solo sera -1 para el tipo C
		int res;
		if (histogramType == EmbeddedValidation.HISTOGRAM_TYPE_C) {
			res = -1;
		} else {
			res = 1;
		}
		return res;
	}

	/*
	 * Depende del tipo de imagen, este debe quedarse aqui
	 * (non-Javadoc)
	 * @see es.us.pid.grupo14.EmbeddedValidation#getHistogramType(ij.ImagePlus, int, int)
	 */
	@Override
	public int getHistogramType(ImagePlus img, int beta1, int beta2) {
		// segun la formula, beta1 siempre sera mayor que beta2
		int res;
		ImageStatistics is = img.getStatistics();
		int min = (int) is.min, max = (int) is.max;
		int bitDepth = img.getBitDepth();
		int maxLevel = (int) Math.pow(2, bitDepth) - 1;
		int gap1 = min, gap2 = maxLevel - max;
		if (beta1 <= gap1) {
			// tipos A y C
			if (beta1 <= gap2) {
				// tipo A
				res = EmbeddedValidation.HISTOGRAM_TYPE_A;
			} else {
				// tipo C
				res = EmbeddedValidation.HISTOGRAM_TYPE_C;
			}
		} else {
			// tipos B y D
			if (beta1 <= gap2) {
				// tipo B
				res = EmbeddedValidation.HISTOGRAM_TYPE_B;
			} else {
				// tipo D
				res = EmbeddedValidation.HISTOGRAM_TYPE_D;
			}
		}
		return res;
	}

	@Override
	public int[][] getMatrixM(int m, int n) {
		int[][] res = new int[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if ((i % 2) == (j % 2)) {
					res[i][j] = 1;
				} else {
					res[i][j] = -1;
				}

			}
		}
		return res;
	}

	@Override
	public int getNumberOfBlocks(ImagePlus img, int m, int n) {
		double h = img.getHeight(), w = img.getWidth(), m1 = (double) m, n1 = (double) n;
		double r1 = Math.floor(h / m1);
		double r2 = Math.floor(w / n1);
		double r3 = r1 * r2;
		int res = (int) r3;
		return res;
	}

	@Override
	public boolean isValidSize(ImagePlus img, byte[] data) {
		// TODO depende del numero de bloques utiles (los que quedan en medio)

		// si T < alphaMax, no es posible calcular la capacidad sin calcular los
		// alphas
		return false;
	} 
	
	
	/*
	 * Depende del tipo de imagen, este debe quedarse aqui
	 * (non-Javadoc)
	 * @see es.us.pid.grupo14.EmbeddedValidation#reescaleHistogram(ij.ImagePlus, int, int, int, boolean)
	 */
	//@Override
	public ImagePlus reescaleHistogram(ImagePlus img, int type, int beta1,
			int beta2, boolean tGreaterAlphaMax) {
		ImagePlus res = null;
		if (type == EmbeddedValidation.HISTOGRAM_TYPE_D) {
			// ImageStatistics is = img.getStatistics();
			int bitDepth = img.getBitDepth();
			int maxLevel = (int) Math.pow(2, bitDepth) - 1;
			int newMax;
			if (tGreaterAlphaMax){
				//en este caso, nunca se sumara beta1
				newMax = maxLevel - beta2;
			}
			else{
				newMax = maxLevel - beta1;
			}
			//es correcto el casting, los numeros utilizan complemento a 2
//			byte newMaxByte = (byte)newMax;
			ImageProcessor ip = img.getProcessor();
			// imagenes en escala de grises de 8 bits
//			byte[] pixels = (byte[]) ip.getPixels();
			int w = img.getWidth(), h = img.getHeight();
			int[][] pixels = ip.getIntArray();
			for (int i = 0; i < h; i++){
				for (int j = 0; j < w; j++){
					if (pixels[j][i] > newMax){
						ip.putPixel(j, i, newMax);
					}
				}
			}
//			for (int i = 0; i < pixels.length; i++) {
//				// truncamos al mayor valor posible
//				if (pixels[i] > newMax) {
//					pixels[i] = newMaxByte;
//				}
//			}
		}
		res = img;
		ImageStatistics is = img.getStatistics();
//		int min = (int) is.min, max = (int) is.max;
		return res;
	}
	
	
	@Override
	public byte[] readFile() throws IOException {
		// Esta funcion necesita lanzar la excepcion IOException por si no se
		// puede acceder al archivo.
		String arg = "";
		String filename = arg;
		String directory = "";

		RandomAccessFile f;
		byte[] res;
		byte[] embedfile;

		// Muestro el dialogo para capturar la ruta del archivo que vamos a
		// inyectar
		OpenDialog od = new OpenDialog("Selecciona el archivo a inyectar", arg);
		filename = od.getFileName();
		if (filename == null) {
			return null;
		}
		directory = od.getDirectory();

		// Accedo al archivo en modo lectura
		f = new RandomAccessFile(directory + filename, "r");
		embedfile = new byte[(int) f.length()];

		// Cargo el archivo en un array de bytes
		f.readFully(embedfile);

		res = embedfile;
		return res;
	}

	@Override
	public double getBitErrorRate(byte[] originalData, byte[] recoveredData, int size) {
		//suponemos que tienen el mismo tamaño
		double res;
		double totalBits = size*8, badBits = 0;
		
		for (int i = 0; i < size; i++){
			//con una XOR sacamos ponemos a 1 las posiciones que difieren
			byte aux = (byte)(originalData[i] ^ recoveredData[i]);
			//y una vez hecho esto, sacamos los bits a 1
			badBits = badBits + getBitsToOne(aux);
		}
		res = badBits/totalBits;
		return res;
	}
	
	public int getBitsToOne(byte b){
		int res = 0;
		byte mask = 0x01;
		for(int i = 0; i < 8; i++){
			byte aux = (byte)(b & mask);
			if (aux != 0){
				res++;
			}
			mask = (byte) (mask << 1);
		}
		return res;
	}
	
	public static void main(String[] args){
//		EmbeddedValidationG8Impl obj = new EmbeddedValidationG8Impl();
//		byte b = (byte) 0xf3;
//		int aux = obj.getBitsToOne(b);
//		System.out.println(aux);
		byte b = (byte) 0xff;
		System.out.println(b);
	}
	

	@Override
	public double getPSNR(ImagePlus original, ImagePlus stego) {
		
		if ((original.getWidth() == stego.getWidth()) && 
				(original.getHeight() == stego.getHeight())){
			double sum = 0;
			int[][] m1 = original.getProcessor().getIntArray();
			int[][] m2 = stego.getProcessor().getIntArray();
			for (int i = 0; i < m1.length; i++) {
				for (int j = 0; j < m1[i].length; j++) {
					double aux = (m1[i][j] - m2[i][j]);
					double aux2 = Math.pow(aux, 2);
					sum = sum + aux2;
				}
			}
			int bitDepth = original.getBitDepth();
			double maxValue = Math.pow(2, bitDepth) - 1, w = original.getWidth(), h = original
					.getHeight();
			double preRes = (maxValue * maxValue * w * h) / sum;
			double res = 10 * Math.log10(preRes);
			return res;
		}
		else{
			return 0;
		}
		
	}

	
	public AlphasImage getAlphasImage(ImagePlus img, int m, int n, int delta){
		
		int[][] pixels = img.getProcessor().getIntArray();
		int w = img.getWidth(), h = img.getHeight();
		int[][] matrixM = getMatrixM(m, n);
		
		
		int fi = 0, col = 0;
		int d1 = h/m, d2 = w/n;
		int[][] alphas = new int[d1][d2];
		int alphaMax = 0;
		
		for (int i = 0; i <= (h - m); i = i + m) {
			for (int j = 0; j <= (w - n); j = j + n) {
				int alpha = getAlpha(matrixM, pixels, i, j, delta);
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
	
	public int getAlpha(int[][] matrixM, int[][] pixels, int i, int j, int delta) {
		// funcion validada
		int alpha = 0;
		int aLimit = i + matrixM.length, bLimit = j + matrixM[0].length;
		int c = 0, d = 0;
		for (int a = i; a < aLimit; a++) {
			d = 0;
			for (int b = j; b < bLimit; b++) {
				alpha = alpha + (delta * matrixM[c][d] * pixels[b][a]);
				d++;
			}
			c++;
		}
		return alpha;
	}

//	@Override
//	public int[] getAlphaMax(ImagePlus img, int beta1, int beta2, int m, int n) {
//		// TODO Auto-generated method stub
//		return null;
//	}
}
