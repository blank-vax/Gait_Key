package SignalProcessing;
import org.fastica.math.Matrix;
import org.fastica.*;
public class Initial {
	/**
 	 *  @author b1ank-vax
	 *  @description This part contains one main() function.It implements blind source seperation, time-domain filter, time alignment.
	 *  @attention Unfortunately, although the transformation of coordinate system by using unit_quaternion has been implemented, the space 
	 *             alignment has not been achieved successfully. 
 	 */	
	
	public static void main(String[] args) {
		
		double[][] dataAftertimeAlign;
		//double[][] rotationMatrix1;
		//double[][] rotationMatrix2;
		double[][] rawData;
		double[][] worldData;
		double[] gravityData;
		double[][] mm;
		double[][] result;
		double[] signal;
		int[] range;
		
		//Complex[] beforeFFT;
		//Complex[] afterFFT;
		
		// Initialization
		int start = 1, end = 4;
		// Collection the acceleration data in x, y, z directions and store them
		
		Acct A = new Acct();
		rawData = A.getAcct(A.setSamples());
		
		worldData = A.getRtoW();
		
		// Returen the data in the gravity direction

		gravityData = A.getGravityacc(worldData);
		
		// Conduct time domain filtering for the data in the gravity direction

		Filter fl = new Filter();
		signal = fl.getSignal(gravityData, gravityData.length);

		// Filter order: 10
		// Filter frequency: 3Hz
		// Sampling frequency: 100
		// low pass

		signal = fl.Filter(10, 3, 100, signal);
		// Handle the signal after filtering

		range = ((Acct) A).temporalAlign(signal, start, end);
		
		// Get the acceleration data after time alignment

		dataAftertimeAlign = A.getPortion(range, rawData);
		
		// Handle with fastICA

		ica I = new ica();
		mm = I.getMM(dataAftertimeAlign);
		
		// Get the inverse matrix W of the mix matrix

		I.Mrinv(mm, mm.length);
		result = I.eliminateAS(dataAftertimeAlign, mm);
		
		// Output the matrix 

		System.out.println("The matrix is : ");
		I.outputA(result);
		
		// Conduct spatial alignment
		// This part is under realization

		System.out.println("Final result is : ");
		I.outputA(result);
	}
}
