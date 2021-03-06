package org.komparator.security.handler;

import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
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

/**
 * This SOAPHandler shows how to set/get values from headers in inbound/outbound
 * SOAP messages.
 *
 * A header is created in an outbound message and is read on an inbound message.
 *
 * The value that is read from the header is placed in a SOAP message context
 * property that can be accessed by other handlers or by the application.
 */
public class OpIDHandler implements SOAPHandler<SOAPMessageContext> {

	public static final String CONTEXT_PROPERTY = "my.property";
	public static final String REQUEST_PROPERTY = "my.property";

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
	@SuppressWarnings("rawtypes")
	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		
		System.out.println();
		System.out.println("\tOperation Random Value ID");
		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		String value =  (String) smc.get(REQUEST_PROPERTY);
		
		
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

				// add header
				SOAPHeader sh = se.getHeader();
				if (sh == null)
					sh = se.addHeader();
				
				// add header element (name, namespace prefix, namespace)
				Name name = se.createName("id", "i", "http://demo");
				SOAPHeaderElement element = sh.addHeaderElement(name);

				element.addTextNode(value);
				
				msg.saveChanges();

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

				// get first header element
				Name name = se.createName("id", "i", "http://demo");
				Iterator it = sh.getChildElements(name);
				
				// check header element
				if (!it.hasNext()) {
					System.out.println("Header element not found.");
					return true;
				}
				SOAPElement element = (SOAPElement) it.next();

				// get header element value
				String valueString = element.getValue();

				// print received header
				if (valueString != null)
				System.out.println("Random Nº ID" + valueString);

				// put header in a property context
				smc.put(REQUEST_PROPERTY, value);
				// set property scope to application client/server class can
				// access it
				smc.setScope(CONTEXT_PROPERTY, Scope.APPLICATION);
				
				msg.saveChanges();

			}
		} catch (Exception e) {
			System.out.println(e);
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