package org.komparator.mediator.ws.it;

import org.junit.Test;
import org.komparator.mediator.ws.EmptyCart_Exception;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidCreditCard_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;

public class BuyCartIT extends BaseIT {
	
	@Test(expected = InvalidCartId_Exception.class)
	public void nullId() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart(null, "teste");
	}
	
	@Test(expected = InvalidCartId_Exception.class)
	public void emptyId() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("", "teste");
	}
	
	@Test(expected = InvalidCartId_Exception.class)
	public void onlySpacesId() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("                         ", "teste");
	}
	
	@Test(expected = InvalidCartId_Exception.class)
	public void tabId() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("\t", "teste");
	}
	
	@Test(expected = InvalidCartId_Exception.class)
	public void newLineId() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("\n","teste");
	}
	
	
	
	@Test(expected = InvalidCreditCard_Exception.class)
	public void nullCC() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("teste",null);
	}
	
	@Test(expected = InvalidCreditCard_Exception.class)
	public void emptyCC() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("teste","");
	}
	
	
	
	@Test(expected = InvalidCreditCard_Exception.class)
	public void onlySpacesCC() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("teste","                         ");
	}
	
	@Test(expected = InvalidCreditCard_Exception.class)
	public void tabCC() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("teste","\t");
	}
	
	@Test(expected = InvalidCreditCard_Exception.class)
	public void newLineCC() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("teste","\n");
	}
}
