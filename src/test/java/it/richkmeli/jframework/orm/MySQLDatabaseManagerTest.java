package it.richkmeli.jframework.orm;

import it.richkmeli.jframework.auth.AuthDatabaseManager;
import it.richkmeli.jframework.orm.dataexample.device.DeviceDatabaseManager;
import it.richkmeli.jframework.orm.dataexample.rmc.RMCDatabaseManager;

public class MySQLDatabaseManagerTest extends DatabaseManagerTest {

    private static final String MYSQL = "mysql";

    @Override
    public void setUp() {
        try {
            authDatabaseManager = new AuthDatabaseManager(MYSQL);
            deviceDatabaseManager = new DeviceDatabaseManager(MYSQL);
            rmcDatabaseManager = new RMCDatabaseManager(MYSQL);
            create();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

}