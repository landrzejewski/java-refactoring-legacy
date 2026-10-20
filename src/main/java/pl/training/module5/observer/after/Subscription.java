package pl.training.module5.observer.after;

@FunctionalInterface
public interface Subscription extends AutoCloseable {
    @Override
    void close();
}
