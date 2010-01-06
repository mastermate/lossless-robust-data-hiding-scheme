package es.us.pid.grupo14.impl;

import ij.ImagePlus;
import ij.gui.PlotWindow;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import es.us.pid.grupo14.EmbeddingAlgorithm;

public class EmbeddingAlgorithmC24Impl implements EmbeddingAlgorithm {
	
	private int selectedChannel;
	// Arrays para guardar las frecuencias para el histograma
	private Hashtable<Integer,Integer> alphasBefore;
	private Hashtable<Integer,Integer> alphasAfter;
	
	public EmbeddingAlgorithmC24Impl() {
		super();
		selectedChannel = 2;
		alphasBefore = new Hashtable<Integer,Integer>();
		alphasAfter = new Hashtable<Integer,Integer>();
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
	public ImagePlus embedBits(ImagePlus img, byte[] bits, int g, int m, int n,
			int t, int beta1, int beta2, int delta) {
		int w = img.getWidth(), h = img.getHeight();
		ImageProcessor ip = new ColorProcessor(w, h);
		ImagePlus res = new ImagePlus("stego-image", ip);
		res.getProcessor().insert(img.getProcessor(), 0, 0);
		int bitCount = 0;
		int dataSize = bits.length * 8;
		int[][] matrixM = getMatrixM(m, n);

		// mientras queden bloques
		for (int i = 0; i < (h - m); i = i + m) {
			for (int j = 0; j < (w - n); j = j + n) {
				int alpha = getAlpha(matrixM, img.getProcessor(), i, j, delta);

				if (!alphasBefore.containsKey(alpha)) {
					alphasBefore.put(alpha, 1);
				}
				// voy guardando la frecuencia acumulada
				alphasBefore.put(alpha, alphasBefore.get(alpha) + 1);

				res = createGap(res, img.getProcessor(), beta1, alpha, t, delta, i, j, m, n);

				// si el alpha esta en rango y quedan aun datos por insertar
				if (isInRange(alpha, t) && (bitCount < dataSize)) {
					res = insertBit(res, img.getProcessor(), bits, bitCount, alpha, beta2,
							t, delta, i, j, m, n);
					bitCount++;
				}
			}
			
			/**
			 * TODO: Esto debería de estar en una fución a parte. Calculo la
			 * frecuencia de los distintos valores de alpha una vez se ha
			 * realizado la inyección
			 **/
			for (int i2 = 0; i2 < (h - m); i2 = i2 + m) {
				for (int j2 = 0; j2 < (w - n); j2 = j2 + n) {
					int alphaStego = getAlpha(matrixM, res.getProcessor(), i2, j2, delta);
					// voy guardando la frecuencia acumulada
					if (!alphasAfter.containsKey(alphaStego)) {
						alphasAfter.put(alphaStego, 1);
					}
					alphasAfter.put(alphaStego, alphasAfter.get(alphaStego) + 1);
				}
			}

			// Muesto los dos histogramas en pantalla nada más terminar con la
			// inyección
			

		}
		getAlphaDist(alphasBefore, "Distribucion del canal "+selectedChannel+" antes de inyectar los datos");
		getAlphaDist(alphasAfter, "Distribucion del canal "+selectedChannel+" despues de inyectar los datos");
		return res;
	}
	
	public ImagePlus insertBit(ImagePlus res, ImageProcessor originalImage, byte[] bits,
			int bitCont, int alpha, int beta2, int t, int delta, int i, int j,
			int m, int n) {

		// funciona correctamente
		int byteIndex = bitCont / 8, byteMod = bitCont % 8;
		boolean bitValue = extractBit(bits[byteIndex], byteMod);
		int aLimit = i + m, bLimit = j + n;
		ImageProcessor ip = res.getProcessor();
		int[] pixel;
		int[] value = new int[3];

		if (bitValue) {
			if (alpha >= 0) {
				for (int a = i; a < aLimit; a++) {
					for (int b = j; b < bLimit; b++) {
						if ((a % 2) == (b % 2)) {
							value = originalImage.getPixel(a, b, value);
							value[selectedChannel] += delta * beta2;
							pixel = value;
						} else {
							pixel = originalImage.getPixel(a, b, value);
						}
						ip.putPixel(a, b, pixel);
					}
				}
			} else {
				for (int a = i; a < aLimit; a++) {
					for (int b = j; b < bLimit; b++) {
						if ((a % 2) != (b % 2)) {
							value = originalImage.getPixel(a, b, value);
							value[selectedChannel] += delta * beta2;
							pixel = value;
						} else {
							pixel = originalImage.getPixel(a, b, value);
						}
						ip.putPixel(a, b, pixel);
					}
				}
			}

		} else {
			for (int a = i; a < aLimit; a++) {
				for (int b = j; b < bLimit; b++) {
					ip.putPixel(a, b, originalImage.getPixel(a, b, value));
				}
			}
		}
		return res;
	}

	public ImagePlus createGap(ImagePlus res, ImageProcessor originalImage, int alpha,
			int beta1, int t, int delta, int i, int j, int m, int n) {

		// metodo validado con tests
		int aLimit = i + m, bLimit = j + n;
		ImageProcessor ip = res.getProcessor();
		int[] pixel;
		int[] value = new int[3];
		
		if (alpha > t) {
			for (int a = i; a < aLimit; a++) {
				for (int b = j; b < bLimit; b++) {
					if ((a % 2) == (b % 2)) {
						value = originalImage.getPixel(a, b, value);
						value[selectedChannel] += delta * beta1;
						pixel = value;
					} else {
						pixel = originalImage.getPixel(a, b, value);
					}
					ip.putPixel(a, b, value);
				}
			}
		} else if (alpha < -t) {
			for (int a = i; a < aLimit; a++) {
				for (int b = j; b < bLimit; b++) {
					if ((a % 2) != (b % 2)) {
						value = originalImage.getPixel(a, b, value);
						value[selectedChannel] += delta * beta1;
						pixel = value;
					} else {
						pixel = originalImage.getPixel(a, b, value);
					}
					ip.putPixel(a, b, value);
				}
			}
		} else {
			for (int a = i; a < aLimit; a++) {
				for (int b = j; b < bLimit; b++) {
					ip.putPixel(a, b, originalImage.getPixel(a, b, value));
				}
			}
		}
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
				value = image.getPixel(a, b, value);
				alpha = alpha + (delta * matrixM[c][d] * value[selectedChannel]);
				d++;
			}
			c++;
		}
		return alpha;
	}

	public boolean isInRange(int alpha, int t) {
		if ((alpha <= t) && (alpha >= -t)) {
			return true;
		} else {
			return false;
		}
	}

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

	public boolean extractBit(byte b, int index) {
		// funcion validada
		int aux = 0;
		switch (index) {
		case 0:
			aux = (b & 0x01);
			break;
		case 1:
			aux = (b & 0x02) >> 1;
			break;
		case 2:
			aux = (b & 0x04) >> 2;
			break;
		case 3:
			aux = (b & 0x08) >> 3;
			break;
		case 4:
			aux = (b & 0x10) >> 4;
			break;
		case 5:
			aux = (b & 0x20) >> 5;
			break;
		case 6:
			aux = (b & 0x40) >> 6;
			break;
		case 7:
			aux = (b & 0x80) >> 7;
			break;
		default:
			break;
		}
		if (aux == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * getAlphaDist
	 * 
	 * @param alphas Valores que toma alpha en la distribucion y el
	 *        numero de veces que aparece.
	 * @param title Titulo de la ventana con el histograma.
	 **/
	public void getAlphaDist(Hashtable<Integer,Integer> alphas, String title) {
				
		// arrays que cargan los datos en la distribuci�n.
		float[] x = new float[alphas.size()];
		float[] y = new float[alphas.size()];
		int pos = 0;
		float maxFreq = 0, minFreq = Float.MAX_VALUE;

		// ordeno la hashtable
		Vector<Integer> vector = new Vector<Integer>(alphas.keySet());
		Collections.sort(vector);

		// a�ado los elementos de la hashtable al x e y
		for (Enumeration<Integer> e = vector.elements(); e.hasMoreElements();) {
			int integerKey = e.nextElement();
			float key = (float) integerKey;
			float value = (float) alphas.get(integerKey);
			x[pos] = key;
			y[pos] = value;
			if (value > maxFreq) {
				maxFreq = value;
			}
			if (value < minFreq) {
				minFreq = value;
			}
			pos++;
		}

		float Xmax = (float) vector.lastElement() + 1, Xmin = (float) vector
				.firstElement() - 1;
		float Ymax = maxFreq + 1, Ymin = minFreq - 1;
		PlotWindow plot = new PlotWindow(title, "Valores de alpha",
				"N�mero de alphas", x, y);
		plot.addPoints(x, y, PlotWindow.CIRCLE);
		plot.setLimits(Xmin, Xmax, Ymin, Ymax);
		plot.setColor(Color.blue);
		plot.draw();

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
	
	public int shiftIntToChannel(int value, int channel){
		int res=0;
		
		switch (channel) {
		case 0:
			//red
			res = value << 16;
			break;
			
		case 1:
			//green
			res = value << 8;
			break;
			
		case 2:
			//blue
			res = value;
			break;
			
		default:
			break;
		}
		return res;
	}

	@Override
	public int getN0() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getN1() {
		// TODO Auto-generated method stub
		return 0;
	}
}
