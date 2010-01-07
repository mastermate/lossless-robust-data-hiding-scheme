package es.us.pid.grupo14.tests;

import ij.ImagePlus;

import org.junit.Before;
import org.junit.Test;

import es.us.pid.grupo14.EmbeddedValidation;
import es.us.pid.grupo14.impl.EmbeddedValidationG8Impl;


public class PSNRTests {

	private EmbeddedValidation val;
	
	@Before
	public void setUp(){
		val = new EmbeddedValidationG8Impl();
	}
	
	@Test
	public void test1(){
		ImagePlus img1 = new ImagePlus("tests-files/bmp-inputs/lena512.bmp");
//		ImagePlus img2 = new ImagePlus("tests-files/rgb/titomc-foca.bmp");
		double psnr = val.getPSNR(img1, img1);
		System.out.println("PSNR test 1 :"+psnr);
		assert (Double.isInfinite(psnr));
	}
	
	@Test
	public void test2(){
		ImagePlus img1 = new ImagePlus("tests-files/bmp-inputs/lena512.bmp");
		ImagePlus img2 = new ImagePlus("tests-files/bmp-inputs/cube.bmp");
		double psnr = val.getPSNR(img1, img2);
		System.out.println("PSNR test 2 :"+psnr);
		assert (psnr == 0);
	}
	
	@Test
	public void test3(){
		ImagePlus img1 = new ImagePlus("tests-files/bmp-inputs/lena512.bmp");
		ImagePlus img2 = new ImagePlus("tests-files/bmp-outputs/stego-lena.bmp");
		double psnr = val.getPSNR(img1, img2);
		System.out.println("PSNR test 3 :"+psnr);
		assert (psnr > 0);
	}
	
	@Test
	public void test4(){
		ImagePlus img1 = new ImagePlus("tests-files/bmp-inputs/baboon grayscale.bmp");
		ImagePlus img2 = new ImagePlus("tests-files/bmp-outputs/stego-baboon-corto.bmp");
		double psnr = val.getPSNR(img1, img2);
		System.out.println("PSNR test 4 :"+psnr);
		assert (psnr > 0);
	}
	
}
