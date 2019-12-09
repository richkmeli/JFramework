package it.richkmeli.jframework.network.tcp.server.http.account;

import it.richkmeli.jframework.network.tcp.server.http.payload.response.KOResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.OKResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.StatusCode;
import it.richkmeli.jframework.network.tcp.server.http.util.ServletException;
import it.richkmeli.jframework.network.tcp.server.http.util.ServletManager;
import it.richkmeli.jframework.network.tcp.server.http.util.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class LogOut {

    protected abstract void doSpecificAction();

    public void doAction(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        PrintWriter out = response.getWriter();
        HttpSession httpSession = request.getSession();
        Session session = null;
        try {
            session = ServletManager.getServerSession(request);

            if (session != null) {
                // remove user from the session
                session.removeUser();
                doSpecificAction();
            }
            ServletManager.resetSession(request, response);

            out.println((new OKResponse(StatusCode.SUCCESS)).json());

        } catch (ServletException e) {
            out.println(e.getKOResponseJSON());
        } catch (Exception e) {
            //e.printStackTrace();
            out.println((new KOResponse(StatusCode.GENERIC_ERROR, e.getMessage())).json());
        }

        out.flush();
        out.close();

    }
}
