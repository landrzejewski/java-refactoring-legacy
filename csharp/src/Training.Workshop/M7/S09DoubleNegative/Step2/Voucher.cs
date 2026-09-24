namespace Training.Workshop.M7.S09DoubleNegative.Step2;

/// <summary>Krok 1 (bez zmian w kroku 2): pozytywny predykat IsExpired() delegujący do IsNotExpired().</summary>
public sealed record Voucher(string Code, DateOnly ValidUntil)
{
    public bool IsNotExpired(DateOnly today)
    {
        return !(today > ValidUntil);
    }

    public bool IsExpired(DateOnly today)
    {
        return !IsNotExpired(today);
    }
}
