package it.richkmeli.jframework.auth.web.account;

import it.richkmeli.jframework.auth.web.util.AuthServletManager;
import it.richkmeli.jframework.auth.web.util.AuthSession;
import it.richkmeli.jframework.auth.web.util.AuthStatusCode;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.KoResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.OkResponse;
import it.richkmeli.jframework.network.tcp.server.http.util.JServletException;

import java.util.Map;

public abstract class LogOutJob {

    protected abstract void doSpecificAction(AuthServletManager authServletManager);

    public void doAction(AuthServletManager authServletManager) {

        try {
            Map<String, String> attribMap = authServletManager.doDefaultProcessRequest();
            authServletManager.checkLogin();

            // server authSession
            AuthSession authSession = authServletManager.getAuthServerSession();

            if (authSession != null) {
                // remove user from the session
                authSession.removeUser();
                doSpecificAction(authServletManager);
            }

            // invalidate HttpSession and set expired cookies in response
            authServletManager.resetSession();

            // reset ServletManager instance
            authServletManager.reset();

            String message = "LogOut succeeded";
            String output = authServletManager.doDefaultProcessResponse(message);

            authServletManager.print(new OkResponse(AuthStatusCode.SUCCESS, output));
        } catch (JServletException e) {
            authServletManager.print(e.getResponse());
        } catch (Throwable e) {
            //e.printStackTrace();
            authServletManager.print(new KoResponse(AuthStatusCode.GENERIC_ERROR, e.getMessage()));
        }

    }
}
