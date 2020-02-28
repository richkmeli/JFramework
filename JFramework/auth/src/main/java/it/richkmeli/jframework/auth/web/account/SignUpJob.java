package it.richkmeli.jframework.auth.web.account;

import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.auth.web.util.AuthServletManager;
import it.richkmeli.jframework.auth.web.util.AuthSession;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.KOResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.OKResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.StatusCode;
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

    public void doAction(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        HttpSession httpSession = request.getSession();
        AuthSession authSession = null;
        PrintWriter out = response.getWriter();

        try {
            authSession = AuthServletManager.getAuthServerSession(request);

            if (authSession.getUser() == null) {
                //Map<String, String> attribMap = ServletManager.doDefaultProcessRequest(request);
                Map<String, String> attribMap = AuthServletManager.extractParameters(request);

                String email = attribMap.get("email");
                String pass = attribMap.get("password");
                if (authSession.getAuthDatabaseManager().isUserPresent(email)) {
                    out.println((new KOResponse(StatusCode.ALREADY_REGISTERED)).json());
                } else {
                    authSession.getAuthDatabaseManager().addUser(new User(email, pass, false));
                    authSession.setUser(email);
                    out.println((new OKResponse(StatusCode.SUCCESS)).json());

                    AuthServletManager.initSessionCookie(request, response);

                    doSpecificAction();
                }
            } else {
                out.println((new KOResponse(StatusCode.ALREADY_LOGGED)).json());
            }
        } catch (JServletException e) {
            out.println((new KOResponse(StatusCode.ALREADY_LOGGED)).json());
        } catch (DatabaseException e) {
            out.println((new KOResponse(StatusCode.DB_ERROR)).json());
        } catch (Exception e) {
            out.println((new KOResponse(StatusCode.GENERIC_ERROR)).json());
        }

    }


}
