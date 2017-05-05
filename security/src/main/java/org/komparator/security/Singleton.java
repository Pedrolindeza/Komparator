package org.komparator.security;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

public class Singleton {

	private static Singleton singleton = new Singleton( );
 	private String name;

   /* A private Constructor prevents any other
    * class from instantiating.
    */
	private Singleton() { }

   /* Static 'instance' method */
   public static Singleton getInstance( ) {
      return singleton;
   }

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUDDI(String uddiURL) throws UDDINamingException {
		
		
		UDDINaming uddiNaming = new UDDINaming("http://a54:uh3wLbpb@uddi.sd.rnl.tecnico.ulisboa.pt:9090");
	
		String uddiName = null;
		
		for (UDDIRecord uddiRecord : uddiNaming.listRecords("A54_Mediator" + "%")) {
			if (uddiRecord.getUrl().equals(uddiURL)) {
				uddiName = uddiRecord.getOrgName();
			}
		}
	
		return uddiName;
	}
}
