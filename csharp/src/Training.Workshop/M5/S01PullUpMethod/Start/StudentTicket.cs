using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S01PullUpMethod.Start;

/// <summary>Start: bilet studencki (-25%) - ta sama etykieta, ale przez string.Format.</summary>
public sealed class StudentTicket : Ticket
{
    public StudentTicket(string title, Money basePrice) : base(title, basePrice)
    {
    }

    public Money Price()
    {
        return BasePrice.Minus(BasePrice.Percent(25));
    }

    public string Label()
    {
        return string.Format(CultureInfo.InvariantCulture, "{0}: {1}", Title, Price());
    }
}
