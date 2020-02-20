package orm.dataexample.auth;

import it.richkmeli.jframework.crypto.Crypto;
import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.orm.DatabaseManager;
import it.richkmeli.jframework.util.Logger;

import java.util.List;

public class AuthDatabaseManagerTest extends DatabaseManager implements AuthModelTest {

    public AuthDatabaseManagerTest(String database) throws DatabaseException {
        schemaName = "AuthSchema";
        tableName = schemaName + "." + "auth";
        table = "(" +
                "email VARCHAR(50) NOT NULL," +
                "password VARCHAR(100) NOT NULL," +
                "admin BOOLEAN NOT NULL " + ("mysql".equalsIgnoreCase(dbtype) ? "DEFAULT 0" : "") + "," +
                "PRIMARY KEY (email)" +
                ")";

        init(database);
    }

    public AuthDatabaseManagerTest() throws DatabaseException {
        this(null);
    }

    public List<UserTest> getAllUsers() throws DatabaseException {
        return readAll(UserTest.class);
    }


    public UserTest getUser(String email) throws DatabaseException {
        return read(new UserTest(email, null));
    }

    public boolean addUser(UserTest user) throws DatabaseException {
        //Logger.info("AuthDatabaseManager, addUser. User: " + user.email);
        //String hash = Crypto.hash(user.getPassword());
        String password = Crypto.hashPassword(user.getPassword(), false);
        user.setPassword(password);
        return create(user);
    }

    public boolean removeUser(String email) throws DatabaseException {
        return super.delete(new UserTest(email, ""));
    }


    public boolean isUserPresent(String email) throws DatabaseException {
        return getUser(email) != null;
    }

    public boolean editPassword(String email, String pass) throws DatabaseException {
        String password = Crypto.hashPassword(pass, false);
        return update(new UserTest(email, password, null));
    }

    public boolean editAdmin(String email, Boolean isAdmin) throws DatabaseException {
        return update(new UserTest(email, null, isAdmin));
    }

    public boolean checkPassword(String email, String pass) throws DatabaseException {
        UserTest user = getUser(email);
        if (user != null) {
            return Crypto.verifyPassword(user.getPassword(), pass);
        } else {
            return false;
        }
    }

    public boolean isAdmin(String email) throws DatabaseException {
        UserTest user = getUser(email);
        if (user != null) {
            return user.getAdmin();
        } else {
            Logger.error("isAdmin, admin null");
            return false;

        }
    }
}
