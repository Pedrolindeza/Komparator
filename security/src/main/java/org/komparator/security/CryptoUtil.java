package org.komparator.security;

import java.io.*;

import java.security.*;
import javax.crypto.*;
import java.util.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;

@SuppressWarnings("unused")
public class CryptoUtil {

	public static byte[] asymCipher(byte[] plainBytes, Key publicKey){
		
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
	
	public static byte[] asymDecipher(byte[] cipherBytes, Key privateKey){
		
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
	
	public byte[] makeDigitalSignature(PrivateKey privateKey, byte[] bytesToSign){
		
		return CertUtil.makeDigitalSignature("SHA256withRSA", privateKey, bytesToSign);
		
	}
	
	public boolean verifyDigitalSignature(PublicKey publicKey, byte[] bytesToVerify, byte[] signature){
		
		return CertUtil.verifyDigitalSignature("SHA256withRSA", publicKey, bytesToVerify, signature);
		
	}
	
	
	public boolean verifyDigitalSignature(Certificate publicKeyCertificate, byte[] bytesToVerify, byte[] signature){
		
		return verifyDigitalSignature(publicKeyCertificate.getPublicKey(), bytesToVerify, signature);
		
	}
	
}
