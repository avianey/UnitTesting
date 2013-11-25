package fr.astek.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

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
     * <li>the <b>unique, not null</b> {@link Order#getRef()}</li>
     * <li>the <b>not null</b> {@link Order#getQuantity()} must be > 0</li>
     * <li>the <b>not null</b> {@link Order#getPrice()}must be >= 0.01</li>
     * </ul>
     * @return the persisted client
     */
    public static Order createOrder(Order order) {
        EntityManager em = EMF.createEntityManager();
        em.getTransaction().begin();
        em.persist(order);
        em.getTransaction().commit();
        return order;
    }

    public static final String PRECONDITION_USER_NULL = "service.generateInvoices.user.null";
    public static final String PRECONDITION_USER_ROLE = "service.generateInvoices.user.role";
    public static final String PRECONDITION_CLIENTS_NULL = "service.generateInvoices.clients.null";
    public static final String PRECONDITION_CLIENTS_EMPTY = "service.generateInvoices.clients.empty";
    
    /**
     * Generates INVOICES for each ORDER associated with the given clients
     * @param user : the <b>not null, connected, admin</b> {@link User}
     * @param clients : the <b>not null, not empty, immutable</b> list of {@link Client}"
     * @return list of created orders
     * @throws TechnicalException, BusinessException 
     */
    public static Collection<String> generateInvoices(User user, ImmutableCollection<Client> clients) throws TechnicalException, BusinessException {
        
        Preconditions.checkNotNull(user, PRECONDITION_USER_NULL);
        Preconditions.checkArgument(User.Role.ADMIN.equals(user.getRole()), PRECONDITION_USER_ROLE);
        Preconditions.checkNotNull(clients, PRECONDITION_CLIENTS_NULL);
        Preconditions.checkArgument(!clients.isEmpty(), PRECONDITION_CLIENTS_EMPTY);
        
        Collection<String> logs = new ArrayList<>();
        EntityManager em = EMF.createEntityManager();
       
        for (Client client : clients) {

            if (em.find(Client.class, client.getId()) == null) {
                continue;
            }
                       
            // Retrieving orders from current client
            final TypedQuery<Order> query = em.createQuery("FROM " + Order.class.getSimpleName() + " o where o.client = :client", Order.class);
            query.setParameter("client", client);
            List<Order> orders = query.getResultList();       
            
            // No order from this client
            if (orders.isEmpty()){
                continue;
            }

            final Path orderPath = Paths.get(
                    new StringBuilder(client.getSiret())
                            .append(new SimpleDateFormat("yyyyddMMHHmmss").format(new Date()))
                            .append(".txt")
                            .toString());
            
            // Creates the file if not already existing
            if (!Files.exists(orderPath)) {
                try {
                    Files.createFile(orderPath);
                } catch (IOException e) {
                    throw new TechnicalException(e);
                }
            }
            logs.add(orderPath.toAbsolutePath().toString());

            try (final BufferedWriter writer = Files.newBufferedWriter(orderPath, StandardCharsets.UTF_8, StandardOpenOption.WRITE)) {
                // Writes headers
                writer.write("INVOICE for " + client.getRaisonSociale() + System.lineSeparator());
                for (Order order : orders){
                    //  Writes order lines
                    writer.write(
                            new StringBuilder("\t")
                                    .append(order.getRef())
                                    .append(" x ")
                                    .append(order.getQuantity())
                                    .append(" = ")
                                    .append(String.valueOf((order.getPrice() * order.getQuantity())))
                                    .append(" USD")
                                    .append(System.lineSeparator())
                                    .toString());
                }   
            } catch(Exception e) {
                throw new TechnicalException(e);
            }
        } 
        return logs;
    }
    
}
