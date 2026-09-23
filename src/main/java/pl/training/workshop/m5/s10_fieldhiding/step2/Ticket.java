package pl.training.workshop.m5.s10_fieldhiding.step2;

/**
 * Krok 2 (rozwiązanie): category() jako metoda instancji - nadpisywalna i wybierana dynamicznie.
 * Teraz label() daje ten sam wynik bez względu na typ referencji.
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

    public String category() {
        return "BILET";
    }

    public String label() {
        return category() + ": " + type;
    }
}
