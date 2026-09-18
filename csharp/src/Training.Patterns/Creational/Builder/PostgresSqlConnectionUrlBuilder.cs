namespace Training.Patterns.Creational.Builder;

public class PostgresSqlConnectionUrlBuilder : IConnectionUrlBuilder
{
    private const string PROTOCOL = "postgres";
    private const int PORT = 5432;

    private readonly GenericConnectionUrlBuilder builder = new();

    public PostgresSqlConnectionUrlBuilder()
    {
        builder.Protocol(PROTOCOL);
        builder.Port(PORT);
    }

    public ConnectionUrl Build() => builder.Build();

    public PostgresSqlConnectionUrlBuilder Host(string host)
    {
        builder.Host(host);
        return this;
    }

    public PostgresSqlConnectionUrlBuilder Port(int port)
    {
        builder.Port(port);
        return this;
    }

    public PostgresSqlConnectionUrlBuilder Protocol(string protocol)
    {
        builder.Protocol(protocol);
        return this;
    }

    public PostgresSqlConnectionUrlBuilder Database(string database)
    {
        builder.Database(database);
        return this;
    }

    public PostgresSqlConnectionUrlBuilder Encoding(string encoding)
    {
        builder.Encoding(encoding);
        return this;
    }

    IConnectionUrlBuilder IConnectionUrlBuilder.Host(string host) => Host(host);

    IConnectionUrlBuilder IConnectionUrlBuilder.Port(int port) => Port(port);

    IConnectionUrlBuilder IConnectionUrlBuilder.Protocol(string protocol) => Protocol(protocol);

    IConnectionUrlBuilder IConnectionUrlBuilder.Database(string database) => Database(database);

    IConnectionUrlBuilder IConnectionUrlBuilder.Encoding(string encoding) => Encoding(encoding);
}
