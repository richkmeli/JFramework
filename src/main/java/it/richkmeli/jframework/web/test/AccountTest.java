package it.richkmeli.jframework.web.test;

import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.util.Logger;
import it.richkmeli.jframework.web.util.Session;

public class AccountTest {

    public static void addUsers(Session session) {
        try {
            session.getAuthDatabaseManager().addUser(new User("richk@i.it", "00000000", true));
            session.getAuthDatabaseManager().addUser(new User("er@fv.it", "00000000", false));
            session.getAuthDatabaseManager().addUser(new User("", "00000000", false));
            session.getAuthDatabaseManager().addUser(new User("richk@i.it", "00000000", true));
        } catch (DatabaseException e) {
            e.printStackTrace();
            Logger.error("Session TEST USERS", e);
        }

    }

}

