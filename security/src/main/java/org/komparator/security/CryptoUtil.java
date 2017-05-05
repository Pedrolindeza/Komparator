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
	
	public static byte[] makeDigitalSignature(PrivateKey privateKey, byte[] bytesToSign){
		
		return CertUtil.makeDigitalSignature("SHA256withRSA", privateKey, bytesToSign);
		
	}
	
	public boolean verifyDigitalSignature(PublicKey publicKey, byte[] bytesToVerify, byte[] signature){
		
		return CertUtil.verifyDigitalSignature("SHA256withRSA", publicKey, bytesToVerify, signature);
		
	}
	
	
	public boolean verifyDigitalSignature(Certificate publicKeyCertificate, byte[] bytesToVerify, byte[] signature){
		
		return verifyDigitalSignature(publicKeyCertificate.getPublicKey(), bytesToVerify, signature);
		
	}
	
}
