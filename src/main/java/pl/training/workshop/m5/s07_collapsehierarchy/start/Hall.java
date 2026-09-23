package pl.training.workshop.m5.s07_collapsehierarchy.start;

/** Start: zwykła sala. Ma jednego potomka, który prawie niczego nie dodaje. */
public class Hall {
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
