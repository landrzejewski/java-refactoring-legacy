namespace Training.Module3.Domain;

public sealed record Parcel
{
    public Parcel(decimal weightKg)
    {
        if (weightKg <= 0m)
        {
            throw new ArgumentException("Weight must be positive", nameof(weightKg));
        }

        WeightKg = weightKg;
    }

    public decimal WeightKg { get; }
}
