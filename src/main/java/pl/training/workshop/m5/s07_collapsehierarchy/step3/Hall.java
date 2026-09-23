package pl.training.workshop.m5.s07_collapsehierarchy.step3;

/** Krok 3 (rozwiązanie): jedna klasa, final. Rozróżnienie IMAX nie było kontraktem, tylko regułą tworzenia. */
public final class Hall {
    private final String name;
    private final int rows;
    private final int seatsPerRow;
    private final int vipFromRow;

    public Hall(String name, int rows, int seatsPerRow, int vipFromRow) {
        this.name = name;
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;
        this.vipFromRow = vipFromRow;
    }

    /** Sala IMAX: VIP zawsze w dwóch ostatnich rzędach (wiedza przeniesiona z konstruktora ImaxHall). */
    public static Hall imax(String name, int rows, int seatsPerRow) {
        return new Hall(name, rows, seatsPerRow, rows - 1);
    }

    public String name() {
        return name;
    }

    public int capacity() {
        return rows * seatsPerRow;
    }

    public boolean isVip(int row) {
        return row >= vipFromRow;
    }

    public String describe() {
        return name + ": " + capacity() + " miejsc, VIP od rzędu " + vipFromRow;
    }
}
