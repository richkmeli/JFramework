package orm;

import it.richkmeli.jframework.orm.DatabaseException;
import orm.dataexample.auth.AuthDatabaseManagerTest;
import orm.dataexample.device.DeviceDatabaseManager;
import orm.dataexample.rmc.RMCDatabaseManager;

public class DerbyDatabaseManagerTest extends DatabaseManagerTest {
    private static final String DERBY = "derby";

    @Override
    public void setUp() {
        try {
            authDatabaseManager = new AuthDatabaseManagerTest(DERBY);
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