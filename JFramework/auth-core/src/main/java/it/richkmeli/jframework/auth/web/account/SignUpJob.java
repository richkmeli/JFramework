package it.richkmeli.jframework.auth.web.account;

import it.richkmeli.jframework.auth.data.exception.AuthDatabaseException;
import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.auth.model.exception.ModelException;
import it.richkmeli.jframework.auth.web.util.AuthServletManager;
import it.richkmeli.jframework.auth.web.util.AuthSession;
import it.richkmeli.jframework.auth.web.util.AuthStatusCode;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.KoResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.OkResponse;
import it.richkmeli.jframework.network.tcp.server.http.util.JServletException;

import java.util.Map;

public abstract class SignUpJob {

    protected abstract void doSpecificAction(AuthServletManager authServletManager);

    public void doAction(AuthServletManager authServletManager) {
        try {
            AuthSession authSession = authServletManager.getAuthServerSession();

            if (authSession.getUserID() == null) {
                Map<String, String> attribMap = authServletManager.doDefaultProcessRequest(false);

                String email = attribMap.get("email");
                String pass = attribMap.get("password");
                // check the model integrity of the data passed
                User.checkUserIntegrity(email, pass, null);

                if (authSession.getAuthDatabaseManager().isUserPresent(email)) {
                    authServletManager.print(new KoResponse(AuthStatusCode.ALREADY_REGISTERED));
                } else {
                    authSession.getAuthDatabaseManager().addUser(new User(email, pass, false));
                    authSession.setUserID(email);
                    authSession.setAdmin(false);

                    authServletManager.initSessionCookie();

                    doSpecificAction(authServletManager);

                    String message = "SignUp succeeded";
                    String output = authServletManager.doDefaultProcessResponse(message);

                    authServletManager.print(new OkResponse(AuthStatusCode.SUCCESS, output));
                }
            } else {
                authServletManager.print(new KoResponse(AuthStatusCode.ALREADY_LOGGED));
            }
        } catch (JServletException e) {
            authServletManager.print(new KoResponse(AuthStatusCode.ALREADY_LOGGED));
        } catch (AuthDatabaseException e) {
            authServletManager.print(new KoResponse(AuthStatusCode.DB_ERROR));
        } catch (ModelException e) {
            authServletManager.print(new KoResponse(AuthStatusCode.MODEL_ERROR, e.getMessage()));
        } catch (Throwable e) {
            authServletManager.print(new KoResponse(AuthStatusCode.GENERIC_ERROR));
        }

    }


}
