package it.richkmeli.jframework.web.account;

import it.richkmeli.jframework.web.response.KOResponse;
import it.richkmeli.jframework.web.response.OKResponse;
import it.richkmeli.jframework.web.response.StatusCode;
import it.richkmeli.jframework.web.util.ServletException;
import it.richkmeli.jframework.web.util.ServletManager;
import it.richkmeli.jframework.web.util.Session;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public abstract class LogIn {

    protected abstract void doSpecificAction();

    public void doAction(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        HttpSession httpSession = request.getSession();
        Session session = null;
        PrintWriter out = response.getWriter();

        try {
            session = ServletManager.getServerSession(request);

            // check if is not already logged
            if (session.getUser() == null) {
                Map<String, String> attribMap = ServletManager.extractParameters(request);
                //Map<String, String> attribMap = ServletManager.doDefaultProcessRequest(request);

                String email = attribMap.get("email");// = request.getParameter("email");
                String pass = attribMap.get("password");
                ;// = request.getParameter("password");

                if (session.getAuthDatabaseManager().isUserPresent(email)) {
                    boolean isAdmin = session.getAuthDatabaseManager().isAdmin(email);
                    if (session.getAuthDatabaseManager().checkPassword(email, pass)) {

                        // set userID into the session
                        session.setUser(email);
                        session.setAdmin(isAdmin);

                        doSpecificAction();

                        JSONObject adminInfo = new JSONObject();
                        adminInfo.put("admin", isAdmin);


                        String output = adminInfo.toString();
//                        String output = ServletManager.doDefaultProcessResponse(request, adminInfo.toString());

                        out.println((new OKResponse(StatusCode.SUCCESS, output)).json());
                    } else {
                        // pass sbagliata
                        out.println((new KOResponse(StatusCode.WRONG_PASSWORD)).json());
                    }
                } else {
                    // mail non trovata
                    out.println((new KOResponse(StatusCode.ACCOUNT_NOT_FOUND, "user: " + request.getAttribute("email") + "; password: " + request.getAttribute("password"))).json());
                }
            } else {
                // already logged
                out.println((new KOResponse(StatusCode.ALREADY_LOGGED)).json());
            }
        } catch (ServletException e) {
            session.setUser(null);
            if (e.getMessage().contains("java.lang.Exception: decrypt, crypto not initialized, current stare: 0")) {
                out.println((new KOResponse(StatusCode.SECURE_CONNECTION, e.getMessage())).json());
            } else {
                out.println((new KOResponse(StatusCode.GENERIC_ERROR, e.getMessage())).json());
            }
        } catch (Exception e) {
            session.setUser(null);
            out.println((new KOResponse(StatusCode.GENERIC_ERROR, e.getMessage())).json());
        }

        out.flush();
        out.close();

    }
}
