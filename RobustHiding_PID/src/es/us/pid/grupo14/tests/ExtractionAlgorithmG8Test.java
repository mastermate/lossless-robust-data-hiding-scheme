package es.us.pid.grupo14.tests;


import ij.ImagePlus;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import es.us.pid.grupo14.HidingResult;
import es.us.pid.grupo14.impl.EmbeddedValidationG8Impl;
import es.us.pid.grupo14.impl.ExtractionAlgorithmG8Impl;
import es.us.pid.grupo14.tests.utils.Utils;

public class ExtractionAlgorithmG8Test {
	
	private ImagePlus stegoImg;
	
	private ImagePlus originalImg;
	
	private ExtractionAlgorithmG8Impl ext;
	
	private EmbeddedValidationG8Impl ev;
	
	private int g;
	
	private int t;
	
	private int m;
	
	private int n;
	
	private int beta1;
	
	private int beta2;
	
	private byte[] data;
	
	private int delta;

	@Before
	public void setUp(){
		stegoImg = new ImagePlus("tests-files/stego-lena.bmp");
		originalImg = new ImagePlus("tests-files/lena512.bmp");
		ext = new ExtractionAlgorithmG8Impl();
		ev = new EmbeddedValidationG8Impl();
		//data = new byte[10];
		byte[] dataAux = {1,2,3,4,6,7,8,9,10,11,12,13,14,15,16,17,-127,-127,
				-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
				-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
				-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
				-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
				-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
				-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
				-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
				-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127};
		data = dataAux;
	}
	
	
	@Test
	public void test1(){
		m = 8;
		n = 8;
		t = 128;
		g = 64;
		beta1 = ext.getBeta1(g, t, m, n);
		beta2 = ext.getBeta2(g, t, m, n);
		System.out.println("Beta 1 = "+beta1);
		System.out.println("Beta 2 = "+beta2);
		int histogramType = ev.getHistogramType(stegoImg, beta1, beta2);
		delta = ev.getDelta(histogramType);
		HidingResult res = ext.extractBits(stegoImg, m, n, t, g, delta, (int)(data.length *8/2), (int)(data.length *8/2));
		ImagePlus imgRest = res.getImg();
		imgRest.show();
		BufferedImage im = imgRest.getBufferedImage();
		try {
			ImageIO.write(im, "bmp", new File("tests-files/restored-lena.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int numBlock=1;
		for (int i = 0; i < 10; i = i + m) {
			for (int j = 0; j < 10; j = j + n) {
				System.out.println("=============");
				System.out.println("Lena original");
				System.out.println("=============");
				System.out.println("Bloque: "+numBlock);
				Utils.printArray(originalImg.getProcessor().getIntArray() ,i,j,m,n);
				System.out.println();
				
				System.out.println("=============");
				System.out.println("Lena con info");
				System.out.println("=============");
				System.out.println("Bloque: "+numBlock);
				Utils.printArray(stegoImg.getProcessor().getIntArray() ,i,j,m,n);
				System.out.println();
				
				System.out.println("=============");
				System.out.println("Lena restaurada");
				System.out.println("=============");
				System.out.println("Bloque: "+numBlock);
				Utils.printArray(imgRest.getProcessor().getIntArray(),i,j,m,n);
				System.out.println();
				
				numBlock++;
			}
		}
//		System.out.println("=============");
//		System.out.println("Lena original");
//		System.out.println("=============");
//		Utils.printImageBlocks(originalImg, m, n);
		
//		System.out.println("=============");
//		System.out.println("Lena restaurada");
//		System.out.println("=============");
//		Utils.printImageBlocks(imgRest, m, n);
		byte[] recoveredData = res.getData();
		for (int i = 0; i < recoveredData.length; i++) {
			System.out.println(recoveredData[i]);
		}
	}
}
