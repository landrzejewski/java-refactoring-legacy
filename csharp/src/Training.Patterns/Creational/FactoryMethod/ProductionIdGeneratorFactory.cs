namespace Training.Patterns.Creational.FactoryMethod;

public class ProductionIdGeneratorFactory : IIdGeneratorFactory
{
    private readonly UuidGenerator uuidGenerator = new();

    public IIdGenerator Create() => uuidGenerator;
}
