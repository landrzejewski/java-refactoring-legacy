namespace Training.Workshop.M6.S08State.Step2;

/// <summary>
/// Krok 2: Pay i Use przeniesione do stanów. Domyślna metoda interfejsu rzuca wyjątek (nie jest pusta!),
/// a stan implementuje tylko dozwolone przejścia. Kolejność: obciążenie, zmiana stanu, efekt.
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
        _state.Pay(this);
    }

    public void Use()
    {
        _state.Use(this);
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

        void Pay(Reservation reservation) => throw Invalid("pay");

        void Use(Reservation reservation) => throw Invalid("use");

        private InvalidOperationException Invalid(string action) =>
            new("cannot " + action + " in " + Status);
    }

    private sealed class NewState : IReservationState
    {
        public static readonly NewState Instance = new();

        public Status Status => Status.New;

        public void Pay(Reservation reservation)
        {
            reservation.Charge();
            reservation.MoveTo(PaidState.Instance);
            reservation.Record("charged");
        }
    }

    private sealed class PaidState : IReservationState
    {
        public static readonly PaidState Instance = new();

        public Status Status => Status.Paid;

        public void Use(Reservation reservation)
        {
            reservation.MoveTo(ClosedState.Used);
            reservation.Record("gate opened");
        }
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
