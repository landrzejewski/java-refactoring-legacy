namespace Training.Workshop.M3.S13BoundaryCheck.Step2.Domain;

/// <summary>
/// Krok 1: po Extract Class zostaje sama polityka cenowa (LCOM4 = 1)
/// i żadnego using technologii.
/// </summary>
public sealed class ScreeningService
{
    private readonly decimal _basePrice;
    private readonly decimal _morningDiscount;

    public ScreeningService(decimal basePrice, decimal morningDiscount)
    {
        _basePrice = basePrice;
        _morningDiscount = morningDiscount;
    }

    public decimal Price(Screening screening)
    {
        return IsMorning(screening) ? _basePrice - _morningDiscount : _basePrice;
    }

    private static bool IsMorning(Screening screening)
    {
        return screening.Start.Hour < 12;
    }
}
