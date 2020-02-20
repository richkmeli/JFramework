package it.richkmeli.jframework.network.tcp.server.http.crypto;

import it.richkmeli.jframework.network.tcp.server.http.payload.response.KOResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.StatusCode;
import it.richkmeli.jframework.network.tcp.server.http.util.JServletException;
import it.richkmeli.jframework.network.tcp.server.http.util.ServletManager;
import it.richkmeli.jframework.network.tcp.server.http.util.Session;
import it.richkmeli.jframework.util.Logger;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.ResourceBundle;

public abstract class SecureConnection {
    private String encryptionKey;

    public SecureConnection() {
        super();
        encryptionKey = ResourceBundle.getBundle("configuration").getString("encryptionkey");

    }

    protected abstract void doBeforeCryptoAction(HttpServletRequest request, HttpServletResponse response, String clientID) throws Exception;

    protected abstract void doFinalCryptoAction() throws Exception;


    public void doAction(HttpServletRequest request, HttpServletResponse response) throws IOException, JServletException {
        HttpSession httpSession = request.getSession();
        Session session = null;
        // try {
            session = ServletManager.getServerSession(request);
//        } catch (ServletException e) {
//            httpSession.setAttribute("error", e);
//            request.getRequestDispatcher(ServletManager.ERROR_JSP).forward(request, response);
//
//        }

        try {
            PrintWriter out = response.getWriter();

            File secureDataServer = new File("TESTsecureDataServer.txt");
            String serverKey = "testkeyServer";
            // TODO rendere unico il client ID, che sarebbe il codice del RMC, vedi se passarlo come parametro o generato o altro fattore

            if (request.getParameterMap().containsKey("clientID")) {
                String clientID = new String(Base64.getUrlDecoder().decode(request.getParameter("clientID")));

                doBeforeCryptoAction(request, response, clientID);

                String clientResponse = "";
                if (request.getParameterMap().containsKey("data")) {
                    clientResponse = request.getParameter("data");

                    // TODO metti info in sessione per farla prendere dalle altre servlet, solo questa fa init, altrimenti bisogna mettere il device id davanti in tutte le richieste
                    String serverResponse = session.getCryptoServer().init(secureDataServer, serverKey, clientID, clientResponse);

                    int serverState = new JSONObject(serverResponse).getInt("state");
                    String serverPayload = new JSONObject(serverResponse).getString("payload");

                    if (serverState == 3) {
                        doFinalCryptoAction();
                    }

                    // TODO cambia con OKResponse
                    out.println(serverPayload);
                    out.flush();
                } else {
                    out.println((new KOResponse(StatusCode.SECURE_CONNECTION, "data parameter not present")).json());
                }
            } else {
                out.println((new KOResponse(StatusCode.SECURE_CONNECTION, "clientID parameter not present")).json());
            }
        } catch (Exception e) {
            Logger.error("SERVLET encryptionKey, doGet", e);
            //httpSession.setAttribute("error", e);
            //request.getRequestDispatcher(ServletManager.ERROR_JSP).forward(request, response);
            throw new JServletException(e);
        }
    }

}
