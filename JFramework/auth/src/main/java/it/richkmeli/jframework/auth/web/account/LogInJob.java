package it.richkmeli.jframework.auth.web.account;

import it.richkmeli.jframework.auth.web.util.AuthServletManager;
import it.richkmeli.jframework.auth.web.util.AuthSession;
import it.richkmeli.jframework.auth.web.util.AuthStatusCode;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.KoResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.OkResponse;
import it.richkmeli.jframework.network.tcp.server.http.util.JServletException;
import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.util.Logger;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public abstract class LogInJob {

    protected abstract void doSpecificAction(HttpServletRequest request, HttpServletResponse response) throws JServletException, DatabaseException;

    public void doAction(HttpServletRequest request, HttpServletResponse response, AuthServletManager authServletManager) throws IOException {
        AuthSession authSession = null;
        PrintWriter out = response.getWriter();

        try {
            authSession = authServletManager.getAuthServerSession();

            // check if is not already logged
            if (authSession.getUserID() == null) {
                Map<String, String> attribMap = authServletManager.doDefaultProcessRequest(false);/*ServletManager.extractParameters(request);
                /*Map<String, String> attribMap = ServletManager.doDefaultProcessRequest(request);*/

                String email = attribMap.get("email");// = request.getParameter("email");
                String pass = attribMap.get("password");
                // = request.getParameter("password");

                if (authSession.getAuthDatabaseManager().isUserPresent(email)) {
                    boolean isAdmin = authSession.getAuthDatabaseManager().isAdmin(email);
                    if (authSession.getAuthDatabaseManager().checkPassword(email, pass)) {

                        // set userID into the session
                        authSession.setUserID(email);
                        authSession.setAdmin(isAdmin);

                        AuthServletManager.initSessionCookie(request, response);

                        try {
                            doSpecificAction(request, response);
                        } catch (JServletException se) {
                            Logger.error(se);
                            throw se;
                        }

                        JSONObject adminInfo = new JSONObject();
                        adminInfo.put("admin", isAdmin);

                        //String output = adminInfo.toString();
                        String output = authServletManager.doDefaultProcessResponse(adminInfo.toString());

                        out.println((new OkResponse(AuthStatusCode.SUCCESS, output)).json());
                    } else {
                        // pass sbagliata
                        out.println((new KoResponse(AuthStatusCode.WRONG_PASSWORD)).json());
                    }
                } else {
                    Logger.error("User: " + email + " not found in AuthDatabase.");
                    // mail non trovata
                    out.println((new KoResponse(AuthStatusCode.ACCOUNT_NOT_FOUND /*"user: " + request.getAttribute("email") + "; password: " + request.getAttribute("password")*/)).json());
                }
            } else {
                // already logged
                out.println((new KoResponse(AuthStatusCode.ALREADY_LOGGED)).json());
            }
        } catch (JServletException e) {
            if (authSession != null) {
                authSession.setUserID(null);
            }
            if (e.getMessage() != null) {
                if (e.getMessage().contains("java.lang.Exception: decrypt, crypto not initialized, current state: 0")) {
                    out.println((new KoResponse(AuthStatusCode.SECURE_CONNECTION, e.getMessage())).json());
                } else {
                    out.println((new KoResponse(AuthStatusCode.GENERIC_ERROR, e.getMessage())).json());
                }
            } else {
                // if the message is Servlet exception is empty, show generic error message
                out.println((new KoResponse(AuthStatusCode.GENERIC_ERROR)).json());
            }
        } catch (Throwable e) {
            if (authSession != null) {
                authSession.setUserID(null);
            }
            out.println((new KoResponse(AuthStatusCode.GENERIC_ERROR, e.getMessage())).json());
        }

        out.flush();
        out.close();

    }
}
