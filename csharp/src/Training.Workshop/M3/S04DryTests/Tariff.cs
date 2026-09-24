namespace Training.Workshop.M3.S04DryTests;

/// <summary>
/// Kod produkcyjny sceny (stabilny): taryfa - ceny formatów i zniżki typów biletów.
/// <see cref="WithDiscount"/> pozwala testom podłożyć taryfę z błędem.
/// </summary>
public sealed record Tariff(
    IReadOnlyDictionary<string, decimal> BasePrices,
    IReadOnlyDictionary<string, int> DiscountPercents)
{
    public IReadOnlyDictionary<string, decimal> BasePrices { get; } = new Dictionary<string, decimal>(BasePrices);

    public IReadOnlyDictionary<string, int> DiscountPercents { get; } = new Dictionary<string, int>(DiscountPercents);

    public static Tariff Standard()
    {
        return new Tariff(
            new Dictionary<string, decimal> { ["2D"] = 25.00m, ["3D"] = 32.00m, ["IMAX"] = 40.00m },
            new Dictionary<string, int> { ["NORMAL"] = 0, ["STUDENT"] = 25, ["SENIOR"] = 30, ["CHILD"] = 40 });
    }

    public Tariff WithDiscount(string type, int percent)
    {
        var changed = new Dictionary<string, int>(DiscountPercents)
        {
            [type] = percent,
        };
        return new Tariff(BasePrices, changed);
    }
}
