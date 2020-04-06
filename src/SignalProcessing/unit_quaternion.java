package SignalProcessing;
public class unit_quaternion {
	/**
 	 *  @author b1ank-vax
	 * 	@description Implements of unit quaternion transformation of coordinate system.
	 */
	
	static double Al, Be, Ga;
	static double argAl[] = null, argBe[] = null, argGa[] = null;
	
	// gravity[0] = Gx, gravity[1] = Gy, gravity[2] = Gz
	
	// Get the value of Alpha and related trigonometric function value
	
	public static void getAlpha(double[] gravity) {
		double tanAl,sinAl,cosAl;
		double[] argAl = new double[3];
		// tanAlpha = Gy/Gz
		
		tanAl = gravity[1]/gravity[2];
		Al = Math.atan(tanAl);
		sinAl = Math.sin(Al);
		cosAl = Math.cos(Al);
		
		// The order is sin, cos and tan
		argAl[0] = sinAl;
		argAl[1] = cosAl;
		argAl[2] = tanAl;
	}
	
	// Get the value of Beta and related trigonometric function value
	
	public static void getBeta(double[] gravity, double[] argAl) {
		double tanBe,sinBe,cosBe;
		double[] argBe = new double[3];
		
		tanBe = ((-1) * gravity[0]) / (gravity[1]*argAl[0] + gravity[2]*argAl[1]);
		Be = Math.atan(tanBe);
		sinBe = Math.sin(Be);
		cosBe = Math.cos(Be);
		
		// The order is sin, cos and tan
		argBe[0] = sinBe;
		argBe[1] = cosBe;
		argBe[2] = tanBe;
	}
	
	// Get the value of Gama and related trigonometric function value
	
	public static void getGama(double[] gravity, double[] magnetic, double[] argAl, double[] argBe) {
		double tanGa,sinGa,cosGa;
		double[] argGa = new double[3];
		
		tanGa = (magnetic[0]*argBe[1]+magnetic[1]*argAl[0]*argBe[0]+magnetic[2]*argBe[0]*argAl[1]) / (magnetic[2]*argAl[0]-magnetic[1]*argAl[1]);
		
		Ga = Math.atan(tanGa);
		sinGa = Math.sin(Ga);
		cosGa = Math.cos(Ga);
		
		// The order is sin, cos and tan
		argGa[0] = sinGa;
		argGa[1] = cosGa;
		argGa[2] = tanGa;
	}
	
	// Calculate the quaternion q
	public static double[] getQ() {
		double[] q = new double[4];
		q[0] = Math.cos(Al/2)* Math.cos(Be/2)* Math.cos(Ga/2) + Math.sin(Al/2)* Math.sin(Be/2)* Math.sin(Ga/2);
		q[1] = Math.cos(Al/2)* Math.sin(Be/2)* Math.cos(Ga/2) - Math.sin(Al/2)* Math.cos(Be/2)* Math.sin(Ga/2);
		q[2] = Math.sin(Al/2)* Math.cos(Be/2)* Math.cos(Ga/2) + Math.cos(Al/2)* Math.sin(Be/2)* Math.sin(Ga/2);
		q[3] = Math.cos(Al/2)* Math.cos(Be/2)* Math.sin(Ga/2) - Math.sin(Al/2)* Math.sin(Be/2)* Math.cos(Ga/2);
		return q;
	}
	
	// Return the conjugation q* of the quaternion q
	public static double[] getQconjugate(double[] q) {
		double[] Qconjugate = new double[4];
		Qconjugate[0] = (-1) * q[0];
		Qconjugate[1] = (-1) * q[1];
		Qconjugate[2] = (-1) * q[2];
		Qconjugate[3] = q[3];
		return Qconjugate;
	}
	
	// Multiplication of quaternion
	public double[] multQ(double[] p, double[] q) {
		double[] multQ = new double[4];
		multQ[0] = p[0]* q[0] - p[1]* q[1] - p[2]* q[2] - p[3]* q[3];
		multQ[1] = p[1]* q[0] + p[0]* q[1] + p[2]* q[3] - p[3]* q[2];
		multQ[2] = p[2]* q[0] + p[0]* q[2] + p[3]* q[1] - p[1]* q[3];
		multQ[3] = p[3]* q[0] + p[0]* q[3] + p[1]* q[2] - p[2]* q[1];
		return multQ;	
	}
}
