using Training.Workshop.Shared;

namespace Training.Workshop.M6.S01Strategy.Step2;

/// <summary>
/// Krok 2: gałęzie przeniesione do strategii (po jednej, test po każdej). W kontekście zostaje
/// wspólna walidacja i jeden switch wybierający strategię - wciąż przy każdym wywołaniu.
/// </summary>
public sealed class TicketPricer
{
    public Money Price(Money basePrice, string ticketType, string? program)
    {
        if (basePrice.CompareTo(Money.Zero) < 0)
        {
            throw new ArgumentException("base price must not be negative");
        }
        if (program == null)
        {
            throw new ArgumentException("program must not be null");
        }
        return basePrice.Minus(PolicyFor(program).Discount(basePrice, ticketType));
    }

    private static IDiscountPolicy PolicyFor(string program)
    {
        return program switch
        {
            "STANDARD" => new StandardDiscount(),
            "STUDENT_WEEK" => new StudentWeekDiscount(new StandardDiscount()),
            "PREMIERE" => new PremiereDiscount(),
            _ => throw new ArgumentException("unknown program: " + program),
        };
    }
}
