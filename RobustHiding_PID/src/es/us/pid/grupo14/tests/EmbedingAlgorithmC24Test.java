package es.us.pid.grupo14.tests;


import ij.ImagePlus;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import es.us.pid.grupo14.impl.EmbeddedValidationC24Impl;
import es.us.pid.grupo14.impl.EmbeddedValidationG8Impl;
import es.us.pid.grupo14.impl.EmbeddingAlgorithmC24Impl;
import es.us.pid.grupo14.impl.EmbeddingAlgorithmG8Impl;

public class EmbedingAlgorithmC24Test {
private ImagePlus img;
	
	private EmbeddingAlgorithmC24Impl emb;
	
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
		
		ImagePlus res = emb.embedBits(img, data, t, g, m, n, beta1, beta2, delta);
		BufferedImage im = res.getBufferedImage();
		try {
			ImageIO.write(im, "bmp", new File("tests-files/stego-kanoute.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
