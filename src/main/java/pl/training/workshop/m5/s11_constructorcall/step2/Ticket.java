package pl.training.workshop.m5.s11_constructorcall.step2;

/**
 * Krok 2 (rozwiązanie): konstruktor nie woła już metody nadpisywalnej. Pole label usunięte
 * (Replace Field with Query) - etykieta liczona na żądanie, gdy obiekt jest w pełni zbudowany.
 */
public class Ticket {
    private final String seat;

    public Ticket(String seat) {
        this.seat = seat;
    }

    protected String describe() {
        return "Miejsce " + seat;
    }

    public final String label() {
        return describe();
    }
}
