package it.richkmeli.jframework.auth.web.account;

import it.richkmeli.jframework.auth.web.util.AuthServletManager;
import it.richkmeli.jframework.auth.web.util.AuthSession;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.KOResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.OKResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.StatusCode;
import it.richkmeli.jframework.network.tcp.server.http.util.JServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class LogOutJob {

    protected abstract void doSpecificAction();

    public void doAction(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
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

            out.println((new OKResponse(StatusCode.SUCCESS)).json());

        } catch (JServletException e) {
            out.println(e.getKOResponseJSON());
        } catch (Exception e) {
            //e.printStackTrace();
            out.println((new KOResponse(StatusCode.GENERIC_ERROR, e.getMessage())).json());
        }

        out.flush();
        out.close();

    }
}
