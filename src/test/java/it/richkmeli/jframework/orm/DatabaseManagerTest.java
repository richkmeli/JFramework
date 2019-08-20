package it.richkmeli.jframework.orm;

import it.richkmeli.jframework.auth.AuthDatabaseManager;
import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.crypto.Crypto;
import it.richkmeli.jframework.crypto.controller.PasswordManager;
import it.richkmeli.jframework.crypto.util.RandomStringGenerator;
import it.richkmeli.jframework.orm.dataexample.device.DeviceDatabaseManager;
import it.richkmeli.jframework.orm.dataexample.device.model.Device;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public abstract class DatabaseManagerTest {
    protected AuthDatabaseManager authDatabaseManager;
    protected DeviceDatabaseManager deviceDatabaseManager;

    @Before
    public abstract void setUp();

    /* @Before
    public void setUp(String db) throws Exception {
        authDatabaseManager = new AuthDatabaseManager(db);
        create();
    }*/

    @After
    public void tearDown() {
        deleteAll();
    }

    @Test
    public void create() {
        try {
            create_authdb();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    private void create_authdb() throws DatabaseException {
        authDatabaseManager.addUser(new User("richk@i.it", "00000000", true));
        authDatabaseManager.addUser(new User("er@fv.it", "00000000", false));
        for (int i = 0; i < 120; i++) {
            String email = RandomStringGenerator.generateAlphanumericString(8) + "@" + RandomStringGenerator.generateAlphanumericString(8) + "." + RandomStringGenerator.generateAlphanumericString(2);
            String password = RandomStringGenerator.generateAlphanumericString(i);
            User u = new User(email,
                    password,
                    false);
            authDatabaseManager.addUser(u);
            assertTrue(authDatabaseManager.checkPassword(email, Crypto.hashPassword(password, true)));
            assertFalse(authDatabaseManager.isAdmin(email));
        }
        assertTrue(authDatabaseManager.checkPassword("richk@i.it", Crypto.hashPassword("00000000", true)));
    }

    @Test
    public void read() {
        try {
            read_autdb();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    private void read_autdb() throws DatabaseException {
        // test read
        authDatabaseManager.addUser(new User("er@fv.it", "00000000", true));
        assertNotNull(authDatabaseManager.getUser("er@fv.it"));

        // test read after delete
        // failed due it is on a deleted user
        readAfterDelete_authdb();
    }

    @Test
    public void readAfterDelete() {
        try {
            readAfterDelete_authdb();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    private void readAfterDelete_authdb() throws DatabaseException {
        // test read after delete
        // failed due it is on a deleted user
        authDatabaseManager.removeUser("richk@i.it");
        assertFalse(authDatabaseManager.checkPassword("richk@i.it", Crypto.hashPassword("00000000", true)));
    }

    @Test
    public void readAll() {
        try {
            readAll_authdb();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    private void readAll_authdb() throws DatabaseException {
        create();
        // test read all
        List<User> users = authDatabaseManager.getAllUsers();
        //System.out.println(users.size());

        assertNotEquals(0, users.size());
    }

    @Test
    public void update() {
        try {
            update_authdb();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    private void update_authdb() throws DatabaseException {
        String email = "update@fv.it";
        authDatabaseManager.addUser(new User(email, "00000000", true));
        assertTrue(authDatabaseManager.checkPassword(email, PasswordManager.hashPassword("00000000", true)));
        assertTrue(authDatabaseManager.isAdmin(email));

        authDatabaseManager.editPassword(email, "00000001");
        assertTrue(authDatabaseManager.checkPassword(email, PasswordManager.hashPassword("00000001", true)));

        authDatabaseManager.editAdmin(email, false);
        assertFalse(authDatabaseManager.isAdmin(email));
    }

    @Test
    public void delete() {
        try {
            delete_authdb();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    private void delete_authdb() throws DatabaseException {
        authDatabaseManager.removeUser("er@fv.it");
        assertNull(authDatabaseManager.getUser("er@fv.it"));
        authDatabaseManager.addUser(new User("er@fv.it", "00000000", true));
        assertNotNull(authDatabaseManager.getUser("er@fv.it"));
    }


    @Test
    public void deleteAll() {
        try {
            deleteAll_authdb();
            //deleteAll_devicedb();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    private void deleteAll_authdb() throws DatabaseException {
        List<User> users = authDatabaseManager.getAllUsers();
        for (User user : users) {
            authDatabaseManager.removeUser(user.getEmail());
        }
        users = authDatabaseManager.getAllUsers();
        assertEquals(0, users.size());
    }

    private void deleteAll_devicedb() throws DatabaseException {
        List<Device> devices = deviceDatabaseManager.refreshDevice();
        for (Device device : devices) {
            deviceDatabaseManager.removeDevice(device.getName());
        }
        devices = deviceDatabaseManager.refreshDevice();
        assertEquals(0, devices.size());
    }
}