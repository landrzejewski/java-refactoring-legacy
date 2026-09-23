package pl.training.workshop.m4.s08_movefield.start;

/** Sala kinowa. Układ sali (od którego rzędu VIP) mieszka... gdzie indziej. */
public final class Hall {
    private final String name;

    public Hall(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }
}
