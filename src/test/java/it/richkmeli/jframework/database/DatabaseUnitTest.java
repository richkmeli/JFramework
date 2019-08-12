package it.richkmeli.jframework.database;

import it.richkmeli.jframework.auth.AuthDatabaseManager;
import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.crypto.Crypto;
import it.richkmeli.jframework.crypto.util.RandomStringGenerator;
import org.junit.Test;

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

            authDatabaseManager.addUser(new User("richk@i.it", "00000000", true));
            authDatabaseManager.addUser(new User("er@fv.it", "00000000", false));

            for (int i = 0; i < 120; i++) {
                User u = new User(RandomStringGenerator.generateAlphanumericString(8) + "@" + RandomStringGenerator.generateAlphanumericString(8) + "." + RandomStringGenerator.generateAlphanumericString(2),
                        RandomStringGenerator.generateAlphanumericString(i),
                        false);
                authDatabaseManager.addUser(u);
            }

            assert authDatabaseManager.checkPassword("richk@i.it", Crypto.hashPassword("00000000", true));
            //assert authDatabaseManager.checkPassword("richk@i.it", "aWNRZ2pGdEFyMjhuS0paZjVzMTN5Zk56MldUa0FCOFl4Ql9jUWVRRmZiMnBxcjB0dmhfZz0=");


            authDatabaseManager.removeUser("richk@i.it");
            authDatabaseManager.removeUser("er@fv.it");

        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }

        assert true;

    }
}