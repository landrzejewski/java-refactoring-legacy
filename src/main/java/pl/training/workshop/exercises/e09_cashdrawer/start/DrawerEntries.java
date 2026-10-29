package pl.training.workshop.exercises.e09_cashdrawer.start;

import pl.training.workshop.exercises.e09_cashdrawer.DrawerLine;

/** Start: jedyne miejsce tworzenia wpisów - tłumaczy surową linię dziennika na wpis szuflady. */
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
