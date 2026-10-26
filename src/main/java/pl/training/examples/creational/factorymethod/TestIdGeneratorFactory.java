package pl.training.examples.creational.factorymethod;

public class TestIdGeneratorFactory implements IdGeneratorFactory {

    @Override
    public IdGenerator create() {
        return new IncrementalIdGenerator();
    }

}
