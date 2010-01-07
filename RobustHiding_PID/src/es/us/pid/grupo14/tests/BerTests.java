package es.us.pid.grupo14.tests;

import org.junit.Before;
import org.junit.Test;

import es.us.pid.grupo14.EmbeddedValidation;
import es.us.pid.grupo14.impl.EmbeddedValidationG8Impl;


public class BerTests {

	private EmbeddedValidation val;

	
	@Before
	public void setUp(){
		val = new EmbeddedValidationG8Impl();
	}
	
	@Test
	public void test1(){
		byte[] aux1 = {0,0,1,1,1};
		byte[] aux2 = {0,0,0,0,0};
		double ratio = val.getBitErrorRate(aux1, aux2, 5);
		System.out.println("Ratio test1: "+ratio);
		assert (3.0/40.0 == ratio);
	}
	
	@Test
	public void test2(){
		byte[] aux1 = {2,5,-3,13,-101};
		double ratio = val.getBitErrorRate(aux1, aux1, 5);
		System.out.println("Ratio test1: "+ratio);
		assert (0.0 == ratio);
	}
	
	@Test
	public void test3(){
		byte[] aux1 = {(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff};
		byte[] aux2 = {0,0,0,0,0};
		double ratio = val.getBitErrorRate(aux1, aux2, 5);
		System.out.println("Ratio test1: "+ratio);
		assert (1.0 == ratio);
	}
	
}
