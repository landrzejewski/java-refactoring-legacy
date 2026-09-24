namespace Training.Workshop.M7.S02MethodObject.Step1;

/// <summary>
/// Krok 1: Extract Method Object - ciało metody skopiowane bez upraszczania
/// do GroupQuoteCalculation. Publiczna metoda zostaje jako fasada i tylko deleguje.
/// </summary>
public sealed class GroupPricing
{
    public Quote Quote(GroupOrder order)
    {
        return new GroupQuoteCalculation(order).Calculate();
    }
}
