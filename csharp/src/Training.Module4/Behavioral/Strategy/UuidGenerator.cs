namespace Training.Module4.Behavioral.Strategy;

public class UuidGenerator : IIdGenerator
{
    public string GetNext() => Guid.NewGuid().ToString();
}
