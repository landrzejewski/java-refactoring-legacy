package pl.training.workshop.exercises.e04_seatpicker.start;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import pl.training.workshop.exercises.e04_seatpicker.HallPlan;
import pl.training.workshop.exercises.e04_seatpicker.Seat;
import pl.training.workshop.shared.Money;

/**
 * Start: koszyk miejsc kasjera w jednej klasie. Operacje wybiera {@code switch} po nazwie,
 * a cofanie to drugi łańcuch {@code if} z odwrotnością każdej operacji. Historia trzyma jeden
 * rekord "na wszystko" (pola używane zależnie od operacji). Przed automatyczną propozycją miejsc
 * koszyk jest zabezpieczany ręcznym punktem kontrolnym: kopią pól do zmiennych lokalnych.
 * Punkt kontrolny powstał, gdy koszyk miał tylko miejsca i okulary; voucher doszedł później
 * i nikt nie dopisał go do kopii.
 */
public final class SeatPicker {
    private static final Money BASE = Money.of("32.00");
    private static final Money VIP = Money.of("10.00");
    private static final Money GLASSES = Money.of("3.00");
    private static final int LIMIT = 4;

    private final HallPlan plan;
    private final List<Seat> seats = new ArrayList<>();
    private boolean ownGlasses;
    private String voucher;
    private final Deque<Change> history = new ArrayDeque<>();

    /** Jeden rekord na wszystkie rodzaje zmian: które pola coś znaczą, zależy od {@code op}. */
    private record Change(String op, Seat seat, Seat other, int index, boolean flag, String text,
                          List<Seat> seatsBefore) {
    }

    public SeatPicker(HallPlan plan) {
        this.plan = plan;
    }

    public void apply(String line) {
        String[] parts = line.trim().split("\\s+");
        String op = parts[0].toUpperCase();
        switch (op) {
            case "SELECT" -> {
                Seat seat = Seat.parse(arg(parts, 1));
                select(seat);
                history.push(new Change(op, seat, null, 0, false, null, null));
            }
            case "DESELECT" -> {
                Seat seat = Seat.parse(arg(parts, 1));
                int index = seats.indexOf(seat);
                if (index < 0) {
                    throw new IllegalArgumentException("miejsca " + seat + " nie ma w koszyku");
                }
                seats.remove(index);
                history.push(new Change(op, seat, null, index, false, null, null));
            }
            case "SWAP" -> {
                Seat from = Seat.parse(arg(parts, 1));
                Seat to = Seat.parse(arg(parts, 2));
                int index = seats.indexOf(from);
                if (index < 0) {
                    throw new IllegalArgumentException("miejsca " + from + " nie ma w koszyku");
                }
                checkFree(to);
                seats.set(index, to);
                history.push(new Change(op, from, to, index, false, null, null));
            }
            case "GLASSES" -> {
                boolean own = parseGlasses(arg(parts, 1));
                history.push(new Change(op, null, null, 0, ownGlasses, null, null));
                ownGlasses = own;
            }
            case "VOUCHER" -> {
                String code = parseVoucher(arg(parts, 1));
                history.push(new Change(op, null, null, 0, false, voucher, null));
                voucher = code;
            }
            case "CLEAR" -> {
                history.push(new Change(op, null, null, 0, false, null, List.copyOf(seats)));
                seats.clear();
            }
            case "AUTO" -> autoArrange(parseCount(arg(parts, 1)));
            case "UNDO" -> undo();
            default -> throw new IllegalArgumentException("nieznana operacja: " + parts[0]);
        }
    }

    private void select(Seat seat) {
        checkFree(seat);
        if (seats.size() >= LIMIT) {
            throw new IllegalStateException("limit " + LIMIT + " miejsc");
        }
        seats.add(seat);
    }

    private void checkFree(Seat seat) {
        if (!plan.exists(seat)) {
            throw new IllegalArgumentException("nie ma miejsca " + seat);
        }
        if (plan.isTaken(seat)) {
            throw new IllegalStateException("miejsce " + seat + " jest zajete");
        }
        if (seats.contains(seat)) {
            throw new IllegalStateException("miejsce " + seat + " jest juz w koszyku");
        }
    }

