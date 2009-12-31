package es.us.pid.grupo14.tests;

import org.junit.Before;
import org.junit.Test;

import es.us.pid.grupo14.impl.EmbeddingAlgorithmG8Impl;


/*
 * Aqui probamos el metodo extractBit
 */
public class ExtractBitFromByteTests {

	private EmbeddingAlgorithmG8Impl em;
	
	@Before
	public void setUp(){
		em = new EmbeddingAlgorithmG8Impl();
	}
	
	@Test
	public void test1(){
		byte b = 0x01;
		boolean res = em.extractBit(b, 0); 
		assert res;
	}
	
	@Test
	public void test2(){
		byte b = (byte)0xf1;
		boolean res = em.extractBit(b, 0); 
		assert res;
	}
	
	@Test
	public void test3(){
		byte b = (byte)0xff;
		boolean res = em.extractBit(b, 0); 
		assert res;
	}
	
	@Test
	public void test4(){
		byte b = (byte)0xf1;
		boolean res = em.extractBit(b, 7); 
		assert res;
	}
	
	@Test
	public void test5(){
		byte b = (byte)0x11;
		boolean res = em.extractBit(b, 4); 
		assert res;
	}
	
	@Test
	public void test6(){
		byte b = (byte)0x11;
		boolean res = em.extractBit(b, 5); 
		assert !res;
	}
	
	@Test
	public void test7(){
		byte b = (byte)0x7f;
		boolean res = em.extractBit(b, 7); 
		assert !res;
	}
	
	@Test
	public void test8(){
		byte b = (byte)0x20;
		boolean res = em.extractBit(b, 6); 
		assert !res;
	}
}
