package pl.training.examples.creational.abstractfactory;

public interface ConnectionAbstractFactory {

    Connection createConnection();

    SecuredConnection createSecuredConnection();

}
