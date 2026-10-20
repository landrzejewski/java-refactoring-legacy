namespace Training.Module4.Creational.FactoryMethod;

public class UuidGenerator : IIdGenerator
{
    public string GetNext() => Guid.NewGuid().ToString();
}
