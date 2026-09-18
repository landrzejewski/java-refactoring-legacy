namespace Training.Patterns.Creational.FactoryMethod;

public class Service
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(Service));

    private readonly IIdGeneratorFactory idGeneratorFactory;

    public void Run()
    {
        var idGenerator = idGeneratorFactory.Create();
        log.Info("Id: " + idGenerator.GetNext());
    }

    public Service(IIdGeneratorFactory idGeneratorFactory)
    {
        this.idGeneratorFactory = idGeneratorFactory;
    }
}
