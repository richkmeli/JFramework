package orm;

import crypto.orm.dataexample.device.DeviceDatabaseManager;
import crypto.orm.dataexample.rmc.RMCDatabaseManager;
import it.richkmeli.jframework.orm.DatabaseException;
import orm.dataexample.auth.AuthDatabaseManagerTest;

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