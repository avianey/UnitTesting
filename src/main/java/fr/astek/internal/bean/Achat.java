/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.astek.internal.bean;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author dlebert
 */

@Entity
public class Achat implements Serializable  {
    
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    private long id;
    
    @Column
    private long idClient;
    
    @Column
    private String produit;
    
    @Column
    private int quantite;
    
    @Column
    private double prixUnitaire;
    


    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the idClient
     */
    public long getIdClient() {
        return idClient;
    }

    /**
     * @param idClient the idClient to set
     */
    public void setIdClient(long idClient) {
        this.idClient = idClient;
    }

    /**
     * @return the produit
     */
    public String getProduit() {
        return produit;
    }

    /**
     * @param produit the produit to set
     */
    public void setProduit(String produit) {
        this.produit = produit;
    }

    /**
     * @return the quantite
     */
    public int getQuantite() {
        return quantite;
    }

    /**
     * @param quantite the quantite to set
     */
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    /**
     * @return the prixUnitaire
     */
    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    /**
     * @param prixUnitaire the prixUnitaire to set
     */
    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

}
