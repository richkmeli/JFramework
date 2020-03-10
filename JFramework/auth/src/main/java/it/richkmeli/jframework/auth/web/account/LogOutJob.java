package it.richkmeli.jframework.auth.web.account;

import it.richkmeli.jframework.auth.web.util.AuthServletManager;
import it.richkmeli.jframework.auth.web.util.AuthSession;
import it.richkmeli.jframework.auth.web.util.AuthStatusCode;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.KoResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.OkResponse;
import it.richkmeli.jframework.network.tcp.server.http.util.JServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class LogOutJob {

    protected abstract void doSpecificAction();

    public void doAction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        HttpSession httpSession = request.getSession();
        AuthSession authSession = null;
        try {
            authSession = AuthServletManager.getAuthServerSession(request);

            if (authSession != null) {
                // remove user from the session
                authSession.removeUser();
                doSpecificAction();
            }
            AuthServletManager.resetSession(request, response);

            out.println((new OkResponse(AuthStatusCode.SUCCESS, "LogOut succeeded")).json());

        } catch (JServletException e) {
            out.println(e.getKoResponseJSON());
        } catch (Exception e) {
            //e.printStackTrace();
            out.println((new KoResponse(AuthStatusCode.GENERIC_ERROR, e.getMessage())).json());
        }

        out.flush();
        out.close();

    }
}
