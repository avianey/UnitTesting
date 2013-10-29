/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.astek.serviceTest;


import fr.astek.internal.bean.Orders;
import fr.astek.internal.bean.Client;
import fr.astek.internal.bean.User;
import fr.astek.internal.error.BusinessException;
import fr.astek.internal.error.TechnicalException;
import fr.astek.service.ServiceToTest;


import java.util.ArrayList;
import java.util.Collection;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author dlebert
 */

public class ServiceTest {

    private static final User getPassingUser() {
        User u = new User();
        u.setLogin("login");
        u.setRole(User.Role.ADMIN);
        return u;
    }
    
    private static final Client getPassingClient() {
        Client client = new Client();
        client.setSiret("12345678912345");
        client.setRaisonSociale("Test client");
        return client;
    }
    
    /**
     * Verify that the {@link ServiceToTest#createUser(fr.astek.internal.bean.User)} method behave properly
     * <ul>
     * <li>rule 1 : The returned {@link User#getId()} != 0 if the creation is successfull</li>
     * <li>rule 2 : The {@link User} parameter can't be null</li>
     * <li>rule 3 : The {@link User#getLogin()} verifies ^[a-z]{3,32}$</li>
     * <li>rule 4 : The {@link User#getRole()} or {@link User#getLogin()} can't be null</li>
     * <li>rule 5 : the creation fails if a {@link User} with the same {@link User#getLogin()} already exists</li>
     * </ul>
     */
    @Test
    public void testCreateUser() throws BusinessException {
        
        // Rule 1 : Successfull creation return an id > 0
        User user = getPassingUser();
        user = ServiceToTest.createUser(user);
        Assert.assertNotNull("ServiceToTest.createUser with a valid user should create and return the user", user);
        Assert.assertNotNull("ServiceToTest.createUser with a valid user should return a user.getId() > 0", user.getId());
        
   
        // Rule 2 : user null 
        user = null;
        try{
            user = ServiceToTest.createUser(user);
        } catch(NullPointerException e){
            //ignore, this exception is expected.
        }
        Assert.assertNull("User parameter is null", user);

        // Rule 3 : Login not matching ^[a-z]{3,32}$
        user = getPassingUser();
        user.setLogin("TT@/%oOOo");
        
        try{
            user = ServiceToTest.createUser(user);
            
}       catch(IllegalArgumentException e){
            //ignore, this exception is expected.
        }
        
        Assert.assertEquals("User.getLogin not matching regex ^[a-z]{3,32}$ ", 0, user.getId());
        
        // Rule 4 : The login is null
        user = getPassingUser();
        user.setLogin(null);
        try{
            user = ServiceToTest.createUser(user);
              
        } catch(NullPointerException e){
            //ignore, this exception is expected.
        }
        Assert.assertEquals("User.getLogin is null", 0, user.getId());
        
        // Rule 4 : The role is null
        user = getPassingUser();
        user.setRole(null);
        try{
            user = ServiceToTest.createUser(user);
            
        } catch(NullPointerException e){
            //ignore, this exception is expected.
        }
        Assert.assertEquals("User.getRole is null ", user.getId(), 0);

        // Rule 5 : Login already used
        user = getPassingUser();
        try {
            user = ServiceToTest.createUser(user);
        } catch(BusinessException e){
            //ignore, this exception is expected.
        }
        Assert.assertTrue("Should not be able to create a user with an already used login",user.getId() == 0);
  
        
        //User u = Mockito.mock(User.class);
        //Mockito.when(u.getRole()).thenReturn(User.Role.CONSULTANT);
        //Mockito.when(u.getRole()).thenReturn(User.Role.ADMIN);
        
    }  
    
