package pl.training.examples.behavioral.strategy.movies;

public class Order {
    private final MovieType movieType;

    public double getTotalValue(long periodInDays) {
        return movieType.getValueFor(periodInDays);
    }

    public Order(final MovieType movieType) {
        this.movieType = movieType;
    }
}
