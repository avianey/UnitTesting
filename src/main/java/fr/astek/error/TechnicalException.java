package fr.astek.error;

import java.io.IOException;

public class TechnicalException extends Exception {

    private static final long serialVersionUID = 1L;

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
