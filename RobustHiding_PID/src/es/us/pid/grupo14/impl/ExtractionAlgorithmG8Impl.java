package es.us.pid.grupo14.impl;

import ij.ImagePlus;
import ij.io.FileInfo;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.io.ByteArrayOutputStream;
import java.util.SortedMap;
import java.util.TreeMap;

import es.us.pid.grupo14.ExtractionAlgorithm;
import es.us.pid.grupo14.HidingResult;

public class ExtractionAlgorithmG8Impl implements ExtractionAlgorithm {

	@Override
	public HidingResult extractBits(ImagePlus stegoImg, int m, int n, int t1,
			int g1, int delta, int n0, int n1) {
		//Hace la extracción de una imagen que no ha sido comprimida
		// TODO Hacerlo para cuando la imagen esté comprimida
		int t, g;
		int[][] matrixM = getMatrixM(m, n);
		if (isJpgImage(stegoImg)){
			int[] tAndG = getNewTandG(stegoImg,n0,n1,m,n,delta,matrixM);
			t = tAndG[0];
			g = tAndG[1];
		}
		else{
			t = t1;
			g = g1;
		}
		int w = stegoImg.getWidth(), h = stegoImg.getHeight();
		int beta1 = delta * getBeta1(g, t, m, n);
		int beta2 = delta * getBeta2(g,t,m,n);
		ImageProcessor ip = new ByteProcessor(w, h);
		ImagePlus recoveredImg = new ImagePlus("recovered-image",ip);
		recoveredImg.getProcessor().insert(stegoImg.getProcessor(), 0, 0);
		int bitCount = 0;
		
		int dataSize = (n0 + n1);
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		byte actualByte=0;
		int[][] pixels = stegoImg.getProcessor().getIntArray();
		
		//mientras queden bloques
		for (int i = 0; i < h; i = i + m) {
			for (int j = 0; j < w; j = j + n) {
				int alpha = getAlpha(matrixM, pixels, i, j, delta);
				//Nota ¿es necesario reasignar?
				recoveredImg = restoreGapBeta1(recoveredImg, pixels, beta1, alpha, t, delta, i, j, m, n);

				if (isInZeroZone(t, alpha) && (bitCount < dataSize)) {
					bitCount++;
					
					if (bitCount % 8 == 0) {
						data.write(actualByte);
						actualByte=0;
					}
				}
				else if(isInOneZone(t, g, alpha) && (bitCount < dataSize))
				{
					actualByte = addBit(actualByte,bitCount%8);
					bitCount++;
					if (bitCount % 8 == 0) {
						data.write(actualByte);
						actualByte=0;
					}
					//Nota: no sé si es necesaria esta parafernalia de recoveredImg = ... pasamos un objeto por referencia ¿no?
					//no debería hacer falta asignarlo de nuevo
					recoveredImg = restoreGapBeta2(recoveredImg, pixels, alpha, beta2, t, g, i, j, m, n);
				}
			}

		}
		
		HidingResult result = new HidingResult();
		result.setImg(recoveredImg);
		result.setData(data.toByteArray());
		return result;
	}

	private int[] getNewTandG(ImagePlus stegoImg, int n0, int n1, int m, int n, int delta, int[][] matrixM) {
		//en la posicion 0 almacenamos T, y en la 1 G
		SortedMap<Integer,Integer> dist = new TreeMap<Integer,Integer>();
		int[] res = new int[2];
		int w = stegoImg.getWidth(), h = stegoImg.getHeight();
		int[][] pixels = stegoImg.getProcessor().getIntArray();
		int cont0 = 0, cont1 = 0;
		
		//obtenemos la distribucion de los alphas
		for (int i = 0; i < h; i = i + m) {
			for (int j = 0; j < w; j = j + n) {
				int alpha = getAlpha(matrixM, pixels, i, j, delta);
				int value;
				if (dist.containsKey(alpha)){
					value = dist.get(alpha) + 1;
				}
				else{
					value = 1;
				}
				dist.put(alpha, value);
			}
		}
		
		int alphaCont = 0;
		
		while (cont0 < n0){
			int val = dist.get(alphaCont);
			
		}
		
		return res;
	}

