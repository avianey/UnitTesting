/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.astek.internal.error;

/**
 *
 * @author dlebert
 */
public class BusinessException extends Exception {
    
    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        super(message);
    }
    
}
