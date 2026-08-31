package pl.training.patterns.creational.builder;

public class GenericConnectionUrlBuilder implements ConnectionUrlBuilder {

    protected static final String ENCODING = "UTF-8";

    protected final ConnectionUrl connectionUrl = new ConnectionUrl();

    public GenericConnectionUrlBuilder() {
        connectionUrl.encoding = ENCODING;
    }

    @Override
    public ConnectionUrl build() {
        return connectionUrl;
    }

    @Override
    public GenericConnectionUrlBuilder host(String host) {
        connectionUrl.host = host;
        return this;
    }

    @Override
    public GenericConnectionUrlBuilder port(int port) {
        connectionUrl.port = port;
        return this;
    }

    @Override
    public GenericConnectionUrlBuilder protocol(String protocol) {
        connectionUrl.protocol = protocol;
        return this;
    }

    @Override
    public GenericConnectionUrlBuilder database(String database) {
        connectionUrl.database = database;
        return this;
    }

    @Override
    public GenericConnectionUrlBuilder encoding(String encoding) {
        connectionUrl.encoding = encoding;
        return this;
    }

}
