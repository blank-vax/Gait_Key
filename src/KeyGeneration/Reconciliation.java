package KeyGeneration;

 public  class Reconciliation{
    /**
     *  @author b1ank-vax
     *  @description This part is used for reconciliation.
     */

    public static String get_compensation_degree(String K_A){
        byte[] transform1 = K_A.getBytes()
        String K_A_after_deal = Base58.decode(Base58.encode(transform1)).toString();
        String compensation_degree = post_processing.xor(K_A, K_A_after_deal);
        return compensation_degree;
    }

    public static String get_compared_K(String K_Bob, String compensation_degree){
        String first_xor_result = post_processing.xor(K_Bob, compensation_degree);
        String final_xor_result = post_processing.xor(compensation_degree, Base58.decode(Base58.encode(first_xor_result.getBytes())).toString());
        return final_xor_result;
    }

 }