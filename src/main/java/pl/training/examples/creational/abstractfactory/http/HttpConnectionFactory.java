package pl.training.examples.creational.abstractfactory.http;

import pl.training.examples.creational.abstractfactory.Connection;
import pl.training.examples.creational.abstractfactory.ConnectionAbstractFactory;
import pl.training.examples.creational.abstractfactory.SecuredConnection;

public class HttpConnectionFactory implements ConnectionAbstractFactory {

    @Override
    public Connection createConnection() {
        return new HttpConnection();
    }

    @Override
    public SecuredConnection createSecuredConnection() {
        return new HttpsConnection();
    }

}
