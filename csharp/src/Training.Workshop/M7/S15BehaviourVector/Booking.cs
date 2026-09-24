using Training.Workshop.Shared;

namespace Training.Workshop.M7.S15BehaviourVector;

/// <summary>Stabilny kontrakt sceny: rezerwacja - mutowalny stan, który też należy do wektora zachowania.</summary>
public sealed class Booking
{
    public Booking(string id, string email, Money amount, BookingStatus status)
    {
        Id = id;
        Email = email;
        Amount = amount;
        Status = status;
    }

    public string Id { get; }

    public string Email { get; }

    public Money Amount { get; }

    public BookingStatus Status { get; private set; }

    public void MarkPaid()
    {
        Status = BookingStatus.Paid;
    }
}
