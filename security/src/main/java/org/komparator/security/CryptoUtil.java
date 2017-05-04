package org.komparator.security;

import java.io.*;

import java.security.*;
import javax.crypto.*;
import java.util.*;


public class CryptoUtil {

	public static byte[] asymCipher(byte[] plainBytes, Key publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		
    	Cipher cipher = null;
    	byte[] cipherBytes = null;
		
		cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    	
    	cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		
    	cipherBytes = cipher.doFinal(plainBytes);	
		
    	return cipherBytes;
    }
	
	public static byte[] asymDecipher(byte[] cipherBytes, Key privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		
		Cipher cipher = null;
    	byte[] plainBytes = null;
    	
    	cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		
    	cipher.init(Cipher.DECRYPT_MODE, privateKey);
		
		plainBytes = cipher.doFinal(cipherBytes);
		
		return plainBytes;
	}

}
