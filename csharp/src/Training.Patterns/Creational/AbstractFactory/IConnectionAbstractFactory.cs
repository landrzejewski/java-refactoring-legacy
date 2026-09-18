namespace Training.Patterns.Creational.AbstractFactory;

public interface IConnectionAbstractFactory
{
    IConnection CreateConnection();

    ISecuredConnection CreateSecuredConnection();
}
