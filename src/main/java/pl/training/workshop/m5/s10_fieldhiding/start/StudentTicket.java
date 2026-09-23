package pl.training.workshop.m5.s10_fieldhiding.start;

/**
 * Start: podklasa UKRYWA pole i metodę statyczną bazy. W obiekcie są teraz dwa niezależne sloty "type";
 * który zobaczysz, zależy od typu referencji, a nie od klasy obiektu.
 */
public class StudentTicket extends Ticket {
    public String type = "STUDENT";

    public static String category() {
        return "BILET ULGOWY";
    }
}
