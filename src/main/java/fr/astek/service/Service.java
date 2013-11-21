package fr.astek.service;

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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;

import fr.astek.bean.Client;
import fr.astek.bean.Order;
import fr.astek.bean.User;
import fr.astek.error.BusinessException;
import fr.astek.error.TechnicalException;

public class Service {
    
    private static EntityManagerFactory EMF = Persistence.createEntityManagerFactory("pu");
    
    /**
     * Create a new {@link User} and return the saved instance with its id
     * @param the <b>not null</b> {@link User} to persist
     * <ul>
     * <li>the <b>unique, not null</b> {@link User#getLogin()} must match ^[a-z]{3,32}$</li>
     * <li>the <b>not null</b> {@link User#getRole()}</li>
     * </ul>
     * @return the persisted user
     */
    
    public static User createUser(User user) {
        Preconditions.checkNotNull(user, "service.createUser.client.null");
        EntityManager em = EMF.createEntityManager();
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        return user;
    }
    
    /**
     * Create a new {@link Client} and return the saved instance with its id
     * @param the <b>not null</b> {@link Client} to persist
     * <ul>
     * <li>the <b>unique, not null</b> {@link Client#getSIRET()} must match "[0-9]{14}"</li>
     * <li>the <b>not null</b> {@link Client#getRaisonSociale()}</li>
     * <ul>
     * @return the persisted client
     */
    public static Client createClient(Client client) {
        Preconditions.checkNotNull(client, "service.createClient.client.null");
        EntityManager em = EMF.createEntityManager();
        em.getTransaction().begin();
        em.persist(client);
        em.getTransaction().commit();
        return client;
    }
    
    /**
     * Create a new {@link Order} and return the saved instance with its id
     * @param the <b>not null</b> {@link Order} to persist
     * <ul>
     * <li>the <b>unique, not null</b> {@link Order#getProduct()} must match ""</li>
     * <li>the <b>not null</b> {@link Order#getQuantity()} must be > 0</li>
     * <li>the <b>not null</b> {@link Order#getPrice()}must be > 0</li>
     * </ul>
     * @return the persisted client
     */
    public static Order createOrder(Order order) {
        
        Preconditions.checkNotNull(order, "service.createOrder.order.null");
        Preconditions.checkNotNull(order.getProduct(), "service.createOrder.order.product.null");
        Preconditions.checkArgument(order.getQuantity() > 0, "service.createOrder.order.quantity.badValue");
        Preconditions.checkArgument(order.getPrice() > 0, "service.createOrder.order.price.badValue");
        Preconditions.checkArgument(!order.getProduct().matches("^[a-zA-Z0-9 _-éèà']{3,32}$"), "service.createOrder.product.pattern");
        
        EntityManager em = EMF.createEntityManager();
        em.getTransaction().begin();
        em.persist(order);
        em.getTransaction().commit();
        
        return order;
    }
    
    /**
     * Generates INVOICES for each ORDER associated with the given clients
     * @param currentUser : the <b>not null, connected, admin</b> {@link User}
     * @param clients : the <b>not null, not empty, immutable</b> list of {@link Client}"
     * @return logs
     * @throws TechnicalException, BusinessException 
     */
    public static Collection<String> generateInvoices(User currentUser, ImmutableCollection<Client> clients) throws TechnicalException, BusinessException {
        
        Preconditions.checkNotNull(currentUser, "service.generateInvoices.currentUser.null");
        Preconditions.checkNotNull(User.Role.ADMIN.equals(currentUser.getRole()), "service.generateInvoices.currentUser.role.notAdmin");
        Preconditions.checkNotNull(clients, "service.generateInvoices.currentClient.null");
        Preconditions.checkArgument(!clients.isEmpty(), "service.generateInvoices.currentClients.empty");
        
        Collection<String> logs = new ArrayList<>();
        EntityManager em = EMF.createEntityManager();
       
        for (Client curClient : clients) {

            if (em.find(Client.class, curClient.getId()) == null){
                continue;
            }
                       
            // Retrieving orders from current client
            Query query = em.createQuery("from Orders o where o.idClient = :idClient");
            query.setParameter("idClient", curClient.getId());
            List<Order> orders = query.getResultList();       
            
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
                    throw new TechnicalException(e);
                }
            } else {
                logs.add(filePath.toAbsolutePath() + " overwrited");
            }
                
            try (final BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.ISO_8859_1, StandardOpenOption.WRITE)) {
                //Writes headers
                String header = "Facture pour " + curClient.getRaisonSociale() ;
                writer.write(header + System.lineSeparator() + System.lineSeparator());
                for (Order order : orders){
                    //  Writes order lines
                    String line = order.getProduct() + " x " + order.getQuantity() + " = "  + (order.getPrice()*order.getQuantity() + " Euros");
                    writer.write("\t" + line + System.lineSeparator());
                }   
            } catch(Exception e) {
                throw new TechnicalException(e);
            }
        } 
        return logs;
    }
    
}
