package KeyGeneration;
import java.util.Scanner;

public class KeyGeneration {
	/**
	 *  @author b1ank-vax
	 *  @description This part contains one main() function, which is used for final keygeneration.It consists of multilevelquantization,
	 *               origin key generation, privacy amplification reconciliation.Finally it outputs the negotiated final key of both Alice 
	 *               and Bob.Also, it modifies the key generation and negotiation processes between two IoT facilities.
	 *  @attention Uncompleted function: RSCode detection.In other words, this program only provides one unformed model, the part of ECC
	 *             detection should be fixed in the future.
	 */

	public static void main(String[] args) {
		double[][] testSamples = new double[3][1200];
		double[] signalAF;
		double mMAX;
		int m;
		int W = 25;
		double Alpha = 0.9;
		double[] tmp = new double[W];
		double[] QuantizationI = null;
		String tmpkey = new String(), final_key = new String();
		StringBuffer finalkey = new StringBuffer();
		post_processing pp = new post_processing();
		
		Scanner scan = new Scanner(System.in);
		// Generate test data
		// All data differ from each other
		for(int i = 0;i<3;i++) {
			for(int j = 0;j<1200;j++) {
				testSamples[i][j] = Math.random()*5-2;
			}
		}
		
		// Instantiate multilevelquantization object
		MultilevelQuantization MQ = new MultilevelQuantization();
		
		for(int k = 0;k<3;k++) {
			// Time domain filter
			signalAF = MQ.lpFilter(testSamples[k]);
			// Generate bit upper-bound
			mMAX = MQ.getUpperbound(signalAF);
			while(true) {
				System.out.println("Please input the value of m: ");
				m = scan.nextInt();
				if(m < mMAX) break;
			}
		
			// Decollate original matirx and generate keys separately
			// The matrix after decollation is signalAF.length / W;
		
			for(int j = 0 ;j < signalAF.length/W;j++) {
				for(int s = 0,l = j*W;l<(j+1)*W;s++,l++) {
					tmp[s] = signalAF[l];
					QuantizationI = MQ.getQuantizationInterval(Alpha, m, tmp);
					tmpkey = MQ.extractKey(m, QuantizationI, tmp);
				}
				// Concatenate the keys generated from different windows
				\
				finalkey.append(tmpkey);
			}

		}
		System.out.println("Key generation finished!");
		final_key = finalkey.toString();
		System.out.println("The key without processing is : ");
		System.out.println(final_key);
		System.out.println("Key length: " + final_key.length());
		final_key = pp.privacyAmp(final_key);
		System.out.println("The result after privacy amplification: ");
		System.out.println(final_key);
		System.out.println("Key length : " + final_key.length());
		if(final_key.length() > 128) {
			System.out.println("Truncature the key");
			final_key = final_key.substring(0, 128);
			System.out.println("The result after truncation: ");
			System.out.println(final_key);
		}
		
		// Add prefix zero to get 128bits result
		else {
			StringBuffer strB = new StringBuffer();
			strB.append(final_key);
			strB.reverse();
			for(int i = 0;i < 128 - final_key.length();i++) {
				strB.append("0");
			}
			strB.reverse();
			final_key = strB.toString();
		}
		// Xor the final result with timestamp
		// Prevent reply attack
		final_key = pp.xor(final_key, pp.getTimestamp());
		System.out.println("Key with timestamp is : ");
		System.out.println(final_key);
		System.out.println("Key length: " + final_key.length());
		// Modify the reconciliation stage
		// Suppose that variable message represents the information Bob and Alice send to each other during the reconciliation
		// final_key is the key generated in one facility while K_Bob is the key generated synchronously in another facility
		// Function: using HMAC-SHA2 for reconciliation
		String message = "Test message!";
		// sender: Alice
		String compensation_A = Reconciliation.get_compensation_degree(final_key);
		String Linked_A_key = compensation_A + final_key;
		String hash_result = sha256.getSHA256StrJava(Linked_A_key);
		String [] reconciliation_message = new String[2];
		reconciliation_message[0] = compensation_A;
		reconciliation_message[1] = hash_result;
		// receiver: Bob
		String K_Bob = "11001000100110011111001001000001110011100101001100111110110111101101001100111001011011110110001101001011101111110111110101110101";
		String K_Alice = Reconciliation.get_compared_K(K_Bob, reconciliation_message[0]);
		if(sha256.getSHA256StrJava(K_Alice+reconciliation_message[0]).equals(reconciliation_message[1])){
			// Pass the verification of adversary, this key is indeed sent by Alice
			// RSCode detects whether the K_Alice can be used by both of them
			// Uncompleted: RSCode detection code segment
		}
		else{
			System.out.println("Adversary detected!Please start a new communication......");
		}
	}
}
