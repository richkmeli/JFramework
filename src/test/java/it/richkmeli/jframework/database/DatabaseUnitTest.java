package it.richkmeli.jframework.database;

import it.richkmeli.jframework.auth.AuthDatabaseManager;
import it.richkmeli.jframework.auth.model.User;
import org.junit.Test;

public class DatabaseUnitTest {


    @Test
    public void AuthDBwithMysql() {
        AuthDatabaseManager authDatabaseManager = null;
        try {
            authDatabaseManager = new AuthDatabaseManager("mysql");

            authDatabaseManager.addUser(new User("richk@i.it", "00000000", true));
            authDatabaseManager.addUser(new User("er@fv.it", "00000000", false));

        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;

    }


    @Test
    public void AuthDBwithDerby() {
        AuthDatabaseManager authDatabaseManager = null;
        try {
            authDatabaseManager = new AuthDatabaseManager("derby");

            authDatabaseManager.addUser(new User("richk@i.it", "00000000", true));
            authDatabaseManager.addUser(new User("er@fv.it", "00000000", false));

        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;

    }
}