package KeyGeneration;
import java.util.ArrayList;

public class post_processing {
	/**
 	 *  @author b1ank-vax
 	 *  @description This part contains three post_processing functions: 
 	 *               xor(): This function is used for bit stream xor.
 	 *               privacyAmp(): This function is used for privacy amplification
 	 *               getTimestamp(): This function is able to get the timestamp and transform it to a 128bits string.
 	 */

	// Define the xor function of two bit stream
	public static String xor(String str1, String str2) {
		
		// The length of the input bit stream must be the same
		String result;
		StringBuffer strB = new StringBuffer();
		for(int i = 0,j = 0;i < str1.length();i++,j++) {
			if(str1.charAt(i) == str2.charAt(j)) {
				strB.append("0");
			}
			else if(str1.charAt(i) != str2.charAt(j)) {
				strB.append("1");
			}
		}
		result = strB.toString();
		return result;
	}
	
	// Privacy amplification
	// Increase the randomness of key by bit stream xor
	public static String privacyAmp(String final_key) {
		
		int length = final_key.length();
		ArrayList<String> strArray = new ArrayList<String>();
		int tellsign = length / 30;
		StringBuffer strB2 = new StringBuffer();
		String key_after_privacyAmp;
		if(tellsign % 2 == 1) {
			tellsign -= 1;
		}
		
		// Decollate the string 
		for(int i = 0;i<tellsign-1;i=i+2) {
			String tmp = final_key.substring(i*30, (i+1)*30);
			strArray.add(tmp);
		}

		for(int j = 0;j<strArray.size()-1;j=j+2) {
			strB2.append(xor(strArray.get(j), strArray.get(j+1)));
		}
		key_after_privacyAmp = strB2.toString();
		return key_after_privacyAmp;
	}
	
	// Get timestamp and transform it to 128 bit strings
	public static String getTimestamp() {
		
		// Get timestamp
		long timeStamp = System.currentTimeMillis();
		int TS = (int)timeStamp;
		String timestamp = new String();
		// Transform it to binary numbers
		String tmp = Integer.toBinaryString(TS);
		// Padding to 128bits using zero
		if(tmp.length() < 128) {
			StringBuffer strB = new StringBuffer();
			strB.append(tmp);
			strB.reverse();
			for(int i = 0;i < 128 - tmp.length();i++) {
				strB.append("0");
			}
			strB.reverse();
			timestamp = strB.toString();
		}
		else {
			timestamp = tmp.substring(0, 128);
		}
		return timestamp;
	}
}
