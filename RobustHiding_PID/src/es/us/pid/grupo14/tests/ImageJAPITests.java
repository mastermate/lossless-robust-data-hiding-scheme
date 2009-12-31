package es.us.pid.grupo14.tests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ij.ImagePlus;
import ij.gui.NewImage;

import org.junit.Test;


public class ImageJAPITests {

	@Test
	public void test1(){
		ImagePlus res = NewImage.createByteImage("stego-image", 512, 512, 1, NewImage.FILL_RAMP);
		res.draw();
		res.show();
		BufferedImage im = res.getBufferedImage();
		try {
			ImageIO.write(im, "bmp", new File("tests-files/stego-test.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
