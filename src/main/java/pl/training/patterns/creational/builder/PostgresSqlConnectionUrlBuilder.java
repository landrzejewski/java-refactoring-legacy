package pl.training.patterns.creational.builder;

public class PostgresSqlConnectionUrlBuilder implements ConnectionUrlBuilder {

    private static final String PROTOCOL = "postgres";
    private static final int PORT = 5432;

    private final GenericConnectionUrlBuilder builder = new GenericConnectionUrlBuilder();

    public PostgresSqlConnectionUrlBuilder() {
        builder.protocol(PROTOCOL);
        builder.port(PORT);
    }

    @Override
    public ConnectionUrl build() {
        return builder.build();
    }

    @Override
    public PostgresSqlConnectionUrlBuilder host(String host) {
        builder.host(host);
        return this;
    }

    @Override
    public PostgresSqlConnectionUrlBuilder port(int port) {
        builder.port(port);
        return this;
    }

    @Override
    public PostgresSqlConnectionUrlBuilder protocol(String protocol) {
        builder.protocol(protocol);
        return this;
    }

    @Override
    public PostgresSqlConnectionUrlBuilder database(String database) {
        builder.database(database);
        return this;
    }

    @Override
    public PostgresSqlConnectionUrlBuilder encoding(String encoding) {
        builder.encoding(encoding);
        return this;
    }

}
