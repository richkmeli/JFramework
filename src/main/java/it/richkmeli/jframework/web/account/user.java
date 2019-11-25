package it.richkmeli.jframework.web.account;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.orm.DatabaseException;
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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class user {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        PrintWriter out = response.getWriter();

        try {
            //ServletManager.doDefaultProcessRequest(request);
            ServletManager.checkLogin(request);

            // server session
            Session session = ServletManager.getServerSession(request);

            String user = session.getUser();
            if (user != null) {
                boolean isAdmin = session.getAuthDatabaseManager().isAdmin(user);

                JSONObject messageJSON = new JSONObject();
                messageJSON.put("user", user);
                messageJSON.put("admin", isAdmin);

                //String output = ServletManager.doDefaultProcessResponse(request, messageJSON.toString());

                out.println((new OKResponse(StatusCode.SUCCESS, messageJSON.toString()).json()));
            } else {
                out.println((new KOResponse(StatusCode.NOT_LOGGED, "You will be redirected to the home page").json()));
            }

            out.flush();
            out.close();

        } catch (ServletException e) {
            out.println(e.getKOResponseJSON());

        } catch (DatabaseException e) {
            out.println((new KOResponse(StatusCode.DB_ERROR, e.getMessage())).json());
        } catch (Exception e) {
            // redirect to the JSP that handles errors
            out.println((new KOResponse(StatusCode.GENERIC_ERROR, e.getMessage())).json());
//            httpSession.setAttribute("error", e);
//            request.getRequestDispatcher(ServletManager.ERROR_JSP).forward(request, response);
        }
        out.flush();
        out.close();
    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        //if the code below is de-commented, this servlet disables DELETE
        //super.doDelete(req, resp);
        PrintWriter out = resp.getWriter();
        HttpSession httpSession = req.getSession();
        Session session = null;
        try {
            session = ServletManager.getServerSession(req);
        } catch (ServletException e) {
            httpSession.setAttribute("error", e);
            req.getRequestDispatcher(ServletManager.ERROR_JSP).forward(req, resp);

        }

        try {
            String user = session.getUser();

            boolean encryption = false;
            if (req.getParameterMap().containsKey("channel")) {
                if ("rmc".equalsIgnoreCase(req.getParameter("channel"))) {
                    encryption = true;
                }
            }

            // Authentication
            if (user != null) {
                if (req.getParameterMap().containsKey("email")) {
                    String payload = req.getParameter("email");
                    if (encryption) {  // RMC
                        payload = session.getCryptoServer().decrypt(payload);
                    }
                    if (session.getUser().equals(payload)) {
                        session.getAuthDatabaseManager().removeUser(payload);
                        session.removeUser();
                        out.println((new OKResponse(StatusCode.SUCCESS).json()));
                    } else {
                        if (session.isAdmin()) {
                            session.getAuthDatabaseManager().removeUser(payload);
                            out.println((new OKResponse(StatusCode.SUCCESS).json()));
                        } else {
                            //unauthorized
                            out.println((new KOResponse(StatusCode.GENERIC_ERROR, "You are not authorized to perform this action!").json()));
                        }
                    }
//                    if (payload.compareTo(session.getUser()) == 0 ||
//                            session.isAdmin()) {
//                        session.getAuthDatabaseManager().removeUser(payload);
//                        session.removeUser();
//                        out.println((new OKResponse(StatusCode.SUCCESS).json()));
//                    } else {
//                        // TODO rimanda da qualche parte perche c'è errore
//                        out.println((new KOResponse(StatusCode.GENERIC_ERROR, "You are not authorized to perform this action!").json()));
//                    }
                } else {
                    // TODO rimanda da qualche parte perche c'è errore
                    out.println((new KOResponse(StatusCode.MISSING_FIELD).json()));
                }
                out.flush();
                out.close();
            } else {
                // non loggato
                // TODO rimanda da qualche parte perche c'è errore
                out.println((new KOResponse(StatusCode.NOT_LOGGED).json()));
            }
        } catch (Exception e) {
            // redirect to the JSP that handles errors
            out.println((new KOResponse(StatusCode.GENERIC_ERROR, e.getMessage()).json()));
        }

    }

    private String GenerateUserListJSON(Session session) {
        //DatabaseManager databaseManager = session.getDatabaseManager();
        List<User> userList = new ArrayList<>();//databaseManager.refreshUser();
        userList.add(new User(session.getUser(), "hidden", session.isAdmin()));

        Type type = new TypeToken<List<User>>() {
        }.getType();
        Gson gson = new Gson();

        // oggetto -> gson
        String usersListJSON = gson.toJson(userList, type);

        return usersListJSON;
    }
}