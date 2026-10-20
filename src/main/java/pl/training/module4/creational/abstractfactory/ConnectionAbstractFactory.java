package pl.training.module4.creational.abstractfactory;

public interface ConnectionAbstractFactory {

    Connection createConnection();

    SecuredConnection createSecuredConnection();

}
