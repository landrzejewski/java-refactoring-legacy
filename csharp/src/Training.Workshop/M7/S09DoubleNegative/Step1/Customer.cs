namespace Training.Workshop.M7.S09DoubleNegative.Step1;

/// <summary>Krok 1: pozytywny predykat Vip delegujący do starej właściwości - dokładne dopełnienie.</summary>
public sealed record Customer(string Email, bool NotVip)
{
    public bool Vip => !NotVip;
}
