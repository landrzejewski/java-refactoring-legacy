using Training.Workshop.Shared;

namespace Training.Workshop.M5.S26Variability.Step3;

/// <summary>
/// Krok 3: Replace State-Altering Conditionals with State - cykl życia rezerwacji.
/// Każdy stan zna tylko swoje dozwolone przejścia; domyślnie przejście rzuca wyjątek.
/// CANCEL w New zwalnia rezerwację, w Paid oddaje pieniądze - ta sama operacja, inne zachowanie.
/// Stan nie wie, kto o operację poprosił (Command) ani jak liczono cenę (Strategy).
/// </summary>
public sealed class Booking
{
    private readonly Money _amount;
    private State _state = State.New;

    public Booking(Money amount)
    {
        ArgumentNullException.ThrowIfNull(amount);
        _amount = amount;
    }

    public Status Status => _state.Status;

    /// <summary>Zwraca kwotę pobraną do kasy.</summary>
    public Money Pay()
    {
        return _state.Pay(this);
    }

    public void Use()
    {
        _state.Use(this);
    }

    /// <summary>Zwraca kwotę oddaną klientowi (zero, gdy rezerwacja nie była opłacona).</summary>
    public Money Cancel()
    {
        return _state.Cancel(this);
    }

    /// <summary>Dla cofania poleceń: powrót do zapamiętanego stanu z pominięciem reguł przejść.</summary>
    internal void Restore(Status status)
    {
        _state = State.Of(status);
    }

    /// <summary>
    /// Stany jako prywatny typ rezerwacji. Enum C# nie ma metod per stała (jak enum Javy),
    /// więc to klasa abstrakcyjna z jednym obiektem na stan; przejścia nadpisują metody bazowe.
    /// </summary>
    private abstract class State
    {
        public static readonly State New = new NewState();
        public static readonly State Paid = new PaidState();
        public static readonly State Used = new FinalState(Status.Used);
        public static readonly State Cancelled = new FinalState(Status.Cancelled);

        public abstract Status Status { get; }

        public static State Of(Status status) => status switch
        {
            Status.New => New,
            Status.Paid => Paid,
            Status.Used => Used,
            Status.Cancelled => Cancelled,
            _ => throw new ArgumentOutOfRangeException(nameof(status), status, null),
        };

        public virtual Money Pay(Booking booking) => throw Invalid("pay");

        public virtual void Use(Booking booking) => throw Invalid("use");

        public virtual Money Cancel(Booking booking) => throw Invalid("cancel");

        private InvalidOperationException Invalid(string action) =>
            new("cannot " + action + " in " + Status.ToString().ToUpperInvariant());

        private sealed class NewState : State
        {
            public override Status Status => Status.New;

            public override Money Pay(Booking booking)
            {
                booking._state = Paid;
                return booking._amount;
            }

            public override Money Cancel(Booking booking)
            {
                booking._state = Cancelled;
                return Money.Zero;
            }
        }

        private sealed class PaidState : State
        {
            public override Status Status => Status.Paid;

            public override void Use(Booking booking)
            {
                booking._state = Used;
            }

            public override Money Cancel(Booking booking)
            {
                booking._state = Cancelled;
                return booking._amount;
            }
        }

        /// <summary>Stany końcowe (Used, Cancelled) - żadne przejście nie jest dozwolone.</summary>
        private sealed class FinalState(Status status) : State
        {
            public override Status Status => status;
        }
    }
}