    /**
     * Verify that the {@link ServiceToTest#testCreateClient(fr.astek.internal.bean.User)} method behave properly
     * <ul>
     * <li>rule 1 : The returned {@link User#getId()} != 0 if the creation is successfull</li>
     * <li>rule 2 : The {@link Client} parameter can't be null</li>
     * <li>rule 3 : The {@link Client#getSiret()} verifies [0-9]{14}</li>
     * <li>rule 4 : The {@link Client#getRaisonSociale()} verifies ^[a-zA-Z0-9 _-éèà']{3,32}$</li>
     * <li>rule 5 : The {@link Client#getRole()} or {@link Client#getRaisonSociale()} can't be null</li>
     * <li>rule 6 : the creation fails if a {@link Client} with the same {@link Client#getSiret()} already exists</li>
     * <li>rule 7 : An exception is thrown if the creation fails</li>
     * </ul>
     */
    
    @Test
    public void testCreateClient() throws BusinessException {
        
        // Rule 1 : Creation successfull id != 0  
        Client client = getPassingClient();
        client = ServiceToTest.createClient(client);
        Assert.assertNotNull("ServiceToTest.createClient with a valid client should create and return the client", client);
        Assert.assertNotNull("ServiceToTest.createClient with a valid client should return a client.getId() != 0", client.getId());
        
        // Rule 2 : Client parameter null
        client = null;
        try {
            client = ServiceToTest.createClient(client);
        } catch(NullPointerException e){
            //ignore, this exception is expected.
        }
        Assert.assertNull("Client parameter is null", client);
               
        // Rule 3 : Client.getSiret not matching [0-9]{14}
        client = getPassingClient();
        client.setSiret("zeffrfre87r8e7fverf4");
        try {
            client = ServiceToTest.createClient(client);
        } catch(BusinessException e){
            //ignore, this exception is expected.
        }
        Assert.assertTrue("Not matching Client.getSiret", client.getId() == 0);
        
        // Rule 4 : Client.getRaisonSociale not matching ^[a-zA-Z0-9 _-éèà']{3,32}$
        client = getPassingClient();
        client.setRaisonSociale("FT");
        try {
            client = ServiceToTest.createClient(client);
        } catch(IllegalArgumentException e){
            //ignore, this exception is expected.
        }
        Assert.assertTrue("Not matching Client.getRaisonSociale", client.getId() == 0);
        
        // Rule 5 : Client.getRaisonSociale is null
        client = getPassingClient();
        client.setRaisonSociale(null);
        try {
            client = ServiceToTest.createClient(client);
        } catch(NullPointerException e){
            //ignore, this exception is expected.
        }
        Assert.assertTrue("Client.getRaisonSociale is null", client.getId() == 0);
        
        // Rule 6 : Client.getSiret is null
        client = getPassingClient();
        client.setSiret(null);
        try {
            client = ServiceToTest.createClient(client);
        } catch(NullPointerException e){
            //ignore, this exception is expected.
        }
        Assert.assertTrue("Client.getSiret is null", client.getId() == 0);
        
        // Rule 7 : Client.getSiret already used
        client = getPassingClient();
        try {
            client = ServiceToTest.createClient(client);
        } catch(BusinessException e){
            //ignore, this exception is expected.
        }
        Assert.assertTrue("Client.getSiret already used", client.getId() == 0);
 
    }
    
