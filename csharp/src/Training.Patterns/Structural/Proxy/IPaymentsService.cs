namespace Training.Patterns.Structural.Proxy;

public interface IPaymentsService
{
    void Pay(IReadOnlyDictionary<string, string> properties);
}
