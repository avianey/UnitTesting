package fr.astek.bean;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import fr.astek.service.Service;
import fr.astek.test.AbstractTest;

/**
 * Illustrates the use of a Parameterized {@link Test} to check javax.validation constraints on the {@link Client} bean.
 * 
 * @author avianey
 * @version 1
 */
@RunWith(Parameterized.class)
public class ClientConstraintsTest extends AbstractTest {
    
    private static final String UNIQUE_SIRET = "12345678901234";
    
    private final String raisonSociale;
    private final String siret;
    private final boolean successExpected;
    private final Set<String> errorsExpected;
    
    @BeforeClass
    public static void createSampleClient() {
        Client client = new Client();
        client.setRaisonSociale("Unique constraint check");
        client.setSiret(UNIQUE_SIRET);
        Service.createClient(client);
    }

    public ClientConstraintsTest(String raisonSociale, String siret, boolean successExpected, String[] errorsExpected) {
        this.raisonSociale = raisonSociale;
        this.siret = siret;
        this.successExpected = successExpected;
        this.errorsExpected = errorsExpected == null ? null : new HashSet<String>(Arrays.asList(errorsExpected));
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[][] { 
                        {"Client OK", "09789605432134", true, null},
                        {"Client SIRET too short", "0978960543213", false, new String[] {Client.ERROR_SIRET_PATTERN}},
                        {"Client SIRET too long", "097896054321345", false, new String[] {Client.ERROR_SIRET_PATTERN}},
                        {"Client SIRET pattern", "0978960543213X", false, new String[] {Client.ERROR_SIRET_PATTERN}},
                        {"Client SIRET null", null, false, new String[] {Client.ERROR_SIRET_NULL}},
                        {null, "09789605432134", false, new String[] {Client.ERROR_RAISON_SOCIALE}},
                        {null, "ABC", false, new String[] {Client.ERROR_RAISON_SOCIALE, Client.ERROR_SIRET_PATTERN}},
                        {"Client existing SIRET", UNIQUE_SIRET, false, null}
                });
    }

    @Test
    public void checkConstraints() {
        Client c = new Client();
        c.setRaisonSociale(raisonSociale);
        c.setSiret(siret);
        try {
            Service.createClient(c);
            Assert.assertTrue("Client successfully created", successExpected);
        } catch (RollbackException e) {
            Assert.assertTrue(!successExpected);
            if (e.getCause() instanceof ConstraintViolationException) {
                Assert.assertNotNull(errorsExpected);
                ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
                Assert.assertTrue(errorsExpected.size() == cve.getConstraintViolations().size());
                for (ConstraintViolation<?> cv : cve.getConstraintViolations()) {
                    Assert.assertTrue("Constrint checked successfully " + cv.getMessage(), errorsExpected.contains(cv.getMessage()));
                }
            } else if (errorsExpected != null) {
                e.printStackTrace();
                Assert.fail();
            }
        }
    }
}
