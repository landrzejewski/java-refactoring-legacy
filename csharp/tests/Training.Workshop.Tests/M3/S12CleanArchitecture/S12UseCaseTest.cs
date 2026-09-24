using System.Globalization;
using Training.Workshop.M3.S12CleanArchitecture.Step4.App;

namespace Training.Workshop.Tests.M3.S12CleanArchitecture;

/// <summary>
/// Test przypadku użycia z kroku 4 na adapterach w pamięci (małe fake'i portów na delegatach):
/// bez HTTP, bez RowStore, bez Outbox. Sprawdza regułę ceny i protokół efektów.
/// </summary>
public sealed class S12UseCaseTest
{
    private readonly List<string> _saved = [];
    private readonly List<string> _notified = [];

    [Fact]
    public void SavesThenNotifies()
    {
        var useCase = new BookSeats(
            new FakeStore(reservation =>
            {
                _saved.Add(reservation.Email + " " + reservation.Total.ToString(CultureInfo.InvariantCulture));
                return "X-7";
            }),
            new FakeNotifier((id, reservation) => _notified.Add(id)));

        var booking = useCase.Execute(new BookSeatsCommand("anna@kino.pl", "3D", [2, 11]));

        Assert.Equal(new Booking("X-7", 74.00m), booking);
        Assert.Equal(["anna@kino.pl 74.00"], _saved);
        Assert.Equal(["X-7"], _notified);
    }

    [Fact]
    public void DoesNotNotifyWhenSavingFails()
    {
        var useCase = new BookSeats(
            new FakeStore(reservation => throw new InvalidOperationException("zapis nieudany")),
            new FakeNotifier((id, reservation) => _notified.Add(id)));

        Assert.Throws<InvalidOperationException>(
            () => useCase.Execute(new BookSeatsCommand("jan@kino.pl", "2D", [1])));
        Assert.Empty(_notified);
    }

    private sealed class FakeStore(Func<NewReservation, string> save) : IReservationStore
    {
        public string Save(NewReservation reservation) => save(reservation);
    }

    private sealed class FakeNotifier(Action<string, NewReservation> created) : IBookingNotifier
    {
        public void ReservationCreated(string id, NewReservation reservation) => created(id, reservation);
    }
}
