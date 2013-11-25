package fr.astek.test;

import javax.persistence.Persistence;

import org.joor.Reflect;
import org.junit.Before;

import fr.astek.service.Service;

public abstract class AbstractBeforeTest {
    
    /**
     * Set the Service to use the Test Persistence Unit.<br/>
     * The Test Persistence Unit is set to clear after a test method is executed.<br/>
     * User BeforeClass instead of <b>Before</b> if you want a clean database only after all the test of a single class.<br/>
     */
    @Before
    public void useTestDatabase() {
        Reflect.on(Service.class).set("EMF", Persistence.createEntityManagerFactory("pu-tu"));
    }
    
}
