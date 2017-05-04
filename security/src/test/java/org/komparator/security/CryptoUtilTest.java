package org.komparator.security;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;

import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;

public class CryptoUtilTest {

    // static members
	//copy past exemplo lab
	final static String CERTIFICATE = "example.cer";

	final static String KEYSTORE = "example.jks";
	final static String KEYSTORE_PASSWORD = "1nsecure";

	final static String KEY_ALIAS = "example";
	final static String KEY_PASSWORD = "ins3cur3";
	
	private static byte[] plainBytes;
	
	static String plainText = "Homens israelitas, acautelai-vos.";
	
	
	static PublicKey publicKey;
	static PrivateKey privateKey;
	
	
    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() throws CertificateException, IOException, UnrecoverableKeyException, KeyStoreException {
    	
    	publicKey = CertUtil.getX509CertificateFromResource(CERTIFICATE).getPublicKey();
    	
    	privateKey = CertUtil.getPrivateKeyFromKeyStoreResource(KEYSTORE,
				KEYSTORE_PASSWORD.toCharArray(), KEY_ALIAS, KEY_PASSWORD.toCharArray());
    	
    	plainBytes = DatatypeConverter.parseBase64Binary(plainText);
    	
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // runs once after all tests in the suite
    }

    // members

    // initialization and clean-up for each test
    @Before
    public void setUp() {
        // runs before each test
    }

    @After
    public void tearDown() {
        // runs after each test
    }

    // tests
    @Test
    public void sucess() {
        
    	byte[] ciphered = CryptoUtil.asymCipher(plainBytes, publicKey);
    	
    	byte[] unciphered = CryptoUtil.asymDecipher(ciphered, privateKey);

        Assert.assertEquals(DatatypeConverter.printBase64Binary(plainBytes), DatatypeConverter.printBase64Binary(unciphered));
       
    }

}
