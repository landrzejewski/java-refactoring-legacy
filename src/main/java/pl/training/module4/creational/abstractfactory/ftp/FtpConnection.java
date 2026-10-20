package pl.training.module4.creational.abstractfactory.ftp;

import pl.training.module4.creational.abstractfactory.Connection;

public class FtpConnection implements Connection {

    @Override
    public int getPort() {
        return 21;
    }

}