	private boolean isJpgImage(ImagePlus stegoImg) {
		FileInfo fi = stegoImg.getFileInfo();
		if (fi.fileFormat == FileInfo.JPEG){
			return true;
		}
		else{
			return false;
		}	
	}

	private boolean isInOneZone(int t, int g, int alpha) {
		return (alpha > t) && (alpha <= (2*t)+g) || 
				(alpha >= -((2*t)+g)) && (alpha < -t);
	}

	private boolean isInZeroZone(int t, int alpha) {
		return (alpha <= t) && (alpha >= -t);
	}
	
	public int getBeta1(int g, int t, int m, int n) {
		double r1 = (((2 * g) + t) * 2) / ((m * n));
		double r2 = Math.ceil(r1);
		int res = (int)r2;
		return res;
	}

	public int getBeta2(int g, int t, int m, int n) {
		double r1 = ((t + g) * 2) / (m * n);
		double r2 = Math.ceil(r1);
		int res = (int)r2;
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

	public ImagePlus restoreGapBeta1(ImagePlus res, int[][] pixels, int alpha,
			int beta1, int t, int g, int i, int j, int m, int n) {
		
		int aLimit = i + m, bLimit = j + n;
		ImageProcessor ip = res.getProcessor();
		int pixel;
		if (alpha > ((2*t) + g)) {
			for (int a = i; a < aLimit; a++) {
				for (int b = j; b < bLimit; b++) {
					if ((a % 2) == (b % 2)) {
						pixel = pixels[a][b] - beta1;
					} else {
						pixel = pixels[a][b];
					}
					ip.putPixel(a, b, pixel);
				}
			}
		} else if (alpha < -((2*t)+g)) {
			for (int a = i; a < aLimit; a++) {
				for (int b = j; b < bLimit; b++) {
					if ((a % 2) != (b % 2)) {
						pixel = pixels[a][b] - beta1;
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
	
	public ImagePlus restoreGapBeta2(ImagePlus res, int[][] pixels, int alpha,
			int beta2, int t, int g, int i, int j, int m, int n) {
		
		int aLimit = i + m, bLimit = j + n;
		ImageProcessor ip = res.getProcessor();
		int pixel;
		if ((alpha > t) && (alpha <= (2*t)+g)) {
			for (int a = i; a < aLimit; a++) {
				for (int b = j; b < bLimit; b++) {
					if ((a % 2) == (b % 2)) {
						pixel = pixels[a][b] - beta2;
					} else {
						pixel = pixels[a][b];
					}
					ip.putPixel(a, b, pixel);
				}
			}
		} else if ((alpha >= -((2*t)+g)) && (alpha < -t)) {
			for (int a = i; a < aLimit; a++) {
				for (int b = j; b < bLimit; b++) {
					if ((a % 2) != (b % 2)) {
						pixel = pixels[a][b] - beta2;
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
	
	@Deprecated
	public void addByteToData(byte[] data, int bitCount){
		int byteIndex = bitCount/8, byteMod = bitCount % 8;
		data[byteIndex] = addBit(data[byteIndex], byteMod);
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
	
	public byte addBit(byte b, int index){
		//funcion validada
		int result = 0;
		switch (index){
			case 0:
				result = (b | 0x01);
				break;
			case 1:
				result = (b | 0x02);
				break;
			case 2:
				result = (b | 0x04);
				break;
			case 3:
				result = (b | 0x08);
				break;
			case 4:
				result = (b | 0x10);
				break;
			case 5:
				result = (b | 0x20);
				break;
			case 6:
				result = (b | 0x40);
				break;
			case 7:
				result = (b | 0x80);
				break;
			default:
				break;
		}
		return (byte)result;
	}
}
