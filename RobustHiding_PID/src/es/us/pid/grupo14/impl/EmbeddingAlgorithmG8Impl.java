package es.us.pid.grupo14.impl;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import es.us.pid.grupo14.EmbeddingAlgorithm;

public class EmbeddingAlgorithmG8Impl implements EmbeddingAlgorithm {

	@Override
	public ImagePlus embedBits(ImagePlus img, byte[] bits, int g, int m, int n,
			int t, int beta1, int beta2, int delta) {

		ImagePlus res = img.createImagePlus();
//		ImageProcessor resProcessor = res.getProcessor();
		int bitCont = 0;

		int[][] matrixM = getMatrixM(m, n);
//		int nBlocks = getNumberOfBlocks(img, m, n);
		int w = img.getWidth(), h = img.getHeight();
		// TODO cuidado con como me devuelve el array de enteros
		int[][] pixels = img.getProcessor().getIntArray();
		// img.getProcessor().pu
		for (int i = 0; i < h; i = i + m) {
			for (int j = 0; j < w; j = j + n) {
				int alpha = getAlpha(matrixM, pixels, i, j, delta);
				res = createGap(res, pixels, beta1, alpha, t, delta, i, j, m, n);

				if (isInRange(alpha, t)) {
					res = insertBit(res, pixels, bits, bitCont, alpha, beta2,
							delta, i, j, m, n);
					bitCont++;
				}
			}

		}

		return res;
	}

	private ImagePlus insertBit(ImagePlus res, int[][] pixels, byte[] bits,
			int bitCont, int alpha, int beta2, int delta, int i, int j, int m, int n) {
		
		// TODO probar tambien esta funcion
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

	private ImagePlus createGap(ImagePlus res, int[][] pixels, int beta1,
			int alpha, int t, int delta, int i, int j, int m, int n) {
		
		// TODO probar este metodo
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

	private int getAlpha(int[][] matrixM, int[][] pixels, int i, int j,
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

	private boolean isInRange(int alpha, int t) {
		if ((alpha <= t) && (alpha >= -t)) {
			return true;
		} else {
			return false;
		}
	}

	private int[][] getMatrixM(int m, int n) {
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

	private boolean extractBit(byte b, int index){
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
}
