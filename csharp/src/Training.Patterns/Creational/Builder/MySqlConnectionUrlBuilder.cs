namespace Training.Patterns.Creational.Builder;

public class MySqlConnectionUrlBuilder : GenericConnectionUrlBuilder
{
    private const string PROTOCOL = "mysql";
    private const int PORT = 3306;

    public MySqlConnectionUrlBuilder()
    {
        connectionUrl.Protocol = PROTOCOL;
        connectionUrl.Port = PORT;
    }
}
