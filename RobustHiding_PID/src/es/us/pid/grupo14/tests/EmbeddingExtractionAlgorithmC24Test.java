package es.us.pid.grupo14.tests;


import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import es.us.pid.grupo14.HidingResult;
import es.us.pid.grupo14.impl.EmbeddedValidationC24Impl;
import es.us.pid.grupo14.impl.EmbeddingAlgorithmC24Impl;
import es.us.pid.grupo14.impl.ExtractionAlgorithmC24Impl;
import es.us.pid.grupo14.tests.utils.Utils;

public class EmbeddingExtractionAlgorithmC24Test {
	
	private ImagePlus img;
	
	private ImagePlus stegoImg;
	
	private EmbeddingAlgorithmC24Impl emb;
	
	private ExtractionAlgorithmC24Impl ext;
	
	private EmbeddedValidationC24Impl val;
	
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
		img = new ImagePlus("tests-files/kanoute-perfil.bmp");
		emb = new EmbeddingAlgorithmC24Impl();
		val = new EmbeddedValidationC24Impl();
		ext = new ExtractionAlgorithmC24Impl();
		byte[] dataAux = {1,2,3,4,6,7,8,9,10,11,12,13,14,15,16,17,-127,-127,
				-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
				-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
				-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
				-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
				-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
				-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
				-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
				-127,-127,-127,-127,-127,-127,-127,-127,-127,-3,-14,-10};
		data = dataAux;
	}
	
	
	@Test
	public void test1(){
		m = 8;
		n = 8;
		t = 128;
		g = 64;
		beta1 = val.getBeta1(g, t, m, n);
		beta2 = val.getBeta2(g, t, m, n);
		System.out.println("Beta 1 = "+beta1);
		System.out.println("Beta 2 = "+beta2);
		int histogramType = val.getHistogramType(img, beta1, beta2);
		delta = val.getDelta(histogramType);
		System.out.println(delta);
		
//		byte[] dataAux = {-3,-14,-10, 1,2,3,4}; //mas de 7 bytes se corrompe parece
//		data = dataAux;
		
		ImagePlus res = emb.embedBits(img, data, g, m, n, t, beta1, beta2, delta);
		System.out.println("Antes");
		Utils.printArray(img.getProcessor().getIntArray(), 0, 0, m, n);
		System.out.println("Despues");
		Utils.printArray(res.getProcessor().getIntArray(), 0, 0, m, n);
		BufferedImage im = res.getBufferedImage();
		
		int numBlock=1;
		for (int i = 0; i < 10; i = i + m) {
			for (int j = 0; j < 10; j = j + n) {
				System.out.println("=============");
				System.out.println("Kanouté original");
				System.out.println("=============");
				System.out.println("Bloque: "+numBlock);
				Utils.printArray(img.getProcessor().getIntArray() ,i,j,m,n);
				System.out.println();
				
				System.out.println("=============");
				System.out.println("Kanouté con info");
				System.out.println("=============");
				System.out.println("Bloque: "+numBlock);
				Utils.printArray(res.getProcessor().getIntArray() ,i,j,m,n);
				System.out.println();
				
				numBlock++;
			}
		}
		try {
			ImageIO.write(im, "bmp", new File("tests-files/stego-kanoute.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void test2(){
		
//		byte[] dataAux = {-3,-14,-10, 1,2,3,4};
//		data = dataAux;
		
		m = 8;
		n = 8;
		t = 128;
		g = 64;
		stegoImg = new ImagePlus("tests-files/stego-kanoute.bmp");
		beta1 = ext.getBeta1(g, t, m, n);
		beta2 = ext.getBeta2(g, t, m, n);
		System.out.println("Beta 1 = "+beta1);
		System.out.println("Beta 2 = "+beta2);
		int histogramType = val.getHistogramType(stegoImg, beta1, beta2);
		delta = val.getDelta(histogramType);
		HidingResult res = ext.extractBits(stegoImg, m, n, t, g, delta, (int)(data.length *8/2), (int)(data.length *8/2));
		ImagePlus imgRest = res.getImg();
		imgRest.show();
		BufferedImage im = imgRest.getBufferedImage();
		try {
			ImageIO.write(im, "bmp", new File("tests-files/restored-kanoute.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int numBlock=1;
		for (int i = 0; i < 10; i = i + m) {
			for (int j = 0; j < 10; j = j + n) {
				System.out.println("=============");
				System.out.println("Kanoute original");
				System.out.println("=============");
				System.out.println("Bloque: "+numBlock);
				Utils.printArray(img.getProcessor().getIntArray() ,i,j,m,n);
				System.out.println();
				
				System.out.println("=============");
				System.out.println("Kanoute con info");
				System.out.println("=============");
				System.out.println("Bloque: "+numBlock);
				Utils.printArray(stegoImg.getProcessor().getIntArray() ,i,j,m,n);
				System.out.println();
				
				System.out.println("=============");
				System.out.println("Kanoute restaurada");
				System.out.println("=============");
				System.out.println("Bloque: "+numBlock);
				Utils.printArray(imgRest.getProcessor().getIntArray(),i,j,m,n);
				System.out.println();
				
				numBlock++;
			}
		}
		byte[] recoveredData = res.getData();
		for (int i = 0; i < recoveredData.length; i++) {
			System.out.println(recoveredData[i]);
		}
	}
	
	@Test
	public void test3()
	{
		ImageProcessor ip = img.getProcessor();
		int[] pixel = new int[3];
		int value1;
		value1 = ip.getPixel(0, 0); 
		pixel = ip.getPixel(0, 0, pixel);
		System.out.println("Antes");
		System.out.println(pixel[0]+", "+pixel[1]+", "+pixel[2]);
		System.out.println(value1);
		System.out.println("Despues");
		pixel[2] += 5;
		ip.putPixel(0, 0, pixel);
		value1 = ip.getPixel(0, 0);
		pixel = ip.getPixel(0, 0, pixel);
		System.out.println(pixel[0]+", "+pixel[1]+", "+pixel[2]);
		System.out.println(value1);
	}
	
	//Inyectamos en el canal B
	@Test
	public void testEmbeddingLena(){		
		img = new ImagePlus("tests-files/lena512color.bmp");
		m = 8;
		n = 8;
		t = 128;
		g = 64;
		beta1 = val.getBeta1(g, t, m, n);
		beta2 = val.getBeta2(g, t, m, n);
		System.out.println("Beta 1 = "+beta1);
		System.out.println("Beta 2 = "+beta2);
		int histogramType = val.getHistogramType(img, beta1, beta2);
		delta = val.getDelta(histogramType);
		System.out.println(delta);
		
		ImagePlus res = emb.embedBits(img, data, g, m, n, t, beta1, beta2, delta);
		BufferedImage im = res.getBufferedImage();
		
		int numBlock=1;
		for (int i = 0; i < 10; i = i + m) {
			for (int j = 0; j < 10; j = j + n) {
				System.out.println("=============");
				System.out.println("lena original");
				System.out.println("=============");
				System.out.println("Bloque: "+numBlock);
				Utils.printArray(img.getProcessor().getIntArray() ,i,j,m,n);
				System.out.println();
				
				System.out.println("=============");
				System.out.println("lena con info");
				System.out.println("=============");
				System.out.println("Bloque: "+numBlock);
				Utils.printArray(res.getProcessor().getIntArray() ,i,j,m,n);
				System.out.println();
				
				numBlock++;
			}
		}
		try {
			ImageIO.write(im, "bmp", new File("tests-files/stego-lena512color.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	//Extraemos del canal B
	@Test
	public void testExtractingLena(){
		m = 8;
		n = 8;
		t = 128;
		g = 64;
		stegoImg = new ImagePlus("tests-files/stego-lena512color.bmp");
		beta1 = ext.getBeta1(g, t, m, n);
		beta2 = ext.getBeta2(g, t, m, n);
		System.out.println("Beta 1 = "+beta1);
		System.out.println("Beta 2 = "+beta2);
		int histogramType = val.getHistogramType(stegoImg, beta1, beta2);
		delta = val.getDelta(histogramType);
		HidingResult res = ext.extractBits(stegoImg, m, n, t, g, delta, (int)(data.length *8/2), (int)(data.length *8/2));
		ImagePlus imgRest = res.getImg();
		imgRest.show();
		BufferedImage im = imgRest.getBufferedImage();
		try {
			ImageIO.write(im, "bmp", new File("tests-files/restored-lena512color.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		byte[] recoveredData = res.getData();
		for (int i = 0; i < recoveredData.length; i++) {
			System.out.println(recoveredData[i]);
		}
	}
	
	//Inyectamos en el canal R
	@Test
	public void testEmbeddingLenaR(){		
		img = new ImagePlus("tests-files/lena512color.bmp");
		m = 8;
		n = 8;
		t = 128;
		g = 64;
		beta1 = val.getBeta1(g, t, m, n);
		beta2 = val.getBeta2(g, t, m, n);
		System.out.println("Beta 1 = "+beta1);
		System.out.println("Beta 2 = "+beta2);
		int histogramType = val.getHistogramType(img, beta1, beta2);
		delta = val.getDelta(histogramType);
		System.out.println(delta);
		emb.setSelectedChannel(0);
		ImagePlus res = emb.embedBits(img, data, g, m, n, t, beta1, beta2, delta);
		BufferedImage im = res.getBufferedImage();
		
		int numBlock=1;
		for (int i = 0; i < 10; i = i + m) {
			for (int j = 0; j < 10; j = j + n) {
				System.out.println("=============");
				System.out.println("lena original");
				System.out.println("=============");
				System.out.println("Bloque: "+numBlock);
				Utils.printArray(img.getProcessor().getIntArray() ,i,j,m,n);
				System.out.println();
				
				System.out.println("=============");
				System.out.println("lena con info");
				System.out.println("=============");
				System.out.println("Bloque: "+numBlock);
				Utils.printArray(res.getProcessor().getIntArray() ,i,j,m,n);
				System.out.println();
				
				numBlock++;
			}
		}
		try {
			ImageIO.write(im, "bmp", new File("tests-files/stego-lena512color.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	//Extraemos del canal R
	@Test
	public void testExtractingLenaR(){
		m = 8;
		n = 8;
		t = 128;
		g = 64;
		stegoImg = new ImagePlus("tests-files/stego-lena512color.bmp");
		beta1 = ext.getBeta1(g, t, m, n);
		beta2 = ext.getBeta2(g, t, m, n);
		System.out.println("Beta 1 = "+beta1);
		System.out.println("Beta 2 = "+beta2);
		int histogramType = val.getHistogramType(stegoImg, beta1, beta2);
		delta = val.getDelta(histogramType);
		ext.setSelectedChannel(0);
		HidingResult res = ext.extractBits(stegoImg, m, n, t, g, delta, (int)(data.length *8/2), (int)(data.length *8/2));
		ImagePlus imgRest = res.getImg();
		imgRest.show();
		BufferedImage im = imgRest.getBufferedImage();
		try {
			ImageIO.write(im, "bmp", new File("tests-files/restored-lena512color.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		byte[] recoveredData = res.getData();
		for (int i = 0; i < recoveredData.length; i++) {
			System.out.println(recoveredData[i]);
		}
	}
	
	//Inyectamos en el canal G
	@Test
	public void testEmbeddingLenaG(){		
		img = new ImagePlus("tests-files/lena512color.bmp");
		m = 8;
		n = 8;
		t = 128;
		g = 64;
		beta1 = val.getBeta1(g, t, m, n);
		beta2 = val.getBeta2(g, t, m, n);
		System.out.println("Beta 1 = "+beta1);
		System.out.println("Beta 2 = "+beta2);
		int histogramType = val.getHistogramType(img, beta1, beta2);
		delta = val.getDelta(histogramType);
		System.out.println(delta);
		emb.setSelectedChannel(1);
		ImagePlus res = emb.embedBits(img, data, g, m, n, t, beta1, beta2, delta);
		BufferedImage im = res.getBufferedImage();
		
		int numBlock=1;
		for (int i = 0; i < 10; i = i + m) {
			for (int j = 0; j < 10; j = j + n) {
				System.out.println("=============");
				System.out.println("lena original");
				System.out.println("=============");
				System.out.println("Bloque: "+numBlock);
				Utils.printArray(img.getProcessor().getIntArray() ,i,j,m,n);
				System.out.println();
				
				System.out.println("=============");
				System.out.println("lena con info");
				System.out.println("=============");
				System.out.println("Bloque: "+numBlock);
				Utils.printArray(res.getProcessor().getIntArray() ,i,j,m,n);
				System.out.println();
				
				numBlock++;
			}
		}
		try {
			ImageIO.write(im, "bmp", new File("tests-files/stego-lena512color.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	//Extraemos del canal G
	@Test
	public void testExtractingLenaG(){
		m = 8;
		n = 8;
		t = 128;
		g = 64;
		stegoImg = new ImagePlus("tests-files/stego-lena512color.bmp");
		beta1 = ext.getBeta1(g, t, m, n);
		beta2 = ext.getBeta2(g, t, m, n);
		System.out.println("Beta 1 = "+beta1);
		System.out.println("Beta 2 = "+beta2);
		int histogramType = val.getHistogramType(stegoImg, beta1, beta2);
		delta = val.getDelta(histogramType);
		ext.setSelectedChannel(1);
		HidingResult res = ext.extractBits(stegoImg, m, n, t, g, delta, (int)(data.length *8/2), (int)(data.length *8/2));
		ImagePlus imgRest = res.getImg();
		imgRest.show();
		BufferedImage im = imgRest.getBufferedImage();
		try {
			ImageIO.write(im, "bmp", new File("tests-files/restored-lena512color.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		byte[] recoveredData = res.getData();
		for (int i = 0; i < recoveredData.length; i++) {
			System.out.println(recoveredData[i]);
		}
	}
}
