namespace Training.Workshop.M6.S08State.Step3;

/// <summary>
/// Krok 3: Expire i Cancel w stanach - kontekst tylko deleguje. Tabela przejść jest czytelna
/// wprost z kodu stanów; stany są bezstanowymi singletonami, dane zostają w Reservation.
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
        _state.Expire(this);
    }

    public void Cancel()
    {
        _state.Cancel(this);
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

        void Expire(Reservation reservation) => throw Invalid("expire");

        void Cancel(Reservation reservation) => throw Invalid("cancel");

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

        public void Expire(Reservation reservation)
        {
            reservation.MoveTo(ClosedState.Expired);
            reservation.Record("seats released");
        }

        public void Cancel(Reservation reservation)
        {
            reservation.MoveTo(ClosedState.Cancelled);
            reservation.Record("seats released");
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

        public void Cancel(Reservation reservation)
        {
            reservation.MoveTo(ClosedState.Cancelled);
            reservation.Record("refund");
            reservation.Record("seats released");
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
