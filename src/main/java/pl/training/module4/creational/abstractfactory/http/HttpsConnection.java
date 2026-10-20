package pl.training.module4.creational.abstractfactory.http;

import pl.training.module4.creational.abstractfactory.SecuredConnection;

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
