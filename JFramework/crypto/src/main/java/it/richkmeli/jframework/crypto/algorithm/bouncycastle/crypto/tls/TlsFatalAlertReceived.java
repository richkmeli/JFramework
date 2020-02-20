package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.tls;

/**
 * @deprecated Migrate to the (D)TLS API in it.richkmeli.jframework.crypto.algorithm.bouncycastle.tls (bctls jar).
 */
public class TlsFatalAlertReceived
        extends TlsException {
    protected short alertDescription;

    public TlsFatalAlertReceived(short alertDescription) {
        super(AlertDescription.getText(alertDescription), null);

        this.alertDescription = alertDescription;
    }

    public short getAlertDescription() {
        return alertDescription;
    }
}
