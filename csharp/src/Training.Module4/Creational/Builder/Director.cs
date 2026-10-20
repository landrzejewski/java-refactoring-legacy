namespace Training.Module4.Creational.Builder;

public class Director
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(Director));

    private readonly IConnectionUrlBuilder connectionUrlBuilder;

    public void Run()
    {
        var connectionUrl = connectionUrlBuilder.Build();
        log.Info("Connection url: " + connectionUrl);
    }

    public Director(IConnectionUrlBuilder connectionUrlBuilder)
    {
        this.connectionUrlBuilder = connectionUrlBuilder;
    }
}
