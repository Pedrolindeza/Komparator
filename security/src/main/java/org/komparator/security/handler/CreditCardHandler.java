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

/**
 * This SOAPHandler shows how to set/get values from headers in inbound/outbound
 * SOAP messages.
 *
 * A header is created in an outbound message and is read on an inbound message.
 *
 * The value that is read from the header is placed in a SOAP message context
 * property that can be accessed by other handlers or by the application.
 */
public class CreditCardHandler implements SOAPHandler<SOAPMessageContext> {

	public static final String CONTEXT_PROPERTY = "my.property";
	
	
	
	final static String CERTIFICATE = "A54_Mediator.cer";

	final static String KEYSTORE = "A54_Mediator.jks";
	final static String KEYSTORE_PASSWORD = "uh3wLbpb";

	final static String KEY_ALIAS = "A54_Mediator";
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
		
		System.out.println();
		System.out.println("\tCredit Card Handler");

		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

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
				
				if (!opn.getLocalPart().equals("buyCart")) {
					return true; }

				NodeList children = sb.getFirstChild().getChildNodes();
				
				for (int i = 0; i < children.getLength(); i++) {
					Node argument = (Node) children.item(i);
					if (argument.getNodeName().equals("creditCardNr")) {
						
						publicKey = CertUtil.getX509CertificateFromResource(CERTIFICATE).getPublicKey();
				    	
				    	privateKey = CertUtil.getPrivateKeyFromKeyStoreResource(KEYSTORE,
								KEYSTORE_PASSWORD.toCharArray(), KEY_ALIAS, KEY_PASSWORD.toCharArray());
						
						String secretArgument = argument.getTextContent();
						
						byte[] plainArg = DatatypeConverter.parseBase64Binary(secretArgument); 
						
						byte[] cipheredArg = CryptoUtil.asymCipher(plainArg, publicKey);
						
						String encodedSecretArgument = DatatypeConverter.printBase64Binary(cipheredArg);
						
						argument.setTextContent(encodedSecretArgument);
						
						msg.saveChanges();
				
					}
				}

			} else {
				System.out.println("\t---------------");
				System.out.println("\t   INBOUND     ");
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
				
				if (!opn.getLocalPart().equals("buyCart")) {
					return true; }

				NodeList children = sb.getFirstChild().getChildNodes();
				
				for (int i = 0; i < children.getLength(); i++) {
					Node argument = (Node) children.item(i);
					if (argument.getNodeName().equals("creditCardNr")) {
						
						publicKey = CertUtil.getX509CertificateFromResource(CERTIFICATE).getPublicKey();
				    	
				    	privateKey = CertUtil.getPrivateKeyFromKeyStoreResource(KEYSTORE,
								KEYSTORE_PASSWORD.toCharArray(), KEY_ALIAS, KEY_PASSWORD.toCharArray());
						
						String secretArgument = argument.getTextContent();
						
						byte[] cipheredArg = DatatypeConverter.parseBase64Binary(secretArgument); 
						
						byte[] plainArg = CryptoUtil.asymDecipher(cipheredArg, publicKey);
						
						String decodedSecretArgument = DatatypeConverter.printBase64Binary(plainArg);
						
						argument.setTextContent(decodedSecretArgument);
						
						msg.saveChanges();
				
					}
				}
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