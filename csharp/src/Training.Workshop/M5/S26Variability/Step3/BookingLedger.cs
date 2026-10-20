using Training.Workshop.Shared;

namespace Training.Workshop.M5.S26Variability.Step3;

/// <summary>
/// Krok 3: odbiorca poleceń bez warunków po statusie - o tym, co wolno i ile pieniędzy się
/// przesuwa, decyduje stan rezerwacji (Booking). Odbiorca prowadzi tylko kasę i rejestr.
/// </summary>
public sealed class BookingLedger
{
    private readonly OrderedDictionary<string, Booking> _bookings = [];
    private Money _till = Money.Zero;

    internal void Open(string id, Money amount)
    {
        if (_bookings.ContainsKey(id))
        {
            throw new ArgumentException("booking exists: " + id);
        }
        _bookings[id] = new Booking(amount);
    }

    internal void Remove(string id)
    {
        _bookings.Remove(id);
    }

    internal Status StatusOf(string id)
    {
        return Booking(id).Status;
    }

    internal Money Pay(string id)
    {
        var paid = Booking(id).Pay();
        _till = _till.Plus(paid);
        return paid;
    }

    internal void Use(string id)
    {
        Booking(id).Use();
    }

    internal Money Cancel(string id)
    {
        var refunded = Booking(id).Cancel();
        _till = _till.Minus(refunded);
        return refunded;
    }

    internal void Restore(string id, Status previous)
    {
        Booking(id).Restore(previous);
    }

    internal void Deposit(Money amount)
    {
        _till = _till.Plus(amount);
    }

    internal void Withdraw(Money amount)
    {
        _till = _till.Minus(amount);
    }

    internal string Summary()
    {
        var list = _bookings.Count == 0 ? "-" : string.Join(", ",
            _bookings.Select(entry => entry.Key + "=" + entry.Value.Status.ToString().ToUpperInvariant()));
        return "Kasa: " + _till + "; " + list;
    }

    private Booking Booking(string id)
    {
        if (!_bookings.TryGetValue(id, out var booking))
        {
            throw new ArgumentException("no booking " + id);
        }
        return booking;
    }
}
