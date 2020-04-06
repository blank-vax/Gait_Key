package KeyGeneration;

public class RSCode { 
    /**
     *  @author b1ank-vax
     *  @description The RSCode is used for detecting the degree of compensation in order to get final negotiated key. 
     *
     */ 
    private static final int MM = 8;  
    private static final int NN = 255;  
    private static final int TT = 10;  
    private static final int KK = 235;  
      
    private int[] pp = {1, 0, 1, 1, 1, 0, 0, 0, 1};  
    private int[] alphaTo = new int[NN+1];  
    private int[] indexOf = new int[NN+1];  
    private int[] gg = new int[NN-KK+1];  
    public int[] recd = new int[NN];  
    public int[] data = new int[KK];  
    public int[] bb = new int[NN-KK];  
      
    /** 
     * Construct function RSCode() 
     * Initialization: generate GF space and corresponding generating polynomials 
     */  
    public RSCode() {  
        generateGF();  
        generatePolynomial();  
    }  
      
    /** 
     * Function generateGF() 
     * Generate GF(2^MM) space 
     */  
    public void generateGF() {  
        int i, mask;  
        mask = 1;  
        alphaTo[MM] = 0;  
        for(i=0; i<MM; i++){  
            alphaTo[i] = mask;  
            indexOf[alphaTo[i]] = i;  
            if(pp[i] != 0){  
                alphaTo[MM] ^= mask;  
            }  
            mask <<= 1;  
        }  
          
        indexOf[alphaTo[MM]] = MM;  
        mask >>= 1;  
          
        for(i=MM+1; i<NN; i++){  
            if(alphaTo[i-1] >= mask){  
                alphaTo[i] = alphaTo[MM] ^ ((alphaTo[i-1]^mask)<<1);  
            }else{  
                alphaTo[i] = alphaTo[i-1]<<1;  
            }  
            indexOf[alphaTo[i]] = i;  
        }  
          
        indexOf[0] = -1;  
    } 
    //GenerateGF   
      
    /** 
     * Function generatePolynomial() 
     * Generate the coefficients of the corresponding generating polynomials 
     */  
    public void generatePolynomial() {  
        int i, j;  
        gg[0] = 2;  
        gg[1] = 1;  
        for(i=2; i<=NN-KK; i++) {  
            gg[i] = 1;  
            for(j=i-1; j>0; j--){  
                if(gg[j] != 0)  
                    gg[j] = gg[j-1] ^ alphaTo[(indexOf[gg[j]]+i) % NN];  
                else  
                    gg[j] = gg[j-1];  
            }  
            gg[0] = alphaTo[(indexOf[gg[0]]+i) % NN];  
        }  
            
        for(i=0; i<=NN-KK; i++) {  
            gg[i] = indexOf[gg[i]];  
        }  
          
        // Output the coefficients of the polynomial   
        System.out.println("The coefficients of the generating polynomial:");  
        for(i=0; i<=NN-KK; i++) {  
            System.out.println(gg[i]);  
        }
        
    }  
  
    /** 
     * Function rsEncode() 
     * RS encoding
     */  
    public void rsEncode() {  
        int i, j;  
        int feedback;  
        for(i=0; i<NN-KK; i++) {  
            bb[i] = 0;  
        }  
        for(i=KK-1; i>=0; i--) {    
            feedback = indexOf[data[i] ^ bb[NN-KK-1]];  
            if(feedback != -1) {  
                for(j=NN-KK-1; j>0; j--) {  
                    if(gg[j] != -1)  
                        bb[j] = bb[j-1] ^ alphaTo[(gg[j]+feedback)%NN];  
                    else  
                        bb[j] = bb[j-1];  
                }  
                bb[0] = alphaTo[(gg[0]+feedback)%NN];  
            }else {  
                for(j=NN-KK-1; j>0; j--) {  
                    bb[j] = bb[j-1];  
                }  
                bb[0] = 0;  
            }  
        }  
        // Output the result of encoding   
        System.out.println("N = " + NN);
        System.out.println("K = " + KK);
        System.out.println("Encoding result:");  
        for(i=0; i<NN-KK; i++) {  
            System.out.println(bb[i]);  
        }  
        System.out.println("Finish the output of encoding result!");
    }  
      
