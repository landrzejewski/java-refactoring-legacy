namespace Training.Workshop.M8.S02StranglerFig;

/// <summary>
/// Stabilny kontrakt sceny: publiczne API kina widziane przez klientów (kasa, strona www).
/// Strangler Fig działa na tej granicy - klienci nie wiedzą, kto obsługuje wywołanie.
/// </summary>
public interface ICinemaApi
{
    /// <summary>Zwraca identyfikator rezerwacji (B1, B2, ...) albo komunikat "ERROR: ...".</summary>
    string Book(string email, string title, int format, int tickets, bool web);

    /// <summary>Raport sprzedaży ze wspólnej bazy rezerwacji.</summary>
    string Report();
}
