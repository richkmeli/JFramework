import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.auth.model.exception.ModelException;
import it.richkmeli.jframework.auth.web.util.AuthSession;
import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.util.log.Logger;

public class AccountTest {


    public static void addUsers(AuthSession authSession) {
        try {
            authSession.getAuthDatabaseManager().addUser(new User("richk@i.it", "00000000", true));
            authSession.getAuthDatabaseManager().addUser(new User("er@fv.it", "00000000", false));
            authSession.getAuthDatabaseManager().addUser(new User("", "00000000", false));
            authSession.getAuthDatabaseManager().addUser(new User("richk@i.it", "00000000", true));
        } catch (DatabaseException | ModelException e) {
            e.printStackTrace();
            Logger.error("Session TEST USERS", e);
        }

    }

}
