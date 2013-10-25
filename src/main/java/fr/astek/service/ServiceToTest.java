/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.astek.service;

import fr.astek.internal.bean.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author dlebert
 */
public class ServiceToTest {
    
    private static EntityManagerFactory EMF = Persistence.createEntityManagerFactory("pu");
    
    public static boolean createUser(User user) {
        boolean result = true;
        EntityManager em = EMF.createEntityManager();
        if (user.getLogin() == null){
            result = false;
        }
        else if (! user.getLogin().matches("^[a-z0-9_-]{3,32}$")){
            result = false;
        }
        else {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        }
        return result;
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
    
    public static Collection<String> generateInvoices(User currentUser, Collection<Client> clients) {
        
        boolean result = true;
        // précondition
        // user connecté / non vide, autorisé
        // existence des clients en base
        EntityManager em = EMF.createEntityManager();

        User test = em.find(User.class, currentUser.getId());
        if (test == null){
            //TODO traitement erreur
            System.out.println("ERREUR : Utilisateur non trouvé");
            result = false;
        } 
        
        if (result){
            for (Client curClient : clients) {

                if (em.find(Client.class, curClient.getId()) == null){
                    
                     System.out.println("ERREUR : Client non trouvé");
                
                } else{
                    
                    System.out.println(curClient.getRaisonSociale());
         
                } 
   
            }

        }
        
        

        // traitement
        // recup des achats clients
        // generation d'une facture sur le FS par client
        
        
        
        // generation du CR de traitement
        
        // retour
        // liste des erreurs
        return null;
    }
    
    
}
