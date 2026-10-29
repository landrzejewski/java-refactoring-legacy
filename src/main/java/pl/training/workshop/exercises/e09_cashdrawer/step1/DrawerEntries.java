package pl.training.workshop.exercises.e09_cashdrawer.step1;

import pl.training.workshop.exercises.e09_cashdrawer.DrawerLine;

/** Krok 1: jedyne miejsce tworzenia wpisów - bez zmian. */
public final class DrawerEntries {
    private DrawerEntries() {
    }

    public static DrawerEntry from(DrawerLine line) {
        return switch (line.kind()) {
            case "CASH" -> new CashSale(line.reference(), line.amount());
            case "CARD" -> new CardSale(line.reference(), line.amount(), line.note());
            case "REFUND" -> new Refund(line.reference(), line.amount(), line.note());
            case "DEPOSIT" -> new Deposit(line.amount(), line.note());
            default -> throw new IllegalArgumentException("unknown entry kind: " + line.kind());
        };
    }
}
