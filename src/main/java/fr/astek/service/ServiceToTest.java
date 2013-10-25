/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.astek.service;

import com.google.common.base.Preconditions;
import fr.astek.internal.bean.*;
import fr.astek.internal.error.TechnicalException;
import fr.astek.internal.service.InvoiceGenerator;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


/**
 *
 * @author dlebert
 */
public class ServiceToTest {
    
    private static EntityManagerFactory EMF = Persistence.createEntityManagerFactory("pu");
    
    /**
     * Create a new {@link User} and return the saved instance with it's id
     * @param user
     *          the <b>non null</b> {@link User} to persist
     *          the <b>non null</b> {@link User#getLogin()} must match ^[a-z]{3,32}$
     *          the <b>non null</b> {@link User#getRole()}
     * @return the persisted user
     */
    
    public static User createUser(User user) {
        // preconditions
        Preconditions.checkNotNull(user, "servicetotest.createuser.user.null");
        Preconditions.checkNotNull(user.getLogin(), "servicetotest.createuser.user.getLogin.null");
        Preconditions.checkNotNull(user.getRole(), "servicetotest.createuser.user.getRole.null");
        Preconditions.checkArgument(user.getLogin().matches("^[a-z]{3,32}$"));
        Preconditions.checkArgument(true);
        // service code
        EntityManager em = EMF.createEntityManager();
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        return user;
    }
    
    public static boolean createClient(Client client) {
        
        boolean result = true;
        if (client.getRaisonSociale()== null){
            result = false;
        }
        else if (! client.getRaisonSociale().matches("^[a-zA-Z0-9 _-éèà']{3,32}$")){
            result = false;
        }
        else {
            EntityManager em = EMF.createEntityManager();
            em.getTransaction().begin();
            em.persist(client);
            em.getTransaction().commit();
        }   
        return result;
    }
    // TODO Achat >> Order
    public static boolean createAchat(Achat achat) {
        boolean result = true;
        
        if (achat.getQuantite() < 1){
            result = false;
        }
        else {
            EntityManager em = EMF.createEntityManager();
            em.getTransaction().begin();
            em.persist(achat);
            em.getTransaction().commit();
        }
        return result;
    }
    
    
    /**
     * 
     * @param currentUser
     * @param clients
     * @return
     * @throws TechnicalException 
     */
    public static Collection<String> generateInvoices(User currentUser, Collection<Client> clients) 
            throws TechnicalException {
        
        boolean result = true;
        Collection<String> erreurs = new ArrayList<>();
        // précondition
        // user connecté / non vide, autorisé
        
        EntityManager em = EMF.createEntityManager();

        if (em.find(User.class, currentUser.getId()) == null){
            //TODO traitement erreur
            System.out.println("ERREUR : Utilisateur non trouvé");
            erreurs.add("ERREUR : UTILISATEUR - " + currentUser.getLogin() + " n'existe pas");
            result = false;
        } 
        
        if (result){
            for (Client curClient : clients) {
                
                // existence des clients en base
                if (em.find(Client.class, curClient.getId()) == null){
                    
                     erreurs.add("ERREUR : CLIENT - " +curClient.getRaisonSociale() + " n'existe pas");
                
                } else{
                    
                    // traitement
                    // recup des achats clients
                    
                    
                    System.out.println(curClient.getRaisonSociale());
                    Query query = em.createQuery("from Achat a  where a.idClient = :idClient");
                    query.setParameter("idClient", curClient.getId());
                    List<Achat> achats = query.getResultList();

                    for (Achat achat : achats){
                        System.out.println(achat.getProduit());
                    }
                    
                    // generation d'une facture sur le FS par client
                    HSSFWorkbook invoice;
                    invoice = InvoiceGenerator.generateInvoice(currentUser, curClient, achats);
                    
                    FileOutputStream fileOut;
                    try {
                      fileOut = new FileOutputStream("Invoice-" + curClient.getRaisonSociale() + ".xls");
                      invoice.write(fileOut);
                      fileOut.close();
                    } catch (IOException e) {
                        throw new TechnicalException(e);
                    }
                    
                    
                } 
   
            }

        }

        
        
        
        
        // generation du CR de traitement
        
        // retour
        // liste des erreurs
        return erreurs;
    }
    
    
}
