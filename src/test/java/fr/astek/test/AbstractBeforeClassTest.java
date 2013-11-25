package fr.astek.test;

import javax.persistence.Persistence;

import org.joor.Reflect;
import org.junit.BeforeClass;

import fr.astek.service.Service;

public abstract class AbstractBeforeClassTest {
    
    /**
     * Set the Service to use the Test Persistence Unit.<br/>
     * The Test Persistence Unit is set to clear after a test class is executed.<br/>
     * User Before instead of <b>BeforeClass</b> if you want a clean database before each test of a single class.<br/>
     */
    @BeforeClass
    public static void useTestDatabase() {
        Reflect.on(Service.class).set("EMF", Persistence.createEntityManagerFactory("pu-tu"));
    }
    
}
