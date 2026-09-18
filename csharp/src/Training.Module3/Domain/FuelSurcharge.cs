namespace Training.Module3.Domain;

public sealed class FuelSurcharge
{
    private readonly decimal rate;

    public FuelSurcharge(decimal rate)
    {
        if (rate < 0m || rate > 1m)
        {
            throw new ArgumentException(
                "Fuel surcharge rate must be between zero and one",
                nameof(rate));
        }

        this.rate = rate;
    }

    public decimal AddTo(decimal baseAmount)
    {
        if (baseAmount < 0m)
        {
            throw new ArgumentException("Base amount must not be negative", nameof(baseAmount));
        }

        return Math.Round(
            baseAmount + baseAmount * rate,
            2,
            MidpointRounding.AwayFromZero);
    }
}
