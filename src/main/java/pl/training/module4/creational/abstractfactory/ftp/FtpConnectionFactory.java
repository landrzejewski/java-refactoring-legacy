package pl.training.module4.creational.abstractfactory.ftp;

import pl.training.module4.creational.abstractfactory.Connection;
import pl.training.module4.creational.abstractfactory.ConnectionAbstractFactory;
import pl.training.module4.creational.abstractfactory.SecuredConnection;

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
