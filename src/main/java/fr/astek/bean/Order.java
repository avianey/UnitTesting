/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.astek.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author dlebert
 */
@Table(name = "T_ORDER")
@Entity
public class Order implements Serializable  {
    
    private static final long serialVersionUID = 1L;

    public static final String ERROR_CLIENT_NULL  = "order.client.null";
    public static final String ERROR_REF_NULL  = "order.ref.null";
    public static final String ERROR_PRICE_NEGATIVE  = "order.price.negative";
    public static final String ERROR_QUANTITY_NEGATIVE  = "order.quantity.negative";
    
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    private long id;
    
    @ManyToOne
    @NotNull(message = ERROR_CLIENT_NULL)
    private Client client;
    
    @Column
    @NotNull(message = ERROR_REF_NULL)
    private String ref;
    
    @Column
    @Min(value = 1, message = ERROR_QUANTITY_NEGATIVE)
    private int quantity;
    
    @Column
    @DecimalMin(value = "0.01", message = ERROR_PRICE_NEGATIVE)
    private double price;
    
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
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * @return the client
     */
    public Client getClient() {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * @return the ref
     */
    public String getRef() {
        return ref;
    }

    /**
     * @param ref the ref to set
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

}
