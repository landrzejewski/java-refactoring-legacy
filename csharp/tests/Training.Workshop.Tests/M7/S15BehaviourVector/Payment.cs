using Training.Workshop.M7.S15BehaviourVector;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M7.S15BehaviourVector;

/// <summary>Wejście testów sceny: stan rezerwacji przed płatnością i numer karty.</summary>
public sealed record Payment(BookingStatus StatusBefore, string? Card)
{
    internal static readonly Payment Success = new(BookingStatus.New, "4111111111111111");
    internal static readonly Payment Declined = new(BookingStatus.New, "4111111111110000");
    internal static readonly Payment AlreadyPaid = new(BookingStatus.Paid, "4111111111111111");
    internal static readonly Payment NoCard = new(BookingStatus.New, null);

    internal Booking Booking() => new("B1", "anna@kino.pl", Money.Of("114.00"), StatusBefore);
}
