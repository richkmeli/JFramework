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
import it.richkmeli.jframework.util.log.Logger;
import org.json.JSONObject;

import java.util.Map;

public abstract class LogInJob {

    protected abstract void doSpecificAction(AuthServletManager authServletManager) throws JServletException, AuthDatabaseException;

    /**
     *
     * @param authServletManager for session purpose, the object passed should be a derived class of AuthServletManager (e.g. XXXAuthServletManager)
     */
    public void doAction(AuthServletManager authServletManager) {
        AuthSession authSession = null;
        
        try {
            authSession = authServletManager.getAuthServerSession();

            // check if is not already logged
            if (authSession.getUserID() == null) {
                Map<String, String> attribMap = authServletManager.doDefaultProcessRequest(false);

                String email = attribMap.get("email");// = request.getParameter("email");
                String pass = attribMap.get("password");
                // check the model integrity of the data passed
                User.checkUserIntegrity(email,pass,null);

                if (authSession.getAuthDatabaseManager().isUserPresent(email)) {
                    boolean isAdmin = authSession.getAuthDatabaseManager().isAdmin(email);
                    if (authSession.getAuthDatabaseManager().checkPassword(email, pass)) {

                        // set userID into the session
                        authSession.setUserID(email);
                        authSession.setAdmin(isAdmin);

                        authServletManager.initSessionCookie();

                        try {
                            doSpecificAction(authServletManager);
                        } catch (JServletException se) {
                            Logger.error(se);
                            throw se;
                        }

                        JSONObject adminInfo = new JSONObject();
                        adminInfo.put("admin", isAdmin);

                        //String output = adminInfo.toString();
                        String output = authServletManager.doDefaultProcessResponse(adminInfo.toString());

                        authServletManager.print(new OkResponse(AuthStatusCode.SUCCESS, output));
                    } else {
                        // pass sbagliata
                        authServletManager.print(new KoResponse(AuthStatusCode.WRONG_PASSWORD));
                    }
                } else {
                    Logger.error("User: " + email + " not found in AuthDatabase.");
                    // mail non trovata
                    authServletManager.print(new KoResponse(AuthStatusCode.ACCOUNT_NOT_FOUND /*"user: " + request.getAttribute("email") + "; password: " + request.getAttribute("password")*/));
                }
            } else {
                // already logged
                authServletManager.print(new KoResponse(AuthStatusCode.ALREADY_LOGGED));
            }
        } catch (JServletException e) {
            if (authSession != null) {
                authSession.setUserID(null);
            }
            if (e.getMessage() != null) {
                if (e.getMessage().contains("java.lang.Exception: decrypt, crypto not initialized, current state: 0")) {
                    authServletManager.print(new KoResponse(AuthStatusCode.SECURE_CONNECTION, e.getMessage()));
                } else {
                    authServletManager.print(new KoResponse(AuthStatusCode.GENERIC_ERROR, e.getMessage()));
                }
            } else {
                // if the message is Servlet exception is empty, show generic error message
                authServletManager.print(new KoResponse(AuthStatusCode.GENERIC_ERROR));
            }
        }catch (ModelException e){
            authServletManager.print(new KoResponse(AuthStatusCode.MODEL_ERROR, e.getMessage()));
        } catch (Throwable e) {
            if (authSession != null) {
                authSession.setUserID(null);
            }
            authServletManager.print(new KoResponse(AuthStatusCode.GENERIC_ERROR, e.getMessage()));
        }
    }
}
