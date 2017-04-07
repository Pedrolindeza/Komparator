package org.komparator.mediator.ws.it;

import org.junit.Test;
import org.komparator.mediator.ws.EmptyCart_Exception;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidCreditCard_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;

public class BuyCartIT extends BaseIT {
	
	@Test(expected = InvalidCartId_Exception.class)
	public void nullId() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart(null, null);
	}
	
	@Test(expected = InvalidCartId_Exception.class)
	public void emptyId() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("", "");
	}
	
	@Test(expected = InvalidCartId_Exception.class)
	public void onlySpacesId() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("                         ", "  ");
	}
	
	@Test(expected = InvalidCartId_Exception.class)
	public void tabId() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("\t", "\n");
	}
	
	@Test(expected = InvalidCartId_Exception.class)
	public void newLineId() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("\n","\t");
	}
}
