using Training.Workshop.Shared;

namespace Training.Workshop.M8.S04ShadowLimits.Step3;

/// <summary>
/// Krok 3: Separate Query from Modifier - czysta część nowej ścieżki. Nie ma dostępu do
/// żadnego portu efektów, więc w cieniu nie da się jej użyć "za mocno".
/// </summary>
public sealed class BookingPlanner
{
    private static readonly Money Ticket2D = Money.Of("25.00");
    private static readonly Money OnlineFee = Money.Of("2.00");

    public BookingPlan Plan(BookingRequest request)
    {
        Money tickets = Ticket2D.Times(request.Tickets);
        Money total = tickets.Plus(OnlineFee.Times(request.Tickets));
        return new BookingPlan("OK " + total,
        [
            new BookingPlan.Charge(request.Card, total),
            new BookingPlan.Save(request.Title + ";" + request.Email + ";"
                + request.Tickets + ";" + total),
            new BookingPlan.SendMail(request.Email,
                "Bilety " + request.Title + " x" + request.Tickets + ", zaplacono " + tickets),
        ]);
    }
}
