namespace Training.Module4.Creational.AbstractFactory;

public interface IConnectionAbstractFactory
{
    IConnection CreateConnection();

    ISecuredConnection CreateSecuredConnection();
}
