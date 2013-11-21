package fr.astek.service;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.RollbackException;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import fr.astek.bean.Client;
import fr.astek.bean.Order;
import fr.astek.bean.User;
import fr.astek.error.BusinessException;
import fr.astek.error.TechnicalException;

public class ServiceTest {

    private static User getPassingUser() {
        User u = new User();
        u.setLogin("login");
        u.setRole(User.Role.ADMIN);
        return u;
    }

    private static Client getPassingClient() {
        Client client = new Client();
        client.setSiret("12345678912345");
        client.setRaisonSociale("Test client");
        return client;
    }

    private static Client mockClientWithOrder(long idClientWithOrder, String clientName) {
        Client client = Mockito.mock(Client.class);
        Mockito.when(client.getId()).thenReturn(idClientWithOrder);
        Mockito.when(client.getRaisonSociale()).thenReturn(clientName);
        return client;
    }

    /**
     * Verify that the {@link Service#createUser(fr.astek.bean.User)} method behave properly
     * <ul>
     * <li>rule 1 : The returned {@link User#getId()} != 0 if the creation is successfull</li>
     * <li>rule 2 : The {@link User} parameter can't be null</li>
     * <li>rule 3 : The {@link User#getLogin()} verifies ^[a-z]{3,32}$</li>
     * <li>rule 4 : The {@link User#getRole()} or {@link User#getLogin()} can't be null</li>
     * <li>rule 5 : the creation fails if a {@link User} with the same {@link User#getLogin()} already exists</li>
     * </ul>
     */
    @Test
    public void testCreateUser() throws BusinessException {

        // Rule 1 : Successfull creation return an id > 0
        User user = getPassingUser();
        user = Service.createUser(user);
        Assert.assertNotNull("ServiceToTest.createUser with a valid user should create and return the user", user);
        Assert.assertNotNull("ServiceToTest.createUser with a valid user should return a user.getId() > 0", user.getId());

        // Rule 2 : user null
        user = null;
        try {
            user = Service.createUser(user);
        } catch (NullPointerException e) {
            // ignore, this exception is expected.
        }
        Assert.assertNull("User parameter is null", user);

        // Rule 3 : Login not matching ^[a-z]{3,32}$
        user = getPassingUser();
        user.setLogin("TT@/%oOOo");

        try {
            user = Service.createUser(user);

        } catch (IllegalArgumentException e) {
            // ignore, this exception is expected.
        }

        Assert.assertEquals("User.getLogin not matching regex ^[a-z]{3,32}$ ", 0, user.getId());

        // Rule 4 : The login is null
        user = getPassingUser();
        user.setLogin(null);
        try {
            user = Service.createUser(user);

        } catch (NullPointerException e) {
            // ignore, this exception is expected.
        }
        Assert.assertEquals("User.getLogin is null", 0, user.getId());

        // Rule 4 : The role is null
        user = getPassingUser();
        user.setRole(null);
        try {
            user = Service.createUser(user);

        } catch (NullPointerException e) {
            // ignore, this exception is expected.
        }
        Assert.assertEquals("User.getRole is null ", user.getId(), 0);

        // Rule 5 : Login already used
        user = getPassingUser();
        try {
            user = Service.createUser(user);
        } catch (RollbackException e) {
            // ignore, this exception is expected.
            user.setId(0);
        }
        Assert.assertTrue("Should not be able to create a user with an already used login", user.getId() == 0);

    }

    /**
     * Verify that the {@link Service#testCreateClient(fr.astek.bean.User)} method behave properly
     * <ul>
     * <li>rule 1 : The returned {@link User#getId()} != 0 if the creation is successfull</li>
     * <li>rule 2 : The {@link Client} parameter can't be null</li>
     * <li>rule 3 : The {@link Client#getSiret()} verifies [0-9]{14}</li>
     * <li>rule 4 : The {@link Client#getRaisonSociale()} verifies ^[a-zA-Z0-9 _-éèà']{3,32}$</li>
     * <li>rule 5 : The {@link Client#getRole()} or {@link Client#getRaisonSociale()} can't be null</li>
     * <li>rule 6 : the creation fails if a {@link Client} with the same {@link Client#getSiret()} already exists</li>
     * <li>rule 7 : An exception is thrown if the creation fails</li>
     * </ul>
     */

