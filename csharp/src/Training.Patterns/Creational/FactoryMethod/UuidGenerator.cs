namespace Training.Patterns.Creational.FactoryMethod;

public class UuidGenerator : IIdGenerator
{
    public string GetNext() => Guid.NewGuid().ToString();
}
