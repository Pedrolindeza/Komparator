package org.komparator.security.handler;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.komparator.security.CertUtil;
import org.komparator.security.CryptoUtil;
import org.komparator.security.Singleton;
import org.w3c.dom.NodeList;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CAClientException;

/**
 * This SOAPHandler shows how to set/get values from headers in inbound/outbound
 * SOAP messages.
 *
 * A header is created in an outbound message and is read on an inbound message.
 *
 * The value that is read from the header is placed in a SOAP message context
 * property that can be accessed by other handlers or by the application.
 */
public class SignatureHandler implements SOAPHandler<SOAPMessageContext> {

	public static final String CONTEXT_PROPERTY = "my.property";
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	static String CERTIFICATE = null;

	static String KEYSTORE = null;
	final static String KEYSTORE_PASSWORD = "uh3wLbpb";

	static String KEY_ALIAS = null;
	final static String KEY_PASSWORD = "uh3wLbpb";

	static PublicKey publicKey;
	static PrivateKey privateKey;
	
	//
	// Handler interface implementation
	//

	/**
	 * Gets the header blocks that can be processed by this Handler instance. If
	 * null, processes all.
	 */
	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	/**
	 * The handleMessage method is invoked for normal processing of inbound and
	 * outbound messages.
	 */
	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		
		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		
		System.out.println();
		System.out.println("\tSupplier Communication");
		
		try {
			if (outboundElement.booleanValue()) {
				
				System.out.println("\t---------------");
				System.out.println("\t   OUTBOUND    ");
				System.out.println("\t---------------");
				System.out.println();
				
				Singleton single = Singleton.getInstance();
				String wsName = single.getName();
				
				if (wsName.equals("A54_Supplier1")) {
					CERTIFICATE = "A54_Supplier1.cer";
					KEYSTORE = "A54_Supplier1.jks";
					KEY_ALIAS = "a54_supplier1";

				} else if (wsName.equals("A54_Supplier2")) {
					CERTIFICATE = "A54_Supplier2.cer";
					KEYSTORE = "A54_Supplier2.jks";
					KEY_ALIAS = "a54_supplier2";

				} else if (wsName.equals("A54_Supplier3")) {
					CERTIFICATE = "A54_Supplier3.cer";
					KEYSTORE = "A54_Supplier3.jks";
					KEY_ALIAS = "a54_supplier3";
				}
			
				privateKey = CertUtil.getPrivateKeyFromKeyStoreResource(KEYSTORE,
							KEYSTORE_PASSWORD.toCharArray(), KEY_ALIAS, KEY_PASSWORD.toCharArray());
				
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();

				// add header
				SOAPHeader sh = se.getHeader();
				if (sh == null)
					sh = se.addHeader();
				
				// add header element (name, namespace prefix, namespace)
				Name name = se.createName("wsname", "n", "http://demo");
				SOAPHeaderElement element2 = sh.addHeaderElement(name);
				// add header element value
				String myName = wsName;
				element2.addTextNode(myName);
				
				String message = soapMessageToString(msg);
				byte[] plainMsg = DatatypeConverter.parseBase64Binary(message);
				byte[] digitalSignature = CryptoUtil.makeDigitalSignature(privateKey, plainMsg);
				
				// add header element (name, namespace prefix, namespace)
				Name diggest = se.createName("signature", "s", "http://demo");
				SOAPHeaderElement element3 = sh.addHeaderElement(diggest);
				// add header element value
				String signature = DatatypeConverter.printBase64Binary(digitalSignature);
				element3.addTextNode(signature);
				
				msg.saveChanges();
				
				return true;
				
			} else {
				System.out.println("\t---------------");
				System.out.println("\t   INBOUND     ");
				System.out.println("\t---------------");
				System.out.println();
				
				// get SOAP envelope header
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPHeader sh = se.getHeader();

				// check header
				if (sh == null) {
					System.out.println("Header not found.");
					return true;
				}

				// get name element
				Name name = se.createName("wsname", "n", "http://demo");
				Iterator it = sh.getChildElements(name);
				// check header element
				if (!it.hasNext()) {
					System.out.println("Header NAME element not found.");
					return true;
				}
				SOAPElement element = (SOAPElement) it.next();
				String myName = element.getValue();
				CAClient ca = new CAClient("http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca");
				String stringCert = ca.getCertificate(myName + ".cer");
				publicKey = CertUtil.getX509CertificateFromPEMString(stringCert).getPublicKey();
				
				// get signature element
				Name diggest = se.createName("signature", "s", "http://demo");
				Iterator it2 = sh.getChildElements(diggest);
				// check header element
				if (!it2.hasNext()) {
					System.out.println("Header Diggest not found.");
					return true;
				}
				SOAPElement element2 = (SOAPElement) it2.next();
				String sig = element2.getValue();
				byte[] signature = DatatypeConverter.parseBase64Binary(sig);
				
				//message
				String message = soapMessageToString(msg);
				byte[] bytesToVerify = DatatypeConverter.parseBase64Binary(message);
				
				if ( !CryptoUtil.verifyDigitalSignature(publicKey, bytesToVerify, signature ))
					throw new RuntimeException();
				
				msg.saveChanges();
			}			
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
		}

		return true;
	}

	/** The handleFault method is invoked for fault message processing. */
	@Override
	public boolean handleFault(SOAPMessageContext smc) {
		System.out.println("Ignoring fault message...");
		return true;
	}

	/**
	 * Called at the conclusion of a message exchange pattern just prior to the
	 * JAX-WS runtime dispatching a message, fault or exception.
	 */
	@Override
	public void close(MessageContext messageContext) {
		// nothing to clean up
	}
	
	public String soapMessageToString(SOAPMessage msg) {
		String message = null;

		ByteArrayOutputStream bytesToSign = null;
		
		bytesToSign = new ByteArrayOutputStream();
		try {
			msg.writeTo(bytesToSign);
		} catch (SOAPException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		message = bytesToSign.toString();
		
		try {
			bytesToSign.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return message;
	}

}