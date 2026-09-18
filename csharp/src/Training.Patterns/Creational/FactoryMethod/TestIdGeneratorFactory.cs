namespace Training.Patterns.Creational.FactoryMethod;

public class TestIdGeneratorFactory : IIdGeneratorFactory
{
    public IIdGenerator Create() => new IncrementalIdGenerator();
}
