package KeyGeneration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import SignalProcessing.Filter;


public class MultilevelQuantization {
	/**
 	 *  @author b1ank-vax
 	 *  @description This part is coded for multilevel quantization. 
	 */
	
	// Get the sampling function after filtration
	public static double[] lpFilter(double[] signal) {
		double[] signalAF;
		Filter fl = new Filter();
		signalAF = fl.Filter(10, 10, 100, signal);
		return signalAF;
	}
	
	// Decollation is implemented in main function, put the different resultes after decollation into the loop
	// Determine the bit upper-bound of key generation
	
	public static double getUpperbound(double[] samples) {
		int W = samples.length;
		int index = 0;
		double[] pa = new double[W];
		double mMAX;
		double E = 0;
		
		// Use hashmap for data storage
		Map<Double, Double> map = new HashMap<Double, Double>();
		for(int i = 0;i<W;i++) {
			if(map.get(samples[i])!=null) {
				map.put(samples[i], map.get(samples[i]) + 1.0);
			}else {
				map.put(samples[i], 1.0);
			}
		}
		// Get all keys of map
		Set<Double> keyset = map.keySet();
		// Create the iterator of key set 
		Iterator<Double> it = keyset.iterator();
		while(it.hasNext()) {
			Double key = it.next();
			Double value = map.get(key);
			pa[index] = value / W;
			index++;
		}
		
		for(int i = 0;i<index;i++) {
			E += (pa[i]) * (Math.log(pa[i]) / Math.log(2));
		}
		mMAX = Math.pow(2, (-1) * E);
		
		return mMAX;
		
	}
	
	// Determin quantization interval
	public static double[] getQuantizationInterval(double Alpha, int m, double[] samples) {
		double[] QuantizationI = new double[2*m];
		double guardRatio = Alpha / (m - 1);
		double intervalRatio = (1 - Alpha) / m;
		double maxSample = 0, minSample = 0;
		double range;
		
		// Finde the maximum and minimum numerical value of input data and assign them seperately to maxSample and minSample
		
		for(int i = 0;i < samples.length; i++) {
			if(samples[i] > maxSample) {
				maxSample = samples[i];
			}
			if(samples[i] < minSample) {
				minSample = samples[i];
			}
		}
		
		// Partition according to the calculated interval
		range = maxSample - minSample;
		
		QuantizationI[0] = minSample;
		for(int j = 1;j < 2*m ;j++) {
			if(j%2==1) {
				QuantizationI[j] = QuantizationI[j-1] + range * intervalRatio;
			}else {
				QuantizationI[j] = QuantizationI[j-1] + range * guardRatio;
			}
		}
		
		return QuantizationI;
	}
	
	// Start multilevel quantization
	public static String extractKey(int m, double[] QuantizationI, double[] samples) {
		
		String two_ary = "01";
		String four_ary = "00011011";
		String eight_ary = "000001010011100101110111";
		int flag = 0;
		if(m == 2) flag = 1;
		else if(m == 4) flag = 2;
		else if(m == 8) flag = 3;
		// Use append method to handle StringBuffer object
		StringBuffer buffer = new StringBuffer();
		for(int i = 0;i < samples.length;i++) {
			for(int j = 0;j < QuantizationI.length-1;j++) {
				if(samples[i] > QuantizationI[j] && samples[i] < QuantizationI[j+1]) {
					if(flag == 1) {
						if(j%2 == 0) {buffer.append(two_ary.charAt(j/2));}
					}
					if(flag == 2) {
						if(j%2 == 0) {buffer.append(four_ary.substring(j, j+2));}
					}
					if(flag == 3) {
						if(j%2 == 0) {buffer.append(eight_ary.substring((j/2)*3, (j/2)*3)+3);}
					}
				}
			}
		}
		
		// Return the final bit stream
		
		return buffer.toString();
	}
	
	public static String concatenatingKey(String keyG, String keyF, String keyS) {
		String concatenatedKey;
		StringBuffer buff = new StringBuffer();
		buff.append(keyG);
		buff.append(keyF);
		buff.append(keyS);
		concatenatedKey = buff.toString();
		return concatenatedKey;
	}
}