    /** 
     * Function rsDecode() 
     * RS decoding
     */  
    public void rsDecode() {  
        int i, j, u, q;  
        int[][] elp = new int[NN-KK+2][NN-KK];  
        int[] d = new int[NN-KK+2];  
        int[] l = new int[NN-KK+2];  
        int[] u_lu = new int[NN-KK+2];  
        int[] s = new int[NN-KK+1];  
          
        int count = 0;  
        int syn_error = 0;  
        int[] root = new int[TT];  
        int[] loc = new int[TT];  
        int[] z = new int[TT+1];  
        int[] err = new int[NN];  
        int[] reg = new int[TT+1];  
          
        // Transform to GF space   
        for(i=0; i<NN; i++) {  
            if(recd[i] == -1)  
                recd[i] = 0;  
            else  
                recd[i] = indexOf[recd[i]];  
        }  
          
        // Find adjoint polynomials    
        for(i=1; i<=NN-KK; i++) {  
            s[i] = 0;  
            for(j=0; j<NN; j++) {  
                if(recd[j] != -1)  
                    s[i] ^= alphaTo[(recd[j]+i*j)%NN];  
            }  
            if(s[i] != 0)  
                syn_error = 1;  
            s[i] = indexOf[s[i]];  
        }  
        System.out.println("syn_error=" + syn_error);  
          
        // Correct the error if any   
        if(syn_error == 1) {  
            // BM iterate for the coefficients of the polynomial   
            d[0] = 0;  
            d[1] = s[1];  
            elp[0][0] = 0;  
            elp[1][0] = 1;  
            for(i=1; i<NN-KK; i++) {  
                elp[0][i] = -1;  
                elp[1][i] = 0;  
            }  
            l[0] = 0;  
            l[1] = 0;  
            u_lu[0] = -1;  
            u_lu[1] = 0;  
            u = 0;  
            do {  
                u++;  
                if(d[u] == -1) {  
                    l[u+1] = l[u];  
                    for(i=0; i<=l[u]; i++) {  
                        elp[u+1][i] = elp[u][i];  
                        elp[u][i] = indexOf[elp[u][i]];  
                    }  
                }else {  
                    q = u-1;  
                    while((d[q]==-1) && (q>0)) {  
                        q--;  
                    }  
                    if(q > 0) {  
                        j = q;  
                        do {  
                            j--;  
                            if((d[j] != -1) && (u_lu[q] < u_lu[j])) {  
                                q = j;  
                            }  
                        }while(j > 0);  
                    }  
                      
                    if(l[u] > l[q] + u - q) {  
                        l[u+1] = l[u];  
                    }else {  
                        l[u+1] = l[q] + u -q;  
                    }  
                      
                    for(i=0; i<NN-KK; i++) {  
                        elp[u+1][i] = 0;  
                    }  
                    for(i=0; i<=l[q]; i++) {  
                        if(elp[q][i] != -1)  
                            elp[u+1][i+u-q] = alphaTo[(d[u]+NN-d[q]+elp[q][i])%NN];  
                    }  
                      
                    for(i=0; i<=l[u]; i++) {  
                        elp[u+1][i] ^= elp[u][i];  
                        elp[u][i] = indexOf[elp[u][i]];  
                    }  
                }  
                u_lu[u+1] = u-l[u+1];  
                  
                if(u < NN-KK) {  
                    if(s[u+1] != -1) {  
                        d[u+1] = alphaTo[s[u+1]];  
                    }else {  
                        d[u+1] = 0;  
                    }  
                      
                    for(i=1; i<=l[u+1]; i++) {  
                        if((s[u+1-i] != -1) && (elp[u+1][i] != 0)) {  
                            d[u+1] ^= alphaTo[(s[u+1-i]+indexOf[elp[u+1][i]])%NN];  
                        }  
                    }  
                    d[u+1] = indexOf[d[u+1]];  
                }  
            }while((u<NN-KK) && (l[u+1]<=TT));  
            u++;  
            System.out.println("Number of error :" + l[u]);  
              
            // Get the location of error and correct the error   
            if(l[u] <= TT) {  
                for(i=0; i<= l[u]; i++) {  
                    elp[u][i] = indexOf[elp[u][i]];  
                }  
                // Get the root of the polynomials in the location of error   
                for(i=1; i<= l[u]; i++) {  
                    reg[i] = elp[u][i];  
                }  
                count = 0;  
                for(i=1; i<=NN; i++) {  
                    q = 1;  
                    for(j=1; j<=l[u]; j++) {  
                        if(reg[j]!=-1) {  
                            reg[j] = (reg[j] + j)%NN;  
                            q ^= alphaTo[reg[j]];  
                        }  
                    }  
                      
                    if(q==0) {  
                        root[count] = i;  
                        loc[count] = NN-i;  
                        System.out.println("Error location:" + loc[count]);  
                        count++;                          
                    }  
                }  

                if(count == l[u]) {  
                    for(i=1; i<=l[u]; i++) {  
                        if((s[i]!=-1) && elp[u][i]!=-1) {  
                            z[i] = alphaTo[s[i]] ^ alphaTo[elp[u][i]];  
                        }else if((s[i]!=-1) && (elp[u][i]==-1)) {  
                            z[i] = alphaTo[s[i]];  
                        }else if((s[i]==-1) && (elp[u][i]!=-1)) {  
                            z[i] = alphaTo[elp[u][i]] ;  
                        }else {  
                            z[i] = 0;  
                        }  
                          
                        for(j=1; j<i; j++) {  
                            if((s[j]!=-1) && (elp[u][i-j]!=-1)) {  
                                z[i] ^= alphaTo[(elp[u][i-j] + s[j])%NN];  
                            }  
                        }  
                          
                        z[i] = indexOf[z[i]];  
                    }  
                         
                    for(i=0; i<NN; i++) {  
                        err[i] = 0;  
                        if(recd[i] != -1)  
                            recd[i] = alphaTo[recd[i]];  
                        else  
                            recd[i] = 0;  
                    }  
                    for(i=0; i<l[u]; i++) {  
                        err[loc[i]] = 1;  
                        for(j=1; j<=l[u]; j++) {  
                            if(z[j] != -1)  
                                err[loc[i]] ^= alphaTo[(z[j]+j*root[i])%NN];  
                        }  
                          
                        if(err[loc[i]] != 0) {  
                            err[loc[i]] = indexOf[err[loc[i]]];  
                            q = 0;  
                            for(j=0; j<l[u]; j++) {  
                                if(j!=i)  
                                    q += indexOf[1^alphaTo[(loc[j]+root[i])%NN]];  
                            }  
                            q = q%NN;  
                            err[loc[i]] = alphaTo[(err[loc[i]]-q+NN)%NN];  
                            recd[loc[i]] ^= err[loc[i]];  
                        }  
                    }  
                }else {  
                    // Too much error for correction 
                    for(i=0; i<NN; i++) {  
                        if(recd[i] != -1)  
                            recd[i] = alphaTo[recd[i]];  
                        else  
                            recd[i] = 0;  
                    }  
                }  
            }else {  
                // Too much error for correction   
                for(i=0; i<NN; i++) {  
                    if(recd[i] != -1)  
                        recd[i] = alphaTo[recd[i]];  
                    else  
                        recd[i] = 0;  
                }  
            }  
        }else {  
            for(i=0; i<NN; i++) {  
                if(recd[i] != -1)  
                    recd[i] = alphaTo[recd[i]];  
                else  
                    recd[i] = 0;  
            }  
        }  
    }  
    /** 
     *  This is a test.
     */  
    public static void main(String[] args) {  
        RSCode rs = new RSCode();  
          
        // Input the data for encoding  
        int i;  
        for(i=0; i<KK; i++) {  
            rs.data[i] = 0;  
        }  
        for(i=0; i< 20; i++) {  
            rs.data[i] = 48 + i;  
        }  
        
        System.out.println("Original data : ");
        for(int j = 0;j < KK; j ++) {
        	System.out.println(rs.data[j]);
        }
         
        System.out.println("Output finished!");
        System.out.println("----------");
        // Encoding   
        rs.rsEncode();  
          
        for(i=0; i<NN-KK; i++)  
            rs.recd[i] = rs.bb[i];  
        for(i=0; i<KK; i++)  
            rs.recd[i+NN-KK] = rs.data[i];  
        
        // Make some mistakes proactively    
        for(i=0; i<6; i++)  
            rs.recd[i] = 1;  
          
        // Decode and correct   
        rs.rsDecode();  
          
        // Output the correct code and the code after correction    
        System.out.println("i  data  recd");  
        for(i=0; i<NN-KK; i++) {  
            System.out.println(i + "   " + rs.bb[i] + "   " + rs.recd[i]);  
        }  
        for(i=NN-KK; i<NN; i++) {  
            System.out.println(i + "   " + rs.data[i-NN+KK] + "   " + rs.recd[i]);  
        } 
    }  
  
}  