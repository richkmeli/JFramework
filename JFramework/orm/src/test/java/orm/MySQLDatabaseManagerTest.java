package orm;

import crypto.orm.dataexample.device.DeviceDatabaseManager;
import crypto.orm.dataexample.rmc.RMCDatabaseManager;
import it.richkmeli.jframework.orm.DatabaseException;
import orm.dataexample.auth.AuthDatabaseManagerTest;

public class MySQLDatabaseManagerTest extends DatabaseManagerTest {

    private static final String MYSQL = "mysql";

    @Override
    public void setUp() {
        try {
            authDatabaseManager = new AuthDatabaseManagerTest(MYSQL);
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