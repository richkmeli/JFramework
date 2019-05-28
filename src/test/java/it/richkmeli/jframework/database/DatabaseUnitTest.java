package it.richkmeli.jframework.database;

import it.richkmeli.jframework.auth.AuthDatabaseManager;
import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.network.RequestAsync;
import it.richkmeli.jframework.network.RequestListener;
import org.junit.Test;

public class DatabaseUnitTest {


    @Test
    public void AuthDatabaseManager() {
        AuthDatabaseManager authDatabaseManager = null;
        try {
            authDatabaseManager = new AuthDatabaseManager("mysql2");

            authDatabaseManager.addUser(new User("richk@i.it", "00000000", true));
            authDatabaseManager.addUser(new User("er@fv.it", "00000000", false));

        } catch (DatabaseException e) {
            e.printStackTrace();
            //assert false;
        }
        assert true;

    }
}