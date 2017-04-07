package org.komparator.mediator.ws.it;

import org.junit.Test;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.NotEnoughItems_Exception;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;

public class AddCartIT extends BaseIT {
 
	 @Test(expected = InvalidCartId_Exception.class)
	 public void addCartNullCartIDTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
	  mediatorClient.addToCart(null, new ItemIdView(), 5);
	 }
	 
	 @Test(expected = InvalidCartId_Exception.class)
	 public void addCartEmptyCartIDTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
	  mediatorClient.addToCart("", new ItemIdView(), 5);
	 }
	 
	 @Test(expected = InvalidCartId_Exception.class)
	 public void addCartSpacesCartIDTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
	  mediatorClient.addToCart("     ", new ItemIdView(), 5);
	 }
	 
	 @Test(expected = InvalidCartId_Exception.class)
	 public void addCartNewlineCartIDTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
	  mediatorClient.addToCart("\n", new ItemIdView(), 5);
	 }
	 
	 @Test(expected = InvalidCartId_Exception.class)
	 public void addCartTabCartIDTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
	  mediatorClient.addToCart("\t", new ItemIdView(), 5);
	 }
	 
	 @Test(expected = InvalidItemId_Exception.class)
	 public void addCartNullItemIDTest() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
	  mediatorClient.addToCart("Cart1", null, 5);
	 }
	 
	 @Test(expected = InvalidQuantity_Exception.class)
	 public void addCartNegativeQuantityTest() throws InvalidQuantity_Exception, InvalidCartId_Exception, InvalidItemId_Exception, NotEnoughItems_Exception {
	  mediatorClient.addToCart("Cart1", new ItemIdView(), -1);
	 }
	 
	 @Test(expected = InvalidQuantity_Exception.class)
	 public void addCartZeroQuantityTest() throws InvalidQuantity_Exception, InvalidCartId_Exception, InvalidItemId_Exception, NotEnoughItems_Exception {
	  mediatorClient.addToCart("Cart1", new ItemIdView(), 0);
	 }
 
 
}