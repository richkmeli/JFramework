package it.richkmeli.jframework.orm;

import it.richkmeli.jframework.auth.AuthDatabaseManager;
import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.crypto.Crypto;
import it.richkmeli.jframework.crypto.util.RandomStringGenerator;
import org.junit.Test;

import java.util.List;

public class DatabaseUnitTest {


    @Test
    public void authDBwithMysql() {
        AuthDatabaseManager authDatabaseManager = null;
        try {
            authDatabaseManager = new AuthDatabaseManager("mysql");

            authDatabaseManager.addUser(new User("richk@i.it", "00000000", true));
            authDatabaseManager.addUser(new User("er@fv.it", "00000000", false));

            for (int i = 0; i < 100; i++) {
                User u = new User(RandomStringGenerator.generateAlphanumericString(8) + "@" + RandomStringGenerator.generateAlphanumericString(8) + "." + RandomStringGenerator.generateAlphanumericString(2),
                        RandomStringGenerator.generateAlphanumericString(10),
                        false);
                authDatabaseManager.addUser(u);
            }

        } catch (DatabaseException e) {
            e.printStackTrace();
            // TODO fai server per i test
            //assert false;
        }
        assert true;

    }


    @Test
    public void authDBwithDerby() {
        AuthDatabaseManager authDatabaseManager = null;
        try {
            authDatabaseManager = new AuthDatabaseManager("derby");

            // add user
            authDatabaseManager.addUser(new User("richk@i.it", "00000000", true));
            authDatabaseManager.addUser(new User("er@fv.it", "00000000", false));
            for (int i = 0; i < 120; i++) {
                User u = new User(RandomStringGenerator.generateAlphanumericString(8) + "@" + RandomStringGenerator.generateAlphanumericString(8) + "." + RandomStringGenerator.generateAlphanumericString(2),
                        RandomStringGenerator.generateAlphanumericString(i),
                        false);
                authDatabaseManager.addUser(u);
            }

            // check add
            assert authDatabaseManager.checkPassword("richk@i.it", Crypto.hashPassword("00000000", true));
            //assert authDatabaseManager.checkPassword("richk@i.it", "aWNRZ2pGdEFyMjhuS0paZjVzMTN5Zk56MldUa0FCOFl4Ql9jUWVRRmZiMnBxcjB0dmhfZz0=");

            // remove user
            authDatabaseManager.removeUser("richk@i.it");
            authDatabaseManager.removeUser("er@fv.it");
            // failed due it is on a deleted user
            assert !authDatabaseManager.checkPassword("richk@i.it", Crypto.hashPassword("00000000", true));

            authDatabaseManager.removeUser("richk@i.it");
            // failed due it is on a deleted user
            assert !authDatabaseManager.checkPassword("richk@i.it", Crypto.hashPassword("00000000", true));

            //List<User> users = authDatabaseManager.getAllUsersCompat();
            //System.out.println(users.size());
            //assert users.size() % 120 == 0 && users.size() != 0;

            List<User> users2 = authDatabaseManager.getAllUsers();
            //System.out.println(users2.size());
            assert users2.size() % 120 == 0 && users2.size() != 0;


            assert authDatabaseManager.getUser("er@fv.it") == null;
            authDatabaseManager.addUser(new User("er@fv.it", "00000000", true));
            assert authDatabaseManager.getUser("er@fv.it") != null;

        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }

        assert true;
    }
}