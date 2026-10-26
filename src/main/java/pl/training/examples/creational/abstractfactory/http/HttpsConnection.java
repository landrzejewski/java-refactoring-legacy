package pl.training.examples.creational.abstractfactory.http;

import pl.training.examples.creational.abstractfactory.SecuredConnection;

public class HttpsConnection implements SecuredConnection {

    @Override
    public int getPort() {
        return 443;
    }

    @Override
    public String getEncryptionAlgorithm() {
        return "AES";
    }

}
