package org.komparator.mediator.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.jws.WebService;

import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.BadQuantity_Exception;
import org.komparator.supplier.ws.BadText_Exception;
import org.komparator.supplier.ws.InsufficientQuantity_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.cli.SupplierClientException;

import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClientException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;


@SuppressWarnings("unused")
@WebService(
		endpointInterface = "org.komparator.mediator.ws.MediatorPortType", 
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
	
	private List<CartView> cartsList = new ArrayList<CartView>();
	
	private List<ShoppingResultView> shoppingResultsList = new ArrayList<ShoppingResultView>();
	
	
	private void resetCartsList() {
		this.cartsList = new ArrayList<CartView>();
	}

	// Main operations -------------------------------------------------------

	@Override
	public List<ItemView> getItems(String productID) throws InvalidItemId_Exception { 
		
		if(productID == null || productID == "" || productID.trim().length() == 0) {
			throwInvalidItemId("The productID you specified is invalid.");
		}
		
		ItemView itemView = new ItemView();
		ItemIdView itemIdView = new ItemIdView();
		List<ItemView> itemViewList = new ArrayList<ItemView>();
		List<SupplierClient> suppClients = (List<SupplierClient>) getSuppliers();
		
		for(SupplierClient suppClient : suppClients) {
			
			try {
				ProductView productView = suppClient.getProduct(productID);
				itemIdView.setProductId(productView.getId());				
				itemIdView.setSupplierId(suppClient.getWsName());				
				itemView.setItemId(itemIdView);
				itemView.setDesc(productView.getDesc());
				itemView.setPrice(productView.getPrice());
				itemViewList.add(itemView);
			}	
			catch(BadProductId_Exception b){
				b.printStackTrace();
			}
			
		}	
		Collections.sort(itemViewList, new Comparator<ItemView>() {
			
			public int compare(ItemView v1, ItemView v2 ) {
				
				int p1 = ((ItemView) v1).getPrice();
				int p2 = ((ItemView) v2).getPrice();
				
				return Integer.compare(p1,p2);
			}
		}
		); 
				
		return itemViewList; 

}
		

	
	@Override
	public List<ItemView> searchItems(String desText) throws InvalidText_Exception {
		
		if(desText == null || desText == "" || desText.trim().length() == 0) {
			throwInvalidText("That description is invalid.");
		}
		
		ItemIdView itemIdView = new ItemIdView();
		ItemView itemView = new ItemView();
		List<SupplierClient> suppClients = (List<SupplierClient>) getSuppliers();
		List<ProductView> productViewList = new ArrayList<ProductView>(); 
		List<ItemView> itemViewList = new ArrayList<ItemView>();
		
		for (SupplierClient suppClient : suppClients) {
			
			try {
				productViewList = suppClient.searchProducts(desText);
				for(ProductView productView : productViewList) {

					itemIdView.setProductId(productView.getId());
					itemIdView.setSupplierId(suppClient.getWsName());
					itemView.setItemId(itemIdView);
					itemView.setDesc(desText);
					itemView.setPrice(productView.getPrice());			
					itemViewList.add(itemView);
				}	
				
			}
			catch(BadText_Exception b){
				b.printStackTrace();
			}
		}	
			
		Collections.sort(itemViewList, new Comparator<ItemView>() {
				
		public int compare(ItemView i1, ItemView i2) {
			
			String s1 = ((ItemView) i1).getItemId().getProductId();
			String s2 = ((ItemView) i2).getItemId().getProductId();
			int sComp = s1.compareTo(s2);
			
			
			if (sComp != 0) {
				return sComp;
			
			
			 } else {
				 int p1 = ((ItemView) i1).getPrice();
					int p2 = ((ItemView) i2).getPrice();
					return Integer.compare(p1,p2);  
	            }
		}});
			
		return itemViewList;
	}
	
	@Override
	public ShoppingResultView buyCart(String cartId, String creditCardNr) throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception  {
		
		if (cartId == null || cartId == "" || cartId.trim().length() == 0) {
			throwInvalidCartId("The cart ID you specified is invalid.");
		}
	  
		if (creditCardNr == null || creditCardNr == "") {
			throwInvalidCreditCard("The credit card ID you specified is invalid");
		}
	  
		String PurchaseId = null;
		
		boolean endPurchases = false;
		boolean incomplete = false;
	  
	  ShoppingResultView shoppingResultView = new ShoppingResultView();
	  
	  shoppingResultView.setResult(Result.EMPTY);
	  
	  try{
	   
		  CreditCardClient creditClientCard = new CreditCardClient("http://ws.sd.rnl.tecnico.ulisboa.pt.8080/cc");
	   
		  if (creditClientCard.validateNumber(creditCardNr)) {
		   
			  CartView cartView = null;
		   
			  for(CartView cart : cartsList) {
				  if(cart.getCartId() == cartId) {
					  cartView = cart;
				  }
			  }
			  
			  if(cartView == null) {
				  
				  throwInvalidCartId("Invalid CartId");
				  
			  }
			  
			  for(CartItemView cartItemView : cartView.getItems()) {
				  
				  try {
					  
					  SupplierClient client = new SupplierClient(endpointManager.getUddiNaming().lookup(cartItemView.getItem().getItemId().getSupplierId())); 
					  try {
						PurchaseId = client.buyProduct(cartItemView.getItem().getItemId().getProductId(), cartItemView.getQuantity());
					} catch (BadQuantity_Exception e) {
						
						e.printStackTrace();
					}
					  shoppingResultView.getPurchasedItems().add(cartItemView);
					  int cost = cartItemView.getQuantity() * cartItemView.getItem().getPrice();
					  shoppingResultView.setTotalPrice(shoppingResultView.getTotalPrice() + cost);
					  endPurchases = true;
					  
				  } catch(SupplierClientException e) {
					  
					  shoppingResultView.getDroppedItems().add(cartItemView);
					  incomplete = true;
					  
				  } catch (UDDINamingException un) {
					  
					  shoppingResultView.getDroppedItems().add(cartItemView);
					  incomplete = true;
					  
				  }  catch(BadProductId_Exception be){
					  
					  shoppingResultView.getDroppedItems().add(cartItemView);
					  incomplete = true;
					  
				  } catch(InsufficientQuantity_Exception ie) {
					  
					  shoppingResultView.getDroppedItems().add(cartItemView);
					  incomplete = true;
					  
				  }
			  }
	    
		  } 
		  
		  else {
			  throwInvalidCreditCard("Invalid creditCard" );
		  }
		  
	  	} catch (CreditCardClientException cce) {
	   
	  		throwInvalidCreditCard("Invalid CreditCard");
	  	}
	  
	  	if(endPurchases) {
	  		if (incomplete) {
	  			shoppingResultView.setResult(Result.PARTIAL);
	  		} 	
	  		else {
	  			shoppingResultView.setResult(Result.COMPLETE);
	  		}
	  	}
	  
	  	shoppingResultView.setId(PurchaseId);
	  	shoppingResultsList.add(shoppingResultView);
	  	return shoppingResultView;
	  
}
	
	@Override
	public void addToCart(String carId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{

		if(carId == null || carId == "" || carId.trim().length() == 0){
			throwInvalidCartId("The ID you specified for the cart is invalid.");
		}
		
		if(itemId == null) {
			throwInvalidItemId("The ID you specified for the item is invalid.");
		}
		
		if(itemQty <= 0) {
			throwInvalidQuantity("The quantity you specified for the item is invalid.");
		}
		
		Collection<SupplierClient> suppliers = getSuppliers();
		
		Boolean cartExists = false;
		
		for(CartView cart : cartsList) {
			if(cart.getCartId() == carId)
				cartExists = true;
		}
		
		if(!cartExists){
			CartView newCart = new CartView();
			newCart.setCartId(carId);
			cartsList.add(newCart);
		}
		
		for(SupplierClient supplier : suppliers) {
			if(supplier.getWsName() == itemId.getSupplierId()){
					
				try{
					ProductView product = supplier.getProduct(itemId.getProductId());
					if(product.getQuantity() < itemQty){
						throwNotEnoughItems("The quantity you want is not available.");
					}
					
					for(CartView cart : cartsList){
						
						if(cart.getCartId() == carId) {
							Boolean exists = false;
							for(CartItemView item : cart.getItems()){
								
								if(item.getItem().getItemId() == itemId) {
									exists = true;
									item.setQuantity(item.getQuantity()+itemQty);
								}
							}
							if(!exists){
								CartItemView newItem = new CartItemView();
								ItemView itemView = new ItemView();
								itemView.setItemId(itemId);
								itemView.setDesc(product.getDesc());
								itemView.setPrice(product.getPrice()*itemQty);
								newItem.setItem(itemView);
								newItem.setQuantity(itemQty);
								cart.getItems().add(newItem);
							}
						}
					}
				} catch(BadProductId_Exception e) {
					e.printStackTrace();
				}
			}	
		}
		
		
	}
	
	
// Auxiliary operations --------------------------------------------------	
	
	
	/* Function Ping */
	
	@Override
	public String ping(String name){
		
		
		String resultado = "";
		try{
			String c = null;
			for(int i = 1; i<3; i++){
				c = endpointManager.getUddiNaming().lookup("A54_Supplier" + i);
				
				if (c!= null){
					SupplierClient client = new	SupplierClient(c);
					resultado += "Supplier 1 :" + client.ping(name) + "\n";
				}
						
			}
		}catch(UDDINamingException exp){
			exp.printStackTrace();}
		catch(SupplierClientException exp){
			exp.printStackTrace();}
		
		System.out.println(resultado);
		
	return resultado;
}
	
	
	/*-------*/
	
	/* Function Clear */
	
	@Override
	public void clear(){
		
		Collection<SupplierClient> suppliers = getSuppliers();
		for(SupplierClient supplier : suppliers){
			supplier.clear();
		}
		resetCartsList();
		
	}
	
	/* ------ */
	
	/* function ListCards */
	
	@Override
	public List<CartView> listCarts() {
		
		return cartsList;
	}
	
	/* -------- */ 
	
	/* Function shopHistory */
	
	@Override
	public List<ShoppingResultView> shopHistory() {
		
		return null;
	}
	
	/* ------ */
	
	
	// View helpers -----------------------------------------------------
	
	public Collection<SupplierClient> getSuppliers(){
		
		UDDINaming uddiNaming = endpointManager.getUddiNaming();
		Collection<UDDIRecord> records = new ArrayList<UDDIRecord>();
		Collection<SupplierClient> suppliers = new ArrayList<SupplierClient>();
		
		if(uddiNaming != null) {
			try{
			
				records = uddiNaming.listRecords("A54_Supplier%");
			
				for(UDDIRecord record : records) {
					suppliers.add(new SupplierClient(record.getUrl(), record.getOrgName()));
				
				}
			} 
			catch (UDDINamingException | SupplierClientException e) {
				e.printStackTrace();
			}
		}
		return suppliers;
	}
    
	// Exception helpers -----------------------------------------------------
	
	/** Helper method to throw new EmptyCart exception */
	private void throwInvalidCreditCard(final String message) throws InvalidCreditCard_Exception {
		InvalidCreditCard faultInfo = new InvalidCreditCard();
		faultInfo.message = message;
		throw new InvalidCreditCard_Exception(message, faultInfo);
	}
	
	/** Helper method to throw new EmptyCart exception */
	private void throwEmptyCart(final String message) throws EmptyCart_Exception {
		EmptyCart faultInfo = new EmptyCart();
		faultInfo.message = message;
		throw new EmptyCart_Exception(message, faultInfo);
	}
	
	/** Helper method to throw new NotEnoughItems exception */
	private void throwNotEnoughItems(final String message) throws NotEnoughItems_Exception {
		NotEnoughItems faultInfo = new NotEnoughItems();
		faultInfo.message = message;
		throw new NotEnoughItems_Exception(message, faultInfo);
	}
	
	/** Helper method to throw new InvalidCartId exception */
	private void throwInvalidCartId(final String message) throws InvalidCartId_Exception {
		InvalidCartId faultInfo = new InvalidCartId();
		faultInfo.message = message;
		throw new InvalidCartId_Exception(message, faultInfo);
	}
	
	/** Helper method to throw new InvalidItemId exception */
	private void throwInvalidItemId(final String message) throws InvalidItemId_Exception {
		InvalidItemId faultInfo = new InvalidItemId();
		faultInfo.message = message;
		throw new InvalidItemId_Exception(message, faultInfo);
	}

	/** Helper method to throw new InvalidQuantity exception */
	private void throwInvalidQuantity(final String message) throws InvalidQuantity_Exception {
		InvalidQuantity faultInfo = new InvalidQuantity();
		faultInfo.message = message;
		throw new InvalidQuantity_Exception(message, faultInfo);
	}
	
	/** Helper method to throw new InvalidText exception */
	private void throwInvalidText(final String message) throws InvalidText_Exception {
		InvalidText faultInfo = new InvalidText();
		faultInfo.message = message;
		throw new InvalidText_Exception(message, faultInfo);
	}


}