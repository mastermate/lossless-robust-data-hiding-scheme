package es.us.pid.grupo14.impl;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import es.us.pid.grupo14.EmbebbedValidation;

public class EmbebbedValidationImpl implements EmbebbedValidation {

	//TODO probar todos estos metodos con imagenes y asegurarse de que funcionan correctamente
	
	@Override
	public int getBeta1(int g, int t, int m, int n) {
		double r1 = ((2 * g) + t) * 2 / (m * n);
		double r2 = Math.ceil(r1);
		int res = (int)r2;
		return res;
	}

	@Override
	public int getBeta2(int g, int t, int m, int n) {
		double r1 = (t + g) * 2 / (m * n);
		double r2 = Math.ceil(r1);
		int res = (int)r2;
		return res;
	}

	@Override
	public int getDelta(int histogramType) {
		int res;
		if (histogramType == EmbebbedValidation.HISTOGRAM_TYPE_C){
			res = -1;
		}
		else{
			res = 1;
		}
		return res;
	}

	@Override
	public int getHistogramType(ImagePlus img, int beta1, int beta2) {
		//segun la formula, beta1 siempre sera mayor que beta2
		int res;
		ImageStatistics is = img.getStatistics();
		int min = (int) is.min, max = (int) is.max;
		int bitDepth = img.getBitDepth();
		int maxLevel = (int)Math.pow(2, bitDepth) - 1;
		int gap1 = min, gap2 = maxLevel - max;
		if (beta1 >= gap1){
			//tipos A y C
			if (beta1 >= gap2){
				//tipo A
				res = EmbebbedValidation.HISTOGRAM_TYPE_A;
			}
			else{
				//tipo C
				res = EmbebbedValidation.HISTOGRAM_TYPE_C;
			}
		}
		else{
			//tipos B y D
			if (beta1 >= gap2){
				//tipo B
				res = EmbebbedValidation.HISTOGRAM_TYPE_B;
			}
			else{
				//tipo D
				res = EmbebbedValidation.HISTOGRAM_TYPE_D;
			}
		}
		return res;
	}

	@Override
	public int[][] getMatrixM(int m, int n) {
		int[][] res = new int[m][n];
		for (int i = 0; i < m; i++){
			for (int j = 0; j < n; j++){
				if ( (i % 2) == (j % 2) ){
					res[i][j] = 1;
				}
				else{
					res[i][j] = -1;
				}
				
			}
		}
		return res;
	}

	@Override
	public int getNumberOfBlocks(ImagePlus img, int m, int n) {
		double h = img.getHeight(), w = img.getWidth(), m1 = (double)m, n1 = (double)n;	
		double r1 = Math.floor(h/m1);
		double r2 = Math.floor(w/n1);
		double r3 = r1*r2;
		int res = (int)r3;
		return res;
	}

	@Override
	public boolean isValidSize(ImagePlus img, byte[] data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ImagePlus reescaleHistogram(ImagePlus img, int type, int beta1,
			int beta2) {
		// TODO Auto-generated method stub
		return null;
	}

}
