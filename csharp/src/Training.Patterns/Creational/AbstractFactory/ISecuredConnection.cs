namespace Training.Patterns.Creational.AbstractFactory;

public interface ISecuredConnection : IConnection
{
    string EncryptionAlgorithm { get; }
}
