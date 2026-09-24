using Training.Workshop.Shared;

namespace Training.Workshop.M6.S01Strategy.Step1;

/// <summary>
/// Krok 1: Extract Interface + strategia przejściowa. Kontekst woła już IDiscountPolicy,
/// ale jedyna implementacja to mała klasa delegująca do starego łańcucha if. Mały, odwracalny ruch.
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
        IDiscountPolicy policy = new LegacyPolicy(program);
        return basePrice.Minus(policy.Discount(basePrice, ticketType));
    }

    private static Money LegacyDiscount(Money basePrice, string ticketType, string program)
    {
        if (program == "PREMIERE")
        {
            return Money.Zero;
        }
        else if (program == "STUDENT_WEEK" && ticketType == "S")
        {
            return basePrice.Percent(50);
        }
        else if (program == "STANDARD" || program == "STUDENT_WEEK")
        {
            var percent = ticketType switch
            {
                "N" => 0,
                "S" => 25,
                "E" => 30,
                "C" => 40,
                _ => throw new ArgumentException("unknown ticket type: " + ticketType),
            };
            return basePrice.Percent(percent);
        }
        throw new ArgumentException("unknown program: " + program);
    }

    /// <summary>Strategia przejściowa (w Javie lambda) - deleguje do starego łańcucha if.</summary>
    private sealed class LegacyPolicy(string program) : IDiscountPolicy
    {
        public Money Discount(Money basePrice, string ticketType) => LegacyDiscount(basePrice, ticketType, program);
    }
}
