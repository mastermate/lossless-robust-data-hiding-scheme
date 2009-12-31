package es.us.pid.grupo14.tests;

import org.junit.Before;
import org.junit.Test;

import es.us.pid.grupo14.EmbeddedValidation;
import es.us.pid.grupo14.impl.EmbeddedValidationG8Impl;
import es.us.pid.grupo14.impl.EmbeddingAlgorithmG8Impl;
import es.us.pid.grupo14.tests.utils.Utils;


public class AlphaTests {

	private EmbeddedValidation ev;
	
	private EmbeddingAlgorithmG8Impl em;
	
	private int[][] array;
	
	private int[][] matrixM;
	
	@Before
	public void setUp(){
		ev = new EmbeddedValidationG8Impl();
		em = new EmbeddingAlgorithmG8Impl(); 
		array = Utils.createRandomByteArray(10, 20);
		matrixM = ev.getMatrixM(3, 3);
	}
	
//	@Test
//	public void boundsTest(){
//		
//		
//	}
	
	@Test
	public void alphaValueTest1(){
		System.out.println("--------------------------");
		System.out.println("Test 1");
		System.out.println("--------------------------");
		int alpha = em.getAlpha(matrixM, array, 3, 3, 1);
		System.out.println("Subarray:");
		Utils.printArray(array,3,3,3,3);
		System.out.println("Matriz M");
		Utils.printArray(matrixM,0,0,3,3);
		System.out.println("Alpha: "+alpha);
	}
	
	@Test
	public void alphaValueTest2(){
		System.out.println("--------------------------");
		System.out.println("Test 2");
		System.out.println("--------------------------");
		int alpha = em.getAlpha(matrixM, array, 7, 7, -1);
		System.out.println("Subarray:");
		Utils.printArray(array,7,7,3,3);
		System.out.println("Matriz M");
		Utils.printArray(matrixM,0,0,3,3);
		System.out.println("Alpha: "+alpha);
	}
	
	@Test
	public void alphaValueTest3(){
		System.out.println("--------------------------");
		System.out.println("Test 3");
		System.out.println("--------------------------");
		int alpha = em.getAlpha(matrixM, array, 6, 7, -1);
		System.out.println("Subarray:");
		Utils.printArray(array,6,7,3,3);
		System.out.println("Matriz M");
		Utils.printArray(matrixM,0,0,3,3);
		System.out.println("Alpha: "+alpha);
	}
	
}
