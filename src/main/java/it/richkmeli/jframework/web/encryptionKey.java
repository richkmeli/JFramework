//package it.richkmeli.jframework.web;
//
//import it.richkmeli.jframework.crypto.Crypto;
//import it.richkmeli.jframework.web.response.KOResponse;
//import it.richkmeli.jframework.web.response.OKResponse;
//import it.richkmeli.jframework.web.response.StatusCode;
//import it.richkmeli.jframework.web.util.ServletManager;
//import it.richkmeli.jframework.web.util.Session;
//import org.json.JSONObject;
//
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.Map;
//import java.util.ResourceBundle;
//
//
//@WebServlet("/encryptionKey")
//public abstract class encryptionKey extends HttpServlet {
//    private static final long serialVersionUID = 1L;
//    private static final int keyLength = 32;
//    private String password;
//
//    public encryptionKey() {
//        super();
//        password = ResourceBundle.getBundle("configuration").getString("encryptionkey");
//
//    }
//
//    public abstract String doSpecificActionGet();
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
//
//        HttpSession httpSession = request.getSession();
//        Session session = null;
//        PrintWriter out = response.getWriter();
//
//        try {
//            session = ServletManager.getServerSession(httpSession);
//
//            Map<String, String> attribMap = ServletManager.doDefaultProcessRequest(request);
//
//            if (session.getUser() != null) {
//                String id = attribMap.get("id");
//                id = Crypto.decryptRC4(id, password);
//
//                String encryptionKey = doSpecificActionGet();
//
//                // encrypt key server-side generated or error message with pre-shared password
//                encryptionKey = Crypto.encryptRC4(encryptionKey, password);
//                encryptionKey = "$" + encryptionKey + "#";
//
//                JSONObject encKeyJSON = new JSONObject(encryptionKey);
//
//                String output = ServletManager.doDefaultProcessResponse(request, encKeyJSON.toString());
//                out.println((new OKResponse(StatusCode.SUCCESS, output)).json());
//            } else {
//                out.println((new KOResponse(StatusCode.NOT_LOGGED)).json());
//            }
//
//        } catch (Exception e) {
//            out.println((new KOResponse(StatusCode.GENERIC_ERROR, e.getMessage())).json());
//        }
//
//        out.flush();
//        out.close();
//
////        HttpSession httpSession = request.getSession();
////        Session session = null;
////        try {
////            session = ServletManager.getServerSession(httpSession);
////        } catch (ServletException e) {
////            httpSession.setAttribute("error", e);
////            request.getRequestDispatcher(ServletManager.ERROR_JSP).forward(request, response);
////
////        }
////
////        try {
////            if (request.getParameterMap().containsKey("id")) {
////
////                String name = request.getParameter("id");
////                name = Crypto.decryptRC4(name, password);
////
////                DeviceDatabaseManager deviceDatabaseManager = session.getDeviceDatabaseManager();
////                String encryptionKey = deviceDatabaseManager.getEncryptionKey(name);
////                if (encryptionKey.isEmpty()) {
////                    encryptionKey = "Error";
////                }
////
////                // encrypt key server-side generated or error message with pre-shared password
////                encryptionKey = Crypto.encryptRC4(encryptionKey, password);
////
////                encryptionKey = "$" + encryptionKey + "#";
////
////                PrintWriter out = response.getWriter();
////                out.println(encryptionKey);
////                out.flush();
////            } else {
////                // argomenti non presenti
////                // TODO rimanda da qualche parte perche c'è errore
////                Logger.error("SERVLET encryptionKey, doGet: argomenti non presenti");
////                httpSession.setAttribute("error", "argomenti non presenti");
////                request.getRequestDispatcher(ServletManager.LOGIN_HTML).forward(request, response);
////            }
////        } catch (Exception e) {
////            Logger.error("SERVLET encryptionKey, doGet", e);
////            httpSession.setAttribute("error", e);
////            request.getRequestDispatcher(ServletManager.ERROR_JSP).forward(request, response);
////        }
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
//        doGet(request, response);
//    }
//
//}
