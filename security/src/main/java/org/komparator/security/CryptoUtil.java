package org.komparator.security;

import java.io.*;

import java.security.*;
import javax.crypto.*;
import java.util.*;


public class CryptoUtil {

	public byte[] asymCipher(byte[] plainBytes, Key publicKey){
		
    	Cipher cipher = null;
    	byte[] cipherBytes = null;
		
    	try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
    	
    	try {
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		}
    	catch (InvalidKeyException e) {
			e.printStackTrace();
		}
    	
    	try {
			cipherBytes = cipher.doFinal(plainBytes);	
		}
    	catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
    	
    	return cipherBytes;
    }
	
	public byte[] asymDecipher(byte[] cipherBytes, Key privateKey){
		
		Cipher cipher = null;
    	byte[] plainBytes = null;
    	
    	try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
    	
		try {
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		
		try {
			plainBytes = cipher.doFinal(cipherBytes);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		
		return plainBytes;
	}

}
