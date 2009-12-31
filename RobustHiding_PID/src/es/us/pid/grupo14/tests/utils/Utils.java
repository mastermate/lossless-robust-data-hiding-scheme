package es.us.pid.grupo14.tests.utils;

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
	
	public static void printArray(int[][] array, int xInit, int yInit, int m, int n){
		for (int i = xInit; i < (xInit + m); i++){
			for (int j = yInit; j < (yInit + n); j++){
				System.out.print(array[i][j]+" ");
			}
			System.out.println();
		}
	}
	
}
