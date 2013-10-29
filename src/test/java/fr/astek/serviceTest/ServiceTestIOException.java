/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.astek.serviceTest;

import fr.astek.internal.bean.Client;
import fr.astek.internal.bean.Orders;
import fr.astek.internal.bean.User;
import fr.astek.internal.error.BusinessException;
import fr.astek.internal.error.TechnicalException;
import fr.astek.service.ServiceToTest;
import java.util.ArrayList;
import java.util.Collection;
import org.jboss.byteman.contrib.bmunit.BMRule;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
/**
 *
 * @author dlebert
 */
@RunWith(BMUnitRunner.class)
public class ServiceTestIOException {
    
    /**
     * Verify that the {@link ServiceToTest#generateInvoices(fr.astek.internal.bean.User, java.util.Collection)} method behave properly
     * when an IOException is thrown
     * Byteman will throw a IOException whenever {@link ServiceToTest#generateInvoices(fr.astek.internal.bean.User, java.util.Collection)}
     * will call the method {@link  java.nio.file.Files#newBufferedWriter}
     */
    
    @BMRule(name="throw IOException",
            targetClass = "java.nio.file.Files",
            targetMethod = "newBufferedWriter",
            action = "throw new java.io.IOException()")
    @Test
    public void testGenerateInvoiceIOException() throws TechnicalException, BusinessException {
        
        Collection<String> result = new ArrayList<>();
        
        //Mocking a valid user
        User user = Mockito.mock(User.class);
        Mockito.when(user.getLogin()).thenReturn("dlebert");
        Mockito.when(user.getRole()).thenReturn(User.Role.ADMIN);
 
        //Creating a valid Client list
        Collection<Client> clients = new ArrayList();
        
        Client ft = new Client();
        ft.setSiret("11111122111111");
        ft.setRaisonSociale("Google");
        ServiceToTest.createClient(ft);
        clients.add(ft);
        
        //Fake client pointing to ft.getId()
        for (int i=1; i<5; i++){
            Client client = Mockito.mock(Client.class);
            Mockito.when(client.getId()).thenReturn(ft.getId());
            Mockito.when(client.getRaisonSociale()).thenReturn("Client"+i);
            clients.add(client);
        }
        
        //Assigning orders to Client ft
        Orders orderFt = new Orders();
        orderFt.setIdClient(ft.getId());
        orderFt.setProduct("Software");
        orderFt.setPrice(new Double(350.25));
        orderFt.setQuantity(3);
        ServiceToTest.createOrder(orderFt);
        
        orderFt = new Orders();
        orderFt.setIdClient(ft.getId());
        orderFt.setProduct("Hardware");
        orderFt.setPrice(new Double(354));
        orderFt.setQuantity(3);
        ServiceToTest.createOrder(orderFt);
        
        orderFt = new Orders();
        orderFt.setIdClient(ft.getId());
        orderFt.setProduct("MOA");
        orderFt.setPrice(new Double(1337.25));
        orderFt.setQuantity(3);
        ServiceToTest.createOrder(orderFt);
        
        try {
            result = ServiceToTest.generateInvoices(user, clients);
        } catch (TechnicalException e) {
            // Expected exception
        }
        Assert.assertTrue("ServiceToTest.generateInvoices throwing an exception should return an empty result", result.isEmpty());
    }
}
