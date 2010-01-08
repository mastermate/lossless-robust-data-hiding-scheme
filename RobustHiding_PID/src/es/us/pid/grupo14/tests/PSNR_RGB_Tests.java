package es.us.pid.grupo14.tests;

import ij.ImagePlus;

import org.junit.Before;
import org.junit.Test;

import es.us.pid.grupo14.EmbeddedValidation;
import es.us.pid.grupo14.impl.EmbeddedValidationC24Impl;


public class PSNR_RGB_Tests {

	private EmbeddedValidation val;
	
	@Before
	public void setUp(){
		val = new EmbeddedValidationC24Impl();
	}
	
	@Test
	public void test1(){
		ImagePlus img1 = new ImagePlus("tests-files/rgb/lena512color.bmp");
		double psnr = val.getPSNR(img1, img1);
		System.out.println("PSNR test 1 :"+psnr);
		assert (Double.isInfinite(psnr));
	}
	
	@Test
	public void test2(){
		ImagePlus img1 = new ImagePlus("tests-files/rgb/lena512color.bmp");
		ImagePlus img2 = new ImagePlus("tests-files/rgb/titomc-foca.bmp");
		double psnr = val.getPSNR(img1, img2);
		System.out.println("PSNR test 2 :"+psnr);
		assert (psnr == 0);
		
	}
	
	@Test
	public void test3(){
		ImagePlus img1 = new ImagePlus("tests-files/rgb/lena512color.bmp");
		ImagePlus img2 = new ImagePlus("tests-files/stego-lena512color.bmp");
		double psnr = val.getPSNR(img1, img2);
		System.out.println("PSNR test 3 :"+psnr);
		assert (psnr > 0);
		
	}
	
	@Test
	public void test4(){
		ImagePlus img1 = new ImagePlus("tests-files/rgb/lena512color.bmp");
		ImagePlus img2 = new ImagePlus("tests-files/restored-lena512color.bmp");
		double psnr = val.getPSNR(img1, img2);
		System.out.println("PSNR test 4 :"+psnr);
		assert (psnr > 0);
		
	}
	
}
