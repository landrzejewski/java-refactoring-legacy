namespace Training.Workshop.M7.S09DoubleNegative.Step1;

/// <summary>Krok 1: pozytywny predykat IsExpired() delegujący do IsNotExpired() - nic jeszcze nie migrujemy.</summary>
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
