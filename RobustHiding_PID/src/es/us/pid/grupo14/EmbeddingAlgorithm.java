package es.us.pid.grupo14;

import ij.ImagePlus;

/**
 * Algoritmo de embebido de informacion oculta en una imagen
 * 
 * @author Grupo14
 *
 */
public interface EmbeddingAlgorithm {

	/**
	 * Algoritmo de inyeccion de bits en la imagen
	 * 
	 * @param img ImagePlus imagen original
	 * @param bits byte[] bits a embeber
	 * @param t int umbral T
	 * @param g int umbral g
	 * @param m int numero de filas de M
	 * @param n int numero de columnas de M
	 * @param beta1 int parametro beta1
	 * @param beta2 int parametro beta2
	 * @param delta int parametro delta
	 * @return ImagePlus imagen con la informacion oculta
	 */
	public ImagePlus embedBits(ImagePlus img, byte[] bits, int t, int g, int m, int n, 
									int beta1, int beta2, int delta);
	
	
	public int getN0();
	
	public int getN1();
}
