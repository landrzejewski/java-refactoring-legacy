using Training.Workshop.Shared;

namespace Training.Workshop.M5.S26Variability.Step2;

/// <summary>
/// Krok 2: odbiorca poleceń - rezerwacje i kasa wydzielone z pulpitu (Extract Class).
/// Cykl życia nadal jest warunkami po statusie w każdej metodzie - to zapach na krok 3.
/// </summary>
public sealed class BookingLedger
{
    private readonly OrderedDictionary<string, Status> _statuses = [];
    private readonly OrderedDictionary<string, Money> _amounts = [];
    private Money _till = Money.Zero;

    internal void Open(string id, Money amount)
    {
        if (_statuses.ContainsKey(id))
        {
            throw new ArgumentException("booking exists: " + id);
        }
        _statuses[id] = Status.New;
        _amounts[id] = amount;
    }

    internal void Remove(string id)
    {
        _statuses.Remove(id);
        _amounts.Remove(id);
    }

    internal Status StatusOf(string id)
    {
        if (!_statuses.TryGetValue(id, out var status))
        {
            throw new ArgumentException("no booking " + id);
        }
        return status;
    }

    internal Money Pay(string id)
    {
        var status = StatusOf(id);
        if (status != Status.New)
        {
            throw Invalid("pay", status);
        }
        _till = _till.Plus(_amounts[id]);
        _statuses[id] = Status.Paid;
        return _amounts[id];
    }

    internal void Use(string id)
    {
        var status = StatusOf(id);
        if (status != Status.Paid)
        {
            throw Invalid("use", status);
        }
        _statuses[id] = Status.Used;
    }

    internal Money Cancel(string id)
    {
        var status = StatusOf(id);
        if (status == Status.New)
        {
            _statuses[id] = Status.Cancelled;
            return Money.Zero;
        }
        else if (status == Status.Paid)
        {
            _till = _till.Minus(_amounts[id]);
            _statuses[id] = Status.Cancelled;
            return _amounts[id];
        }
        throw Invalid("cancel", status);
    }

    internal void Restore(string id, Status previous)
    {
        _statuses[id] = previous;
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
        var bookings = _statuses.Count == 0 ? "-" : string.Join(", ",
            _statuses.Select(entry => entry.Key + "=" + entry.Value.ToString().ToUpperInvariant()));
        return "Kasa: " + _till + "; " + bookings;
    }

    private static InvalidOperationException Invalid(string action, Status status)
    {
        return new InvalidOperationException("cannot " + action + " in " + status.ToString().ToUpperInvariant());
    }
}
