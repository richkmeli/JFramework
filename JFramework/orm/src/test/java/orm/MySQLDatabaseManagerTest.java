package orm;

import it.richkmeli.jframework.orm.DatabaseException;
import orm.dataexample.auth.AuthDatabaseManagerTest;
import orm.dataexample.device.DeviceDatabaseManager;
import orm.dataexample.rmc.RMCDatabaseManager;

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