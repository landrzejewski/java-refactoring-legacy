namespace Training.Module4.Creational.FactoryMethod;

public class TestIdGeneratorFactory : IIdGeneratorFactory
{
    public IIdGenerator Create() => new IncrementalIdGenerator();
}
