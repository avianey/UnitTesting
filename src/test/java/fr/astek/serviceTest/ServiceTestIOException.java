/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.astek.serviceTest;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.byteman.contrib.bmunit.BMRule;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import fr.astek.bean.Client;
import fr.astek.bean.Order;
import fr.astek.bean.User;
import fr.astek.error.BusinessException;
import fr.astek.error.TechnicalException;
import fr.astek.service.Service;
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
        Service.createClient(ft);
        clients.add(ft);
        
        //Fake client pointing to ft.getId()
        for (int i=1; i<5; i++){
            Client client = Mockito.mock(Client.class);
            Mockito.when(client.getId()).thenReturn(ft.getId());
            Mockito.when(client.getRaisonSociale()).thenReturn("Client"+i);
            clients.add(client);
        }
        
    }
}
