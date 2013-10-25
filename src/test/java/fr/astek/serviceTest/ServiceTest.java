/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.astek.serviceTest;


import fr.astek.internal.bean.Achat;
import fr.astek.internal.bean.Client;
import fr.astek.internal.bean.User;
import fr.astek.internal.error.TechnicalException;
import fr.astek.service.ServiceToTest;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;


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
    
    /**
     * Verify that the {@link ServiceToTest#createUser(fr.astek.internal.bean.User)} method behave properly
     * <ul>
     * <li>rule 1 : The {@link User} parameter is not null</li>
     * <li>rule 2 : The {@link User#getLogin()} verifies TODO</li>
     * <li>rule 3 : The {@link User#getRole()} is not null</li>
     * <li>rule 4 : An exception is thrown if the creation fails</li>
     * <li>rule 5 : The returned {@link User#getId()} is not null is the creation is successfull</li>
     * <li>rule 6 : the creation fails if a {@link User} with the same {@link User#getLogin()} already exists</li>
     * </ul>
     */
    @Test
    public void testCreateUser() {
        
        //User valid
        User user = getPassingUser();
        user = ServiceToTest.createUser(user);
        Assert.assertNotNull("ServiceToTest.createUser with a valid user should create and return the user", user);
        Assert.assertNotNull("ServiceToTest.createUser with a valid user should return a user.getId() not null", user.getId());
        
   
        //Rule 1 : {@link User} null 
        user = null;
        try{
            user = ServiceToTest.createUser(user);
        } catch(NullPointerException e){
            //ignore, this exception is expected.
        }
        Assert.assertNull("rule 1", user);

        //Rule 2 : Login not matching ^[a-z]{3,32}$
        user = getPassingUser();
        user.setLogin("TT@/%oOOo");
        
        try{
            user = ServiceToTest.createUser(user);
            
}       catch(IllegalArgumentException e){
            //ignore, this exception is expected.
        }
        
        Assert.assertEquals("User.getLogin not matching regex ^[a-z]{3,32}$ ", user.getId(), 0);
        
        //Rule 3 : The {@link User#getRole()} is not null
        user = getPassingUser();
        user.setRole(null);
        try{
            user = ServiceToTest.createUser(user);
            
        } catch(NullPointerException e){
            //ignore, this exception is expected.
        }
        
        Assert.assertEquals("User.getRole is null ", user.getId(), 0);

        //Rule 6 : Tying to persist a user with an already taken login
        user = getPassingUser();
        try{
            user = ServiceToTest.createUser(user);
        } catch(RollbackException e){
            //ignore, this exception is expected.
        }
        Assert.assertEquals("Should not be able to create a user with an already used login", user.getId(), 0);
        
        
        User u = Mockito.mock(User.class);
        Mockito.when(u.getRole()).thenReturn(User.Role.CONSULTANT);
        
        Mockito.when(u.getRole()).thenReturn(User.Role.ADMIN);
        
    }        
    
    @Test
    public void testCreateClient() {
        
        Client client = new Client();
        client.setSiret("12345678912345");
        client.setRaisonSociale("France Telecom");
        Assert.assertTrue("Un client valide n'a pas pu être crée", ServiceToTest.createClient(client));
        
        //Création client avec une raison sociale non valide
        Client badClient = new Client();
        badClient.setSiret("12345678912345");
        badClient.setRaisonSociale("FT");
        Assert.assertFalse("Un client avec une raison sociale non valide a été crée", ServiceToTest.createClient(badClient));
        
        //Création d'un client vide
        Client emptyClient = new Client();
        Assert.assertFalse("Un client vide a été crée", ServiceToTest.createClient(emptyClient));
        
    }
    @Test
    public void testGenerateInvoice() throws TechnicalException {
    
        //Création d'un utilisateur valide
        User user = new User();
        user.setLogin("dlebert");
        user.setRole(User.Role.ADMIN);
        ServiceToTest.createUser(user);
        
        //Création de la liste clients
        
        Collection<Client> clients = new ArrayList();
        
        Client ft = new Client();
        ft.setSiret("12345678912345");
        ft.setRaisonSociale("France Telecom");
        ServiceToTest.createClient(ft);
        
        clients.add(ft);
 
        Achat achatFt = new Achat();
        achatFt.setIdClient(ft.getId());
        achatFt.setProduit("Logiciel");
        achatFt.setPrixUnitaire(new Double(350.25));
        achatFt.setQuantite(3);
        ServiceToTest.createAchat(achatFt);
        
        Client clientNonEnregistre = new Client();
        clientNonEnregistre.setSiret("12345678912345");
        clientNonEnregistre.setRaisonSociale("Client non enregistré");
        
        clients.add(clientNonEnregistre);
        
        Client clientSansAchat = new Client();
        clientSansAchat.setSiret("12345678912345");
        clientSansAchat.setRaisonSociale("CLIENT sans achat");
        ServiceToTest.createClient(clientSansAchat);
        
        clients.add(clientSansAchat);
       
        ServiceToTest.generateInvoices(user, clients);
   
    }
    
}
