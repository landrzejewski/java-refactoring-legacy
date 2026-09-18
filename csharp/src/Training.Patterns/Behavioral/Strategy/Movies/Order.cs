namespace Training.Patterns.Behavioral.Strategy.Movies;

public class Order
{
    private readonly MovieType movieType;

    public double GetTotalValue(long periodInDays) => movieType.GetValueFor(periodInDays);

    public Order(MovieType movieType)
    {
        this.movieType = movieType;
    }
}
