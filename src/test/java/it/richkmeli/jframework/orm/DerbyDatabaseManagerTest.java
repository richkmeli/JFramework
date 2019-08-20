package it.richkmeli.jframework.orm;

import it.richkmeli.jframework.auth.AuthDatabaseManager;

public class DerbyDatabaseManagerTest extends DatabaseManagerTest {
    private static final String DERBY = "derby";

    @Override
    public void setUp() {
        try {
            authDatabaseManager = new AuthDatabaseManager(DERBY);
            //deviceDatabaseManager = new DeviceDatabaseManager(DERBY);
            create();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }
}