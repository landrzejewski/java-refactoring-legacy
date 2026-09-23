package pl.training.workshop.m8.s02_stranglerfig;

/**
 * Stabilny kontrakt sceny: publiczne API kina widziane przez klientów (kasa, strona www).
 * Strangler Fig działa na tej granicy - klienci nie wiedzą, kto obsługuje wywołanie.
 */
public interface CinemaApi {
    /** Zwraca identyfikator rezerwacji (B1, B2, ...) albo komunikat "ERROR: ...". */
    String book(String email, String title, int format, int tickets, boolean web);

    /** Raport sprzedaży ze wspólnej bazy rezerwacji. */
    String report();
}
