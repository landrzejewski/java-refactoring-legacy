package pl.training.workshop.m5.s26_variability.step3;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import pl.training.workshop.shared.Money;

/** Krok 3 (bez zmian od kroku 2): BOOK jako polecenie. Cenę liczą strategie z kroku 1, cofnięcie usuwa rezerwację. */
public final class BookCommand implements DeskCommand {
    private static final Map<String, Money> FORMATS = Map.of(
            "2D", Money.of("25.00"), "3D", Money.of("32.00"), "IMAX", Money.of("40.00"));

    private final String id;
    private final String format;
    private final List<String> ticketTypes;

    private BookCommand(String id, String format, List<String> ticketTypes) {
        this.id = id;
        this.format = format;
        this.ticketTypes = List.copyOf(ticketTypes);
    }

    static BookCommand from(String[] args) {
        if (args.length < 4) {
            throw new IllegalArgumentException("BOOK <id> <format> <typ>...");
        }
        return new BookCommand(args[1], args[2], Arrays.asList(args).subList(3, args.length));
    }

    @Override
    public String execute(BookingLedger ledger) {
        Money base = FORMATS.get(format);
        if (base == null) {
            throw new IllegalArgumentException("unknown format: " + format);
        }
        Money total = Money.ZERO;
        for (String type : ticketTypes) {
            total = total.plus(TicketPricings.forType(type).price(base));
        }
        ledger.open(id, total);
        return id + " NEW " + total;
    }

    @Override
    public void undo(BookingLedger ledger) {
        ledger.remove(id);
    }

    @Override
    public String journalEntry() {
        return "BOOK " + id;
    }
}
