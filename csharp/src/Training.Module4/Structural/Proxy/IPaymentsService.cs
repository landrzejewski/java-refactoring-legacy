namespace Training.Module4.Structural.Proxy;

public interface IPaymentsService
{
    void Pay(IReadOnlyDictionary<string, string> properties);
}
