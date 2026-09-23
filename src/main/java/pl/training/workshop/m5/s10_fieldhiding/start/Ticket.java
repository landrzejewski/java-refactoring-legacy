package pl.training.workshop.m5.s10_fieldhiding.start;

/**
 * Start: pole {@code type} i metoda statyczna {@code category()} wyglądają na "nadpisywalne", ale nie są.
 * label() jest skompilowane w Ticket, więc czyta pole Ticket.type i woła Ticket.category() -
 * także dla obiektu StudentTicket.
 */
public class Ticket {
    public String type = "NORMAL";

    public static String category() {
        return "BILET";
    }

    public String label() {
        return category() + ": " + type;
    }
}
