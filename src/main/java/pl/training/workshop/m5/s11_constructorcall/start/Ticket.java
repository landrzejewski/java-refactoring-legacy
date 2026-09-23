package pl.training.workshop.m5.s11_constructorcall.start;

/**
 * Start: konstruktor bazy woła metodę nadpisywalną {@code describe()}, żeby zapamiętać etykietę.
 * Override w podklasie wykona się, ZANIM konstruktor podklasy przypisze jej pola.
 * (javac -Xlint:this-escape ostrzega właśnie przed tym.)
 */
public class Ticket {
    private final String seat;
    private final String label;

    public Ticket(String seat) {
        this.seat = seat;
        this.label = describe();
    }

    protected String describe() {
        return "Miejsce " + seat;
    }

    public final String label() {
        return label;
    }
}
