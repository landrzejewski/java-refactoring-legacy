namespace Training.Workshop.M7.S09DoubleNegative.Start;

/// <summary>Start: negatywny predykat IsNotExpired - czytelnik musi go odwracać w głowie.</summary>
public sealed record Voucher(string Code, DateOnly ValidUntil)
{
    public bool IsNotExpired(DateOnly today)
    {
        return !(today > ValidUntil);
    }
}
