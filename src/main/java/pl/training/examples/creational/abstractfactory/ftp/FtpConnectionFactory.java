package pl.training.examples.creational.abstractfactory.ftp;

import pl.training.examples.creational.abstractfactory.Connection;
import pl.training.examples.creational.abstractfactory.ConnectionAbstractFactory;
import pl.training.examples.creational.abstractfactory.SecuredConnection;

public class FtpConnectionFactory implements ConnectionAbstractFactory {

    @Override
    public Connection createConnection() {
        return new FtpConnection();
    }

    @Override
    public SecuredConnection createSecuredConnection() {
        return new SftpConnection();
    }

}
