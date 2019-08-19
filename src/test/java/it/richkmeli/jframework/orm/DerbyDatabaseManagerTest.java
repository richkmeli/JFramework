package it.richkmeli.jframework.orm;

import it.richkmeli.jframework.auth.AuthDatabaseManager;

public class DerbyDatabaseManagerTest extends DatabaseManagerTest {

    @Override
    public void setUp() {
        try {
            authDatabaseManager = new AuthDatabaseManager("derby");
            create();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }
}