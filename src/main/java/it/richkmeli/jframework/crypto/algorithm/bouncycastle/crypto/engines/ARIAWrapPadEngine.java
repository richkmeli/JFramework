package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.engines;

public class ARIAWrapPadEngine
        extends RFC5649WrapEngine {
    public ARIAWrapPadEngine() {
        super(new ARIAEngine());
    }
}
