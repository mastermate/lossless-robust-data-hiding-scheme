package es.us.pid.grupo14.impl;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.Blitter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import es.us.pid.grupo14.EmbeddingAlgorithm;

public class EmbeddingAlgorithmG8Impl implements EmbeddingAlgorithm {
  
  //Arrays para guardar las frecuencias para el histograma
	Hashtable alphasBefore = new Hashtable();
  Hashtable alphasAfter = new Hashtable();

	@Override
	public ImagePlus embedBits(ImagePlus img, byte[] bits, int t, int g, int m, int n,
			int beta1, int beta2, int delta) {
		
		int w = img.getWidth(), h = img.getHeight();
		ImageProcessor ip = new ByteProcessor(w, h);
		ImagePlus res = new ImagePlus("stego-image",ip);
		res.getProcessor().insert(img.getProcessor(), 0, 0);
		int bitCont = 0;
		int dataSize = bits.length * 8;
		int[][] matrixM = getMatrixM(m, n);
		
		// TODO cuidado con como me devuelve el array de enteros
		int[][] pixels = img.getProcessor().getIntArray();
		
		
		//mientras queden bloques
		for (int i = 0; i < h; i = i + m) {
			for (int j = 0; j < w; j = j + n) {
				int alpha = getAlpha(matrixM, pixels, i, j, delta);
				
				if (!alphasBefore.containsKey(alpha)){
  			  alphasBefore.set(alpha, 1);
  			}
				//voy guardando la frecuencia acumulada
				alphasBefore.put(alpha, alphasBefore.get(alpha) + 1);
				
				res = createGap(res, pixels, beta1, alpha, t, delta, i, j, m, n);

				//si el alpha esta en rango y quedan aun datos por insertar
				if (isInRange(alpha, t) && (bitCont < dataSize)) {
					res = insertBit(res, pixels, bits, bitCont, alpha, beta2, t,
							delta, i, j, m, n);
					bitCont++;
				}
			}
			
			
			/** TODO: Esto debería de estar en una fución a parte.
			 * Calculo la frecuencia de los distintos valores de alpha una
			 * vez se ha realizado la inyección
			 **/
			// TODO cuidado con como me devuelve el array de enteros
  		int[][] pixelsStego = res.getProcessor().getIntArray();
			//Mientras queden bloques (con la stego-imagen)
			for( int i = 0; i < h; i = i + m){
			  for (int j = 0; j < w; j = j + n) {
		      int alphaStego = getAlpha(matrixM, pixelsStego, i, j, delta);
  				//voy guardando la frecuencia acumulada
  				if (!alphasAfter.containsKey(alphaStego)){
  				  alphasAfter.set(alphaStego, 1);
  				}
  				alphasAfter.put(alphaStego, alphasAfter.get(alphaStego) + 1);
		    }
			}
			
			//Muesto los dos histogramas en pantalla nada más terminar con la inyección
			getAlphaDist(alphasBefore, "Distribución antes de inyectar los datos");
			getAlphaDist(alphasAfter, "Distribución después de inyectar los datos");
			
		}

		return res;
	}

	public ImagePlus insertBit(ImagePlus res, int[][] pixels, byte[] bits,
			int bitCont, int alpha, int beta2, int t,int delta, int i, int j, int m, int n) {
		
		//funciona correctamente
		int byteIndex = bitCont/8, byteMod = bitCont % 8;
		boolean bitValue = extractBit(bits[byteIndex],byteMod);
		int aLimit = i + m, bLimit = j + n;
		ImageProcessor ip = res.getProcessor();
		int pixel;
		
		if (bitValue){
			if (alpha >= 0){
				for (int a = i; a < aLimit; a++) {
					for (int b = j; b < bLimit; b++) {
						if ((a % 2) == (b % 2)) {
							pixel = pixels[a][b] + (delta * beta2);
						} else {
							pixel = pixels[a][b];
						}
						ip.putPixel(a, b, pixel);
					}
				}
			}
			else{
				for (int a = i; a < aLimit; a++) {
					for (int b = j; b < bLimit; b++) {
						if ((a % 2) != (b % 2)) {
							pixel = pixels[a][b] + (delta * beta2);
						} else {
							pixel = pixels[a][b];
						}
						ip.putPixel(a, b, pixel);
					}
				}
			}
			
		}
		else{
			for (int a = i; a < aLimit; a++) {
				for (int b = j; b < bLimit; b++) {
					ip.putPixel(a, b, pixels[a][b]);
				}
			}
		}
		return res;
	}

