namespace Training.Workshop.M3.S12CleanArchitecture.Start;

/// <summary>
/// Punkt startowy aplikacji. Na razie tylko tworzy kontroler - graf obiektów składa
/// sam kontroler. W kroku 4 ta klasa stanie się prawdziwym composition root.
/// Test woła tylko tę metodę, więc refaktoryzacja na żywo go nie psuje.
/// </summary>
public static class CinemaApplication
{
    public static ReservationController ReservationController(RowStore db, Outbox outbox)
    {
        return new ReservationController(db, outbox);
    }
}