    /**
     * Verify that the {@link ServiceToTest#testGenerateInvoice(fr.astek.internal.bean.User, @literal Collection<fr.astek.internal.bean.Client>)} method behave properly
     * <ul>
     * <li>rule 1 : Return ..... TODO</li>
     * <li>rule 2 : The {@link User} parameter not null</li>
     * <li>rule 3 : The {@link User#getRole() } can't be null</li>
     * <li>rule 4 : The {@link User#getRole() } must be User.Role.ADMIN</li>
     * <li>rule 5 : The {@literal Collection<{@link Client}>} not null</li>
     * <li>rule 6 : The {@literal Collection<{@link Client}>} not empty</li>
     * <li>rule ... : TO complete</li>
     * </ul>
     */
    @Test
    public void testGenerateInvoice() throws TechnicalException, BusinessException {
        
        Collection<String> result = new ArrayList<>();
        
        //Cas passant 
        
        //Creating a valid user
        User user = ServiceTest.getPassingUser();
        user.setLogin("dlebert");
        user.setRole(User.Role.ADMIN);
        ServiceToTest.createUser(user);
        
        //Creating a valid Client list
        
        Collection<Client> clients = new ArrayList();
        
        Client ft = new Client();
        ft.setSiret("11111111111111");
        ft.setRaisonSociale("France Telecom");
        ServiceToTest.createClient(ft);
        
        clients.add(ft);
 
        Orders achatFt = new Orders();
        achatFt.setIdClient(ft.getId());
        achatFt.setProduct("Logiciel");
        achatFt.setPrice(new Double(350.25));
        achatFt.setQuantity(3);
        ServiceToTest.createOrder(achatFt);
       
        result = ServiceToTest.generateInvoices(user, clients);
        
        //TODO ASSERT
        Assert.assertFalse("ServiceToTest.generateInvoices successfull should return a result not empty", result.isEmpty());
        
        // Rule 2 : user parameter not null
        result = new ArrayList<>();
        user = null;
        try{
            result = ServiceToTest.generateInvoices(user, clients);
        } catch (NullPointerException e){
            //Expected exception
        }
        Assert.assertTrue("ServiceToTest.generateInvoices with parameter User null return nothing", result.isEmpty());
        
        // Rule 3 : User.getRole is null
        result = new ArrayList<>();
        user = getPassingUser();
        user.setRole(null);
        try{
            result = ServiceToTest.generateInvoices(user, clients);
        } catch (NullPointerException e){
            //Expected exception
        }
        Assert.assertTrue("ServiceToTest.generateInvoices with parameter User.getRole null should return nothing", result.isEmpty());
        
        // Rule 4 : User.getRole not User.Role.ADMIN
        result = new ArrayList<>();
        user = getPassingUser();
        user.setRole(User.Role.CONSULTANT);
        try{
            result = ServiceToTest.generateInvoices(user, clients);
        } catch (IllegalArgumentException e){
            //Expected exception
        }
        Assert.assertTrue("ServiceToTest.generateInvoices with parameter User.getRole not User.Role.ADMIN should return nothing", result.isEmpty());
        
        // Rule 5 : clients parameter null
        user = getPassingUser();
        clients = null;
        try{
            result = ServiceToTest.generateInvoices(user, clients);
        } catch (NullPointerException e){
            //Expected exception
        }
        Assert.assertTrue("ServiceToTest.generateInvoices with parameter clients null should return nothing", result.isEmpty());
        
        // Rule 6 : clients parameter empty
        user = getPassingUser();
        clients = new ArrayList();
        try{
            result = ServiceToTest.generateInvoices(user, clients);
        } catch (IllegalArgumentException e){
            //Expected exception
        }
        Assert.assertTrue("ServiceToTest.generateInvoices with parameter clients empty should return nothing", result.isEmpty());

        // Rule 6 : client not registered in database
        user = getPassingUser();
        clients = new ArrayList();
        Client client = ServiceTest.getPassingClient();
        clients.add(client);
        try{
            result = ServiceToTest.generateInvoices(user, clients);
        } catch (BusinessException e){
            //Expected exception
        }
        Assert.assertTrue("ServiceToTest.generateInvoices with parameter clients empty should return nothing", result.isEmpty());

        // Rule 7 : client with no orders
        user = getPassingUser();
        clients = new ArrayList();
        client = ServiceTest.getPassingClient();
        client.setRaisonSociale("Client without order");
        client.setSiret("12121212121221");
        client = ServiceToTest.createClient(client);
        
        clients.add(client);
        try{
            result = ServiceToTest.generateInvoices(user, clients);
        } catch (BusinessException e){
            //Expected exception
        }
        Assert.assertTrue("ServiceToTest.generateInvoices with parameter clients empty should return nothing", result.isEmpty());

        
    }
}
