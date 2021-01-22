package it.richkmeli.jframework.network.tcp.server.http.crypto;

import it.richkmeli.jframework.network.tcp.server.http.payload.response.BaseStatusCode;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.KoResponse;
import it.richkmeli.jframework.network.tcp.server.http.util.JServletException;
import it.richkmeli.jframework.network.tcp.server.http.util.ServletManager;
import it.richkmeli.jframework.network.tcp.server.http.util.Session;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.ResourceBundle;

public abstract class SecureConnectionJob {
    private String encryptionKey;

    public SecureConnectionJob() {
        super();
        encryptionKey = ResourceBundle.getBundle("configuration").getString("encryptionkey");

    }

    /**
     * an implementation of this abstract method should contains all activities to be done BEFORE performing encryption process.
     *
     * @param request  HTTP servlet request
     * @param response HTTP servlet response
     * @param clientID client identifier
     * @throws Exception
     */
    protected abstract void doBeforeCryptoAction(HttpServletRequest request, HttpServletResponse response, String clientID) throws Exception;

    /**
     * an implementation of this abstract method should contains all activities to be done AFTER performing encryption process.
     *
     * @throws Exception
     */
    protected abstract void doFinalCryptoAction() throws Exception;


    public void doAction(HttpServletRequest request, HttpServletResponse response) throws IOException, JServletException {
        HttpSession httpSession = request.getSession();
        Session session = null;
        session = ServletManager.getServerSession(request);

        PrintWriter out = response.getWriter();
        try {
            File secureDataServer = new File("secureDataServer.txt");
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
                    out.println((new KoResponse(BaseStatusCode.SECURE_CONNECTION, "data parameter not present")).json());
                }
            } else {
                out.println((new KoResponse(BaseStatusCode.SECURE_CONNECTION, "clientID parameter not present")).json());
            }
        } catch (Throwable e) {
            //Logger.error("SERVLET encryptionKey, doGet", e);
            //throw new JServletException(e);
            out.println((new KoResponse(BaseStatusCode.GENERIC_ERROR, e.getMessage())).json());
        }
    }

}
