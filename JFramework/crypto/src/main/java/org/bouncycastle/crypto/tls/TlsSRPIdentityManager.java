package org.bouncycastle.crypto.tls;

/**
 * @deprecated Migrate to the (D)TLS API in org.bouncycastle.tls (bctls jar).
 */
public interface TlsSRPIdentityManager {
    /**
     * Lookup the {@link TlsSRPLoginParameters} corresponding to the specified identity.
     * <p>
     * NOTE: To avoid "identity probing", unknown identities SHOULD be handled as recommended in RFC
     * 5054 2.5.1.3. {@link SimulatedTlsSRPIdentityManager} is provided for this purpose.
     *
     * @param identity the SRP identity sent by the connecting client
     * @return the {@link TlsSRPLoginParameters} for the specified identity, or else 'simulated'
     * parameters if the identity is not recognized. A null value is also allowed, but not
     * recommended.
     */
    TlsSRPLoginParameters getLoginParameters(byte[] identity);
}