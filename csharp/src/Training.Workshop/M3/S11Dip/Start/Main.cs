using Training.Workshop.M3.S11Dip.Start.App;

namespace Training.Workshop.M3.S11Dip.Start;

/// <summary>
/// Composition root wariantu: jedyne miejsce, które składa graf obiektów.
/// Test woła tylko tę metodę, więc refaktoryzacja konstruktorów na żywo go nie psuje
/// (Introduce Parameter w IDE sam przeniesie tu tworzenie zależności).
/// </summary>
public static class Main
{
    public static ConfirmReservation ConfirmReservation()
    {
        return new ConfirmReservation();
    }
}
