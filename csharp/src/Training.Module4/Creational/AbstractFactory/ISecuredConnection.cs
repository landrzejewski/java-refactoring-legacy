namespace Training.Module4.Creational.AbstractFactory;

public interface ISecuredConnection : IConnection
{
    string EncryptionAlgorithm { get; }
}
