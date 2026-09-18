namespace Training.Patterns.Creational.Builder;

public class Director
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(Director));

    private readonly IConnectionUrlBuilder connectionUrlBuilder;

    public void Run()
    {
        var connectionUrl = connectionUrlBuilder.Build();
        log.Info("IConnection url: " + connectionUrl);
    }

    public Director(IConnectionUrlBuilder connectionUrlBuilder)
    {
        this.connectionUrlBuilder = connectionUrlBuilder;
    }
}
