namespace Training.Patterns.Behavioral.Strategy.Movies;

/// <summary>Java enum with fields -> closed class with one static instance per constant.</summary>
public sealed class MovieType
{
    public static readonly MovieType REGULAR = new("REGULAR", 2, 2, 1.5);
    public static readonly MovieType CHILDREN = new("CHILDREN", 3, 1.5, 1.5);
    public static readonly MovieType NEW_RELEASE = new("NEW_RELEASE", 0, 0, 3);

    private readonly string name;

    public int FreeRentalPeriodInDays { get; }

    public double InitialCost { get; }

    public double CostPerDay { get; }

    internal double GetValueFor(long periodInDays)
    {
        if (periodInDays < 0)
        {
            throw new ArgumentException(
                    "periodInDays must not be negative");
        }
        return InitialCost + GetValueForPeriod(periodInDays);
    }

    private double GetValueForPeriod(long periodInDays)
    {
        var paidDays = Math.Max(
                0, periodInDays - FreeRentalPeriodInDays);
        return paidDays * CostPerDay;
    }

    public static IReadOnlyList<MovieType> Values() => [REGULAR, CHILDREN, NEW_RELEASE];

    public override string ToString() => name;

    private MovieType(
            string name,
            int freeRentalPeriodInDays,
            double initialCost,
            double costPerDay)
    {
        this.name = name;
        FreeRentalPeriodInDays = freeRentalPeriodInDays;
        InitialCost = initialCost;
        CostPerDay = costPerDay;
    }
}
