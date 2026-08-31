package pl.training.patterns.creational.builder;

public interface ConnectionUrlBuilder {

    ConnectionUrlBuilder host(String host);

    ConnectionUrlBuilder port(int port);

    ConnectionUrlBuilder protocol(String protocol);

    ConnectionUrlBuilder database(String database);

    ConnectionUrlBuilder encoding(String encoding);

    ConnectionUrl build();

}