    @Test
    public void testCreateClient() throws BusinessException {

        // Rule 1 : Creation successfull id != 0
        Client client = getPassingClient();
        client = Service.createClient(client);
        Assert.assertNotNull("ServiceToTest.createClient with a valid client should create and return the client", client);
        Assert.assertNotNull("ServiceToTest.createClient with a valid client should return a client.getId() != 0", client.getId());

        // Rule 2 : Client parameter null
        client = null;
        try {
            client = Service.createClient(client);
        } catch (NullPointerException e) {
            // ignore, this exception is expected.
        }
        Assert.assertNull("Client parameter is null", client);

        // Rule 3 : Client.getSiret not matching [0-9]{14}
        client = getPassingClient();
        client.setSiret("zeffrfre87r8e7fverf4");
        try {
            client = Service.createClient(client);
        } catch (RollbackException e) {
            // ignore, this exception is expected.
            client.setId(0);
        }
        Assert.assertTrue("Not matching Client.getSiret", client.getId() == 0);

        // Rule 4 : Client.getRaisonSociale not matching ^[a-zA-Z0-9 _-éèà']{3,32}$
        client = getPassingClient();
        client.setRaisonSociale("FT");
        try {
            client = Service.createClient(client);
        } catch (IllegalArgumentException e) {
            // ignore, this exception is expected.
        }
        Assert.assertTrue("Not matching Client.getRaisonSociale", client.getId() == 0);

        // Rule 5 : Client.getRaisonSociale is null
        client = getPassingClient();
        client.setRaisonSociale(null);
        try {
            client = Service.createClient(client);
        } catch (NullPointerException e) {
            // ignore, this exception is expected.
        }
        Assert.assertTrue("Client.getRaisonSociale is null", client.getId() == 0);

        // Rule 6 : Client.getSiret is null
        client = getPassingClient();
        client.setSiret(null);
        try {
            client = Service.createClient(client);
        } catch (NullPointerException e) {
            // ignore, this exception is expected.
        }
        Assert.assertTrue("Client.getSiret is null", client.getId() == 0);

        // Rule 7 : Client.getSiret already used
        client = getPassingClient();
        try {
            client = Service.createClient(client);
        } catch (RollbackException e) {
            client.setId(0);
        }
        Assert.assertTrue("Client.getSiret already used", client.getId() == 0);
    }

