namespace Training.Patterns.Behavioral.Strategy;

public class Service
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(Service));

    private readonly IIdGenerator idGenerator;

    public void Run()
    {
        log.Info("Id: " + idGenerator.GetNext());
    }

    public Service(IIdGenerator idGenerator)
    {
        this.idGenerator = idGenerator;
    }
}
