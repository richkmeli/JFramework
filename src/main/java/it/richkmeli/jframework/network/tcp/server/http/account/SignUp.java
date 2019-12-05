package it.richkmeli.jframework.network.tcp.server.http.account;

import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.KOResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.OKResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.StatusCode;
import it.richkmeli.jframework.network.tcp.server.http.util.ServletException;
import it.richkmeli.jframework.network.tcp.server.http.util.ServletManager;
import it.richkmeli.jframework.network.tcp.server.http.util.Session;
import it.richkmeli.jframework.orm.DatabaseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public abstract class SignUp {

    protected abstract void doSpecificAction();

    public void doAction(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        HttpSession httpSession = request.getSession();
        Session session = null;
        PrintWriter out = response.getWriter();

        try {
            session = ServletManager.getServerSession(request);

            if (session.getUser() == null) {
                //Map<String, String> attribMap = ServletManager.doDefaultProcessRequest(request);
                Map<String, String> attribMap = ServletManager.extractParameters(request);

                String email = attribMap.get("email");
                String pass = attribMap.get("password");
                if (session.getAuthDatabaseManager().isUserPresent(email)) {
                    out.println((new KOResponse(StatusCode.ALREADY_REGISTERED)).json());
                } else {
                    session.getAuthDatabaseManager().addUser(new User(email, pass, false));
                    session.setUser(email);
                    out.println((new OKResponse(StatusCode.SUCCESS)).json());

                    ServletManager.initSessionCookie(request, response);

                    doSpecificAction();
                }
            } else {
                out.println((new KOResponse(StatusCode.ALREADY_LOGGED)).json());
            }
        } catch (ServletException e) {
            out.println((new KOResponse(StatusCode.ALREADY_LOGGED)).json());
        } catch (DatabaseException e) {
            out.println((new KOResponse(StatusCode.DB_ERROR)).json());
        } catch (Exception e) {
            out.println((new KOResponse(StatusCode.GENERIC_ERROR)).json());
        }

    }


}
