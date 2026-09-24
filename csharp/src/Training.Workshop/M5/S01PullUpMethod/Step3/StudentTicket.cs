using Training.Workshop.Shared;

namespace Training.Workshop.M5.S01PullUpMethod.Step3;

/// <summary>Krok 3: w podklasie zostaje tylko to, co naprawdę różne - cena (-25%).</summary>
public sealed class StudentTicket : Ticket
{
    public StudentTicket(string title, Money basePrice) : base(title, basePrice)
    {
    }

    public override Money Price()
    {
        return BasePrice.Minus(BasePrice.Percent(25));
    }
}
