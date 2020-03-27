package orm;

import it.richkmeli.jframework.crypto.Crypto;
import it.richkmeli.jframework.crypto.controller.PasswordManager;
import it.richkmeli.jframework.util.RandomStringGenerator;
import it.richkmeli.jframework.orm.DatabaseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import orm.dataexample.auth.AuthDatabaseManagerTest;
import orm.dataexample.auth.UserTest;
import orm.dataexample.device.DeviceDatabaseManager;
import orm.dataexample.device.model.Device;
import orm.dataexample.rmc.RMCDatabaseManager;
import orm.dataexample.rmc.model.RMC;

import java.util.List;

import static org.junit.Assert.*;

public abstract class DatabaseManagerTest {
    public static final int ENTRIES = 10;
    protected AuthDatabaseManagerTest authDatabaseManager;
    protected DeviceDatabaseManager deviceDatabaseManager;
    protected RMCDatabaseManager rmcDatabaseManager;

    @Before
    public abstract void setUp();

    /* @Before
    public void setUp(String db) throws Exception {
        authDatabaseManager = new AuthDatabaseManager(db);
        create();
    }*/

    @After
    public void tearDown() {
        deleteAll();
    }

    @Test
    public void create() {
        try {
            createAuthdb();
            createDevicedb();
            createRMCdb();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    private void createAuthdb() throws DatabaseException {
        authDatabaseManager.addUser(new UserTest("richk@i.it", "00000000", true));
        authDatabaseManager.addUser(new UserTest("er@fv.it", "00000000", false));
        for (int i = 0; i < ENTRIES; i++) {
            String email = RandomStringGenerator.generateAlphanumericString(8) + "@" + RandomStringGenerator.generateAlphanumericString(8) + "." + RandomStringGenerator.generateAlphanumericString(2);
            String password = RandomStringGenerator.generateAlphanumericString(i);
            UserTest u = new UserTest(email,
                    password,
                    false);
            authDatabaseManager.addUser(u);
            assertTrue(authDatabaseManager.checkPassword(email, Crypto.hashPassword(password, true)));
            assertFalse(authDatabaseManager.isAdmin(email));
        }
        assertTrue(authDatabaseManager.checkPassword("richk@i.it", Crypto.hashPassword("00000000", true)));
    }

    private void createDevicedb() throws DatabaseException {
        String email = "richk@i.it";
        authDatabaseManager.addUser(new UserTest(email, PasswordManager.hashPassword("00000000", false), false));
        deviceDatabaseManager.addDevice(new Device("device1", "192.168.0.100", "9000", "20-10-2018",
                "testencryptionkey", email, "start##start##start", ""));

        for (int i = 0; i < ENTRIES; i++) {
            String device = "device" + RandomStringGenerator.generateAlphanumericString(8) + "_" + i;
            email = RandomStringGenerator.generateAlphanumericString(8) + "@" + RandomStringGenerator.generateAlphanumericString(8) + "." + RandomStringGenerator.generateAlphanumericString(2);
            authDatabaseManager.addUser(new UserTest(email, PasswordManager.hashPassword("00000000", false), false));
            String encryptionKey = RandomStringGenerator.generateAlphanumericString(32);
            String commands = RandomStringGenerator.generateAlphanumericString(50);
            String commandsOutput = RandomStringGenerator.generateAlphanumericString(100);

            deviceDatabaseManager.addDevice(new Device(device,
                    "192.168.0.100", "9000", "20-10-2018", encryptionKey,
                    email, commands, commandsOutput));

            assertEquals(encryptionKey, deviceDatabaseManager.getEncryptionKey(device));
            assertEquals(commands, deviceDatabaseManager.getCommands(device));
            assertEquals(commandsOutput, deviceDatabaseManager.getCommandsOutput(device));
        }
    }

    private void createRMCdb() throws DatabaseException {
        String email = "richk@i.it";
        authDatabaseManager.addUser(new UserTest(email, PasswordManager.hashPassword("00000000", false), false));
        rmcDatabaseManager.addRMC(new RMC(email, "ClientID_1"));

        for (int i = 0; i < ENTRIES; i++) {
            email = RandomStringGenerator.generateAlphanumericString(8) + "@" + RandomStringGenerator.generateAlphanumericString(8) + "." + RandomStringGenerator.generateAlphanumericString(2);
            String clientID = RandomStringGenerator.generateAlphanumericString(32);
            authDatabaseManager.addUser(new UserTest(email, PasswordManager.hashPassword("00000000", false), false));

            rmcDatabaseManager.addRMC(new RMC(email, clientID));
            assertFalse(rmcDatabaseManager.getAllRMCs().isEmpty());
        }
    }

    @Test
    public void read() {
        try {
            readAuthdb();
            readDevicedb();
            readRMCdb();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    private void readAuthdb() throws DatabaseException {
        // test read
        authDatabaseManager.addUser(new UserTest("er@fv.it", "00000000", true));

        assertNotNull(authDatabaseManager.getUser("er@fv.it"));

        // test read after delete
        // failed due it is on a deleted user
        readAfterDeleteAuthdb();
    }

    private void readDevicedb() throws DatabaseException {
        // test read
        String device = "deviceread";
        authDatabaseManager.addUser(new UserTest("richk@i.it", PasswordManager.hashPassword("00000000", false), false));
        deviceDatabaseManager.addDevice(new Device(device, "192.168.0.100", "9000", "20-10-2018",
                "testencryptionkey", "richk@i.it", "start##start##start", ""));

        assertNotNull(deviceDatabaseManager.getDevice(device));
        // test read after delete
        // failed due it is on a deleted user
        readAfterDeleteDevicedb();
    }

    private void readRMCdb() throws DatabaseException {
        // test read
        String email = "richk@i.it";
        String clientID = "clientIDread";
        authDatabaseManager.addUser(new UserTest(email, PasswordManager.hashPassword("00000000", false), false));

        rmcDatabaseManager.addRMC(new RMC(email, clientID));
        assertNotNull(rmcDatabaseManager.getRMCs(email));
        // test read after delete
        // failed due it is on a deleted user
        readAfterDeleteRMCdb();
    }

    @Test
    public void readAfterDelete() {
        try {
            readAfterDeleteAuthdb();
            readAfterDeleteDevicedb();
            readAfterDeleteRMCdb();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    private void readAfterDeleteAuthdb() throws DatabaseException {
        // test read after delete
        // failed due it is on a deleted user
        authDatabaseManager.removeUser("richk@i.it");
        assertNull(authDatabaseManager.getUser("richk@i.it"));
    }

    private void readAfterDeleteDevicedb() throws DatabaseException {
        // test read after delete
        String device = "deviceread";
        deviceDatabaseManager.removeDevice(device);
        assertNull(deviceDatabaseManager.getDevice(device));
    }

    private void readAfterDeleteRMCdb() throws DatabaseException {
        // test read after delete
        String email = "richk@i.it";
        String clientID = "clientIDread";

        List<RMC> rmcs = rmcDatabaseManager.getRMCs(email);
        for (RMC rmc : rmcs) {
            rmcDatabaseManager.removeRMC(rmc);
        }
        rmcs = rmcDatabaseManager.getRMCs(email);
        assertTrue(rmcs.isEmpty());
    }

    @Test
    public void readAll() {
        try {
            readAllAuthdb();
            readAllDevicedb();
            readAllRMCdb();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    private void readAllAuthdb() throws DatabaseException {
        createAuthdb();
        // test read all
        List<UserTest> users = authDatabaseManager.getAllUsers();
        //System.out.println(users.size());

        assertNotEquals(0, users.size());
    }

    private void readAllDevicedb() throws DatabaseException {
        createDevicedb();
        // test read all
        List<Device> devices = deviceDatabaseManager.getAllDevices();
        //System.out.println(users.size());

        assertNotEquals(0, devices.size());
    }

    private void readAllRMCdb() throws DatabaseException {
        createRMCdb();
        // test read all
        List<RMC> rmcs = rmcDatabaseManager.getAllRMCs();
        //System.out.println(users.size());

        assertFalse(rmcs.isEmpty());
    }

    @Test
    public void update() {
        try {
            updateAuthdb();
            updateDevicedb();
            //updateRMCdb();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }


    private void updateAuthdb() throws DatabaseException {
        String email = "update@fv.it";
        deviceDatabaseManager.addDevice(new Device("deviceupdate", "192.168.0.100", "9000", "20-10-2018",
                "testencryptionkey", "richk@i.it", "start##start##start", ""));

        authDatabaseManager.addUser(new UserTest(email, "00000000", true));
        assertTrue(authDatabaseManager.checkPassword(email, PasswordManager.hashPassword("00000000", true)));
        assertTrue(authDatabaseManager.isAdmin(email));

        authDatabaseManager.editPassword(email, "00000001");
        assertTrue(authDatabaseManager.checkPassword(email, PasswordManager.hashPassword("00000001", true)));

        authDatabaseManager.editAdmin(email, false);
        assertFalse(authDatabaseManager.isAdmin(email));
    }

    private void updateDevicedb() throws DatabaseException {
        String device = "deviceread";
        deviceDatabaseManager.addDevice(new Device(device, "192.168.0.100", "9000", "20-10-2018",
                "testencryptionkey", "richk@i.it", "start##start##start", ""));

        deviceDatabaseManager.editDevice(new Device(device, null, null, null, "newpassword", null, null, null));
        assertEquals("newpassword", deviceDatabaseManager.getEncryptionKey(device));

        deviceDatabaseManager.editDevice(new Device(device, null, null, null, null, null, "newcommands", "newcommandsoutput"));

        assertEquals("newcommands", deviceDatabaseManager.getCommands(device));
        assertEquals("newcommandsoutput", deviceDatabaseManager.getCommandsOutput(device));

        deviceDatabaseManager.editCommands(device, "newcommands2");
        assertEquals("newcommands2", deviceDatabaseManager.getCommands(device));
    }


   /* private void updateRMCdb() throws DatabaseException {
        String email ="richk@i.it";
        String clientID = "clientIDupdate";
        rmcDatabaseManager.addRMC(new RMC(email,clientID));
        rmcDatabaseManager.editRMC(new RMC(email,"clientIDupdate2"));

        List<RMC> rmcs = rmcDatabaseManager.getRMCs(email);
        for (RMC rmc : rmcs){
            if("clientIDupdate2".equalsIgnoreCase(rmc.getRmcId())){
                assert true;
            }
        }
        assert false;
    }*/

    @Test
    public void delete() {
        try {
            deleteAuthdb();
            deleteDevicedb();
            deleteRMCdb();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    private void deleteAuthdb() throws DatabaseException {
        String user = "usertestdelete@user.it";
        authDatabaseManager.removeUser(user);
        assertNull(authDatabaseManager.getUser(user));
        authDatabaseManager.addUser(new UserTest(user, "00000000", true));
        assertNotNull(authDatabaseManager.getUser(user));
    }

    private void deleteDevicedb() throws DatabaseException {
        String device = "deviceRemoveTest";
        deviceDatabaseManager.removeDevice(device);
        assertNull(deviceDatabaseManager.getDevice(device));

        deviceDatabaseManager.addDevice(new Device(device, "192.168.0.100", "9000", "20-10-2018",
                "testencryptionkey", "richk@i.it", "start##start##start", ""));
        assertNotNull(deviceDatabaseManager.getDevice(device));

    }

    private void deleteRMCdb() throws DatabaseException {
        String email = "richk@i.it";

        for (RMC rmc : rmcDatabaseManager.getRMCs(email)) {
            rmcDatabaseManager.removeRMC(rmc);
        }
        assertTrue(rmcDatabaseManager.getRMCs(email).isEmpty());

        for (RMC rmc : rmcDatabaseManager.getRMCs(email)) {
            rmcDatabaseManager.removeRMC(rmc.getRmcId());
        }
        assertTrue(rmcDatabaseManager.getRMCs(email).isEmpty());
    }


    @Test
    public void deleteAll() {
        try {
            // delete before the schemas in which are contained foreign keys or specify in the foreign key ON DELETE <SET NULL>/<DELETE>
            deleteAllDevicedb();
            deleteAllAuthdb();
            deleteAllRMCdb();
        } catch (DatabaseException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    private void deleteAllAuthdb() throws DatabaseException {
        List<UserTest> users = authDatabaseManager.getAllUsers();
        for (UserTest user : users) {
            authDatabaseManager.removeUser(user.getEmail());
        }
        users = authDatabaseManager.getAllUsers();
        assertEquals(0, users.size());
    }

    private void deleteAllDevicedb() throws DatabaseException {
        List<Device> devices = deviceDatabaseManager.getAllDevices();
        for (Device device : devices) {
            deviceDatabaseManager.removeDevice(device.getName());
        }
        devices = deviceDatabaseManager.getAllDevices();
        assertEquals(0, devices.size());
    }

    private void deleteAllRMCdb() throws DatabaseException {
        List<RMC> rmcs = rmcDatabaseManager.getAllRMCs();

        for (RMC rmc : rmcs) {
            rmcDatabaseManager.removeRMC(rmc);
        }
        rmcs = rmcDatabaseManager.getAllRMCs();
        assertTrue(rmcs.isEmpty());
    }
}

