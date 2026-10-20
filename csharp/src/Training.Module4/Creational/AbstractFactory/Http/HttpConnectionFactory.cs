namespace Training.Module4.Creational.AbstractFactory.Http;

public class HttpConnectionFactory : IConnectionAbstractFactory
{
    public IConnection CreateConnection() => new HttpConnection();

    public ISecuredConnection CreateSecuredConnection() => new HttpsConnection();
}
