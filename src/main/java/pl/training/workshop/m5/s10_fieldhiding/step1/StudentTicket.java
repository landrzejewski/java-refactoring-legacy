package pl.training.workshop.m5.s10_fieldhiding.step1;

/** Krok 1: ukrywające pole usunięte; ukrywająca metoda statyczna jeszcze zostaje. */
public class StudentTicket extends Ticket {
    public StudentTicket() {
        super("STUDENT");
    }

    public static String category() {
        return "BILET ULGOWY";
    }
}
