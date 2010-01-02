package es.us.pid.grupo14;

import ij.ImagePlus;

/**
 * Algoritmo de extraccion de informacion oculta de la imagen
 * 
 * @author Grupo14
 *
 */
public interface ExtractionAlgorithm {

	/**
	 * Algoritmo de extraccion de informacion de una stego-imagen
	 * 
	 * @param stegoImg ImagePlues imagen con informacion oculta
	 * @param m int 
	 * @param n int
	 * @param t int
	 * @param g int
	 * @param delta int
	 * @return HidingResult contiene tanto la imagen original como la informacion oculta
	 */
	public HidingResult extractBits(ImagePlus stegoImg, int m, int n, int t, int g, int delta, int n0, int n1);
	
}
