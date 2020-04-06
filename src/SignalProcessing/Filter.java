package SignalProcessing;

import biz.source_code.dsp.filter.FilterPassType;
import biz.source_code.dsp.filter.IirFilterCoefficients;
import biz.source_code.dsp.filter.IirFilterDesignExstrom;
public class Filter {
	/**
 	 *  @author b1ank-vax
 	 *  @description This section is used for Iir time-domain filter.
 	 */
	
	static IirFilterCoefficients iirFilterCoefficients;
	
	// Start Iir time domain filtration
	
	// Encapsulate filter function
	
	public synchronized static double[] IIRFilter(double[] signal, double[] a, double[] b) {

        double[] in = new double[b.length];
        double[] out = new double[a.length-1];

        double[] outData = new double[signal.length];

        for (int i = 0; i < signal.length; i++) {

            System.arraycopy(in, 0, in, 1, in.length - 1);
            in[0] = signal[i];

            //calculate y based on a and b coefficients
            //and in and out.
            float y = 0;
            for(int j = 0 ; j < b.length ; j++){
                y += b[j] * in[j];

            }

            for(int j = 0;j < a.length-1;j++){
                y -= a[j+1] * out[j];
            }

            //shift the out array
            System.arraycopy(out, 0, out, 1, out.length - 1);
            out[0] = y;

            outData[i] = y;


        }
        return outData;
    }

	// Generate signal matrix according to the combination of acceleration vector and time
	public static double[] getSignal(double[] gravityData, int n) {
		double[] time = new double[n];
		double[] signal = new double[n];
		
		for(int i = 0;i < n;i++) {
			time[i] = i / 100.0;
			signal[i] = gravityData[i];
		}
		return signal;	
	}
	
	// Return the low pass filter with specific filtering frequency and carry out time domain filtering 

	public static double[] Filter(int stage, double leachArg, double frequency, double[] signal) {
		// Generate the low pass filter with specific filtering frequency
		iirFilterCoefficients = IirFilterDesignExstrom.design(FilterPassType.lowpass, stage, leachArg/frequency, leachArg/frequency);
		
		// Conduct filtering for input data
		signal = IIRFilter(signal, iirFilterCoefficients.a, iirFilterCoefficients.b);
		return signal;
	}
}
