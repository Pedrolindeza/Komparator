package org.komparator.security.handler;

import java.io.ByteArrayOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
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
import org.w3c.dom.NodeList;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;

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

		String propertyValue = (String) smc.get(CONTEXT_PROPERTY);
		
		if (propertyValue.equals("A54_Supplier1")) {
			CERTIFICATE = "A54_Supplier1.cer";
			KEYSTORE = "A54_Supplier1.jks";
			KEY_ALIAS = "A54_Supplier1";

		} else if (propertyValue.equals("A54_Supplier2")) {
			CERTIFICATE = "A54_Supplier2.cer";
			KEYSTORE = "A54_Supplier2.jks";
			KEY_ALIAS = "A54_Supplier2";

		} else if (propertyValue.equals("A54_Supplier3")) {
			CERTIFICATE = "A54_Supplier3.cer";
			KEYSTORE = "A54_Supplier3.jks";
			KEY_ALIAS = "A54_Supplier3";
		}
		
		System.out.println();
		System.out.println("\tSupplier Communication");
		try {
			if (outboundElement.booleanValue()) {
				
				System.out.println("\t---------------");
				System.out.println("\t   OUTBOUND    ");
				System.out.println("\t---------------");
				System.out.println();

				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();

				// add header
				SOAPHeader sh = se.getHeader();
				if (sh == null)
					sh = se.addHeader();
				
				QName svcn = (QName) smc.get(MessageContext.WSDL_SERVICE);
				QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);
				
				ByteArrayOutputStream array = new ByteArrayOutputStream();
				msg.writeTo(array);
				String aux = array.toString();
				byte[] plainBytes = DatatypeConverter.parseBase64Binary(aux);
				
		    	CAClient ca = new CAClient("http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca");
				
				String stringCert = ca.getCertificate(CERTIFICATE);
				
				publicKey = CertUtil.getX509CertificateFromPEMString(stringCert).getPublicKey();
		    	
		    	privateKey = CertUtil.getPrivateKeyFromKeyStoreResource(KEYSTORE,
						KEYSTORE_PASSWORD.toCharArray(), KEY_ALIAS, KEY_PASSWORD.toCharArray());
		    	
		    	byte[] signature = CryptoUtil.makeDigitalSignature(privateKey, plainBytes );
		    	
			}else {
				System.out.println("\t---------------");
				System.out.println("\t   INBOUND     ");
				System.out.println("\t---------------");
				System.out.println();

				
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

}