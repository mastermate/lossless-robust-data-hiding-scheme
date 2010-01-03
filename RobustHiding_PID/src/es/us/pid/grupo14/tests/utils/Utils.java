package es.us.pid.grupo14.tests.utils;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.util.Date;
import java.util.Random;

public class Utils {

	public static int[][] createRandomByteArray(int m, int n){
		int[][] res = new int[m][n];
		double aux; 
		int b;
		for (int i = 0; i < m; i++){
			for (int j = 0; j < n; j++){
				aux = Math.random()*255;
				b = (int)aux;
				res[i][j] = b;
			}
		}
		return res;
	}
	
	public static int[][] createRandomIntArray(int m, int n){
		int[][] res = new int[m][n];
		Random rnd = new Random();
		Date today = new Date();
		rnd.setSeed(today.getTime());
		for (int i = 0; i < m; i++){
			for (int j = 0; j < n; j++){
				res[i][j] = rnd.nextInt(Integer.MAX_VALUE);
			}
		}
		return res;
	}
	
	public static void printArray(int[][] array, int xInit, int yInit, int m, int n){
		for (int i = xInit; i < (xInit + m); i++){
			for (int j = yInit; j < (yInit + n); j++){
				System.out.print(array[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	public static void printImageBlocks(ImagePlus img, int m, int n)
	{
		int w = img.getWidth(), h = img.getHeight();
		ImageProcessor ip = img.getProcessor();
		int numBlock = 1;
		for (int i = 0; i < h; i = i + m) {
			for (int j = 0; j < w; j = j + n) {
				System.out.println("Bloque: "+numBlock);
				printArray(ip.getIntArray(),i,j,m,n);
				System.out.println();
				numBlock++;
			}
		}	
	}
}
