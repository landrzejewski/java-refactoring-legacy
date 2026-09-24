namespace Training.Workshop.M3.S12CleanArchitecture.Step2;

/// <summary>Krok 2: adapter wyjściowy - mapuje rekord na kolumny tabeli.</summary>
public sealed class RowStoreReservationStore : IReservationStore
{
    private readonly RowStore _db;

    public RowStoreReservationStore(RowStore db)
    {
        _db = db;
    }

    public string Save(NewReservation reservation)
    {
        return _db.Insert([
            reservation.Email, reservation.Format, reservation.Seats, reservation.Total]);
    }
}
