using System.Collections.Immutable;

namespace Training.Workshop.M6.S09Observer.Step3;

/// <summary>
/// Krok 3: subject z Subscribe - serwis nie zna już żadnego konkretnego odbiorcy.
/// Kontrakt: synchronicznie, w kolejności subskrypcji, fail-fast, iteracja po migawce listy.
/// </summary>
public sealed class PaymentService
{
    private readonly List<string> _paid = [];
    private ImmutableList<Registration> _listeners = [];

    public ISubscription Subscribe(IPaymentListener listener)
    {
        ArgumentNullException.ThrowIfNull(listener);
        var registration = new Registration(listener);
        ImmutableInterlocked.Update(ref _listeners, listeners => listeners.Add(registration));
        return new Unsubscribe(this, registration);
    }

    public void Confirm(Payment payment)
    {
        if (payment.Amount.Amount <= 0)
        {
            throw new ArgumentException("amount must be positive");
        }
        _paid.Add(payment.ReservationId);
        var paidEvent = new ReservationPaid(
            payment.ReservationId, payment.Email, payment.Phone, payment.Amount);
        foreach (var registration in _listeners)
        {
            registration.Listener.OnPaid(paidEvent);
        }
    }

    public IReadOnlyList<string> Paid => _paid.ToList();

    /// <summary>Osobny obiekt na każde Subscribe - ten sam odbiorca zapisany dwa razy to dwie rejestracje.</summary>
    private sealed class Registration(IPaymentListener listener)
    {
        public IPaymentListener Listener => listener;
    }

    private sealed class Unsubscribe(PaymentService service, Registration registration) : ISubscription
    {
        private int _active = 1;

        public void Dispose()
        {
            if (Interlocked.CompareExchange(ref _active, 0, 1) == 1)
            {
                ImmutableInterlocked.Update(ref service._listeners, listeners => listeners.Remove(registration));
            }
        }
    }
}
