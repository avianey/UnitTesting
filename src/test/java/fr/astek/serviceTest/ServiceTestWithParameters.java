/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.astek.serviceTest;

import fr.astek.internal.bean.Orders;
import fr.astek.service.ServiceToTest;


import java.util.Arrays;
import java.util.Collection;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @author dlebert
 */
@RunWith(Parameterized.class)
public class ServiceTestWithParameters {

    private String product;
    private int quantity;
    private double price;
      
    // Constructor
    public ServiceTestWithParameters(String product, int quantity, double price) {
       this.product = product;
       this.quantity = quantity;
       this.price = price;
    }
    
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{{"Software", 2, 30.00}, {"Hardware", 6, 50.00}, {"MOA", 4, 320.00}, {"Database", 5, 62.00}});
    }
    
    /**
     * Calling {@link ServiceTestWithParameters#ServiceTest()} for each Parameters entered before
     */
    @Test
    public void ServiceTest() {
        Orders order = new Orders();
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setPrice(price);
        ServiceToTest.createOrder(order);
	   System.out.println("id : " + order.getId());
           Assert.assertTrue(order.getId() > 0);
    }

}
