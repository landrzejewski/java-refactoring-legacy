package pl.training.examples.creational.abstractfactory.ftp;

import pl.training.examples.creational.abstractfactory.Connection;

public class FtpConnection implements Connection {

    @Override
    public int getPort() {
        return 21;
    }

}
