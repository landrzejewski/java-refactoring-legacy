package pl.training.workshop.m5.s10_fieldhiding.step2;

/** Krok 2: prawdziwy override z @Override - kompilator pilnuje, że coś nadpisujemy. */
public class StudentTicket extends Ticket {
    public StudentTicket() {
        super("STUDENT");
    }

    @Override
    public String category() {
        return "BILET ULGOWY";
    }
}
