package es.us.pid.grupo14;

import ij.ImagePlus;

/**
 * Interfaz que define el pre-procesado de la imagen antes de realizar el embebido
 * Se encarga de calcular ciertos valores y parametros necesarios para los posteriores
 * algoritmos.
 * 
 * @author Grupo 14
 *
 */
public interface EmbeddedValidation {

	public static final int HISTOGRAM_TYPE_A = 0;
	
	public static final int HISTOGRAM_TYPE_B = 1;

	public static final int HISTOGRAM_TYPE_C = 2;
	
	public static final int HISTOGRAM_TYPE_D = 3;
	
	/**
	 * Metodo para calcular si un conjunto de bytes cabe en una imagen dada
	 * 
	 * @param img la imagen original
	 * @param data los datos a ser embebidos
	 * @return boolean cierto si el conjunto de bytes cabe en la imagen, falso en caso contrario
	 */
	public boolean isValidSize(ImagePlus img, byte[] data);
	
	/**
	 * Metodo para calcular el parametro Beta1
	 * 
	 * @param g int umbral G
	 * @param t int umbral T
	 * @param m int numero de filas de la matriz M
	 * @param n int numero de columnas de la matriz M
	 * @return int beta1
	 */
	public int getBeta1(int g, int t, int m, int n);
	
	/**
	 * Metodo para calcular el parametro Beta2
	 * 
	 * @param g int umbral G
	 * @param t int umbral T
	 * @param m int numero de filas de la matriz M
	 * @param n int numero de columnas de la matriz M
	 * @return int beta2 
	 */
	public int getBeta2(int g, int t, int m, int n);
	
	
	/**
	 * Metodo para determinar el tipo de histograma de la imagen y asi definir delta
	 * y modificar el histograma en caso de que sea necesario
	 * 
	 * @param img ImagePlus imagen original
	 * @param beta1 int parametro beta1
	 * @param beta2 int parametro beta2
	 * @return int tipo de histograma
	 */
	public int getHistogramType(ImagePlus img, int beta1, int beta2);
	
	
	/**
	 * Metodo para calcular delta, que sera 1 o -1 dependiendo del tipo de histograma
	 * 
	 * @param histogramType int el tipo de histograma
	 * @return int el valor de delta
	 */
	public int getDelta(int histogramType);
	
	/**
	 * Metodo para reescalar la imagen en caso de que sea tipo D, asi le hacemos el espacio
	 * suficiente para permitir el embebido de datos sumando beta1 y beta2
	 * 
	 * @param img ImagePlus la imagen original
	 * @param type int el tipo de histograma de la imagen
	 * @param beta1 int parametro beta1
	 * @param beta2 int parametro beta2
	 * @return ImagePlus la imagen reescalada
	 */
	public ImagePlus reescaleHistogram(ImagePlus img, int type, int beta1, int beta2);
	
	
	/**
	 * Metodo para crear la matriz M de 1's y -1's para un tamaño dado
	 * 
	 * @param m int numero de filas de la matriz M
	 * @param n int numero de columnas de la matriz M
	 * @return int[][] matriz M
	 */
	public int[][] getMatrixM(int m, int n);
	
	/**
	 * Numero de bloques en que queda dividida la imagen
	 * 
	 * @param img ImagePlus imagen original
	 * @param m int numero de filas de la matriz M
	 * @param n int numero de columnas de la matriz M
	 * @return int numero de bloques
	 */
	public int getNumberOfBlocks(ImagePlus img, int m, int n);
	
  /**
   * Dialogo que permite cargar un archivo para inyectarlo en la imagen
   * 
   * @return byte [] embedfile array con los bytes del archivo que vamos a inyectar
   */
   public void readFile();
  
}




