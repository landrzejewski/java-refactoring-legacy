namespace Training.Workshop.M7.S02MethodObject.Step2;

/// <summary>
/// Krok 2: fasada bez zmian - publiczne API i sposób tworzenia obiektu metody
/// (nowy na każde wywołanie) zostają takie jak w kroku 1. Zmienia się GroupQuoteCalculation.
/// </summary>
public sealed class GroupPricing
{
    public Quote Quote(GroupOrder order)
    {
        return new GroupQuoteCalculation(order).Calculate();
    }
}
