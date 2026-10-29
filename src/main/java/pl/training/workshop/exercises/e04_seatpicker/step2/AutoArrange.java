package pl.training.workshop.exercises.e04_seatpicker.step2;

import pl.training.workshop.exercises.e04_seatpicker.HallPlan;
import pl.training.workshop.exercises.e04_seatpicker.Seat;

/**
 * Krok 2: AUTO - przed zmianą prosi koszyk o migawkę, a przywraca ją zarówno przy awarii, jak i przy
 * cofnięciu. Polecenie nie zna już pól koszyka. Świadoma zmiana zachowania: migawka obejmuje cały
 * koszyk, więc voucher nie ginie (w start i kroku 1 ginął, bo ręczna kopia go nie obejmowała).
 */
public final class AutoArrange implements Command {
    private static final int LIMIT = 4;

    private final Cart cart;
    private final int count;
    private Cart.Memento before;

    public AutoArrange(Cart cart, int count) {
        this.cart = cart;
        this.count = count;
    }

    @Override
    public void execute() {
        if (count < 1 || count > LIMIT) {
            throw new IllegalArgumentException("limit " + LIMIT + " miejsc");
        }
        before = cart.snapshot();
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
        cart.restore(before);
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
