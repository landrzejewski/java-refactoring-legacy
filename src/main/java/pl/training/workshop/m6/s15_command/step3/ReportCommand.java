package pl.training.workshop.m6.s15_command.step3;

/** Krok 3: gałąź REPORT jako obiekt komendy. */
public final class ReportCommand implements ConsoleCommand {
    @Override
    public String execute(String args, Till till) {
        return "Kasa: " + till.cash() + ", biletow: " + till.tickets();
    }
}
