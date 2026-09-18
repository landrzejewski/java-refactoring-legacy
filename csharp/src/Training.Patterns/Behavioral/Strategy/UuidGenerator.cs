namespace Training.Patterns.Behavioral.Strategy;

public class UuidGenerator : IIdGenerator
{
    public string GetNext() => Guid.NewGuid().ToString();
}
