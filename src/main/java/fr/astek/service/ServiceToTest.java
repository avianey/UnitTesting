/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.astek.service;

import com.google.common.base.Preconditions;
import fr.astek.internal.bean.*;
import fr.astek.internal.error.BusinessException;
import fr.astek.internal.error.TechnicalException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.RollbackException;


/**
 *
 * @author dlebert
 */
public class ServiceToTest {
    
    private static EntityManagerFactory EMF = Persistence.createEntityManagerFactory("pu");
    
    /**
     * Create a new {@link User} and return the saved instance with his id
     * @param user
     *          the <b>not null</b> {@link User} to persist
     *          the <b>unique, not null</b> {@link User#getLogin()} must match ^[a-z]{3,32}$
     *          the <b>not null</b> {@link User#getRole()}
     * @return the persisted user
     */
    
    public static User createUser(User user) throws BusinessException {
        
        // Preconditions
        Preconditions.checkNotNull(user, "servicetotest.createuser.user.null");
        Preconditions.checkNotNull(user.getLogin(), "servicetotest.createuser.user.getLogin().null");
        Preconditions.checkNotNull(user.getRole(), "servicetotest.createuser.user.getRole().null");
        Preconditions.checkArgument(user.getLogin().matches("^[a-z]{3,32}$"));

        // Service code   
        EntityManager em = EMF.createEntityManager();
        em.getTransaction().begin();
        em.persist(user);
        try {
            em.getTransaction().commit();
        } catch(RollbackException e){
            //rollback changes if an exception occured
            em.getTransaction().begin();
            em.getTransaction().rollback();
            user.setId(0);
            throw new BusinessException("User \"" + user.getLogin() + "\"couldn't be created. Constraint error");
        }
        return user;
    }
    
    /**
     * Create a new {@link Client} and return the saved instance with his id
     * @param client
     *          the <b>not null</b> {@link Client} to persist
     *          the <b>unique, not null</b> {@link Client#getSIRET()} must match "[0-9]{14}"
     *          the <b>not null</b> {@link Client#getRaisonSociale()} must match ^[a-zA-Z0-9 _-éèà']{3,32}$
     * @return the persisted client
     */
    
    public static Client createClient(Client client) throws BusinessException {
        
        Preconditions.checkNotNull(client, "servicetotest.createclient.client.null");
        Preconditions.checkNotNull(client.getSiret(), "servicetotest.createclient.user.getSiret().null");
        Preconditions.checkNotNull(client.getRaisonSociale(), "servicetotest.createuser.user.getRaisonSociale().null");
        Preconditions.checkArgument(client.getRaisonSociale().matches("^[a-zA-Z0-9 _-éèà']{3,32}$"));
        
        EntityManager em = EMF.createEntityManager();
        em.getTransaction().begin();
        em.persist(client);
        try {
            em.getTransaction().commit();
        } catch(RollbackException e){
            //rollback changes if an exception occured
            em.getTransaction().begin();
            em.getTransaction().rollback();        
            client.setId(0);
            throw new BusinessException("Client \"" + client.getRaisonSociale() + "\" couldn't be created. Constraint error");
        }
        return client;
    }
    
    /**
     * Create a new {@link Orders} and return the saved instance with his id
     * @param order
     *          the <b>not null</b> {@link Orders} to persist
     *          the <b>unique, not null</b> {@link Orders#getProduct()} must match ""
     *          the <b>not null</b> {@link Orders#getQuantity()} must be > 0
     *          the <b>not null</b> {@link Orders#getPrice()}must be > 0
     * @return the persisted client
     */

    public static Orders createOrder(Orders order) {
        
        //TODO Preconditions
        Preconditions.checkNotNull(order, "servicetotest.createOrder.order.null");
        Preconditions.checkNotNull(order.getProduct(), "servicetotest.createOrder.order.getProduct().null");
        Preconditions.checkArgument(order.getQuantity() > 0, "servicetotest.createOrder.order.getQuantity() < 0");
        Preconditions.checkArgument(order.getPrice() > 0, "servicetotest.createOrder.order.getPrice() < 0");
        Preconditions.checkArgument(order.getProduct().matches("^[a-zA-Z0-9 _-éèà']{3,32}$"));
        
        EntityManager em = EMF.createEntityManager();
        em.getTransaction().begin();
        em.persist(order);
        em.getTransaction().commit();
        
        return order;
    }
    
    /**
     * 
     * @param currentUser
     * @param clients
     *          the <b>not null, connected, admin</b> {@link User} 
     *          the <b>not null, not empty</b> List of client"
     * @return Actions performed logs
     * @throws TechnicalException, BusinessException 
     */
    public static Collection<String> generateInvoices(User currentUser, Collection<Client> clients) 
            throws TechnicalException, BusinessException {
        
        Collection<String> logs = new ArrayList<>();
        EntityManager em = EMF.createEntityManager();
       
        // Préconditions
        // User 
            // Not null 
        Preconditions.checkNotNull(currentUser, "ServiceToTest.generateInvoices.currentUser.null");
            // Role not null(connected) 
        Preconditions.checkNotNull(currentUser.getRole(), "ServiceToTest.generateInvoices.currentUser.getRole().null");
            // Admin
        Preconditions.checkArgument(currentUser.getRole().equals(User.Role.ADMIN), "ServiceToTest.generateInvoices.currentUser.getRole().notAdmin");
        
        // Clients
            // List not null
        Preconditions.checkNotNull(clients, "ServiceToTest.generateInvoices.currentClient.null");
            // List not empty
        Preconditions.checkArgument(!clients.isEmpty(), "ServiceToTest.generateInvoices.currentClients.empty");
        
        for (Client curClient : clients) {

            // Current Client 
                // register in database
            if (em.find(Client.class, curClient.getId()) == null){
                continue;
            }
                       
            // Retrieving orders from current client
            Query query = em.createQuery("from Orders o where o.idClient = :idClient");
            query.setParameter("idClient", curClient.getId());
            List<Orders> orders = query.getResultList();       
            
            // No order from this client
            if (orders.isEmpty()){
                continue;
            }

            Path filePath = Paths.get("Invoice-" + curClient.getRaisonSociale() + ".txt");
            
            //Creates the file if not already existing
            if (!Files.exists(filePath)) {
                try {
                    Files.createFile(filePath);
                    logs.add(filePath.toAbsolutePath() + " created");
                } catch (IOException e) {
                    logs.clear();
                    throw new TechnicalException(e);
                }
            } else {
                logs.add(filePath.toAbsolutePath() + " overwrited");
            }
                
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, 
                         StandardCharsets.ISO_8859_1, StandardOpenOption.WRITE)) {
                //Writes headers
                String header = "Facture pour " + curClient.getRaisonSociale() ;
                writer.write(header + System.lineSeparator() + System.lineSeparator());
                for (Orders order : orders){
                    //  Writes order lines
                    String line = order.getProduct() + " x " + order.getQuantity() + " = "  + (order.getPrice()*order.getQuantity() + " Euros");
                    writer.write("\t" + line + System.lineSeparator());
                }   
            } catch(Exception e) {
                logs.clear();
                throw new TechnicalException(e);
            }
        } 
        return logs;
    }
    
}
