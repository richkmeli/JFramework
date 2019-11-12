package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.tls;

/**
 * @deprecated Migrate to the (D)TLS API in it.richkmeli.jframework.crypto.algorithm.bouncycastle.tls (bctls jar).
 */
public class TlsFatalAlert
        extends TlsException {
    protected short alertDescription;

    public TlsFatalAlert(short alertDescription) {
        this(alertDescription, null);
    }

    public TlsFatalAlert(short alertDescription, Throwable alertCause) {
        super(AlertDescription.getText(alertDescription), alertCause);

        this.alertDescription = alertDescription;
    }

    public short getAlertDescription() {
        return alertDescription;
    }
}
