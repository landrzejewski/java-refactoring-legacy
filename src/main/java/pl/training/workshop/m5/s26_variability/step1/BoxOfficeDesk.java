package pl.training.workshop.m5.s26_variability.step1;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import pl.training.workshop.m5.s26_variability.Status;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: oś "cena wg typu biletu" wydzielona jako Strategy - TicketPricings wybiera strategię
 * po kodzie typu, a pulpit tylko ją woła. Operacje i cykl życia bez zmian (jedna oś naraz).
 */
public final class BoxOfficeDesk {
    private static final Map<String, Money> FORMATS = Map.of(
            "2D", Money.of("25.00"), "3D", Money.of("32.00"), "IMAX", Money.of("40.00"));

    private final Map<String, Status> statuses = new LinkedHashMap<>();
    private final Map<String, Money> amounts = new LinkedHashMap<>();
    private final List<Entry> journal = new ArrayList<>();
    private Money till = Money.ZERO;

    public String handle(String line) {
        String[] args = line.strip().split(" +");
        String operation = args[0].toUpperCase(Locale.ROOT);
        try {
            return switch (operation) {
                case "BOOK" -> book(args);
                case "PAY", "USE", "CANCEL" -> change(operation, args);
                case "UNDO" -> undo();
                case "REPORT" -> report();
                default -> "Nieznana operacja: " + args[0];
            };
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return "Blad: " + exception.getMessage();
        }
    }

    private String book(String[] args) {
        if (args.length < 4) {
            throw new IllegalArgumentException("BOOK <id> <format> <typ>...");
        }
        Money base = FORMATS.get(args[2]);
        if (base == null) {
            throw new IllegalArgumentException("unknown format: " + args[2]);
        }
        Money total = Money.ZERO;
        for (int i = 3; i < args.length; i++) {
            total = total.plus(TicketPricings.forType(args[i]).price(base));
        }
        if (statuses.containsKey(args[1])) {
            throw new IllegalArgumentException("booking exists: " + args[1]);
        }
        statuses.put(args[1], Status.NEW);
        amounts.put(args[1], total);
        journal.add(new Entry("BOOK", args[1], null, Money.ZERO));
        return args[1] + " NEW " + total;
    }

    private String change(String operation, String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException(operation + " <id>");
        }
        String id = args[1];
        Status status = statuses.get(id);
        if (status == null) {
            throw new IllegalArgumentException("no booking " + id);
        }
        Money amount = amounts.get(id);
        Money moved = Money.ZERO;
        Status next = switch (status) {
            case NEW -> switch (operation) {
                case "PAY" -> {
                    till = till.plus(amount);
                    moved = amount;
                    yield Status.PAID;
                }
                case "CANCEL" -> Status.CANCELLED;
                default -> null;
            };
            case PAID -> switch (operation) {
                case "USE" -> Status.USED;
                case "CANCEL" -> {
                    till = till.minus(amount);
                    moved = amount;
                    yield Status.CANCELLED;
                }
                default -> null;
            };
            case USED, CANCELLED -> null;
        };
        if (next == null) {
            throw new IllegalStateException("cannot " + operation.toLowerCase(Locale.ROOT) + " in " + status);
        }
        statuses.put(id, next);
        journal.add(new Entry(operation, id, status, moved));
        if (operation.equals("PAY")) {
            return id + " " + next + " " + moved;
        }
        return id + " " + next + (moved.isGreaterThan(Money.ZERO) ? " zwrot " + moved : "");
    }

    private String undo() {
        if (journal.isEmpty()) {
            throw new IllegalStateException("nothing to undo");
        }
        Entry last = journal.removeLast();
        switch (last.operation()) {
            case "BOOK" -> {
                statuses.remove(last.id());
                amounts.remove(last.id());
            }
            case "PAY" -> {
                till = till.minus(last.moved());
                statuses.put(last.id(), last.previous());
            }
            case "CANCEL" -> {
                till = till.plus(last.moved());
                statuses.put(last.id(), last.previous());
            }
            default -> statuses.put(last.id(), last.previous());
        }
        return "Cofnieto: " + last.operation() + " " + last.id();
    }

    private String report() {
        String bookings = statuses.isEmpty() ? "-" : statuses.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", "));
        String log = journal.isEmpty() ? "-" : journal.stream()
                .map(entry -> entry.operation() + " " + entry.id())
                .collect(Collectors.joining(", "));
        return "Kasa: " + till + "; " + bookings + "; dziennik: " + log;
    }

    /** Wpis dziennika: z niego liczone jest cofanie - osobny switch po nazwie operacji. */
    private record Entry(String operation, String id, Status previous, Money moved) {
    }
}
