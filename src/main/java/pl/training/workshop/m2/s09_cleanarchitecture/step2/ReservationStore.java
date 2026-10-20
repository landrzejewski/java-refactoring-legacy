package pl.training.workshop.m2.s09_cleanarchitecture.step2;

/**
 * Krok 2: port wyjściowy zdefiniowany przez potrzebę przypadku użycia.
 * Zwraca identyfikator; gdy nie da się zapisać - {@link IllegalStateException}.
 */
public interface ReservationStore {
    String save(NewReservation reservation);
}
