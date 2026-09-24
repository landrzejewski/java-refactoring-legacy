namespace Training.Workshop.M7.S09DoubleNegative.Step2;

/// <summary>Krok 1 (bez zmian w kroku 2): pozytywny predykat Vip delegujący do starej właściwości.</summary>
public sealed record Customer(string Email, bool NotVip)
{
    public bool Vip => !NotVip;
}
