namespace Training.Workshop.M6.S08State.Step1;

/// <summary>
/// Krok 1: pole _status zastąpione obiektem stanu (na razie zna tylko swój Status).
/// Warunki bez zmian - porównują _state.Status. Efekty i zmiana stanu w metodach pomocniczych.
/// </summary>
public sealed class Reservation : IReservationActions
{
    private readonly string _id;
    private readonly IPayments _payments;
    private readonly List<string> _effects = [];
    private IReservationState _state = NewState.Instance;

    public Reservation(string id, IPayments payments)
    {
        _id = id;
        _payments = payments;
    }

    public void Pay()
    {
        if (_state.Status != Status.New)
        {
            throw new InvalidOperationException("cannot pay in " + _state.Status);
        }
        Charge();
        MoveTo(PaidState.Instance);
        Record("charged");
    }

    public void Use()
    {
        if (_state.Status != Status.Paid)
        {
            throw new InvalidOperationException("cannot use in " + _state.Status);
        }
        MoveTo(ClosedState.Used);
        Record("gate opened");
    }

    public void Expire()
    {
        if (_state.Status != Status.New)
        {
            throw new InvalidOperationException("cannot expire in " + _state.Status);
        }
        MoveTo(ClosedState.Expired);
        Record("seats released");
    }

    public void Cancel()
    {
        if (_state.Status == Status.New)
        {
            MoveTo(ClosedState.Cancelled);
            Record("seats released");
        }
        else if (_state.Status == Status.Paid)
        {
            MoveTo(ClosedState.Cancelled);
            Record("refund");
            Record("seats released");
        }
        else
        {
            throw new InvalidOperationException("cannot cancel in " + _state.Status);
        }
    }

    public Status Status => _state.Status;

    public IReadOnlyList<string> Effects => _effects.ToList();

    private void Charge()
    {
        _payments.Charge(_id);
    }

    private void MoveTo(IReservationState next)
    {
        _state = next;
    }

    private void Record(string effect)
    {
        _effects.Add(effect);
    }

    private interface IReservationState
    {
        Status Status { get; }
    }

    private sealed class NewState : IReservationState
    {
        public static readonly NewState Instance = new();

        public Status Status => Status.New;
    }

    private sealed class PaidState : IReservationState
    {
        public static readonly PaidState Instance = new();

        public Status Status => Status.Paid;
    }

    /// <summary>Stany końcowe - żadne przejście nie jest dozwolone.</summary>
    private sealed class ClosedState(Status status) : IReservationState
    {
        public static readonly ClosedState Used = new(Status.Used);
        public static readonly ClosedState Expired = new(Status.Expired);
        public static readonly ClosedState Cancelled = new(Status.Cancelled);

        public Status Status => status;
    }
}
