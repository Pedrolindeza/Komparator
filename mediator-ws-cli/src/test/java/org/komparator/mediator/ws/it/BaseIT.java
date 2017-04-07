package org.komparator.mediator.ws.it;

import java.awt.List;
import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;

public class BaseIT {
	
	protected static SupplierClient supplier;
	protected static final String supURL= "http://localhost:8081/supplier-ws/endpoint";

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static MediatorClient mediatorClient;

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		String uddiEnabled = testProps.getProperty("uddi.enabled");
		String uddiURL = testProps.getProperty("uddi.url");
		String wsName = testProps.getProperty("ws.name");
		String wsURL = testProps.getProperty("ws.url");
		supplier = new SupplierClient(supURL);
		
		if ("true".equalsIgnoreCase(uddiEnabled)) {
			mediatorClient = new MediatorClient(uddiURL, wsName);
		} else {
			mediatorClient = new MediatorClient(wsURL);
		}
		
		{
		ProductView product = new ProductView();
		product.setId("BE");
		product.setDesc("nada mesmo");
		product.setPrice(20);
		product.setQuantity(20);
		supplier.createProduct(product);
		}
		{
		ProductView product = new ProductView();
		product.setId("ALWAYS");
		product.setDesc("nada outra vez");
		product.setPrice(5);
		product.setQuantity(6);
		supplier.createProduct(product);
		}
		{
		ProductView product = new ProductView();
		product.setId("JESUS");
		product.setDesc("nada outra vez vez");
		product.setPrice(7);
		product.setQuantity(4);
		supplier.createProduct(product);
		}
		
	}

	@AfterClass
	public static void cleanup() {
	}
	
	@After
	public void teardown(){
		mediatorClient.clear();
		supplier.clear();
	}

}
