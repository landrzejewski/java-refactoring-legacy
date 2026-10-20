package pl.training.module4.creational.factorymethod;

public class TestIdGeneratorFactory implements IdGeneratorFactory {

    @Override
    public IdGenerator create() {
        return new IncrementalIdGenerator();
    }

}
