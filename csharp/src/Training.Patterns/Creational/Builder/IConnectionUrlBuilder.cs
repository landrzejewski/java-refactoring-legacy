namespace Training.Patterns.Creational.Builder;

public interface IConnectionUrlBuilder
{
    IConnectionUrlBuilder Host(string host);

    IConnectionUrlBuilder Port(int port);

    IConnectionUrlBuilder Protocol(string protocol);

    IConnectionUrlBuilder Database(string database);

    IConnectionUrlBuilder Encoding(string encoding);

    ConnectionUrl Build();
}
