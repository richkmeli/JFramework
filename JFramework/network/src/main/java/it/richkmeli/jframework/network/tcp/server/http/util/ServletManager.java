package it.richkmeli.jframework.network.tcp.server.http.util;

import it.richkmeli.jframework.crypto.algorithm.SHA256;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.BaseStatusCode;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.KoResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.Response;
import it.richkmeli.jframework.util.DataFormat;
import it.richkmeli.jframework.util.TypeConverter;
import it.richkmeli.jframework.util.log.Logger;
import org.json.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public abstract class ServletManager {
    public static final String HTTP_SESSION_NAME = "session";
    public static final String JFSESSIONID = "JFRAMEWORKSESSIONID";
    //private static final int JFSESSIONCOOKIE_MAXAGE = 3600; // Number of seconds until the cookie expires
    protected static Session session;
    protected HttpServletRequest httpServletRequest;
    protected HttpServletResponse httpServletResponse;
    // to get attribMap, use doDefaultProcessRequest()
    protected Map<String, String> attribMap;
    protected String servletPath;

    public ServletManager(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        try {
            session = getServerSession();
        } catch (JServletException e) {
            //e.printStackTrace();
            Logger.error(e);
        }
    }

    public abstract void doSpecificProcessRequest() throws JServletException;

    public abstract String doSpecificProcessResponse(String output) throws JServletException;

    //public abstract <T extends Session> T getNewSessionInstance() throws ServletException, DatabaseException;

    public Map<String, String> doDefaultProcessRequest() throws JServletException {
        return doDefaultProcessRequest(true);
    }

    public Map<String, String> doDefaultProcessRequest(boolean checkSessionCookie) throws JServletException {
        attribMap = extractParameters(httpServletRequest);
        // server session
        session = ServletManager.getServerSession(httpServletRequest);

        // set servletPath for specific process request
        servletPath = httpServletRequest.getServletPath();

        if (checkSessionCookie) {
            checkSessionCookie(httpServletRequest, httpServletResponse);
        }

        doSpecificProcessRequest();
        Logger.debug(TypeConverter.mapToJson(attribMap));
        return attribMap;
    }

    public String doDefaultProcessResponse(String input) throws JServletException {
        // server session
        ServletManager.setServerSession(session, httpServletRequest);

        // set servletPath for specific process response
        servletPath = httpServletRequest.getServletPath();

        return doSpecificProcessResponse(input);
    }


    /**
     * search parameters into URL (GET, ...) and into body (Data format supported are classic
     * encoding key=att&... and JSON).
     *
     * @param request
     * @return
     */
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
                if (DataFormat.isJSONValid(body)) {
                    JSONObject bodyJSON = new JSONObject(body);
                    for (String key : bodyJSON.keySet()) {
                        String value = bodyJSON.getString(key);
                        attribMap.put(key, value);
                    }
                } else {
                    Logger.info("extractParameters: the body is not JSON formatted.");
                }
            } else {
                //Logger.info("Servlet: " + request.getServletPath() + ", extractParameters: the body is empty.");
            }
        } catch (IOException e) {
            //e.printStackTrace();
            Logger.error(e);
        }

        return attribMap;
    }

    public void initSessionCookie() {
        initSessionCookie(httpServletRequest, httpServletResponse);
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
            //Logger.info("Cookie JFRAMEWORKSESSIONID: " + id);
            String hashed = SHA256.hashToString(id.getBytes());
            cookie = new Cookie(JFSESSIONID, hashed);
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
        String hashed = SHA256.hashToString(id.getBytes());
        return new Cookie(JFSESSIONID, hashed);
    }

    public static void checkSessionCookie(HttpServletRequest request, HttpServletResponse response) throws JServletException {
        Cookie extractedJframeworkSessionID = getCookie(request, JFSESSIONID);
        if (!isCookiePresent(request, JFSESSIONID) || extractedJframeworkSessionID.getValue().equalsIgnoreCase("")) {
            String warning = "Cookie: " + JFSESSIONID + " is not present in HTTP cookies";
            Logger.warning(warning);
            // invalidate httpsession
            //request.getSession().invalidate();
            //response.reset();
            throw new JServletException(new KoResponse(BaseStatusCode.JFRAMEWORK_SESSIONID_ERROR));
        }
        if (!extractedJframeworkSessionID.getValue().equalsIgnoreCase(generateSessionCookie(request).getValue())) {
            String error = "JFRAMEWORKSESSIONID mismatch, possible Session Hijacking Attack";
            Logger.error(error);
            resetSession(request, response);

            throw new JServletException(new KoResponse(BaseStatusCode.JFRAMEWORK_SESSIONID_ERROR));
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

    /**
     * delete ServletManager object
     *
     * @param request
     * @param response
     */

    public void reset(HttpServletRequest request, HttpServletResponse response) {
        try {
            session = new Session();
        } catch (/*DatabaseException*/Exception e) {
            Logger.error(e);
        }
        this.httpServletRequest = null;
        this.httpServletResponse = null;
        attribMap = null;
        servletPath = null;
        resetSession(request, response);
    }

    /**
     * invalidate HttpSession and set expired cookies in response
     */

    public void resetSession() {
        resetSession(httpServletRequest, httpServletResponse);
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


    public Session getServerSession() throws JServletException {
        return getServerSession(httpServletRequest);
    }

    public static void setServerSession(Session session1, HttpServletRequest request) throws JServletException {
        // http session
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(HTTP_SESSION_NAME, session1);
    }

    public static Session getServerSession(HttpServletRequest request) throws JServletException {
        // http session
        HttpSession httpSession = request.getSession();
        // server session
        Session session1 = (Session) httpSession.getAttribute(HTTP_SESSION_NAME);
        if (session1 == null) {
            try {
                session = new Session();
                setServerSession(session, request);
            } catch (/*DatabaseException*/Exception e) {
                throw new JServletException(e);
            }
        } else {
            try {
                if (session1.getCryptoServer() == null) {
                    try {
                        Logger.info("HTTPSession: Session not null | CryptoServer null");
                        session = new Session();
                        setServerSession(session, request);
                    } catch (/*DatabaseException*/Exception e) {
                        throw new JServletException(e);
                    }
                } else {
                    session = session1;
                }
            } catch (/*DatabaseException*/Exception e) {
                throw new JServletException(e);
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
        T session = (T) httpSession.getAttribute(HTTP_SESSION_NAME + sessionName);
        if (session == null) {
            try {
                session = getNewSessionInstance();
                httpSession.setAttribute(HTTP_SESSION_NAME + sessionName, session);
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


    public void print(Response response) {
        print(httpServletResponse, response);
    }

    public static void print(HttpServletResponse httpServletResponse, Response response) {
        if (httpServletResponse != null) {
            PrintWriter out = null;
            try {
                out = httpServletResponse.getWriter();
                out.println(response.json());
                // close out
                out.flush();
                out.close();
            } catch (IOException e) {
                //e.printStackTrace();
                Logger.error(e);
            }
        } else {
            Logger.info("Servlet Manager Print error, response: " + response.getMessage());
        }
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }
}

