namespace Training.Module4.Creational.AbstractFactory;

public class Service
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(Service));

    private readonly IConnectionAbstractFactory connectionAbstractFactory;

    public void Run()
    {
        var connection = connectionAbstractFactory.CreateConnection();
        var securedConnection = connectionAbstractFactory.CreateSecuredConnection();
        log.Info("Connection: " + connection.Port);
        log.Info("Secured connection: " + securedConnection.Port);
    }

    public Service(IConnectionAbstractFactory connectionAbstractFactory)
    {
        this.connectionAbstractFactory = connectionAbstractFactory;
    }
}
