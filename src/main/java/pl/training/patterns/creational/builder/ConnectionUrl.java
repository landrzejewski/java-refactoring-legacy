package pl.training.patterns.creational.builder;

public class ConnectionUrl {
    String host;
    int port;
    String protocol;
    String database;
    String encoding;

    @Override
    public String toString() {
        return "jdbc:%s://%s:%d/%s?encoding=%s".formatted(protocol, host, port, database, encoding);
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getEncoding() {
        return this.encoding;
    }
}
