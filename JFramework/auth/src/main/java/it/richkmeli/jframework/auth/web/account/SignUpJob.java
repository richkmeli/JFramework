package it.richkmeli.jframework.auth.web.account;

import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.auth.web.util.AuthServletManager;
import it.richkmeli.jframework.auth.web.util.AuthSession;
import it.richkmeli.jframework.auth.web.util.AuthStatusCode;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.KoResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.OkResponse;
import it.richkmeli.jframework.network.tcp.server.http.util.JServletException;
import it.richkmeli.jframework.orm.DatabaseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public abstract class SignUpJob {

    protected abstract void doSpecificAction();

    public void doAction(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession httpSession = request.getSession();
        AuthSession authSession = null;
        PrintWriter out = response.getWriter();

        try {
            authSession = AuthServletManager.getAuthServerSession(request);

            if (authSession.getUserID() == null) {
                //Map<String, String> attribMap = ServletManager.doDefaultProcessRequest(request);
                Map<String, String> attribMap = AuthServletManager.extractParameters(request);

                String email = attribMap.get("email");
                String pass = attribMap.get("password");
                if (authSession.getAuthDatabaseManager().isUserPresent(email)) {
                    out.println((new KoResponse(AuthStatusCode.ALREADY_REGISTERED)).json());
                } else {
                    authSession.getAuthDatabaseManager().addUser(new User(email, pass, false));
                    authSession.setUserID(email);
                    out.println((new OkResponse(AuthStatusCode.SUCCESS, "SignUp succeeded")).json());

                    AuthServletManager.initSessionCookie(request, response);

                    doSpecificAction();
                }
            } else {
                out.println((new KoResponse(AuthStatusCode.ALREADY_LOGGED)).json());
            }
        } catch (JServletException e) {
            out.println((new KoResponse(AuthStatusCode.ALREADY_LOGGED)).json());
        } catch (DatabaseException e) {
            out.println((new KoResponse(AuthStatusCode.DB_ERROR)).json());
        } catch (Exception e) {
            out.println((new KoResponse(AuthStatusCode.GENERIC_ERROR)).json());
        }

    }


}
