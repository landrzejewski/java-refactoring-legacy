package pl.training.workshop.m3.s09_lsp.start;

import java.util.Set;

/**
 * Start: sala archiwalna - plan miejsc zamkniętego seansu, potrzebny tylko raportom.
 * Dziedziczy po Hall, "bo też ma plan miejsc", ale {@code reserve} rzuca
 * UnsupportedOperationException. Kontrakt Hall nie przewiduje odmowy, więc podtyp
 * wzmacnia warunek wstępny (do "nigdy") - kasa dostająca Hall wybucha w runtime.
 */
public class ReadOnlyHall extends Hall {
    public ReadOnlyHall(int capacity, Set<Integer> taken) {
        super(capacity, taken);
    }

    @Override
    public void reserve(int seat) {
        throw new UnsupportedOperationException("sala archiwalna - tylko do odczytu");
    }
}