    /**
     * Verify that the {@link ServiceToTest#testGenerateInvoice(fr.astek.internal.bean.User, @literal Collection<fr.astek.internal.bean.Client>)} method behave
     * properly
     * <ul>
     * <li>rule 1 : Return ..... TODO</li>
     * <li>rule 2 : The {@link User} parameter not null</li>
     * <li>rule 3 : The {@link User#getRole() } can't be null</li>
     * <li>rule 4 : The {@link User#getRole() } must be User.Role.ADMIN</li>
     * <li>rule 5 : The {@literal Collection<{@link Client}>} not null</li>
     * <li>rule 6 : The {@literal Collection<{@link Client}>} not empty</li>
     * <li>rule 7 : A {@link Client } not registered in database</li>
     * <li>rule 8 : A {@link Client } with no orders should not be created</li>
     * </ul>
     */
    @Test
    public void testGenerateInvoice() throws TechnicalException, BusinessException {

        Collection<String> result = new ArrayList<>();

        // Cas passant

        // Mocking a valid user
        User user = Mockito.mock(User.class);
        Mockito.when(user.getLogin()).thenReturn("dlebert");
        Mockito.when(user.getRole()).thenReturn(User.Role.ADMIN);

        // Creating a valid Client list
        Collection<Client> clients = new ArrayList();

        Client ft = new Client();
        ft.setSiret("11111111111111");
        ft.setRaisonSociale("France Telecom");
        Service.createClient(ft);
        clients.add(ft);

        // Fake client pointing to ft.getId()
        for (int i = 1; i < 5; i++) {
            Client client = ServiceTest.mockClientWithOrder(ft.getId(), "Client" + i);
            clients.add(client);
        }

        // Assigning orders to Client ft
        Order achatFt = new Order();
        achatFt.setIdClient(ft.getId());
        achatFt.setProduct("Software");
        achatFt.setPrice(new Double(350.25));
        achatFt.setQuantity(3);
        Service.createOrder(achatFt);

        achatFt = new Order();
        achatFt.setIdClient(ft.getId());
        achatFt.setProduct("Hardware");
        achatFt.setPrice(new Double(354));
        achatFt.setQuantity(3);
        Service.createOrder(achatFt);

        achatFt = new Order();
        achatFt.setIdClient(ft.getId());
        achatFt.setProduct("MOA");
        achatFt.setPrice(new Double(1337.25));
        achatFt.setQuantity(3);
        Service.createOrder(achatFt);

        result = Service.generateInvoices(user, clients);

        Assert.assertFalse("ServiceToTest.generateInvoices successfull should return a result not empty", result.isEmpty());

        // Rule 2 : user parameter not null
        result = new ArrayList<>();
        user = null;
        try {
            result = Service.generateInvoices(user, clients);
        } catch (NullPointerException e) {
            // Expected exception
        }
        Assert.assertTrue("ServiceToTest.generateInvoices with parameter User null return nothing", result.isEmpty());

        // Rule 3 : User.getRole is null
        result = new ArrayList<>();
        user = getPassingUser();
        user.setRole(null);
        try {
            result = Service.generateInvoices(user, clients);
        } catch (NullPointerException e) {
            // Expected exception
        }
        Assert.assertTrue("ServiceToTest.generateInvoices with parameter User.getRole null should return nothing", result.isEmpty());

        // Rule 4 : User.getRole not User.Role.ADMIN
        result = new ArrayList<>();
        user = getPassingUser();
        user.setRole(User.Role.CONSULTANT);
        try {
            result = Service.generateInvoices(user, clients);
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
        Assert.assertTrue("ServiceToTest.generateInvoices with parameter User.getRole not User.Role.ADMIN should return nothing", result.isEmpty());

        // Rule 5 : clients parameter null
        user = getPassingUser();
        clients = null;
        try {
            result = Service.generateInvoices(user, clients);
        } catch (NullPointerException e) {
            // Expected exception
        }
        Assert.assertTrue("ServiceToTest.generateInvoices with parameter clients null should return nothing", result.isEmpty());

        // Rule 6 : clients parameter empty
        user = getPassingUser();
        clients = new ArrayList();
        try {
            result = Service.generateInvoices(user, clients);
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
        Assert.assertTrue("ServiceToTest.generateInvoices with parameter clients empty should return nothing", result.isEmpty());

        // Rule 7 : client not registered in database
        user = getPassingUser();
        clients = new ArrayList();
        Client test = ServiceTest.mockClientWithOrder(ft.getId(), "clientBeforeClientWithoutNotInDatabese");
        clients.add(test);
        Client client = ServiceTest.getPassingClient();
        clients.add(client);
        test = ServiceTest.mockClientWithOrder(ft.getId(), "clientAfterClientWithoutNotInDatabese");
        clients.add(test);

        result = Service.generateInvoices(user, clients);
        Assert.assertTrue("ServiceToTest.generateInvoices with parameter clients empty should return nothing", result.size() == 2);

        // Rule 8 : client with no orders
        user = getPassingUser();
        clients = new ArrayList();

        test = ServiceTest.mockClientWithOrder(ft.getId(), "clientBeforeClientWithoutOrder");
        clients.add(test);

        client = ServiceTest.getPassingClient();
        client.setRaisonSociale("Client without order");
        client.setSiret("12121212121221");
        client = Service.createClient(client);
        clients.add(client);

        test = ServiceTest.mockClientWithOrder(ft.getId(), "clientAfterClientWithoutOreder");
        clients.add(test);
        try {
            result = Service.generateInvoices(user, clients);
        } catch (BusinessException e) {
            // Expected exception
        }
        Assert.assertTrue("ServiceToTest.generateInvoices with parameter clients empty should return nothing", result.size() == 2);
    }
}