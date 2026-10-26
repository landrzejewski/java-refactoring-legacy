package pl.training.examples.creational.abstractfactory.http;

import pl.training.examples.creational.abstractfactory.Connection;

public class HttpConnection implements Connection {

    @Override
    public int getPort() {
        return 80;
    }

}
