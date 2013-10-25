/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.astek.internal.bean;

import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Id;

/**
 *
 * @author dlebert
 */
public class Commande {
    
    @Id
    private long id;
    
    @Column
    private long idClient;
    
    @Column
    private Date date;
    


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
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }


}
