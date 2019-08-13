package it.richkmeli.jframework.crypto.algorithm;

import java.security.Provider;
import java.security.Security;

public class ProviderManager {
    public static void init(Provider provider) {
        if (Security.getProvider(provider.getName()) == null) {
            Security.addProvider(provider);
        }/* else {
            Logger.info(BouncyCastleProvider.PROVIDER_NAME + " is already present");
        }*/
    }
}
