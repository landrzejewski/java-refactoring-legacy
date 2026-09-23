package pl.training.workshop.m6.s08_state;

import java.util.List;

/** Stabilny kontrakt rezerwacji - wspólny dla start i kroków, używany przez test tabeli przejść. */
public interface ReservationActions {
    void pay();

    void use();

    void expire();

    void cancel();

    Status status();

    /** Efekty uboczne w kolejności wystąpienia. */
    List<String> effects();
}
