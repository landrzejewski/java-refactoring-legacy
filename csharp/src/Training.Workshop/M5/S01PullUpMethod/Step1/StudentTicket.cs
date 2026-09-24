using Training.Workshop.Shared;

namespace Training.Workshop.M5.S01PullUpMethod.Step1;

/// <summary>Krok 1: ujednolicenie ciała <c>Label()</c> - teraz tekstowo identyczne jak w StandardTicket.</summary>
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
        return Title + ": " + Price();
    }
}
