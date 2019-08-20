package it.richkmeli.jframework.orm;

import it.richkmeli.jframework.auth.AuthDatabaseManager;
import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.crypto.Crypto;
import it.richkmeli.jframework.crypto.controller.PasswordManager;
import it.richkmeli.jframework.crypto.util.RandomStringGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public abstract class DatabaseManagerTest {
    protected AuthDatabaseManager authDatabaseManager;

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
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    @Test
    public void read() {
        try {
            // test read
            authDatabaseManager.addUser(new User("er@fv.it", "00000000", true));
            assertNotNull(authDatabaseManager.getUser("er@fv.it"));

            // test read after delete
            // failed due it is on a deleted user
            authDatabaseManager.removeUser("richk@i.it");
            assertFalse(authDatabaseManager.checkPassword("richk@i.it", Crypto.hashPassword("00000000", true)));
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    @Test
    public void readAfterDelete() {
        try {
            // test read after delete
            // failed due it is on a deleted user
            authDatabaseManager.removeUser("richk@i.it");
            assertFalse(authDatabaseManager.checkPassword("richk@i.it", Crypto.hashPassword("00000000", true)));
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    @Test
    public void readAll() {
        try {
            create();
            // test read all
            List<User> users = authDatabaseManager.getAllUsers();
            //System.out.println(users.size());

            assertNotEquals(0, users.size());
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    @Test
    public void update() {
        try {
            String email = "update@fv.it";
            authDatabaseManager.addUser(new User(email, "00000000", true));
            assertTrue(authDatabaseManager.checkPassword(email, PasswordManager.hashPassword("00000000", true)));
            assertTrue(authDatabaseManager.isAdmin(email));

            authDatabaseManager.editPassword(email, "00000001");
            assertTrue(authDatabaseManager.checkPassword(email, PasswordManager.hashPassword("00000001", true)));

            authDatabaseManager.editAdmin(email, false);
            assertFalse(authDatabaseManager.isAdmin(email));
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    @Test
    public void delete() {
        try {
            authDatabaseManager.removeUser("er@fv.it");
            assertNull(authDatabaseManager.getUser("er@fv.it"));
            authDatabaseManager.addUser(new User("er@fv.it", "00000000", true));
            assertNotNull(authDatabaseManager.getUser("er@fv.it"));
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }


    @Test
    public void deleteAll() {
        try {
            List<User> users = authDatabaseManager.getAllUsers();
            for (User user : users) {
                authDatabaseManager.removeUser(user.getEmail());
            }
            users = authDatabaseManager.getAllUsers();
            assertEquals(0, users.size());
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }
}