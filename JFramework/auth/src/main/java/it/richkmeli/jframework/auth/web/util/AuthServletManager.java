package it.richkmeli.jframework.auth.web.util;

import it.richkmeli.jframework.network.tcp.server.http.payload.response.KOResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.StatusCode;
import it.richkmeli.jframework.network.tcp.server.http.util.JServletException;
import it.richkmeli.jframework.network.tcp.server.http.util.ServletManager;
import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.util.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/** AuthServletJob add authentication to ServletJob. A new job has to extend AuthServletJob
 *
 */
public abstract class AuthServletManager extends ServletManager {
    protected static AuthSession authSession;

    public AuthServletManager(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        try {
            authSession = getAuthServerSession();
        } catch (JServletException e) {
            //e.printStackTrace();
            Logger.error(e);
        }
    }

    public abstract void doSpecificProcessRequestAuth() throws JServletException;

    public abstract String doSpecificProcessResponseAuth(String input) throws JServletException;

    @Override
    public void doSpecificProcessRequest() throws JServletException {
        authSession = AuthServletManager.getAuthServerSession(request);
        doSpecificProcessRequestAuth();
    }

    @Override
    public String doSpecificProcessResponse(String input) throws JServletException {
        // server session
        authSession = AuthServletManager.getAuthServerSession(request);
        return doSpecificProcessResponseAuth(input);
    }


    public Map<String, String> doDefaultProcessRequest() throws JServletException {
        return doDefaultProcessRequest(true);
    }


    public String doDefaultProcessResponse(String input) throws JServletException {
        // server session
        authSession = AuthServletManager.getAuthServerSession(request);

        // set servletPath for specific process response
        servletPath = request.getServletPath();

        return doSpecificProcessResponse(input);
    }


    public void checkLogin() throws JServletException {
        checkLogin(request);
    }

    public static void checkLogin(HttpServletRequest request) throws JServletException {
        // server session
        AuthSession authSession = getAuthServerSession(request);

        String user = authSession.getUser();
        // Authentication
        if (user == null) {
            Logger.error("ServletManager, user not logged");
            throw new JServletException(new KOResponse(StatusCode.NOT_LOGGED, "user not logged"));
        }

    }


    public void reset(HttpServletRequest request, HttpServletResponse response) {
        super.reset(request,response);
        try {
            authSession = new AuthSession();
        } catch (DatabaseException e) {
            Logger.error(e);
        }
    }


    public AuthSession getAuthServerSession() throws JServletException {
        return getAuthServerSession(request);
    }

    public static AuthSession getAuthServerSession(HttpServletRequest request) throws JServletException {
        // http session
        HttpSession httpSession = request.getSession();
        // server session
        authSession = (AuthSession) httpSession.getAttribute("auth_session");
        if (authSession == null) {
            try {
                authSession = new AuthSession(getServerSession(request));
                httpSession.setAttribute("auth_session", authSession);
            } catch (DatabaseException e) {
                throw new JServletException(e);
                //httpSession.setAttribute("error", e);
                //request.getRequestDispatcher("JSP/error.jsp").forward(request, response);
            }
        } else {
            try {
                if (authSession.getAuthDatabaseManager() == null) {
                    Logger.error("HTTPSession: jFramework Session not null | AuthDatabaseManager null");
                    authSession = new AuthSession();
                    httpSession.setAttribute("auth_session", authSession);
                } else {
                    //Logger.info("HTTPSession: "+session.getUser()+" " + session.isAdmin() + " " + session.getAuthDatabaseManager());
                }
            } catch (DatabaseException e) {
                throw new JServletException(e);
                //httpSession.setAttribute("error", e);
                //request.getRequestDispatcher("JSP/error.jsp").forward(request, response);
            }
        }
        return authSession;
    }


}

