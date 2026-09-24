using Training.Workshop.Shared;

namespace Training.Workshop.M6.S01Strategy.Start;

/// <summary>
/// Start: algorytm zniżki wybierany łańcuchem if po nazwie programu. Każdy nowy program
/// (np. "tydzień studenta") dopisuje gałąź w środku metody, która zawiera też walidację.
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
        Money discount;
        if (program == "PREMIERE")
        {
            discount = Money.Zero;
        }
        else if (program == "STUDENT_WEEK" && ticketType == "S")
        {
            discount = basePrice.Percent(50);
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
            discount = basePrice.Percent(percent);
        }
        else
        {
            throw new ArgumentException("unknown program: " + program);
        }
        return basePrice.Minus(discount);
    }
}
