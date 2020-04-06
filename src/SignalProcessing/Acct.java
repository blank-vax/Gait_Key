package SignalProcessing;
import java.util.Scanner;
import org.fastica.math.Matrix;

/**
 *  @author b1ank-vax
 *  @description This part is designed with the aim of interact with Android API to get the data from three dimension of the phone sensor.
 */

public class Acct {
	
	static int samples;
	// Set the amount of data
	
	public static int setSamples() {
		int samples;
		Scanner scan2 = new Scanner(System.in);
		System.out.println("Please input the number of samples(no fewer than 5): ");
		samples = scan2.nextInt();
		return samples;
	}
	
	// Collect data to construct the acceleration matrix
	public static double[][] getAcct(int samples){
		// Establish the input connection which is able to adjust the source on the basis of Android API return value 
		Scanner scan3 = new Scanner(System.in);
		
		double[][] acct = new double[3][samples];
		for(int i = 0;i<samples;i++) {
			System.out.println("Please input the " + (i+1) + " sample data(x,y,z): ");
			for(int j = 0;j<3;j++) {
				acct[j][i] = scan3.nextDouble();
			}
		}
		return acct;
	}
	
	// Get the result in world coordinate system transformed from the device coordinate system
	public static double[][] getRtoW(){
		double[][] worldData = {};
		// Android API 1
		return worldData;
	}
	
	// Find and return the relation matrix between acceleration and time in gravity direction
	public static double[] getGravityacc(double[][] worldData) {
		
		// Return the acceleration in gravity direction in world coordinate system
		return worldData[0];
	}
	
	// Start time alignment for the data after wave filtration
	
	public int[] temporalAlign(double[] signal,int start, int end) {
		int[] range = {0};
		int count = 0;
		for(int i = 1;i<signal.length-1;i++) {
			if((signal[i] > signal[i-1]) && (signal[i] > signal[i+1])) {
				count += 1;
				if(count == 1) {
					range[0] = i;
				}
			}
			if(count == end-start) {range[1] = i;break;}
		}
		return range;
	}
	// Return part of the matrix

	public static double[][] getPortion(int[] range, double[][] raw){
		double[][] portion = null;
		int i1,i2,j;
		int start = range[0], end = range[1];
		for(i1 = 0, i2 = start;i1 <= (end - start);i1++,i2++) {
			for(j=0;j<3;j++) {
				portion[j][i1] = raw[j][i2];
			}
		}
		return portion;
	}
}
