package pl.training.workshop.exercises.e04_seatpicker.step2;

import java.util.ArrayDeque;
import java.util.Deque;

import pl.training.workshop.exercises.e04_seatpicker.HallPlan;
import pl.training.workshop.exercises.e04_seatpicker.Seat;

/**
 * Krok 2: wywołujący - tłumaczy linię skryptu na obiekt polecenia, wykonuje je i odkłada na stos.
 * UNDO zdejmuje ostatnie polecenie i prosi je o cofnięcie; drugi łańcuch if zniknął, a każda
 * linia skryptu dostaje własny obiekt z własnymi danymi do cofnięcia.
 */
public final class SeatPicker {
    private final Cart cart;
    private final Deque<Command> history = new ArrayDeque<>();

    public SeatPicker(HallPlan plan) {
        this.cart = new Cart(plan);
    }

    public void apply(String line) {
        String[] parts = line.trim().split("\\s+");
        String op = parts[0].toUpperCase();
        if (op.equals("UNDO")) {
            undo();
            return;
        }
        Command command = parse(op, parts);
        command.execute();
        history.push(command);
    }

    private Command parse(String op, String[] parts) {
        return switch (op) {
            case "SELECT" -> new SelectSeat(cart, Seat.parse(arg(parts, 1)));
            case "DESELECT" -> new DeselectSeat(cart, Seat.parse(arg(parts, 1)));
            case "SWAP" -> new SwapSeats(cart, Seat.parse(arg(parts, 1)), Seat.parse(arg(parts, 2)));
            case "GLASSES" -> new SetGlasses(cart, parseGlasses(arg(parts, 1)));
            case "VOUCHER" -> new SetVoucher(cart, parseVoucher(arg(parts, 1)));
            case "CLEAR" -> new ClearSeats(cart);
            case "AUTO" -> new AutoArrange(cart, parseCount(arg(parts, 1)));
            default -> throw new IllegalArgumentException("nieznana operacja: " + parts[0]);
        };
    }

    private void undo() {
        if (history.isEmpty()) {
            throw new IllegalStateException("nie ma czego cofnac");
        }
        history.pop().undo();
    }

    public String state() {
        return cart.state();
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
