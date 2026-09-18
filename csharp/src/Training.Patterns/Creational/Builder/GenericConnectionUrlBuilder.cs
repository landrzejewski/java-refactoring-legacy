namespace Training.Patterns.Creational.Builder;

public class GenericConnectionUrlBuilder : IConnectionUrlBuilder
{
    protected const string ENCODING = "UTF-8";

    protected readonly ConnectionUrl connectionUrl = new();

    public GenericConnectionUrlBuilder()
    {
        connectionUrl.Encoding = ENCODING;
    }

    public ConnectionUrl Build() => connectionUrl;

    // Java uses covariant return types (GenericConnectionUrlBuilder); C# interface implementations
    // cannot be covariant, so the public methods return the concrete type and the interface is implemented explicitly
    public GenericConnectionUrlBuilder Host(string host)
    {
        connectionUrl.Host = host;
        return this;
    }

    public GenericConnectionUrlBuilder Port(int port)
    {
        connectionUrl.Port = port;
        return this;
    }

    public GenericConnectionUrlBuilder Protocol(string protocol)
    {
        connectionUrl.Protocol = protocol;
        return this;
    }

    public GenericConnectionUrlBuilder Database(string database)
    {
        connectionUrl.Database = database;
        return this;
    }

    public GenericConnectionUrlBuilder Encoding(string encoding)
    {
        connectionUrl.Encoding = encoding;
        return this;
    }

    IConnectionUrlBuilder IConnectionUrlBuilder.Host(string host) => Host(host);

    IConnectionUrlBuilder IConnectionUrlBuilder.Port(int port) => Port(port);

    IConnectionUrlBuilder IConnectionUrlBuilder.Protocol(string protocol) => Protocol(protocol);

    IConnectionUrlBuilder IConnectionUrlBuilder.Database(string database) => Database(database);

    IConnectionUrlBuilder IConnectionUrlBuilder.Encoding(string encoding) => Encoding(encoding);
}
