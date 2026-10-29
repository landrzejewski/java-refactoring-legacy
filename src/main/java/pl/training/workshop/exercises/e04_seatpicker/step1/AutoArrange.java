package pl.training.workshop.exercises.e04_seatpicker.step1;

import java.util.List;

import pl.training.workshop.exercises.e04_seatpicker.HallPlan;
import pl.training.workshop.exercises.e04_seatpicker.Seat;

/**
 * Krok 1: AUTO - czyści koszyk i wybiera {@code count} wolnych miejsc obok siebie w najlepszym
 * rzędzie. Punkt kontrolny przeniesiony z SeatPicker bez zmian: polecenie samo kopiuje pola
 * koszyka (miejsca i okulary) i samo je przywraca, więc musi znać budowę koszyka. Voucher nadal
 * nie jest kopiowany - to ten sam zapach, tylko w innym miejscu.
 */
public final class AutoArrange implements Command {
    private static final int LIMIT = 4;

    private final Cart cart;
    private final int count;
    private List<Seat> seatsBefore = List.of();
    private boolean glassesBefore;

    public AutoArrange(Cart cart, int count) {
        this.cart = cart;
        this.count = count;
    }

    @Override
    public void execute() {
        if (count < 1 || count > LIMIT) {
            throw new IllegalArgumentException("limit " + LIMIT + " miejsc");
        }
        seatsBefore = cart.seats();
        glassesBefore = cart.ownGlasses();
        cart.clear();
        cart.setOwnGlasses(false);
        cart.setVoucher(null);
        try {
            HallPlan plan = cart.plan();
            for (int row : plan.rowsFromMiddle()) {
                int first = freeRun(plan, row);
                if (first > 0) {
                    for (int i = 0; i < count; i++) {
                        cart.add(new Seat(row, first + i));
                    }
                    return;
                }
            }
            throw new IllegalStateException("brak " + count + " miejsc obok siebie");
        } catch (IllegalStateException exception) {
            undo();
            throw exception;
        }
    }

    @Override
    public void undo() {
        cart.replaceAll(seatsBefore);
        cart.setOwnGlasses(glassesBefore);
    }

    /** Numer pierwszego miejsca ciągu {@code count} wolnych miejsc w rzędzie albo 0. */
    private int freeRun(HallPlan plan, int row) {
        int run = 0;
        for (int number = 1; number <= plan.seatsPerRow(); number++) {
            run = plan.isTaken(new Seat(row, number)) ? 0 : run + 1;
            if (run == count) {
                return number - count + 1;
            }
        }
        return 0;
    }
}
