package es.us.pid.grupo14.tests;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import org.junit.Before;
import org.junit.Test;

import es.us.pid.grupo14.EmbeddedValidation;
import es.us.pid.grupo14.impl.EmbeddedValidationG8Impl;
import es.us.pid.grupo14.impl.EmbeddingAlgorithmG8Impl;
import es.us.pid.grupo14.tests.utils.Utils;


/*
 * Aqui probamos el metodo insertBit
 */
public class InyecBitTests {

private EmbeddedValidation ev;
	
	private EmbeddingAlgorithmG8Impl em;
	
	private ImagePlus ip;
	
	private int[][] array1;
	
	private int[][] array2;
	
	private int[][] matrixM;
	
	private int delta;
	
	private int m;
	
	private int n;
	
	private int beta2;
	
	private byte[] data;
	
	private int bitCont;
	
	//private int alpha;
	
	@Before
	public void setUp(){
		m = 3;
		n = 3;
		ev = new EmbeddedValidationG8Impl();
		em = new EmbeddingAlgorithmG8Impl(); 
		array1 = Utils.createRandomByteArray(10, 20);
		array2 = new int[10][20];
		matrixM = ev.getMatrixM(m, n);
		ip = new ImagePlus();
		ImageProcessor ipc = new ByteProcessor(10, 20);
		ipc.setIntArray(array2);
		ip.setProcessor("processor", ipc);
		data = new byte[1];
		data[0] = 63;
	}
	
	/*
	 * |Alpha| < T, debe sumar beta2 a unas posiciones u otras dependiendo de
	 * si alpha es negativo o positivo
	 * 
	 * Delta = 1
	 */
	@Test
	public void test1a(){
		//para alpha > T en valor absoluto
		System.out.println();
		System.out.println();
		System.out.println("----------------------------");
		System.out.println("|Alpha| < T ---- Delta = 1");
		System.out.println("----------------------------");
		System.out.println("Bytes anteriores a beta2");
		Utils.printArray(array1, 0, 0, m, n);
		delta = 1;
		int alpha = em.getAlpha(matrixM, array1, 0, 0, 1);
		int t = Math.abs(alpha) + 1;
		int g = 5;
		bitCont = 7;
		beta2 = ev.getBeta2(g, t, m, n);
		System.out.println("-------------------");
		System.out.println("Alpha = "+alpha);
		System.out.println("T = "+t);
		System.out.println("G = "+g);
		System.out.println("Beta2 = "+beta2);
		System.out.println("Cadena de bits = 00111111");
		System.out.println("Posicion a insertar = 7 -> Valor del bit = 0");
		System.out.println("-------------------");
		ImagePlus res = em.insertBit(ip, array1, data , bitCont, alpha, beta2, t, delta, 0, 0, m, n);
		int[][] bytesSalida = res.getProcessor().getIntArray();
		System.out.println("Bytes posteriores a beta2");
		Utils.printArray(bytesSalida, 0, 0, m, n);
	}
	
//	/*
//	 * |Alpha| < T, debe sumar beta2 a unas posiciones u otras dependiendo de
//	 * si alpha es negativo o positivo
//	 * 
//	 * Delta = -1
//	 */
//	@Test
//	public void test1b(){
//		//para alpha > T en valor absoluto
//		System.out.println();
//		System.out.println();
//		System.out.println("----------------------------");
//		System.out.println("|Alpha| > T ---- Delta = -1");
//		System.out.println("----------------------------");
//		System.out.println("Bytes anteriores a beta1");
//		Utils.printArray(array1, 0, 0, m, n);
//		delta = -1;
//		int alpha = em.getAlpha(matrixM, array1, 0, 0, 1);
//		int t = Math.abs(alpha) - 1;
//		int g = 5;
//		beta2 = ev.getBeta2(g, t, m, n);
//		System.out.println("-------------------");
//		System.out.println("Alpha = "+alpha);
//		System.out.println("T = "+t);
//		System.out.println("G = "+g);
//		System.out.println("Beta2 = "+beta2);
//		System.out.println("-------------------");
//		ImagePlus res = em.inserBit(ip, array1, beta2, alpha, t, delta, 0, 0, m, n);
//		int[][] bytesSalida = res.getProcessor().getIntArray();
//		System.out.println("Bytes posteriores a beta1");
//		Utils.printArray(bytesSalida, 0, 0, m, n);
//	}
//	
//	/*
//	 * |Alpha| = T, debe sumar tambien
//	 */
//	@Test
//	public void test2(){
//		System.out.println();
//		System.out.println();
//		System.out.println("----------------------------");
//		System.out.println("Alpha = T");
//		System.out.println("----------------------------");
//		//para alpha = T (no debe tocar nada)
//		System.out.println("Bytes anteriores a beta1");
//		Utils.printArray(array1, 0, 0, m, n);
//		delta = 1;
//		int alpha = em.getAlpha(matrixM, array1, 0, 0, 1);
//		int t = Math.abs(alpha);
//		int g = 5;
//		beta2 = ev.getBeta2(g, t, m, n);
//		System.out.println("-------------------");
//		System.out.println("Alpha = "+alpha);
//		System.out.println("T = "+t);
//		System.out.println("G = "+g);
//		System.out.println("Beta2 = "+beta2);
//		System.out.println("-------------------");
//		ImagePlus res = em.inserBit(ip, array1, beta2, alpha, t, delta, 0, 0, m, n);
//		int[][] bytesSalida = res.getProcessor().getIntArray();
//		System.out.println("Bytes posteriores a beta1");
//		Utils.printArray(bytesSalida, 0, 0, m, n);
//	}
//	
//	/*
//	 * |Alpha| > T, debe dejar el cachito de la matriz igual
//	 */
//	@Test
//	public void test3(){
//		System.out.println();
//		System.out.println();
//		System.out.println("----------------------------");
//		System.out.println("Alpha = T");
//		System.out.println("----------------------------");
//		//para alpha = T (no debe tocar nada)
//		System.out.println("Bytes anteriores a beta1");
//		Utils.printArray(array1, 0, 0, m, n);
//		delta = 1;
//		int alpha = em.getAlpha(matrixM, array1, 0, 0, 1);
//		int t = Math.abs(alpha) + 1;
//		int g = 5;
//		beta2 = ev.getBeta2(g, t, m, n);
//		System.out.println("-------------------");
//		System.out.println("Alpha = "+alpha);
//		System.out.println("T = "+t);
//		System.out.println("G = "+g);
//		System.out.println("Beta2 = "+beta2);
//		System.out.println("-------------------");
//		ImagePlus res = em.inserBit(ip, array1, beta2, alpha, t, delta, 0, 0, m, n);
//		int[][] bytesSalida = res.getProcessor().getIntArray();
//		System.out.println("Bytes posteriores a beta1");
//		Utils.printArray(bytesSalida, 0, 0, m, n);
//		System.out.println("Es una imagen en escala de grises? "+(ip.getType() == ImagePlus.GRAY8));
//	}
	
	
}
