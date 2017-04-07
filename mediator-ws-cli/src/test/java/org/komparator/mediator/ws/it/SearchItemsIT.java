package org.komparator.mediator.ws.it;

import org.junit.Test;
import org.komparator.mediator.ws.InvalidText_Exception;

public class SearchItemsIT extends BaseIT {
 
  @Test(expected = InvalidText_Exception.class)
  public void nullId() throws InvalidText_Exception{
   mediatorClient.searchItems(null);
  }
  
  @Test(expected = InvalidText_Exception.class)
  public void emptyId() throws InvalidText_Exception{
   mediatorClient.searchItems("");
  }
  
  @Test(expected = InvalidText_Exception.class)
  public void onlySpacesId() throws InvalidText_Exception{
   mediatorClient.searchItems("                         ");
  }
  
  @Test(expected = InvalidText_Exception.class)
  public void tabId() throws InvalidText_Exception{
   mediatorClient.searchItems("\t");
  }
  
  @Test(expected = InvalidText_Exception.class)
  public void newLineId() throws InvalidText_Exception{
   mediatorClient.searchItems("\n");
  }
  
}
