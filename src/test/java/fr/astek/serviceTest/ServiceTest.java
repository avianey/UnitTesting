/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.astek.serviceTest;


import fr.astek.internal.bean.Client;
import fr.astek.internal.bean.User;
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

    @Test
    public void testCreateUser() {
        
        //Création d'un utilisateur valide
        User user = new User();
        user.setLogin("login");
        user.setRole(User.Role.ADMIN);
        Assert.assertTrue("L'utilisateur valide n'a pas pu être crée correctement",ServiceToTest.createUser(user));

        //Création d'un utilisateur non valide (login)
        User badUser = new User();
        badUser.setLogin("TT");
        badUser.setRole(User.Role.ADMIN);
        Assert.assertFalse("Un utilisateur avec un login trop court a été crée", ServiceToTest.createUser(badUser));
        
        //Création d'un utilisateur non valide (login)
        User failChar = new User();
        failChar.setLogin("@!%BadCaract");
        failChar.setRole(User.Role.ADMIN);
        Assert.assertFalse("Un utilisateur avec un login contenant des caractères spéciaux a été crée", ServiceToTest.createUser(failChar));
        
        //Création d'un utilisateur vide
        User emptyUser = new User();
        Assert.assertFalse(ServiceToTest.createUser(emptyUser));
        
        
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
    public void testGenerateInvoice() {
    
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
        
        Client sncf = new Client();
        sncf.setSiret("12345678912345");
        sncf.setRaisonSociale("SNCF");
        ServiceToTest.createClient(sncf);
        
        clients.add(sncf);
        
        Client random = new Client();
        random.setSiret("12345678912345");
        random.setRaisonSociale("RANDOM client");
        ServiceToTest.createClient(random);
        
        clients.add(random);
       
        ServiceToTest.generateInvoices(user, clients);
   
    }
    
}