	public ImagePlus createGap(ImagePlus res, int[][] pixels, int alpha,
			int beta1, int t, int delta, int i, int j, int m, int n) {
		
		//metodo validado con tests
		int aLimit = i + m, bLimit = j + n;
		ImageProcessor ip = res.getProcessor();
		int pixel;
		if (alpha > t) {
			for (int a = i; a < aLimit; a++) {
				for (int b = j; b < bLimit; b++) {
					if ((a % 2) == (b % 2)) {
						pixel = pixels[a][b] + (delta * beta1);
					} else {
						pixel = pixels[a][b];
					}
					ip.putPixel(a, b, pixel);
				}
			}
		} else if (alpha < -t) {
			for (int a = i; a < aLimit; a++) {
				for (int b = j; b < bLimit; b++) {
					if ((a % 2) != (b % 2)) {
						pixel = pixels[a][b] + (delta * beta1);
					} else {
						pixel = pixels[a][b];
					}
					ip.putPixel(a, b, pixel);
				}
			}
		} else {
			for (int a = i; a < aLimit; a++) {
				for (int b = j; b < bLimit; b++) {
					ip.putPixel(a, b, pixels[a][b]);
				}
			}
		}
		return res;
	}

	public int getAlpha(int[][] matrixM, int[][] pixels, int i, int j,
			int delta) {
		//funcion validada
		int alpha = 0;
		int aLimit = i + matrixM.length, bLimit = j + matrixM[0].length;
		int c = 0, d = 0;
		for (int a = i; a < aLimit; a++) {
			d = 0;
			for (int b = j; b < bLimit; b++) {
				alpha = alpha + (delta * matrixM[c][d] * pixels[a][b]);
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

//	private int getNumberOfBlocks(ImagePlus img, int m, int n) {
//		double h = img.getHeight(), w = img.getWidth(), m1 = (double) m, n1 = (double) n;
//		double r1 = Math.floor(h / m1);
//		double r2 = Math.floor(w / n1);
//		double r3 = r1 * r2;
//		int res = (int) r3;
//		return res;
//	}

	public boolean extractBit(byte b, int index){
		//funcion validada
		int aux = 0;
		switch (index){
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
		if (aux == 0){
			return false;
		}
		else{
			return true;
		}
	}

  /**
   * getAlphaDist
   *
   * @param  int [] alphas Valores que toma alpha en la distribución y el número
   *          de veces que aparece.
   * @param String title. Título de la ventana con el histograma.
   **/
  public void getAlphaDist(Hashtable alphas, String title){
    
     //arrays que cargan los datos en la distribución.
     float [] x = new float [alphas.size()];
     float [] y = new float [alphas.size()];
     int pos=0;
     float maxFreq=0, minFreq=Float.MAX_VALUE;
     
     //ordeno la hashtable
     Vector vector = new Vector(alphas.keySet());
     Collections.sort(vector);
     
     //añado los elementos de la hashtable al x e y
     for (Enumeration e = vector.elements(); e.hasMoreElements();) {
       float key = (Float)e.nextElement();
       float value = (Float)alphas.get(key);
       x[pos] = key;
       y[pos] = value;
       if(value > maxFreq){
         maxFreq = value;
       }
       if(value < minFreq){
         minFreq = value;
       }
       pos++;
     }
     
     float Xmax = (Float) vector.lastElement() + 1, Xmin = (Float) vector.firtsElement() - 1;
     float Ymax =maxFreq + 1, Ymin=minFreq - 1;
     PlotWindow plot = new PlotWindow(title, 
                                      "Valores de alpha", 
                                      "Número de alphas",
                                       x,
                                       y); 
     plot.addPoints(x,y,PlotWindow.CIRCLE);
     plot.setLimits(Xmin, Xmax, Ymin, Ymax);
     plot.setColor(Color.blue);
     plot.draw();

  }
  
}
