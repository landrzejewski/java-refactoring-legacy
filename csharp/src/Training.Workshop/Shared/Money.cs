using System.Globalization;

namespace Training.Workshop.Shared;

/// <summary>
/// Kwota w złotych, zawsze zaokrąglona do 2 miejsc (HALF_UP = AwayFromZero).
/// Wspólny, nierefaktoryzowany typ wartości warsztatu CineLegacy.
/// </summary>
public sealed record Money : IComparable<Money>
{
    public static readonly Money Zero = Of("0.00");

    public Money(decimal amount)
    {
        Amount = Math.Round(amount, 2, MidpointRounding.AwayFromZero);
    }

    public decimal Amount { get; }

    public static Money Of(string amount) => new(decimal.Parse(amount, CultureInfo.InvariantCulture));

    public static Money Of(long amount) => new(amount);

    public Money Plus(Money other) => new(Amount + other.Amount);

    public Money Minus(Money other) => new(Amount - other.Amount);

    public Money Times(int factor) => new(Amount * factor);

    /// <summary>Procent kwoty, np. <c>Percent(25)</c> to 25% tej kwoty.</summary>
    public Money Percent(int percent) => new(Amount * percent / 100);

    public Money Max(Money other) => CompareTo(other) >= 0 ? this : other;

    public bool IsGreaterThan(Money other) => CompareTo(other) > 0;

    public int CompareTo(Money? other) => other is null ? 1 : Amount.CompareTo(other.Amount);

    public override string ToString() => Amount.ToString("0.00", CultureInfo.InvariantCulture);
}