    /**
     * Automatyczna propozycja: czyści koszyk i wybiera {@code count} wolnych miejsc obok siebie
     * w najlepszym rzędzie. Gdy się nie uda, przywraca koszyk z punktu kontrolnego.
     */
    private void autoArrange(int count) {
        if (count < 1 || count > LIMIT) {
            throw new IllegalArgumentException("limit " + LIMIT + " miejsc");
        }
        // punkt kontrolny: kopia pól do zmiennych lokalnych
        List<Seat> seatsBefore = new ArrayList<>(seats);
        boolean glassesBefore = ownGlasses;
        seats.clear();
        ownGlasses = false;
        voucher = null;
        try {
            for (int row : plan.rowsFromMiddle()) {
                int first = freeRun(row, count);
                if (first > 0) {
                    for (int i = 0; i < count; i++) {
                        seats.add(new Seat(row, first + i));
                    }
                    history.push(new Change("AUTO", null, null, 0, glassesBefore, null, seatsBefore));
                    return;
                }
            }
            throw new IllegalStateException("brak " + count + " miejsc obok siebie");
        } catch (IllegalStateException exception) {
            seats.clear();
            seats.addAll(seatsBefore);
            ownGlasses = glassesBefore;
            throw exception;
        }
    }

    /** Numer pierwszego miejsca ciągu {@code count} wolnych miejsc w rzędzie albo 0. */
    private int freeRun(int row, int count) {
        int run = 0;
        for (int number = 1; number <= plan.seatsPerRow(); number++) {
            run = plan.isTaken(new Seat(row, number)) ? 0 : run + 1;
            if (run == count) {
                return number - count + 1;
            }
        }
        return 0;
    }

    private void undo() {
        if (history.isEmpty()) {
            throw new IllegalStateException("nie ma czego cofnac");
        }
        Change change = history.pop();
        if (change.op().equals("SELECT")) {
            seats.remove(change.seat());
        } else if (change.op().equals("DESELECT")) {
            seats.add(change.index(), change.seat());
        } else if (change.op().equals("SWAP")) {
            seats.set(change.index(), change.seat());
        } else if (change.op().equals("GLASSES")) {
            ownGlasses = change.flag();
        } else if (change.op().equals("VOUCHER")) {
            voucher = change.text();
        } else if (change.op().equals("CLEAR")) {
            seats.addAll(change.seatsBefore());
        } else if (change.op().equals("AUTO")) {
            seats.clear();
            seats.addAll(change.seatsBefore());
            ownGlasses = change.flag();
        }
    }

    public String state() {
        String list = seats.isEmpty() ? "-" : seats.stream().map(Seat::toString).collect(Collectors.joining(" "));
        return "miejsca: " + list
                + " | okulary: " + (ownGlasses ? "wlasne" : "wypozyczone")
                + " | voucher: " + (voucher == null ? "-" : voucher)
                + " | suma: " + total();
    }

    private Money total() {
        Money sum = Money.ZERO;
        for (Seat seat : seats) {
            Money price = BASE;
            if (plan.isVip(seat)) {
                price = price.plus(VIP);
            }
            if (!ownGlasses) {
                price = price.plus(GLASSES);
            }
            sum = sum.plus(price);
        }
        if ("KINO10".equals(voucher)) {
            sum = sum.minus(sum.percent(10));
        }
        return sum;
    }

    private static String arg(String[] parts, int index) {
        if (parts.length <= index) {
            throw new IllegalArgumentException("brak argumentu operacji " + parts[0].toUpperCase());
        }
        return parts[index];
    }

    private static boolean parseGlasses(String text) {
        return switch (text.toLowerCase()) {
            case "own" -> true;
            case "rent" -> false;
            default -> throw new IllegalArgumentException("okulary: own albo rent");
        };
    }

    private static String parseVoucher(String text) {
        if (text.equalsIgnoreCase("none")) {
            return null;
        }
        if (text.equals("KINO10")) {
            return text;
        }
        throw new IllegalArgumentException("nieznany voucher: " + text);
    }

    private static int parseCount(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("zla liczba miejsc: " + text);
        }
    }
}
