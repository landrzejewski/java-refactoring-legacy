using Training.Workshop.M6.S09Observer;
using Training.Workshop.M6.S09Observer.Step3;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M6.S09Observer;

/// <summary>Kontrakt subjectu po refaktoryzacji: rejestracje, wyrejestrowanie, rozszerzenie.</summary>
public sealed class S09SolutionTest
{
    private readonly Payment _payment = new("R7", "jan@kino.pl", "600", Money.Of("25.00"));

    [Fact]
    public void SameListenerSubscribedTwiceIsNotifiedTwice()
    {
        var log = new List<string>();
        var listener = new Listener(paidEvent => log.Add(paidEvent.ReservationId));
        var service = new PaymentService();
        service.Subscribe(listener);
        service.Subscribe(listener);
        service.Confirm(_payment);
        Assert.Equal(["R7", "R7"], log);
    }

    [Fact]
    public void ClosingSubscriptionIsIdempotentAndRemovesOnlyItsOwnRegistration()
    {
        var log = new List<string>();
        var listener = new Listener(paidEvent => log.Add(paidEvent.ReservationId));
        var service = new PaymentService();
        var first = service.Subscribe(listener);
        service.Subscribe(listener);
        first.Dispose();
        first.Dispose();
        service.Confirm(_payment);
        Assert.Equal(["R7"], log);
    }

    [Fact]
    public void NewReceiverIsAnExtensionNotARefactoring()
    {
        var log = new List<string>();
        var service = new PaymentService();
        service.Subscribe(new Listener(paidEvent => log.Add("push " + paidEvent.ReservationId)));
        service.Confirm(_payment);
        Assert.Equal(["push R7"], log);
    }

    /// <summary>Odbiorca z lambdy (w Javie IPaymentListener jest interfejsem funkcyjnym).</summary>
    private sealed class Listener(Action<ReservationPaid> onPaid) : IPaymentListener
    {
        public void OnPaid(ReservationPaid paidEvent) => onPaid(paidEvent);
    }
}
