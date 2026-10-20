package pl.training.module4.creational.abstractfactory.http;

import pl.training.module4.creational.abstractfactory.Connection;

public class HttpConnection implements Connection {

    @Override
    public int getPort() {
        return 80;
    }

}
