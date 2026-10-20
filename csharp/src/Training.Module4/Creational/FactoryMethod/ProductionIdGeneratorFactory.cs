namespace Training.Module4.Creational.FactoryMethod;

public class ProductionIdGeneratorFactory : IIdGeneratorFactory
{
    private readonly UuidGenerator uuidGenerator = new();

    public IIdGenerator Create() => uuidGenerator;
}
