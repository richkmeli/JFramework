package it.richkmeli.jframework.orm;

import it.richkmeli.jframework.auth.AuthDatabaseManager;
import it.richkmeli.jframework.orm.dataexample.device.DeviceDatabaseManager;
import it.richkmeli.jframework.orm.dataexample.rmc.RMCDatabaseManager;

public class DerbyDatabaseManagerTest extends DatabaseManagerTest {
    private static final String DERBY = "derby";

    @Override
    public void setUp() {
        try {
            authDatabaseManager = new AuthDatabaseManager(DERBY);
            deviceDatabaseManager = new DeviceDatabaseManager(DERBY);
            rmcDatabaseManager = new RMCDatabaseManager(DERBY);
            create();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }
}