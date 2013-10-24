/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.astek.internal.bean;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 *
 * @author dlebert
 */
public class LigneCommande {
    
    @Id
    private long id;
    
    @Column
    private long idCommande;
    
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
     * @return the idCommande
     */
    public long getIdCommande() {
        return idCommande;
    }

    /**
     * @param idCommande the idCommande to set
     */
    public void setIdCommande(long idCommande) {
        this.idCommande = idCommande;
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
