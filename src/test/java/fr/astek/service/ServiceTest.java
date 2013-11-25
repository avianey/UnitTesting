package fr.astek.service;

import java.io.IOException;
import java.util.Collection;

import org.jboss.byteman.contrib.bmunit.BMRule;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;

import fr.astek.bean.Client;
import fr.astek.bean.Order;
import fr.astek.bean.User;
import fr.astek.bean.User.Role;
import fr.astek.error.BusinessException;
import fr.astek.error.TechnicalException;
import fr.astek.test.AbstractBeforeClassTest;

@RunWith(BMUnitRunner.class)
public class ServiceTest extends AbstractBeforeClassTest {
    
    private static Client client;

    @BeforeClass
    public static void createDataSet() {
        // one client
        client = new Client();
        client.setRaisonSociale("Astek");
        client.setSiret("12345678901234");
        Service.createClient(client);
        // two orders
        Order order1 = new Order();
        order1.setQuantity(1);
        order1.setPrice(10.50f);
        order1.setRef("ref1");
        order1.setClient(client);
        Service.createOrder(order1);
        Order order2 = new Order();
        order2.setQuantity(5);
        order2.setPrice(33.33f);
        order2.setRef("ref1");
        order2.setClient(client);
        Service.createOrder(order2);
    }

    @Test
    public void generateInvoicePreconditions() throws TechnicalException, BusinessException {

        Collection<String> result;
        User admin = Mockito.mock(User.class);
        Mockito.when(admin.getRole()).thenReturn(User.Role.ADMIN);
        ImmutableList<Client> clients = new ImmutableList.Builder<Client>().add(client).build();
        
        /****************
         * PASSING CASE *
         ****************/
        
        result = Service.generateInvoices(admin, clients);
        Assert.assertNotNull("No result provided", result);
        Assert.assertFalse("No INVOICE generated", result.isEmpty());
        Assert.assertEquals("More than one INVOICE generated", 1, result.size());
        
        /*****************
         * FAILING CASES *
         *****************/
        
        // null clients
        try {
            result = Service.generateInvoices(admin, null);
            Assert.fail("Argument clients cannot be null");
        } catch (NullPointerException e) {
            Assert.assertEquals("Technical exception occured", Service.PRECONDITION_CLIENTS_NULL, e.getMessage());
        }
        
        // admin null
        try {
            result = Service.generateInvoices(null, clients);
            Assert.fail("Argument user cannot be null");
        } catch (NullPointerException e) {
            Assert.assertEquals("Technical exception occured", Service.PRECONDITION_USER_NULL, e.getMessage());
        }
        
        // clients empty
        try {
            result = Service.generateInvoices(admin, new ImmutableList.Builder<Client>().build());
            Assert.fail("Argument clients cannot be empty");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Technical exception occured", Service.PRECONDITION_CLIENTS_EMPTY, e.getMessage());
        }
        
        // user not admin
        Mockito.when(admin.getRole()).thenReturn(Role.CONSULTANT);
        try {
            result = Service.generateInvoices(admin, clients);
            Assert.fail("Argument user is not an ADMIN");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Technical exception occured", Service.PRECONDITION_USER_ROLE, e.getMessage());
        }
        
    }
    

    /**
     * Verify that the {@link Service#generateInvoices(User, com.google.common.collect.ImmutableCollection)} method behave properly when an IOException occurs.<br/>
     * ByteMan will throw a IOException whenever the {@link  java.nio.file.Files#createFile(java.nio.file.Path, java.nio.file.attribute.FileAttribute...)} method is called.
     */
    @BMRule(
        name="throw IOException",
        targetClass = "java.nio.file.Files",
        targetMethod = "createFile",
        action = "throw new java.io.IOException()"
    )
    @Test
    public void testGenerateInvoiceIOException() {
        try {

            Collection<String> result;
            User admin = Mockito.mock(User.class);
            Mockito.when(admin.getRole()).thenReturn(User.Role.ADMIN);
            ImmutableList<Client> clients = new ImmutableList.Builder<Client>().add(client).build();
            
            /****************
             * PASSING CASE *
             ****************/
            
            result = Service.generateInvoices(admin, clients);
            Assert.assertNotNull("No result provided", result);
            Assert.assertFalse("No INVOICE generated", result.isEmpty());
            Assert.assertEquals("More than one INVOICE generated", 1, result.size());
        } catch (TechnicalException e) {
            Assert.assertTrue("IOException not correctly encapsulated", e.getCause() instanceof IOException);
        } catch (Exception e) {
            Assert.fail("IOException not correctly encapsulated");
            e.printStackTrace();
        }
    }
    
    
}
