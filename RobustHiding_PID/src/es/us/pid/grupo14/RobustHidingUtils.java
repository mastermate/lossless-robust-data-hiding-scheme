package es.us.pid.grupo14;

import ij.ImagePlus;

public class RobustHidingUtils {
	
	public static double getBitErrorRate(byte[] originalData, byte[] recoveredData, int size) {
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
	
	private static int getBitsToOne(byte b){
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
	
	public static AlphasImage getAlphasImage(ImagePlus img, int m, int n, int delta){
		
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
	
	public static int getAlpha(int[][] matrixM, int[][] pixels, int i, int j, int delta) {
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
	
	public static int[][] getMatrixM(int m, int n) {
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
	
}
