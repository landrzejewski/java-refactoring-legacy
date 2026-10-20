package pl.training.module4.creational.abstractfactory.http;

import pl.training.module4.creational.abstractfactory.Connection;
import pl.training.module4.creational.abstractfactory.ConnectionAbstractFactory;
import pl.training.module4.creational.abstractfactory.SecuredConnection;

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
