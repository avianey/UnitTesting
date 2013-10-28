/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.astek.internal.error;

import java.io.IOException;

/**
 *sdgfs
 * @author dlebert
 */
public class TechnicalException extends Exception {

    public TechnicalException() {
        super();
    }

    public TechnicalException(String message) {
        super(message);
    }

    public TechnicalException(IOException e) {
        super(e);
    }

    public TechnicalException(String message, IOException e) {
        super(message, e);
    }
    
    public TechnicalException(Exception e) {
        super(e);
    }
    
    public TechnicalException(String message, Exception e) {
        super(message, e);
    }
    
}
