using Training.Workshop.Shared;

namespace Training.Workshop.M8.S12ExpandContract;

/// <summary>
/// Stabilny kontrakt sceny: rezerwacja zapisywana w bazie. Równość po wartości - także listy
/// miejsc (rekord C# porównywałby listę po referencji).
/// </summary>
public sealed record Booking(string Id, string Email, IReadOnlyList<string> Seats, Money Total)
{
    public IReadOnlyList<string> Seats { get; } = Seats.ToList();

    public bool Equals(Booking? other)
    {
        return other is not null
            && Id == other.Id
            && Email == other.Email
            && Seats.SequenceEqual(other.Seats)
            && Total.Equals(other.Total);
    }

    public override int GetHashCode()
    {
        return HashCode.Combine(Id, Email, string.Join(",", Seats), Total);
    }

    public override string ToString()
    {
        return "Booking[" + Id + ", " + Email + ", " + string.Join(",", Seats) + ", " + Total + "]";
    }
}
