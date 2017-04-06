package org.komparator.mediator.ws;

import javax.jws.WebService;


@WebService(
		endpointInterface = "org.komparator.supplier.ws.MediatorPortType", 
		wsdlLocation = "mediator1_0.wsdl", 
		name = "MediatorWebService", 
		portName = "MediatorPort", 
		targetNamespace = "http://ws.mediator.komparator.org/", 
		serviceName = "MediatorService"
)



public class MediatorPortImpl implements MediatorPortType {

	// end point manager
	private MediatorEndpointManager endpointManager;

	public MediatorPortImpl(MediatorEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	// Main operations -------------------------------------------------------

    // TODO
	
    
	// Auxiliary operations --------------------------------------------------	
	
    // TODO

	
	// View helpers -----------------------------------------------------
	
    // TODO

    
	// Exception helpers -----------------------------------------------------

    // TODO

}
