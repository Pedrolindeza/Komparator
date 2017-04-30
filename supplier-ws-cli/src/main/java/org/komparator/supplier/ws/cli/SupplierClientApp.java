package org.komparator.supplier.ws.cli;

/** Main class that starts the Supplier Web Service client. */
public class SupplierClientApp {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + SupplierClientApp.class.getName() + " wsURL");
			return;
		}
		
		
		// the following remote invocations are just basic examples
		// the actual tests are made using JUnit
		String uddiURL = null;
		String wsName = null;
		String wsURL = null;
		
		
		SupplierClient client = null;
		if (args.length == 1) {
			wsURL = args[0];
			System.out.printf("Creating client for server at %s%n", wsURL);
			client = new SupplierClient(wsURL);
		} else if (args.length >= 3) {
			uddiURL = args[0];
			wsName = args[1];
			wsURL = args[2];
			System.out.printf("Creating client for server at %s%n", wsURL);
			client = new SupplierClient(uddiURL, wsName);
			client.setVerbose(true);
		}
			
		System.out.println("Invoke ping()...");
		String result = client.ping("client");
		System.out.print("Result: ");
		System.out.println(result);
	}
	
	
	
	// Create server implementation object, according to options
	

}
