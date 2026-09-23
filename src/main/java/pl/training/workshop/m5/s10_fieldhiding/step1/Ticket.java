package pl.training.workshop.m5.s10_fieldhiding.step1;

/**
 * Krok 1: jedno pole zamiast dwóch slotów - prywatne, final, ustawiane przez konstruktor
 * (Encapsulate Field + parametr konstruktora). Podklasa przekazuje swoją wartość przez super(...).
 */
public class Ticket {
    private final String type;

    public Ticket() {
        this("NORMAL");
    }

    protected Ticket(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }

    public static String category() {
        return "BILET";
    }

    public String label() {
        return category() + ": " + type;
    }
}
