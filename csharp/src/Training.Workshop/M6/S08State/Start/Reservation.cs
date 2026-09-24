namespace Training.Workshop.M6.S08State.Start;

/// <summary>
/// Start: każda operacja sprawdza status warunkami i sama go zmienia. Reguły przejść
/// New -&gt; Paid -&gt; Used, New -&gt; Expired, New/Paid -&gt; Cancelled są rozsiane po metodach.
/// </summary>
public sealed class Reservation : IReservationActions
{
    private readonly string _id;
    private readonly IPayments _payments;
    private readonly List<string> _effects = [];
    private Status _status = Status.New;

    public Reservation(string id, IPayments payments)
    {
        _id = id;
        _payments = payments;
    }

    public void Pay()
    {
        if (_status != Status.New)
        {
            throw new InvalidOperationException("cannot pay in " + _status);
        }
        _payments.Charge(_id);
        _status = Status.Paid;
        _effects.Add("charged");
    }

    public void Use()
    {
        if (_status != Status.Paid)
        {
            throw new InvalidOperationException("cannot use in " + _status);
        }
        _status = Status.Used;
        _effects.Add("gate opened");
    }

    public void Expire()
    {
        if (_status != Status.New)
        {
            throw new InvalidOperationException("cannot expire in " + _status);
        }
        _status = Status.Expired;
        _effects.Add("seats released");
    }

    public void Cancel()
    {
        if (_status == Status.New)
        {
            _status = Status.Cancelled;
            _effects.Add("seats released");
        }
        else if (_status == Status.Paid)
        {
            _status = Status.Cancelled;
            _effects.Add("refund");
            _effects.Add("seats released");
        }
        else
        {
            throw new InvalidOperationException("cannot cancel in " + _status);
        }
    }

    public Status Status => _status;

    public IReadOnlyList<string> Effects => _effects.ToList();
}
