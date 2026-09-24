using Training.Workshop.Shared;

namespace Training.Workshop.M8.S04ShadowLimits.Step1;

/// <summary>Krok 1 (bez zmian): stara ścieżka rezerwacji - autorytatywna, wykonuje prawdziwe efekty.</summary>
public sealed class LegacyBookingFlow
{
    private readonly Infrastructure _infra;

    public LegacyBookingFlow(Infrastructure infra)
    {
        _infra = infra;
    }

    public string Book(BookingRequest r)
    {
        double total = 25.00 * r.Tickets + 2.00 * r.Tickets;
        Money amount = new Money((decimal)total);
        _infra.Charge(r.Card, amount);
        _infra.Save(r.Title + ";" + r.Email + ";" + r.Tickets + ";" + amount);
        _infra.SendMail(r.Email, "Bilety " + r.Title + " x" + r.Tickets + ", zaplacono " + amount);
        return "OK " + amount;
    }
}
