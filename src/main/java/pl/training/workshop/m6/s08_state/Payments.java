package pl.training.workshop.m6.s08_state;

/** Port bramki płatności - efekt zewnętrzny, który może się nie udać. */
@FunctionalInterface
public interface Payments {
    void charge(String reservationId);
}
