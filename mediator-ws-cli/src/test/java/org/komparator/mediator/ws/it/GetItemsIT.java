package org.komparator.mediator.ws.it;


import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.komparator.mediator.ws.InvalidItemId_Exception;


@SuppressWarnings("unused")
public class GetItemsIT extends BaseIT{
	
	@Test(expected = InvalidItemId_Exception.class)
	public void nullId() throws InvalidItemId_Exception{
		mediatorClient.getItems(null);
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void emptyId() throws InvalidItemId_Exception{
		mediatorClient.getItems("");
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void onlySpacesId() throws InvalidItemId_Exception{
		mediatorClient.getItems("                         ");
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void tabId() throws InvalidItemId_Exception{
		mediatorClient.getItems("\t");
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void newLineId() throws InvalidItemId_Exception{
		mediatorClient.getItems("\n");
	}
}
