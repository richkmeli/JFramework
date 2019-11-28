package it.richkmeli.jframework.web.util;

import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.util.Logger;
import it.richkmeli.jframework.web.response.KOResponse;
import it.richkmeli.jframework.web.response.StatusCode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public abstract class ServletManager {
    protected static Session session;
    protected HttpServletRequest request;
    protected Map<String, String> attribMap;
    protected String servletPath;

    public ServletManager(HttpServletRequest request) {
        this.request = request;
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
        attribMap = extractParameters(request);
        // server session
        session = ServletManager.getServerSession(request);

        // set servletPath for specific process request
        servletPath = request.getServletPath();

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

    public Session getServerSession() throws ServletException {
        return getServerSession(request);
    }

    public static Session getServerSession(HttpServletRequest request) throws ServletException {
        // http session
        HttpSession httpSession = request.getSession();
        // server session
        Session session = (Session) httpSession.getAttribute("session");
        if (session == null) {
            try {
                session = new Session();
                httpSession.setAttribute("session", session);
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
                    out.append(name + ": " + value + "\n");
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
            list.append(attribute + " : " + httpSession.getAttribute(attribute));
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

