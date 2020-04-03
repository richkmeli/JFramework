package it.richkmeli.jframework.auth.web.util;

import it.richkmeli.jframework.network.tcp.server.http.payload.response.KoResponse;
import it.richkmeli.jframework.network.tcp.server.http.util.JServletException;
import it.richkmeli.jframework.network.tcp.server.http.util.ServletManager;
import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.util.log.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Authenticated Servlet Manager
 * AuthServletJob add authentication to ServletJob. A new job has to extend AuthServletJob
 */
public abstract class AuthServletManager extends ServletManager {
    public static final String HTTP_AUTH_SESSION_NAME = "auth_session";
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

    public AuthServletManager(AuthServletManager authServletManager) {
        super(authServletManager.httpServletRequest, authServletManager.httpServletResponse);
        try {
            authSession = authServletManager.getAuthServerSession();
        } catch (JServletException e) {
            //e.printStackTrace();
            Logger.error(e);
        }
    }


    public abstract void doSpecificProcessRequestAuth() throws JServletException;

    public abstract String doSpecificProcessResponseAuth(String input) throws JServletException;

    @Override
    public void doSpecificProcessRequest() throws JServletException {
        authSession = AuthServletManager.getAuthServerSession(httpServletRequest);
        doSpecificProcessRequestAuth();
    }

    @Override
    public String doSpecificProcessResponse(String input) throws JServletException {
        // server session
        AuthServletManager.setAuthServerSession(authSession, httpServletRequest);
        return doSpecificProcessResponseAuth(input);
    }


    public Map<String, String> doDefaultProcessRequest() throws JServletException {
        return doDefaultProcessRequest(true);
    }


    public String doDefaultProcessResponse(String input) throws JServletException {
        // server session
        authSession = AuthServletManager.getAuthServerSession(httpServletRequest);

        // set servletPath for specific process response
        servletPath = httpServletRequest.getServletPath();

        return doSpecificProcessResponse(input);
    }


    public void checkLogin() throws JServletException {
        checkLogin(httpServletRequest);
    }

    public static void checkLogin(HttpServletRequest request) throws JServletException {
        // server session
        AuthSession authSession = getAuthServerSession(request);

        String user = authSession.getUserID();
        // Authentication
        if (user == null) {
            Logger.error("ServletManager, user not logged");
            throw new JServletException(new KoResponse(AuthStatusCode.NOT_LOGGED));
        }

    }

    public void reset() {
        super.reset(this.httpServletRequest, this.httpServletResponse);
        try {
            authSession = new AuthSession();
        } catch (DatabaseException e) {
            Logger.error(e);
        }
    }

    public AuthSession getAuthServerSession() throws JServletException {
        return getAuthServerSession(httpServletRequest);
    }

    public static AuthSession getAuthServerSession(HttpServletRequest request) throws JServletException {
        // http session
        HttpSession httpSession = request.getSession();
        // server session
        // overwrite java authsession with http stored authsession
        AuthSession authSession1 = (AuthSession) httpSession.getAttribute(HTTP_AUTH_SESSION_NAME);
        if (authSession1 == null) {
            // object not present in HTTP session
            try {
                authSession = new AuthSession(getServerSession(request));
                httpSession.setAttribute(HTTP_AUTH_SESSION_NAME, authSession);
            } catch (DatabaseException e) {
                throw new JServletException(e);
            }
        } else {
            // object present in HTTP session, but not initialized
            try {
                if (authSession1.getAuthDatabaseManager() == null) {
                    Logger.error("HTTPSession: jFramework Session not null | AuthDatabaseManager null");
                    authSession = new AuthSession(getServerSession(request));
                    httpSession.setAttribute(HTTP_AUTH_SESSION_NAME, authSession);
                } else {
                    authSession = authSession1;
                }
            } catch (DatabaseException e) {
                throw new JServletException(e);
            }
        }
        return authSession;
    }

    public static void setAuthServerSession(AuthSession authSession1, HttpServletRequest request) throws JServletException {
        // http session
        HttpSession httpSession = request.getSession();
        // set java authsession to http stored authsession
        // update authSession (derived) with Session object inside (base)
        if (authSession != null) {
            if (authSession.getCryptoServer() == null) {
                authSession = new AuthSession(authSession, getServerSession(request));
            }
            httpSession.setAttribute(HTTP_AUTH_SESSION_NAME, authSession);
        }
        authSession = authSession1;
    }

}

