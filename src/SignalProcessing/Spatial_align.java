package SignalProcessing;
public class Spatial_align {
	/**
 	 * @author: b1ank-vax
 	 * @description: This part is designed for spatial alignment using unit quaternion.   
	 */
	
	 // Conduct filtering for original data
	public static double[][] preProcessing(double[][] Acc){
		double[][] dataAfterfilter = null;
		dataAfterfilter[0][0] = (0.25) * Acc[0][0];
		dataAfterfilter[1][0] = (0.25) * Acc[1][0];
		dataAfterfilter[2][0] = (0.25) * Acc[2][0];
		for(int i = 0;i<Acc.length;i++) {
			for(int j = 1;j<Acc[i].length;j++) {
				dataAfterfilter[i][j] = dataAfterfilter[i][j-1] + (0.25)*(Acc[i][j] - dataAfterfilter[i][j-1]);
			}
		}
		
		return dataAfterfilter;
		
	}
	
	// Get the final transform result of coordinate system using quaternion coperation
	public static double[] systemTrans(double[] device, double[] gravity, double[] magnetic) {
		
		double[] newDevice = null;
		unit_quaternion U = new unit_quaternion();
		U.getAlpha(gravity);
		U.getBeta(gravity, U.argAl);
		U.getGama(gravity, magnetic, U.argAl, U.argBe);
		double[] Q = U.getQ();
		double[] Qconjugate = U.getQconjugate(Q);
		double[] tmp = new double[4];
		double[] result = new double[3];
		newDevice[0] = 0;
		for(int i = 1;i<4;i++) {newDevice[i] = device[i-1];}
		tmp = U.multQ(Q, newDevice);
		tmp = U.multQ(tmp, Qconjugate);
		
		// Finally we get the acceleration in G, F, S directions
		result[0] = tmp[1];
		result[1] = tmp[2];
		result[2] = tmp[3];
		
		return result;
		
	}
	
}
