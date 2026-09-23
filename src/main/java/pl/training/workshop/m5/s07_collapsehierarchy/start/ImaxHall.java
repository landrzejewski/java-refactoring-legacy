package pl.training.workshop.m5.s07_collapsehierarchy.start;

/**
 * Start: podklasa bez własnego stanu. Override'y tylko wołają super, a jedyna różnica to reguła
 * w konstruktorze (VIP w dwóch ostatnich rzędach). Nikt nie sprawdza instanceof ImaxHall.
 */
public class ImaxHall extends Hall {
    public ImaxHall(String name, int rows, int seatsPerRow) {
        super(name, rows, seatsPerRow, rows - 1);
    }

    @Override
    public int capacity() {
        return super.capacity();
    }

    @Override
    public String describe() {
        return super.describe();
    }
}
