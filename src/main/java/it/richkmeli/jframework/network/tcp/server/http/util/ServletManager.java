package it.richkmeli.jframework.network.tcp.server.http.util;

import it.richkmeli.jframework.crypto.algorithm.SHA256;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.KOResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.StatusCode;
import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.util.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public abstract class ServletManager {
    public static final String JFSESSIONID = "JFRAMEWORKSESSIONID";
    //private static final int JFSESSIONCOOKIE_MAXAGE = 3600; // Number of seconds until the cookie expires
    protected static Session session;
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected Map<String, String> attribMap;
    protected String servletPath;

    public ServletManager(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        try {
            session = getServerSession();
        } catch (ServletException e) {
            //e.printStackTrace();
            Logger.error(e);
        }
    }

    public abstract void doSpecificProcessRequest() throws ServletException;

    public abstract String doSpecificProcessResponse(String output) throws ServletException;

    //public abstract <T extends Session> T getNewSessionInstance() throws ServletException, DatabaseException;

    public Map<String, String> doDefaultProcessRequest() throws ServletException {
        return doDefaultProcessRequest(true);
    }

    public Map<String, String> doDefaultProcessRequest(boolean checkSessionCookie) throws ServletException {
        attribMap = extractParameters(request);
        // server session
        session = ServletManager.getServerSession(request);

        // set servletPath for specific process request
        servletPath = request.getServletPath();

        if (checkSessionCookie) {
            checkSessionCookie(request, response);
        }

        doSpecificProcessRequest();
        return attribMap;
    }

    public String doDefaultProcessResponse(String input) throws ServletException {
        // server session
        session = ServletManager.getServerSession(request);

        // set servletPath for specific process response
        servletPath = request.getServletPath();

        return doSpecificProcessResponse(input);
    }


    // search parameters into URL (GET, ...) and into body (Data format supported are classic encoding key=att&... and JSON).
    public static Map<String, String> extractParameters(HttpServletRequest request) {
        Map<String, String> attribMap = new HashMap<>();
        // search parameter into URI and body (classic encoding)
        List<String> list = Collections.list(request.getParameterNames());
        for (String parameter : list) {
            String value = request.getParameter(parameter);
            attribMap.put(parameter, value);
        }
        // search JSON parameter into body
        try {
            String body = getBody(request);
            if (!"".equalsIgnoreCase(body)) {
                if (isJSONValid(body)) {
                    JSONObject bodyJSON = new JSONObject(body);
                    for (String key : bodyJSON.keySet()) {
                        String value = bodyJSON.getString(key);
                        attribMap.put(key, value);
                    }
                } else {
                    Logger.info("extractParameters: the body is not JSON formatted.");
                }
            } else {
                Logger.info("Servlet: " + request.getServletPath() + ", extractParameters: the body is empty.");
            }
        } catch (IOException e) {
            //e.printStackTrace();
            Logger.error(e);
        }

        return attribMap;
    }

    public void checkLogin() throws ServletException {
        checkLogin(request);
    }

    public static void checkLogin(HttpServletRequest request) throws ServletException {
        // server session
        Session session = getServerSession(request);

        String user = session.getUser();
        // Authentication
        if (user == null) {
            Logger.error("ServletManager, user not logged");
            throw new ServletException(new KOResponse(StatusCode.NOT_LOGGED, "user not logged"));
        }

    }

    public static void initSessionCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie;
        // check if JFRAMEWORKSESSIONID exists, if it doesn't exist it is raised an exception
        if (isCookiePresent(request, JFSESSIONID)) {
            //Logger.info("Cookie JFRAMEWORKSESSIONID: already present");
            cookie = getCookie(request, JFSESSIONID);
        } else {
            String id = request.getRemoteAddr()
                    //+ "##" + request.getRemoteUser()
                    + "##" + getCookie(request, "JSESSIONID").getValue()
                    + "##" + request.getHeader("User-Agent");
            Logger.info("Cookie JFRAMEWORKSESSIONID: " + id);
            cookie = new Cookie(JFSESSIONID, SHA256.hashToString(id.getBytes()));
            //cookie.setMaxAge(JFSESSIONCOOKIE_MAXAGE);
        }
        response.addCookie(cookie);
    }

    // set JFRAMEWORKSESSIONID cookie for Session Hijacking Attack protection
    public static Cookie generateSessionCookie(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        Cookie jsessionidCookie = getCookie(request, "JSESSIONID");
        String jsessionid = "";
        if (jsessionidCookie == null) {
            Logger.error("JSESSIONID is not present");
        } else {
            jsessionid = jsessionidCookie.getValue();
        }
        String userAgent = request.getHeader("User-Agent");

        String id = remoteAddr + "##" + jsessionid + "##" + userAgent;
        //Logger.info("Cookie JFRAMEWORKSESSIONID: " + id);
        return new Cookie(JFSESSIONID, SHA256.hashToString(id.getBytes()));
    }

    public static void checkSessionCookie(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        Cookie extractedjframeworkSessionID = getCookie(request, JFSESSIONID);
        if (!isCookiePresent(request, JFSESSIONID) || extractedjframeworkSessionID.getValue().equalsIgnoreCase("")) {
            String error = "Cookie: " + JFSESSIONID + " is not present in HTTP cookies";
            Logger.error(error);
            // invalidate httpsession
            //request.getSession().invalidate();
            //response.reset();
            throw new ServletException(new KOResponse(StatusCode.JFRAMEWORK_SESSIONID_ERROR));
        }
        if (!extractedjframeworkSessionID.getValue().equalsIgnoreCase(generateSessionCookie(request).getValue())) {
            String error = "JFRAMEWORKSESSIONID mismatch, possible Session Hijacking Attack";
            Logger.error(error);
            resetSession(request, response);

            throw new ServletException(new KOResponse(StatusCode.JFRAMEWORK_SESSIONID_ERROR));
        }
    }

    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        Cookie cookie = null;
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equalsIgnoreCase(name)) {
                    cookie = c;//c.getValue();
                }
            }
        } else {
            Logger.error("No cookies are present");
        }
        return cookie;
    }

    public void reset(HttpServletRequest request, HttpServletResponse response) {
        try {
            session = new Session();
        } catch (DatabaseException e) {
            Logger.error(e);
        }
        this.request = null;
        this.response = null;
        attribMap = null;
        servletPath = null;
        resetSession(request, response);
    }


    public static void resetSession(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = getCookie(request, JFSESSIONID);
        // invalidate httpsession and delete cookie
        request.getSession().invalidate();
        //response.reset();
        //Cookie cookie = new Cookie(JFRAMEWORKSESSIONID, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
            /*cookie = new Cookie("JSESSIONID", "");
            cookie.setMaxAge(0);
            response.addCookie(cookie);*/
    }

    private static boolean isCookiePresent(HttpServletRequest request, String name) {
        return getCookie(request, name) != null;
    }


    public Session getServerSession() throws ServletException {
        return getServerSession(request);
    }

    public static Session getServerSession(HttpServletRequest request) throws ServletException {
        // http session
        HttpSession httpSession = request.getSession();
        // server session
        session = (Session) httpSession.getAttribute("session");
        if (session == null) {
            try {
                session = new Session();
                httpSession.setAttribute("session", session);
            } catch (DatabaseException e) {
                throw new ServletException(e);
                //httpSession.setAttribute("error", e);
                //request.getRequestDispatcher("JSP/error.jsp").forward(request, response);
            }
        } else {
            try {
                if (session.getAuthDatabaseManager() == null) {
                    Logger.error("HTTPSession: jFramework Session not null | AuthDatabaseManager null");
                    session = new Session();
                    httpSession.setAttribute("session", session);
                } else {
                    //Logger.info("HTTPSession: "+session.getUser()+" " + session.isAdmin() + " " + session.getAuthDatabaseManager());
                }
            } catch (DatabaseException e) {
                throw new ServletException(e);
                //httpSession.setAttribute("error", e);
                //request.getRequestDispatcher("JSP/error.jsp").forward(request, response);
            }
        }
        return session;
    }


   /* public <T extends Session> T getExtendedServerSession(String sessionName, HttpServletRequest request) throws ServletException {
        // http session
        HttpSession httpSession = request.getSession();
        // server session
        return getExtendedServerSession(sessionName, httpSession);
    }


    public <T extends Session> T getExtendedServerSession(String sessionName, HttpSession httpSession) throws ServletException {
        T session = (T) httpSession.getAttribute("session" + sessionName);
        if (session == null) {
            try {
                session = getNewSessionInstance();
                httpSession.setAttribute("session" + sessionName, session);
            } catch (Exception e) {
                throw new ServletException(e);
                //httpSession.setAttribute("error", e);
                //request.getRequestDispatcher("JSP/error.jsp").forward(request, response);
            }
        }
        return session;
    }*/


    public static String printHttpHeaders(HttpServletRequest request) {
        StringBuilder out = new StringBuilder();
        Enumeration names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            Enumeration values = request.getHeaders(name);  // support multiple values
            if (values != null) {
                while (values.hasMoreElements()) {
                    String value = (String) values.nextElement();
                    out.append(name).append(": ").append(value).append("\n");
                }
            }
        }
        return out.toString();
    }

    public static String printHttpAttributes(HttpSession httpSession) {
        StringBuilder list = new StringBuilder();
        Enumeration<String> attributes = httpSession.getAttributeNames();
        list.append("{");
        while (attributes.hasMoreElements()) {
            String attribute = attributes.nextElement();
            list.append(attribute).append(" : ").append(httpSession.getAttribute(attribute));
            list.append(attributes.hasMoreElements() ? " - " : "}");
        }

        return list.toString();
    }

    public static String getBody(HttpServletRequest request) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

}

